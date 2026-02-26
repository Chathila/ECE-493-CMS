package cms.services;

public final class AuthenticationMessages {
    public static final String MISSING_FIELDS = "Email and password are required.";
    public static final String INVALID_CREDENTIALS = "Invalid email or password.";
    public static final String ACCOUNT_INACTIVE_OR_LOCKED = "This account is inactive or locked.";
    public static final String TRY_AGAIN_LATER = "Authentication service is unavailable. Please try again later.";

    private AuthenticationMessages() {
    }
}
