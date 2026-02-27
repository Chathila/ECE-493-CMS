package com.ece493.cms.unit;

import com.ece493.cms.model.PasswordChangeRequest;
import com.ece493.cms.model.PasswordChangeResult;
import com.ece493.cms.model.UserAccount;
import com.ece493.cms.repository.UserAccountRepository;
import com.ece493.cms.security.PasswordHasher;
import com.ece493.cms.service.PasswordChangeServiceImpl;
import com.ece493.cms.service.PasswordPolicyService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PasswordChangeServiceImplTest {
    @Test
    void rejectsWhenUserNotLoggedIn() {
        PasswordChangeServiceImpl service = serviceWith(new StubRepo(), true);

        PasswordChangeResult result = service.changePassword(null, request("Old12345", "New12345", "New12345"));

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void rejectsWhenRequiredFieldsMissing() {
        PasswordChangeServiceImpl service = serviceWith(new StubRepo(), true);

        PasswordChangeResult result = service.changePassword("user@cms.com", request("", "New12345", "New12345"));

        assertEquals(400, result.getStatusCode());
        assertTrue(result.getMessage().contains("Missing required fields"));
    }

    @Test
    void rejectsWhenNewPasswordMissing() {
        PasswordChangeServiceImpl service = serviceWith(new StubRepo(), true);

        PasswordChangeResult result = service.changePassword("user@cms.com", request("Old12345", "", "New12345"));

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsWhenConfirmationMissing() {
        PasswordChangeServiceImpl service = serviceWith(new StubRepo(), true);

        PasswordChangeResult result = service.changePassword("user@cms.com", request("Old12345", "New12345", ""));

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsWhenRequestIsNull() {
        PasswordChangeServiceImpl service = serviceWith(new StubRepo(), true);

        PasswordChangeResult result = service.changePassword("user@cms.com", null);

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsWhenUserEmailBlankAfterTrim() {
        PasswordChangeServiceImpl service = serviceWith(new StubRepo(), true);

        PasswordChangeResult result = service.changePassword("   ", request("Old12345", "New12345", "New12345"));

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void rejectsWhenAuthServiceUnavailable() {
        PasswordChangeServiceImpl service = new PasswordChangeServiceImpl(
                new StubRepo(),
                meetsPolicy(true),
                new PasswordHasher(),
                () -> false
        );

        PasswordChangeResult result = service.changePassword("user@cms.com", request("Old12345", "New12345", "New12345"));

        assertEquals(503, result.getStatusCode());
    }

    @Test
    void rejectsWhenUserNotFound() {
        StubRepo repo = new StubRepo();
        PasswordChangeServiceImpl service = serviceWith(repo, true);

        PasswordChangeResult result = service.changePassword("missing@cms.com", request("Old12345", "New12345", "New12345"));

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void rejectsWhenCurrentPasswordInvalid() {
        StubRepo repo = new StubRepo();
        repo.seed("user@cms.com", "Old12345", "ACTIVE", "AUTHOR");
        PasswordChangeServiceImpl service = serviceWith(repo, true);

        PasswordChangeResult result = service.changePassword("user@cms.com", request("Wrong123", "New12345", "New12345"));

        assertEquals(401, result.getStatusCode());
        assertFalse(repo.updated);
    }

    @Test
    void rejectsWhenNewPasswordWeak() {
        StubRepo repo = new StubRepo();
        repo.seed("user@cms.com", "Old12345", "ACTIVE", "AUTHOR");
        PasswordChangeServiceImpl service = new PasswordChangeServiceImpl(
                repo,
                meetsPolicy(false),
                new PasswordHasher(),
                () -> true
        );

        PasswordChangeResult result = service.changePassword("user@cms.com", request("Old12345", "weak", "weak"));

        assertEquals(403, result.getStatusCode());
        assertTrue(result.getMessage().contains("CMS policy"));
    }

    @Test
    void rejectsWhenConfirmationMismatch() {
        StubRepo repo = new StubRepo();
        repo.seed("user@cms.com", "Old12345", "ACTIVE", "AUTHOR");
        PasswordChangeServiceImpl service = serviceWith(repo, true);

        PasswordChangeResult result = service.changePassword("user@cms.com", request("Old12345", "New12345", "Different123"));

        assertEquals(400, result.getStatusCode());
        assertTrue(result.getMessage().contains("must match"));
    }

    @Test
    void rejectsWhenUpdateReturnsFalse() {
        StubRepo repo = new StubRepo();
        repo.seed("user@cms.com", "Old12345", "ACTIVE", "AUTHOR");
        repo.forceUpdateResult = false;
        PasswordChangeServiceImpl service = serviceWith(repo, true);

        PasswordChangeResult result = service.changePassword("user@cms.com", request("Old12345", "New12345", "New12345"));

        assertEquals(503, result.getStatusCode());
    }

    @Test
    void changesPasswordAndRequiresReloginOnSuccess() {
        StubRepo repo = new StubRepo();
        repo.seed("user@cms.com", "Old12345", "ACTIVE", "AUTHOR");
        PasswordChangeServiceImpl service = serviceWith(repo, true);

        PasswordChangeResult result = service.changePassword("user@cms.com", request("Old12345", "New12345", "New12345"));

        assertEquals(200, result.getStatusCode());
        assertTrue(result.requiresRelogin());
        assertTrue(repo.updated);
        assertNotEquals(repo.seedHash, repo.updatedHash);
        assertNotNull(repo.updatedSalt);
    }

    private PasswordChangeServiceImpl serviceWith(StubRepo repo, boolean policyPasses) {
        return new PasswordChangeServiceImpl(
                repo,
                meetsPolicy(policyPasses),
                new PasswordHasher(),
                () -> true
        );
    }

    private PasswordPolicyService meetsPolicy(boolean pass) {
        return password -> pass;
    }

    private PasswordChangeRequest request(String current, String next, String confirm) {
        return new PasswordChangeRequest(current, next, confirm);
    }

    private static class StubRepo implements UserAccountRepository {
        private UserAccount user;
        private boolean updated;
        private boolean forceUpdateResult = true;
        private String seedHash;
        private String updatedHash;
        private String updatedSalt;

        void seed(String email, String rawPassword, String status, String role) {
            PasswordHasher hasher = new PasswordHasher();
            String salt = hasher.generateSalt();
            String hash = hasher.hashPassword(rawPassword, salt);
            this.seedHash = hash;
            this.user = new UserAccount(1L, email, hash, salt, status, role, Instant.now());
        }

        @Override
        public boolean existsByEmail(String email) {
            return user != null && user.getEmail().equals(email);
        }

        @Override
        public Optional<UserAccount> findByEmail(String email) {
            if (user == null || !user.getEmail().equals(email)) {
                return Optional.empty();
            }
            return Optional.of(user);
        }

        @Override
        public void save(UserAccount userAccount) {
            this.user = userAccount;
        }

        @Override
        public boolean updatePasswordCredentialsByEmail(String email, String passwordHash, String passwordSalt) {
            if (!forceUpdateResult || user == null || !user.getEmail().equals(email)) {
                return false;
            }
            updated = true;
            updatedHash = passwordHash;
            updatedSalt = passwordSalt;
            user = new UserAccount(user.getUserId(), user.getEmail(), passwordHash, passwordSalt, user.getStatus(), user.getRole(), user.getCreatedAt());
            return true;
        }

        @Override
        public long countByEmail(String email) {
            return existsByEmail(email) ? 1 : 0;
        }
    }
}
