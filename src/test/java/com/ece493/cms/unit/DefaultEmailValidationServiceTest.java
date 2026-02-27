package com.ece493.cms.unit;

import com.ece493.cms.service.DefaultEmailValidationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultEmailValidationServiceTest {
    private final DefaultEmailValidationService service = new DefaultEmailValidationService();

    @Test
    void serviceIsAvailableAndValidatesFormat() {
        assertTrue(service.isServiceAvailable());
        assertTrue(service.isValidFormat("user@example.com"));
        assertFalse(service.isValidFormat("not-an-email"));
        assertFalse(service.isValidFormat(null));
    }
}
