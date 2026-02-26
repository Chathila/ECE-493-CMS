package cms.unit;

import cms.models.AccountStatus;
import cms.models.LoginRequest;
import cms.models.LoginResult;
import cms.models.UserAccount;
import cms.models.UserRole;
import cms.persistence.InMemoryUserRepository;
import cms.services.AuthenticationMessages;
import cms.services.AuthenticationServiceImpl;
import cms.services.AuthenticationServiceUnavailableException;
import cms.services.HashResult;
import cms.services.PasswordHasher;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticationServiceImplTest {

    @Test
    void successfulLoginReturnsAuthorizedRedirect() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        repo.save(new UserAccount(
            "user@example.com",
            "hash",
            "salt",
            AccountStatus.ACTIVE,
            UserRole.EDITOR,
            Instant.parse("2026-02-05T00:00:00Z")
        ));
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(repo, new StubHasher(true));

        LoginResult result = service.login(new LoginRequest(" USER@example.com ", "Strong1!"));

        assertTrue(result.isSuccess());
        assertEquals("/dashboard?role=editor", result.getRedirectPath());
    }

    @Test
    void missingFieldsReturnValidationError() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(new InMemoryUserRepository(), new StubHasher(false));

        LoginResult result = service.login(new LoginRequest("", " "));

        assertEquals(List.of(AuthenticationMessages.MISSING_FIELDS), result.getErrors());
    }

    @Test
    void nullAndBlankPasswordAreBothRejectedWhenEmailProvided() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(new InMemoryUserRepository(), new StubHasher(false));

        LoginResult nullPassword = service.login(new LoginRequest("user@example.com", null));
        LoginResult blankPassword = service.login(new LoginRequest("user@example.com", "   "));

        assertEquals(List.of(AuthenticationMessages.MISSING_FIELDS), nullPassword.getErrors());
        assertEquals(List.of(AuthenticationMessages.MISSING_FIELDS), blankPassword.getErrors());
    }

    @Test
    void unknownUserReturnsInvalidCredentials() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(new InMemoryUserRepository(), new StubHasher(true));

        LoginResult result = service.login(new LoginRequest("missing@example.com", "Strong1!"));

        assertEquals(List.of(AuthenticationMessages.INVALID_CREDENTIALS), result.getErrors());
    }

    @Test
    void inactiveAndLockedAccountsAreBlocked() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        repo.save(new UserAccount("inactive@example.com", "h", "s", AccountStatus.INACTIVE, UserRole.AUTHOR, Instant.now()));
        repo.save(new UserAccount("locked@example.com", "h", "s", AccountStatus.LOCKED, UserRole.AUTHOR, Instant.now()));

        AuthenticationServiceImpl service = new AuthenticationServiceImpl(repo, new StubHasher(true));

        LoginResult inactive = service.login(new LoginRequest("inactive@example.com", "x"));
        LoginResult locked = service.login(new LoginRequest("locked@example.com", "x"));

        assertEquals(List.of(AuthenticationMessages.ACCOUNT_INACTIVE_OR_LOCKED), inactive.getErrors());
        assertEquals(List.of(AuthenticationMessages.ACCOUNT_INACTIVE_OR_LOCKED), locked.getErrors());
    }

    @Test
    void wrongPasswordReturnsInvalidCredentials() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        repo.save(new UserAccount("user@example.com", "h", "s", AccountStatus.ACTIVE, UserRole.AUTHOR, Instant.now()));
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(repo, new StubHasher(false));

        LoginResult result = service.login(new LoginRequest("user@example.com", "bad"));

        assertEquals(List.of(AuthenticationMessages.INVALID_CREDENTIALS), result.getErrors());
    }

    @Test
    void unavailableAuthenticationServiceThrows() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(
            new InMemoryUserRepository(),
            new StubHasher(true),
            () -> false
        );

        assertThrows(AuthenticationServiceUnavailableException.class,
            () -> service.login(new LoginRequest("user@example.com", "Strong1!")));
    }

    @Test
    void unsupportedPasswordVerificationFallsBackToInvalidCredentials() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        repo.save(new UserAccount("user@example.com", "h", "s", AccountStatus.ACTIVE, UserRole.AUTHOR, Instant.now()));

        AuthenticationServiceImpl service = new AuthenticationServiceImpl(repo, new PasswordHasher() {
            @Override
            public HashResult hash(String plainTextPassword) {
                return new HashResult("h", "s");
            }
        });

        LoginResult result = service.login(new LoginRequest("user@example.com", "Strong1!"));
        assertEquals(List.of(AuthenticationMessages.INVALID_CREDENTIALS), result.getErrors());
    }

    private static final class StubHasher implements PasswordHasher {
        private final boolean verifyResult;

        private StubHasher(boolean verifyResult) {
            this.verifyResult = verifyResult;
        }

        @Override
        public HashResult hash(String plainTextPassword) {
            return new HashResult("hash", "salt");
        }

        @Override
        public boolean verify(String plainTextPassword, String storedHash, String storedSalt) {
            return verifyResult;
        }
    }
}
