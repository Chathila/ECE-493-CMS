package cms.unit;

import cms.models.RegistrationRequest;
import cms.models.RegistrationResult;
import cms.models.UserAccount;
import cms.persistence.InMemoryUserRepository;
import cms.services.CmsPasswordPolicyService;
import cms.services.DefaultEmailValidationService;
import cms.services.EmailValidationService;
import cms.services.EmailValidationUnavailableException;
import cms.services.HashResult;
import cms.services.PasswordHasher;
import cms.services.RegistrationMessages;
import cms.services.RegistrationService;
import cms.services.RegistrationServiceImpl;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationServiceImplTest {

    @Test
    void successfulRegistrationCreatesActiveAccountAndHashesPassword() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationService service = new RegistrationServiceImpl(
            repo,
            new DefaultEmailValidationService(),
            new CmsPasswordPolicyService(),
            new StubHasher(),
            Clock.fixed(Instant.parse("2026-02-05T00:00:00Z"), ZoneOffset.UTC)
        );

        RegistrationResult result = service.register(new RegistrationRequest("  USER@Example.com ", "Strong1!"));

        assertTrue(result.isSuccess());
        assertEquals("/login?message=Registration+successful.+You+can+now+log+in.", result.getRedirectPath());
        assertEquals(1, repo.count());

        UserAccount account = repo.findByEmail("user@example.com").orElseThrow();
        assertTrue(account.isActive());
        assertEquals("user@example.com", account.getEmail());
        assertEquals("hashed-value", account.getPasswordHash());
        assertEquals("salt-value", account.getPasswordSalt());
        assertEquals(Instant.parse("2026-02-05T00:00:00Z"), account.getCreatedAt());
        assertNotEquals("Strong1!", account.getPasswordHash());
    }

    @Test
    void missingFieldsReturnErrorAndNoAccountCreated() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationService service = baselineService(repo);

        RegistrationResult result = service.register(new RegistrationRequest("", "  "));

        assertFalse(result.isSuccess());
        assertEquals(List.of(RegistrationMessages.MISSING_FIELDS), result.getErrors());
        assertEquals(0, repo.count());
    }

    @Test
    void validationServiceUnavailableReturnsTryAgainLater() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        EmailValidationService unavailableService = email -> {
            throw new EmailValidationUnavailableException("down");
        };
        RegistrationService service = new RegistrationServiceImpl(
            repo,
            unavailableService,
            new CmsPasswordPolicyService(),
            new StubHasher(),
            Clock.systemUTC()
        );

        RegistrationResult result = service.register(new RegistrationRequest("user@example.com", "Strong1!"));

        assertFalse(result.isSuccess());
        assertEquals(List.of(RegistrationMessages.TRY_AGAIN_LATER), result.getErrors());
        assertEquals(0, repo.count());
    }

    @Test
    void duplicateAndWeakPasswordReturnExpectedErrors() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationService service = baselineService(repo);
        service.register(new RegistrationRequest("duplicate@example.com", "Strong1!"));

        RegistrationResult result = service.register(new RegistrationRequest("duplicate@example.com", "weak"));

        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().contains(RegistrationMessages.DUPLICATE_EMAIL));
        assertTrue(result.getErrors().contains(RegistrationMessages.WEAK_PASSWORD));
        assertEquals(1, repo.count());
    }

    @Test
    void invalidEmailWithStrongPasswordReturnsInvalidEmailOnly() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationService service = baselineService(repo);

        RegistrationResult result = service.register(new RegistrationRequest("bad-email", "Strong1!"));

        assertFalse(result.isSuccess());
        assertEquals(List.of(RegistrationMessages.INVALID_EMAIL), result.getErrors());
    }

    @Test
    void nullEmailAndNullPasswordReturnMissingFields() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationService service = baselineService(repo);

        RegistrationResult result = service.register(new RegistrationRequest(null, null));

        assertFalse(result.isSuccess());
        assertEquals(List.of(RegistrationMessages.MISSING_FIELDS), result.getErrors());
    }

    @Test
    void blankAndNullPasswordAreRejectedWhenEmailPresent() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        RegistrationService service = baselineService(repo);

        RegistrationResult nullPassword = service.register(new RegistrationRequest("user@example.com", null));
        RegistrationResult blankPassword = service.register(new RegistrationRequest("user@example.com", "  "));

        assertFalse(nullPassword.isSuccess());
        assertFalse(blankPassword.isSuccess());
        assertEquals(List.of(RegistrationMessages.MISSING_FIELDS), nullPassword.getErrors());
        assertEquals(List.of(RegistrationMessages.MISSING_FIELDS), blankPassword.getErrors());
    }

    private RegistrationService baselineService(InMemoryUserRepository repo) {
        return new RegistrationServiceImpl(
            repo,
            new DefaultEmailValidationService(),
            new CmsPasswordPolicyService(),
            new StubHasher(),
            Clock.systemUTC()
        );
    }

    static class StubHasher implements PasswordHasher {
        @Override
        public HashResult hash(String plainTextPassword) {
            return new HashResult("hashed-value", "salt-value");
        }
    }
}
