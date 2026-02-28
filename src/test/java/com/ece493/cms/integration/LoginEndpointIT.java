package com.ece493.cms.integration;

import com.ece493.cms.controller.LoginServlet;
import com.ece493.cms.repository.JdbcUserAccountRepository;
import com.ece493.cms.security.PasswordHasher;
import com.ece493.cms.service.AuthenticationService;
import com.ece493.cms.service.AuthenticationServiceImpl;
import com.ece493.cms.service.DefaultAccountStatusService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void getLoginReturnsHtmlForm() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = getLoginPage();

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Log In to CMS"));
    }

    @Test
    void postLoginSuccessRedirectsToAuthorizedHome() throws Exception {
        seedUser("login-ok@cms.com", "Valid123", "ACTIVE", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postLogin("{\"email\":\"login-ok@cms.com\",\"password\":\"Valid123\"}");

        assertEquals(302, response.getStatus());
        assertTrue(response.getHeader("Location").startsWith("/home?role="));
        assertEquals(1, new JdbcUserAccountRepository(dataSource).countByEmail("login-ok@cms.com"));
    }

    @Test
    void postLoginMissingFieldsReturns400() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postLogin("{}");

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Missing required fields"));
    }

    @Test
    void postLoginIncorrectCredentialsReturns401() throws Exception {
        seedUser("login-bad@cms.com", "Valid123", "ACTIVE", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postLogin("{\"email\":\"login-bad@cms.com\",\"password\":\"Wrong123\"}");

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("Authentication failed"));
    }

    @Test
    void postLoginUnknownEmailPromptsRegistration() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postLogin("{\"email\":\"unknown@cms.com\",\"password\":\"Valid123\"}");

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("Please register"));
    }

    @Test
    void postLoginInactiveOrLockedReturns403() throws Exception {
        seedUser("login-locked@cms.com", "Valid123", "LOCKED", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postLogin("{\"email\":\"login-locked@cms.com\",\"password\":\"Valid123\"}");

        assertEquals(403, response.getStatus());
        assertTrue(response.getBody().contains("inactive or locked"));
    }

    @Test
    void postLoginServiceUnavailableReturns503() throws Exception {
        AuthenticationService unavailableService = new AuthenticationServiceImpl(
                new JdbcUserAccountRepository(dataSource),
                new PasswordHasher(),
                new DefaultAccountStatusService(),
                () -> false
        );
        LoginServlet unavailableLoginServlet = new LoginServlet(unavailableService, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        unavailableLoginServlet.service(
                ServletHttpTestSupport.postJsonRequest("{\"email\":\"login-ok@cms.com\",\"password\":\"Valid123\"}"),
                response.asResponse()
        );

        assertEquals(503, response.getStatus());
        assertTrue(response.getBody().contains("try again later"));
    }
}
