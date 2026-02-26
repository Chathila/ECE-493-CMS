package cms.services;

import cms.models.AccountStatus;
import cms.models.LoginRequest;
import cms.models.LoginResult;
import cms.models.UserAccount;
import cms.persistence.UserRepository;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;

public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final BooleanSupplier serviceAvailability;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
        this(userRepository, passwordHasher, () -> true);
    }

    public AuthenticationServiceImpl(UserRepository userRepository,
                                     PasswordHasher passwordHasher,
                                     BooleanSupplier serviceAvailability) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.serviceAvailability = Objects.requireNonNull(serviceAvailability);
    }

    @Override
    public LoginResult login(LoginRequest request) {
        if (!serviceAvailability.getAsBoolean()) {
            throw new AuthenticationServiceUnavailableException("Authentication service unavailable");
        }

        String normalizedEmail = normalizeEmail(request.email());
        String password = request.password();

        if (normalizedEmail.isEmpty() || password == null || password.isBlank()) {
            return LoginResult.failure(List.of(AuthenticationMessages.MISSING_FIELDS));
        }

        Optional<UserAccount> maybeAccount = userRepository.findByEmail(normalizedEmail);
        if (maybeAccount.isEmpty()) {
            return LoginResult.failure(List.of(AuthenticationMessages.INVALID_CREDENTIALS));
        }

        UserAccount account = maybeAccount.get();
        if (account.getStatus() != AccountStatus.ACTIVE) {
            return LoginResult.failure(List.of(AuthenticationMessages.ACCOUNT_INACTIVE_OR_LOCKED));
        }

        boolean matches;
        try {
            matches = passwordHasher.verify(password, account.getPasswordHash(), account.getPasswordSalt());
        } catch (UnsupportedOperationException ex) {
            matches = false;
        }
        if (!matches) {
            return LoginResult.failure(List.of(AuthenticationMessages.INVALID_CREDENTIALS));
        }

        return LoginResult.success("/dashboard?role=" + account.getRole().name().toLowerCase(Locale.ROOT));
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
