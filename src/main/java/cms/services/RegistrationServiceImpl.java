package cms.services;

import cms.models.RegistrationRequest;
import cms.models.RegistrationResult;
import cms.models.UserAccount;
import cms.persistence.UserRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository userRepository;
    private final EmailValidationService emailValidationService;
    private final PasswordPolicyService passwordPolicyService;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    public RegistrationServiceImpl(UserRepository userRepository,
                                   EmailValidationService emailValidationService,
                                   PasswordPolicyService passwordPolicyService,
                                   PasswordHasher passwordHasher,
                                   Clock clock) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.emailValidationService = Objects.requireNonNull(emailValidationService);
        this.passwordPolicyService = Objects.requireNonNull(passwordPolicyService);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public RegistrationResult register(RegistrationRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        String password = request.password();

        List<String> errors = new ArrayList<>();
        if (normalizedEmail.isEmpty() || password == null || password.isBlank()) {
            errors.add(RegistrationMessages.MISSING_FIELDS);
            return RegistrationResult.failure(errors);
        }

        boolean validEmail;
        try {
            validEmail = emailValidationService.isValidEmail(normalizedEmail);
        } catch (EmailValidationUnavailableException ex) {
            errors.add(RegistrationMessages.TRY_AGAIN_LATER);
            return RegistrationResult.failure(errors);
        }

        if (!validEmail) {
            errors.add(RegistrationMessages.INVALID_EMAIL);
        }

        if (userRepository.existsByEmail(normalizedEmail)) {
            errors.add(RegistrationMessages.DUPLICATE_EMAIL);
        }

        passwordPolicyService.validationError(password).ifPresent(errors::add);

        if (!errors.isEmpty()) {
            return RegistrationResult.failure(errors);
        }

        HashResult hashResult = passwordHasher.hash(password);
        UserAccount account = new UserAccount(
            normalizedEmail,
            hashResult.hash(),
            hashResult.salt(),
            true,
            clock.instant()
        );
        userRepository.save(account);

        String redirectPath = "/login?message=" + URLEncoder.encode(RegistrationMessages.SUCCESS, StandardCharsets.UTF_8);
        return RegistrationResult.success(RegistrationMessages.SUCCESS, redirectPath);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
