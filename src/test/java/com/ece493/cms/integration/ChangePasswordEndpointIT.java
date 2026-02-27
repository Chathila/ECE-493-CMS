package com.ece493.cms.integration;

import com.ece493.cms.controller.ChangePasswordServlet;
import com.ece493.cms.repository.JdbcUserAccountRepository;
import com.ece493.cms.security.PasswordHasher;
import com.ece493.cms.service.DefaultPasswordPolicyService;
import com.ece493.cms.service.PasswordChangeService;
import com.ece493.cms.service.PasswordChangeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void getChangePasswordReturnsHtmlForm() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = getChangePasswordPage();

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Change Password"));
    }

    @Test
    void postChangePasswordSuccessUpdatesPasswordAndRequiresRelogin() throws Exception {
        seedUser("cp-ok@cms.com", "Old12345", "ACTIVE", "AUTHOR");
        ServletHttpTestSupport.SessionCapture session = loggedInSession("cp-ok@cms.com");

        ServletHttpTestSupport.ResponseCapture response = postChangePassword(
                "{\"current_password\":\"Old12345\",\"new_password\":\"New12345\",\"confirm_password\":\"New12345\"}",
                session
        );

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Password changed successfully"));
        assertTrue(session.isInvalidated());

        ServletHttpTestSupport.ResponseCapture oldLogin = postLogin("{\"email\":\"cp-ok@cms.com\",\"password\":\"Old12345\"}");
        ServletHttpTestSupport.ResponseCapture newLogin = postLogin("{\"email\":\"cp-ok@cms.com\",\"password\":\"New12345\"}");

        assertEquals(401, oldLogin.getStatus());
        assertEquals(302, newLogin.getStatus());
        assertEquals(1, new JdbcUserAccountRepository(dataSource).countByEmail("cp-ok@cms.com"));
    }

    @Test
    void postChangePasswordInvalidCurrentPasswordReturns401() throws Exception {
        seedUser("cp-bad-current@cms.com", "Old12345", "ACTIVE", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postChangePassword(
                "{\"current_password\":\"Wrong123\",\"new_password\":\"New12345\",\"confirm_password\":\"New12345\"}",
                loggedInSession("cp-bad-current@cms.com")
        );

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("Current password is invalid"));
    }

    @Test
    void postChangePasswordWeakPasswordReturns403() throws Exception {
        seedUser("cp-weak@cms.com", "Old12345", "ACTIVE", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postChangePassword(
                "{\"current_password\":\"Old12345\",\"new_password\":\"weak\",\"confirm_password\":\"weak\"}",
                loggedInSession("cp-weak@cms.com")
        );

        assertEquals(403, response.getStatus());
        assertTrue(response.getBody().contains("CMS policy"));
    }

    @Test
    void postChangePasswordMismatchReturns400() throws Exception {
        seedUser("cp-mismatch@cms.com", "Old12345", "ACTIVE", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postChangePassword(
                "{\"current_password\":\"Old12345\",\"new_password\":\"New12345\",\"confirm_password\":\"Diff12345\"}",
                loggedInSession("cp-mismatch@cms.com")
        );

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("must match"));
    }

    @Test
    void postChangePasswordWithoutSessionReturns401() throws Exception {
        seedUser("cp-nosession@cms.com", "Old12345", "ACTIVE", "AUTHOR");

        ServletHttpTestSupport.ResponseCapture response = postChangePassword(
                "{\"current_password\":\"Old12345\",\"new_password\":\"New12345\",\"confirm_password\":\"New12345\"}",
                null
        );

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("must log in"));
    }

    @Test
    void postChangePasswordServiceUnavailableReturns503() throws Exception {
        seedUser("cp-down@cms.com", "Old12345", "ACTIVE", "AUTHOR");

        PasswordChangeService unavailableService = new PasswordChangeServiceImpl(
                new JdbcUserAccountRepository(dataSource),
                new DefaultPasswordPolicyService(),
                new PasswordHasher(),
                () -> false
        );
        ChangePasswordServlet unavailableServlet = new ChangePasswordServlet(unavailableService, "<html></html>");

        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        unavailableServlet.service(
                ServletHttpTestSupport.postJsonRequest(
                        "{\"current_password\":\"Old12345\",\"new_password\":\"New12345\",\"confirm_password\":\"New12345\"}",
                        loggedInSession("cp-down@cms.com")
                ),
                response.asResponse()
        );

        assertEquals(503, response.getStatus());
        assertTrue(response.getBody().contains("try again later"));
    }
}
