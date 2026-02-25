package cms.services;

public interface EmailValidationService {
    boolean isValidEmail(String email) throws EmailValidationUnavailableException;
}
