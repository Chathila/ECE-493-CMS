package com.ece493.cms.unit;

import com.ece493.cms.controller.ChangePasswordServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.PasswordChangeResult;
import com.ece493.cms.service.PasswordChangeService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordServletTest {
    @Test
    void readFieldReturnsNullWhenBodyEmpty() throws Exception {
        PasswordChangeService service = (email, request) -> PasswordChangeResult.error(400, "bad");
        ChangePasswordServlet servlet = new ChangePasswordServlet(service, "<html></html>");

        Method readField = ChangePasswordServlet.class.getDeclaredMethod("readField", String.class, Pattern.class);
        readField.setAccessible(true);

        Object value = readField.invoke(servlet, "", Pattern.compile("current_password"));

        assertNull(value);
    }

    @Test
    void readFieldReturnsNullWhenBodyNull() throws Exception {
        PasswordChangeService service = (email, request) -> PasswordChangeResult.error(400, "bad");
        ChangePasswordServlet servlet = new ChangePasswordServlet(service, "<html></html>");

        Method readField = ChangePasswordServlet.class.getDeclaredMethod("readField", String.class, Pattern.class);
        readField.setAccessible(true);

        Object value = readField.invoke(servlet, null, Pattern.compile("current_password"));

        assertNull(value);
    }

    @Test
    void readFieldReturnsNullWhenPatternMissing() throws Exception {
        PasswordChangeService service = (email, request) -> PasswordChangeResult.error(400, "bad");
        ChangePasswordServlet servlet = new ChangePasswordServlet(service, "<html></html>");

        Method readField = ChangePasswordServlet.class.getDeclaredMethod("readField", String.class, Pattern.class);
        readField.setAccessible(true);

        Object value = readField.invoke(servlet, "{\"foo\":\"bar\"}", Pattern.compile("current_password"));

        assertNull(value);
    }

    @Test
    void getReturnsHtmlForm() throws Exception {
        PasswordChangeService service = (email, request) -> PasswordChangeResult.error(400, "bad");
        ChangePasswordServlet servlet = new ChangePasswordServlet(service, "<html>Change Password</html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Change Password"));
    }

    @Test
    void postSuccessInvalidatesSession() throws Exception {
        PasswordChangeService service = (email, request) -> PasswordChangeResult.success("ok");
        ChangePasswordServlet servlet = new ChangePasswordServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "user@cms.com");

        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{\"current_password\":\"Old12345\",\"new_password\":\"New12345\",\"confirm_password\":\"New12345\"}", session),
                response.asResponse()
        );

        assertEquals(200, response.getStatus());
        assertTrue(session.isInvalidated());
        assertTrue(response.getBody().contains("ok"));
    }

    @Test
    void postErrorReturnsJsonWithoutInvalidatingSession() throws Exception {
        PasswordChangeService service = (email, request) -> PasswordChangeResult.error(401, "Current password is invalid.");
        ChangePasswordServlet servlet = new ChangePasswordServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "user@cms.com");

        servlet.service(ServletHttpTestSupport.postJsonRequest("{}", session), response.asResponse());

        assertEquals(401, response.getStatus());
        assertFalse(session.isInvalidated());
        assertEquals("application/json; charset=UTF-8", response.getContentType());
    }

    @Test
    void postSuccessWithoutSessionStillReturns200() throws Exception {
        PasswordChangeService service = (email, request) -> PasswordChangeResult.success("ok");
        ChangePasswordServlet servlet = new ChangePasswordServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{\"current_password\":\"Old12345\",\"new_password\":\"New12345\",\"confirm_password\":\"New12345\"}"),
                response.asResponse()
        );

        assertEquals(200, response.getStatus());
    }
}
