package cms.services;

public class EmailValidationUnavailableException extends RuntimeException {
    public EmailValidationUnavailableException(String message) {
        super(message);
    }
}
