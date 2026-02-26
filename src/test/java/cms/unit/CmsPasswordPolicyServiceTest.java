package cms.unit;

import cms.services.CmsPasswordPolicyService;
import cms.services.RegistrationMessages;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CmsPasswordPolicyServiceTest {

    private final CmsPasswordPolicyService service = new CmsPasswordPolicyService();

    @Test
    void invalidPasswordsFailAllPolicyBranches() {
        assertEquals(RegistrationMessages.WEAK_PASSWORD, service.validationError(null).orElseThrow());
        assertEquals(RegistrationMessages.WEAK_PASSWORD, service.validationError("short").orElseThrow());
        assertEquals(RegistrationMessages.WEAK_PASSWORD, service.validationError("lowercase1!").orElseThrow());
        assertEquals(RegistrationMessages.WEAK_PASSWORD, service.validationError("UPPERCASE1!").orElseThrow());
        assertEquals(RegistrationMessages.WEAK_PASSWORD, service.validationError("NoDigits!").orElseThrow());
        assertEquals(RegistrationMessages.WEAK_PASSWORD, service.validationError("NoSpecial1").orElseThrow());
    }

    @Test
    void compliantPasswordPassesPolicy() {
        assertTrue(service.validationError("Strong1!").isEmpty());
    }
}
