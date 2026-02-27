package com.ece493.cms.unit;

import com.ece493.cms.model.LoginResult;
import com.ece493.cms.model.LoginSubmission;
import com.ece493.cms.model.UserAccount;
import com.ece493.cms.repository.UserAccountRepository;
import com.ece493.cms.security.PasswordHasher;
import com.ece493.cms.service.AccountStatusService;
import com.ece493.cms.service.AuthenticationAvailabilityService;
import com.ece493.cms.service.AuthenticationServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticationServiceImplTest {
    @Test
    void returnsMissingFieldsWhenEmailOrPasswordMissing() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(
                new StubRepo(Optional.empty()),
                new PasswordHasher(),
                statusService(true),
                availableService(true)
        );

        LoginResult result = service.authenticate(new LoginSubmission("", "secret"));

        assertEquals(400, result.getStatusCode());
        assertTrue(result.getMessage().contains("Missing required fields"));
    }

    @Test
    void returnsMissingFieldsWhenPasswordBlank() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(
                new StubRepo(Optional.empty()),
                new PasswordHasher(),
                statusService(true),
                availableService(true)
        );

        LoginResult result = service.authenticate(new LoginSubmission("user@cms.com", " "));

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void returnsServiceUnavailableWhenAuthenticationServiceDown() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(
                new StubRepo(Optional.empty()),
                new PasswordHasher(),
                statusService(true),
                availableService(false)
        );

        LoginResult result = service.authenticate(new LoginSubmission("user@cms.com", "secret"));

        assertEquals(503, result.getStatusCode());
        assertTrue(result.getMessage().contains("try again later"));
    }

    @Test
    void returnsUnauthorizedWhenUserNotFound() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(
                new StubRepo(Optional.empty()),
                new PasswordHasher(),
                statusService(true),
                availableService(true)
        );

        LoginResult result = service.authenticate(new LoginSubmission("missing@cms.com", "secret"));

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void returnsUnauthorizedWhenPasswordIncorrect() {
        PasswordHasher hasher = new PasswordHasher();
        String salt = hasher.generateSalt();
        String hash = hasher.hashPassword("Correct123", salt);

        AuthenticationServiceImpl service = new AuthenticationServiceImpl(
                new StubRepo(Optional.of(user("user@cms.com", hash, salt, "ACTIVE", "AUTHOR"))),
                hasher,
                statusService(true),
                availableService(true)
        );

        LoginResult result = service.authenticate(new LoginSubmission("user@cms.com", "Wrong123"));

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void returnsForbiddenWhenAccountLocked() {
        PasswordHasher hasher = new PasswordHasher();
        String salt = hasher.generateSalt();
        String hash = hasher.hashPassword("Correct123", salt);

        AuthenticationServiceImpl service = new AuthenticationServiceImpl(
                new StubRepo(Optional.of(user("user@cms.com", hash, salt, "LOCKED", "AUTHOR"))),
                hasher,
                statusService(false),
                availableService(true)
        );

        LoginResult result = service.authenticate(new LoginSubmission("user@cms.com", "Correct123"));

        assertEquals(403, result.getStatusCode());
        assertTrue(result.getMessage().contains("inactive or locked"));
    }

    @Test
    void returnsRedirectForValidCredentialsAndActiveAccount() {
        PasswordHasher hasher = new PasswordHasher();
        String salt = hasher.generateSalt();
        String hash = hasher.hashPassword("Correct123", salt);

        AuthenticationServiceImpl service = new AuthenticationServiceImpl(
                new StubRepo(Optional.of(user("user@cms.com", hash, salt, "ACTIVE", "PROGRAM_CHAIR"))),
                hasher,
                statusService(true),
                availableService(true)
        );

        LoginResult result = service.authenticate(new LoginSubmission("user@cms.com", "Correct123"));

        assertEquals(302, result.getStatusCode());
        assertTrue(result.getRedirectLocation().startsWith("/home?role="));
        assertTrue(result.getRedirectLocation().contains("PROGRAM_CHAIR"));
        assertEquals("user@cms.com", result.getAuthenticatedEmail());
    }

    @Test
    void fallsBackToUserRoleWhenAccountRoleIsNull() {
        PasswordHasher hasher = new PasswordHasher();
        String salt = hasher.generateSalt();
        String hash = hasher.hashPassword("Correct123", salt);

        AuthenticationServiceImpl service = new AuthenticationServiceImpl(
                new StubRepo(Optional.of(new UserAccount(1L, "user@cms.com", hash, salt, "ACTIVE", null, Instant.now()))),
                hasher,
                statusService(true),
                availableService(true)
        );

        LoginResult result = service.authenticate(new LoginSubmission("user@cms.com", "Correct123"));

        assertEquals(302, result.getStatusCode());
        assertTrue(result.getRedirectLocation().contains("USER"));
    }

    @Test
    void returnsMissingFieldsWhenSubmissionIsNull() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(
                new StubRepo(Optional.empty()),
                new PasswordHasher(),
                statusService(true),
                availableService(true)
        );

        LoginResult result = service.authenticate(null);

        assertEquals(400, result.getStatusCode());
    }

    private UserAccount user(String email, String hash, String salt, String status, String role) {
        return new UserAccount(1L, email, hash, salt, status, role, Instant.now());
    }

    private AccountStatusService statusService(boolean allows) {
        return status -> allows;
    }

    private AuthenticationAvailabilityService availableService(boolean available) {
        return () -> available;
    }

    private static class StubRepo implements UserAccountRepository {
        private final Optional<UserAccount> user;

        private StubRepo(Optional<UserAccount> user) {
            this.user = user;
        }

        @Override
        public boolean existsByEmail(String email) {
            return false;
        }

        @Override
        public Optional<UserAccount> findByEmail(String email) {
            return user;
        }

        @Override
        public void save(UserAccount userAccount) {
            throw new UnsupportedOperationException("Not needed");
        }

        @Override
        public boolean updatePasswordCredentialsByEmail(String email, String passwordHash, String passwordSalt) {
            return false;
        }

        @Override
        public long countByEmail(String email) {
            return 0;
        }
    }
}
