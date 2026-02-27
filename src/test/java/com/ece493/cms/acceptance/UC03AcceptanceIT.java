package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC03AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_successfulPasswordChange() throws Exception {
        seedUser("at01-cp@cms.com", "Old12345", "ACTIVE", "AUTHOR");
        ServletHttpTestSupport.SessionCapture session = loggedInSession("at01-cp@cms.com");

        ServletHttpTestSupport.ResponseCapture response = postChangePassword(
                "{\"current_password\":\"Old12345\",\"new_password\":\"New12345\",\"confirm_password\":\"New12345\"}",
                session
        );

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Password changed successfully"));
        assertTrue(session.isInvalidated());
    }

    @Test
    void AT02_incorrectCurrentPassword() throws Exception {
        seedUser("at02-cp@cms.com", "Old12345", "ACTIVE", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postChangePassword(
                "{\"current_password\":\"Wrong123\",\"new_password\":\"New12345\",\"confirm_password\":\"New12345\"}",
                loggedInSession("at02-cp@cms.com")
        );

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("Current password is invalid"));
    }

    @Test
    void AT03_newPasswordDoesNotMeetSecurityRequirements() throws Exception {
        seedUser("at03-cp@cms.com", "Old12345", "ACTIVE", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postChangePassword(
                "{\"current_password\":\"Old12345\",\"new_password\":\"weak\",\"confirm_password\":\"weak\"}",
                loggedInSession("at03-cp@cms.com")
        );

        assertEquals(403, response.getStatus());
        assertTrue(response.getBody().contains("CMS policy"));
    }

    @Test
    void AT04_newPasswordAndConfirmationDoNotMatch() throws Exception {
        seedUser("at04-cp@cms.com", "Old12345", "ACTIVE", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postChangePassword(
                "{\"current_password\":\"Old12345\",\"new_password\":\"New12345\",\"confirm_password\":\"Different123\"}",
                loggedInSession("at04-cp@cms.com")
        );

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("must match"));
    }
}
