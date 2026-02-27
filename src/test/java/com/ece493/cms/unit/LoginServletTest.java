package com.ece493.cms.unit;

import com.ece493.cms.controller.LoginServlet;
import com.ece493.cms.model.LoginResult;
import com.ece493.cms.service.AuthenticationService;
import com.ece493.cms.integration.ServletHttpTestSupport;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginServletTest {
    @Test
    void readFieldReturnsNullWhenBodyIsNull() throws Exception {
        AuthenticationService service = submission -> LoginResult.error(401, "nope");
        LoginServlet servlet = new LoginServlet(service, "<html></html>");

        Method readField = LoginServlet.class.getDeclaredMethod("readField", String.class, Pattern.class);
        readField.setAccessible(true);

        Object value = readField.invoke(servlet, null, Pattern.compile("email"));

        assertNull(value);
    }

    @Test
    void readFieldReturnsNullWhenBodyIsEmpty() throws Exception {
        AuthenticationService service = submission -> LoginResult.error(401, "nope");
        LoginServlet servlet = new LoginServlet(service, "<html></html>");

        Method readField = LoginServlet.class.getDeclaredMethod("readField", String.class, Pattern.class);
        readField.setAccessible(true);

        Object value = readField.invoke(servlet, "", Pattern.compile("email"));

        assertNull(value);
    }

    @Test
    void getReturnsLoginHtml() throws Exception {
        AuthenticationService service = submission -> LoginResult.error(401, "nope");
        LoginServlet servlet = new LoginServlet(service, "<html>Login</html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Login"));
    }

    @Test
    void postSuccessSetsRedirect() throws Exception {
        AuthenticationService service = submission -> LoginResult.successRedirect("/home?role=AUTHOR", "a@b.com");
        LoginServlet servlet = new LoginServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();

        servlet.service(ServletHttpTestSupport.postJsonRequest("{\"email\":\"a@b.com\",\"password\":\"x\"}", session), response.asResponse());

        assertEquals(302, response.getStatus());
        assertEquals("/home?role=AUTHOR", response.getHeader("Location"));
        assertEquals("a@b.com", session.getAttribute("user_email"));
    }

    @Test
    void postErrorReturnsJsonMessage() throws Exception {
        AuthenticationService service = submission -> LoginResult.error(401, "Bad \"credentials\"");
        LoginServlet servlet = new LoginServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.postJsonRequest("{}"), response.asResponse());

        assertEquals(401, response.getStatus());
        assertEquals("application/json; charset=UTF-8", response.getContentType());
        assertTrue(response.getBody().contains("Bad \\\"credentials\\\""));
    }

    @Test
    void postSuccessFallsBackToRequestEmailWhenAuthenticatedEmailMissing() throws Exception {
        AuthenticationService service = submission -> LoginResult.successRedirect("/home?role=AUTHOR");
        LoginServlet servlet = new LoginServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();

        servlet.service(ServletHttpTestSupport.postJsonRequest("{\"email\":\"fallback@cms.com\",\"password\":\"x\"}", session), response.asResponse());

        assertEquals(302, response.getStatus());
        assertEquals("fallback@cms.com", session.getAttribute("user_email"));
    }
}
