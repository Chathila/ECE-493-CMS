package cms.services;

public final class RegistrationMessages {
    public static final String MISSING_FIELDS = "Email and password are required.";
    public static final String INVALID_EMAIL = "Please enter a valid email address.";
    public static final String DUPLICATE_EMAIL = "This email is already registered. Please use a different email.";
    public static final String WEAK_PASSWORD = "Password must be at least 8 characters and include uppercase, lowercase, digit, and special character.";
    public static final String TRY_AGAIN_LATER = "Email validation service is unavailable. Please try again later.";
    public static final String SUCCESS = "Registration successful. You can now log in.";

    private RegistrationMessages() {
    }
}
