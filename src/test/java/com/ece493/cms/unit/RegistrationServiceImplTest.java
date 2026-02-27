package com.ece493.cms.unit;

import com.ece493.cms.model.RegistrationResult;
import com.ece493.cms.model.UserAccount;
import com.ece493.cms.repository.UserAccountRepository;
import com.ece493.cms.security.PasswordHasher;
import com.ece493.cms.service.EmailValidationService;
import com.ece493.cms.service.PasswordPolicyService;
import com.ece493.cms.service.RegistrationServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceImplTest {
    @Test
    void returnsMissingFieldsWhenEmailOrPasswordMissing() {
        RegistrationServiceImpl service = new RegistrationServiceImpl(new InMemoryRepo(), availableEmailService(true), pwdPolicy(true), new PasswordHasher());

        RegistrationResult result = service.register("", "pass");

        assertEquals(400, result.getStatusCode());
        assertTrue(result.getMessage().contains("Missing required fields"));
    }

    @Test
    void returnsMissingFieldsWhenPasswordMissingWithValidEmail() {
        RegistrationServiceImpl service = new RegistrationServiceImpl(new InMemoryRepo(), availableEmailService(true), pwdPolicy(true), new PasswordHasher());

        RegistrationResult result = service.register("user@cms.com", "");

        assertEquals(400, result.getStatusCode());
        assertTrue(result.getMessage().contains("Missing required fields"));
    }

    @Test
    void returnsServiceUnavailableWhenEmailValidationServiceDown() {
        RegistrationServiceImpl service = new RegistrationServiceImpl(new InMemoryRepo(), unavailableEmailService(), pwdPolicy(true), new PasswordHasher());

        RegistrationResult result = service.register("a@b.com", "Valid123");

        assertEquals(503, result.getStatusCode());
    }

    @Test
    void returnsInvalidEmailWhenFormatFails() {
        RegistrationServiceImpl service = new RegistrationServiceImpl(new InMemoryRepo(), availableEmailService(false), pwdPolicy(true), new PasswordHasher());

        RegistrationResult result = service.register("bad-email", "Valid123");

        assertEquals(400, result.getStatusCode());
        assertTrue(result.getMessage().contains("valid email"));
    }

    @Test
    void returnsDuplicateErrorWhenEmailAlreadyExists() {
        InMemoryRepo repo = new InMemoryRepo();
        repo.existingEmail = "already@cms.com";
        RegistrationServiceImpl service = new RegistrationServiceImpl(repo, availableEmailService(true), pwdPolicy(true), new PasswordHasher());

        RegistrationResult result = service.register("already@cms.com", "Valid123");

        assertEquals(409, result.getStatusCode());
    }

    @Test
    void returnsWeakPasswordErrorWhenPolicyFails() {
        RegistrationServiceImpl service = new RegistrationServiceImpl(new InMemoryRepo(), availableEmailService(true), pwdPolicy(false), new PasswordHasher());

        RegistrationResult result = service.register("a@b.com", "weak");

        assertEquals(400, result.getStatusCode());
        assertTrue(result.getMessage().contains("CMS policy"));
    }

    @Test
    void savesActiveUserWithHashedPasswordAndRedirectsOnSuccess() {
        InMemoryRepo repo = new InMemoryRepo();
        RegistrationServiceImpl service = new RegistrationServiceImpl(repo, availableEmailService(true), pwdPolicy(true), new PasswordHasher());

        RegistrationResult result = service.register("new@cms.com", "Valid123");

        assertEquals(302, result.getStatusCode());
        assertEquals("/login", result.getRedirectLocation());
        assertNotNull(repo.saved);
        assertEquals("new@cms.com", repo.saved.getEmail());
        assertEquals("ACTIVE", repo.saved.getStatus());
        assertNotEquals("Valid123", repo.saved.getPasswordHash());
        assertNotNull(repo.saved.getPasswordSalt());
    }

    private EmailValidationService availableEmailService(boolean validFormat) {
        return new EmailValidationService() {
            @Override
            public boolean isServiceAvailable() {
                return true;
            }

            @Override
            public boolean isValidFormat(String email) {
                return validFormat;
            }
        };
    }

    private EmailValidationService unavailableEmailService() {
        return new EmailValidationService() {
            @Override
            public boolean isServiceAvailable() {
                return false;
            }

            @Override
            public boolean isValidFormat(String email) {
                return true;
            }
        };
    }

    private PasswordPolicyService pwdPolicy(boolean meets) {
        return password -> meets;
    }

    private static class InMemoryRepo implements UserAccountRepository {
        private String existingEmail;
        private UserAccount saved;

        @Override
        public boolean existsByEmail(String email) {
            return email != null && email.equals(existingEmail);
        }

        @Override
        public Optional<UserAccount> findByEmail(String email) {
            return Optional.empty();
        }

        @Override
        public void save(UserAccount userAccount) {
            this.saved = userAccount;
        }

        @Override
        public long countByEmail(String email) {
            return existsByEmail(email) ? 1 : 0;
        }
    }
}
