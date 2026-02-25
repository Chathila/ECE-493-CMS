package cms.services;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public class DefaultEmailValidationService implements EmailValidationService {
    // Require a domain with at least one dot (e.g., user@example.com)
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$";

    private final BooleanSupplier serviceAvailability;

    public DefaultEmailValidationService() {
        this(() -> true);
    }

    public DefaultEmailValidationService(BooleanSupplier serviceAvailability) {
        this.serviceAvailability = Objects.requireNonNull(serviceAvailability);
    }

    @Override
    public boolean isValidEmail(String email) {
        if (!serviceAvailability.getAsBoolean()) {
            throw new EmailValidationUnavailableException("Email validation service unavailable");
        }
        return email != null && email.matches(EMAIL_REGEX);
    }
}
