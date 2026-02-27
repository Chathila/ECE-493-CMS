package com.ece493.cms.unit;

import com.ece493.cms.service.DefaultPasswordPolicyService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultPasswordPolicyServiceTest {
    private final DefaultPasswordPolicyService service = new DefaultPasswordPolicyService();

    @Test
    void rejectsNullAndShortPasswords() {
        assertFalse(service.meetsPolicy(null));
        assertFalse(service.meetsPolicy("Aa1"));
    }

    @Test
    void rejectsPasswordWithoutUpperLowerOrDigit() {
        assertFalse(service.meetsPolicy("lowercase1"));
        assertFalse(service.meetsPolicy("UPPERCASE1"));
        assertFalse(service.meetsPolicy("NoDigitsX"));
    }

    @Test
    void acceptsPasswordWithAllRequiredCharacterTypes() {
        assertTrue(service.meetsPolicy("Strong123"));
    }

    @Test
    void acceptsPasswordWithSpecialCharactersWhenCorePolicySatisfied() {
        assertTrue(service.meetsPolicy("Strong1!A"));
    }
}
