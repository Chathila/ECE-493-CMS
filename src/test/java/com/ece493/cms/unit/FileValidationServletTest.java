package com.ece493.cms.unit;

import com.ece493.cms.controller.FileValidationServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.FileValidationResult;
import com.ece493.cms.service.FileValidationService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileValidationServletTest {
    @Test
    void postReturnsSuccessJson() throws Exception {
        FileValidationService service = (authorEmail, manuscriptFile) -> FileValidationResult.success("ok");
        FileValidationServlet servlet = new FileValidationServlet(service);
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "author@cms.com");

        servlet.service(ServletHttpTestSupport.postJsonRequest(validPayload(), session), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("ok"));
    }

    @Test
    void postReturnsUnauthorizedWhenSessionMissing() throws Exception {
        FileValidationService service = (authorEmail, manuscriptFile) ->
                authorEmail == null ? FileValidationResult.error(401, "login") : FileValidationResult.success("ok");
        FileValidationServlet servlet = new FileValidationServlet(service);
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.postJsonRequest(validPayload()), response.asResponse());

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("login"));
    }

    @Test
    void readTextFieldReturnsNullWhenBodyNull() throws Exception {
        FileValidationServlet servlet = new FileValidationServlet((authorEmail, manuscriptFile) -> FileValidationResult.success("ok"));
        Method method = FileValidationServlet.class.getDeclaredMethod("readTextField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, null, "filename");

        assertNull(value);
    }

    @Test
    void readTextFieldReturnsNullWhenBodyEmptyOrMissing() throws Exception {
        FileValidationServlet servlet = new FileValidationServlet((authorEmail, manuscriptFile) -> FileValidationResult.success("ok"));
        Method method = FileValidationServlet.class.getDeclaredMethod("readTextField", String.class, String.class);
        method.setAccessible(true);

        Object empty = method.invoke(servlet, "", "filename");
        Object missing = method.invoke(servlet, "{\"other\":\"x\"}", "filename");

        assertNull(empty);
        assertNull(missing);
    }

    @Test
    void readTextFieldReturnsValueWhenPresent() throws Exception {
        FileValidationServlet servlet = new FileValidationServlet((authorEmail, manuscriptFile) -> FileValidationResult.success("ok"));
        Method method = FileValidationServlet.class.getDeclaredMethod("readTextField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"filename\":\"paper.pdf\"}", "filename");

        assertEquals("paper.pdf", value);
    }

    private String validPayload() {
        return "{\"filename\":\"paper.pdf\",\"content_base64\":\"ZGF0YQ==\"}";
    }
}
