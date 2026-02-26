package cms.unit;

import cms.services.DefaultEmailValidationService;
import cms.services.EmailValidationUnavailableException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultEmailValidationServiceTest {

    @Test
    void validEmailPassesValidation() {
        DefaultEmailValidationService service = new DefaultEmailValidationService();
        assertTrue(service.isValidEmail("user@example.com"));
    }

    @Test
    void invalidAndNullEmailsFailValidation() {
        DefaultEmailValidationService service = new DefaultEmailValidationService();
        assertFalse(service.isValidEmail("invalid"));
        assertFalse(service.isValidEmail(null));
    }

    @Test
    void serviceUnavailableThrows() {
        DefaultEmailValidationService service = new DefaultEmailValidationService(() -> false);
        assertThrows(EmailValidationUnavailableException.class, () -> service.isValidEmail("user@example.com"));
    }
}
