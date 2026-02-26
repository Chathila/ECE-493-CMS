package cms.services;

public class AuthenticationServiceUnavailableException extends RuntimeException {
    public AuthenticationServiceUnavailableException(String message) {
        super(message);
    }
}
