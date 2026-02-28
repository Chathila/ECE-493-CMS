package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.repository.JdbcUserAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UC01AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_successfulUserRegistration() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRegister("{\"name\":\"AT One\",\"email\":\"at01@cms.com\",\"password\":\"Valid123\"}");

        assertEquals(302, response.getStatus());
        assertEquals("/login", response.getHeader("Location"));
        assertEquals(1, new JdbcUserAccountRepository(dataSource).countByEmail("at01@cms.com"));
    }

    @Test
    void AT02_missingRequiredFields() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRegister("{}");

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Missing required fields"));
    }

    @Test
    void AT03_invalidEmailFormat() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRegister("{\"name\":\"AT Three\",\"email\":\"invalid-email\",\"password\":\"Valid123\"}");

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("valid email"));
    }

    @Test
    void AT04_duplicateEmailAddress() throws Exception {
        postRegister("{\"name\":\"Dup One\",\"email\":\"dupat@cms.com\",\"password\":\"Valid123\"}");

        ServletHttpTestSupport.ResponseCapture response = postRegister("{\"name\":\"Dup Two\",\"email\":\"dupat@cms.com\",\"password\":\"Valid123\"}");

        assertEquals(409, response.getStatus());
        assertTrue(response.getBody().contains("already registered"));
    }

    @Test
    void AT05_weakPassword() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRegister("{\"name\":\"Weak AT\",\"email\":\"weakat@cms.com\",\"password\":\"short\"}");

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("CMS policy"));
    }
}
