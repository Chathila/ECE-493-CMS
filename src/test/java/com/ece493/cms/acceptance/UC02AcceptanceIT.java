package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC02AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_successfulLoginWithValidCredentials() throws Exception {
        seedUser("at01-login@cms.com", "Valid123", "ACTIVE", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postLogin("{\"email\":\"at01-login@cms.com\",\"password\":\"Valid123\"}");

        assertEquals(302, response.getStatus());
        assertTrue(response.getHeader("Location").startsWith("/home?role="));
    }

    @Test
    void AT02_missingRequiredFields() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postLogin("{}");

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Missing required fields"));
    }

    @Test
    void AT03_incorrectEmailOrPassword() throws Exception {
        seedUser("at03-login@cms.com", "Valid123", "ACTIVE", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postLogin("{\"email\":\"at03-login@cms.com\",\"password\":\"Wrong123\"}");

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("Authentication failed"));
    }

    @Test
    void AT04_inactiveOrLockedAccount() throws Exception {
        seedUser("at04-login@cms.com", "Valid123", "INACTIVE", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postLogin("{\"email\":\"at04-login@cms.com\",\"password\":\"Valid123\"}");

        assertEquals(403, response.getStatus());
        assertTrue(response.getBody().contains("inactive or locked"));
    }
}
