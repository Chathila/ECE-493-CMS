package com.ece493.cms.integration;

import com.ece493.cms.repository.JdbcUserAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void getRegisterReturnsHtmlForm() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = getRegisterPage();

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Create CMS Account"));
    }

    @Test
    void postRegisterSuccessCreatesAccountAndRedirects() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRegister("{\"email\":\"integration@cms.com\",\"password\":\"Valid123\"}");

        assertEquals(302, response.getStatus());
        assertEquals("/login", response.getHeader("Location"));
        assertEquals(1, new JdbcUserAccountRepository(dataSource).countByEmail("integration@cms.com"));
    }

    @Test
    void postRegisterMissingFieldsReturns400AndNoAccount() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRegister("{}");

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Missing required fields"));
        assertEquals(0, new JdbcUserAccountRepository(dataSource).countByEmail("missing@cms.com"));
    }

    @Test
    void postRegisterEmptyBodyReturns400() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRegister("");

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Missing required fields"));
    }

    @Test
    void postRegisterInvalidEmailReturns400() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRegister("{\"email\":\"bad-email\",\"password\":\"Valid123\"}");

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("valid email"));
    }

    @Test
    void postRegisterWeakPasswordReturns400() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRegister("{\"email\":\"weak@cms.com\",\"password\":\"weak\"}");

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("CMS policy"));
        assertEquals(0, new JdbcUserAccountRepository(dataSource).countByEmail("weak@cms.com"));
    }

    @Test
    void postRegisterDuplicateEmailReturns409() throws Exception {
        postRegister("{\"email\":\"dup@cms.com\",\"password\":\"Valid123\"}");

        ServletHttpTestSupport.ResponseCapture duplicateResponse = postRegister("{\"email\":\"dup@cms.com\",\"password\":\"Valid123\"}");

        assertEquals(409, duplicateResponse.getStatus());
        assertTrue(duplicateResponse.getBody().contains("already registered"));
        assertEquals(1, new JdbcUserAccountRepository(dataSource).countByEmail("dup@cms.com"));
    }
}
