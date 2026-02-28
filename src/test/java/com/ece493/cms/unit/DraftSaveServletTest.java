package com.ece493.cms.unit;

import com.ece493.cms.controller.DraftSaveServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.DraftSaveResult;
import com.ece493.cms.service.DraftSaveService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DraftSaveServletTest {
    @Test
    void postReturnsSuccessJson() throws Exception {
        DraftSaveService service = (authorEmail, request) -> DraftSaveResult.success("ok");
        DraftSaveServlet servlet = new DraftSaveServlet(service);
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "author@cms.com");

        servlet.service(ServletHttpTestSupport.postJsonRequest(validPayload(), session), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("ok"));
    }

    @Test
    void postReturnsErrorJson() throws Exception {
        DraftSaveService service = (authorEmail, request) -> DraftSaveResult.error(400, "invalid");
        DraftSaveServlet servlet = new DraftSaveServlet(service);
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.postJsonRequest("{}"), response.asResponse());

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("invalid"));
    }

    @Test
    void readTextFieldReturnsNullWhenBodyIsNull() throws Exception {
        DraftSaveServlet servlet = new DraftSaveServlet((authorEmail, request) -> DraftSaveResult.success("ok"));
        Method method = DraftSaveServlet.class.getDeclaredMethod("readTextField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, null, "title");

        assertNull(value);
    }

    @Test
    void readTextFieldReturnsNullWhenBodyIsEmpty() throws Exception {
        DraftSaveServlet servlet = new DraftSaveServlet((authorEmail, request) -> DraftSaveResult.success("ok"));
        Method method = DraftSaveServlet.class.getDeclaredMethod("readTextField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "", "title");

        assertNull(value);
    }

    @Test
    void readTextOrArrayFieldReadsArrayWhenPresent() throws Exception {
        DraftSaveServlet servlet = new DraftSaveServlet((authorEmail, request) -> DraftSaveResult.success("ok"));
        Method method = DraftSaveServlet.class.getDeclaredMethod("readTextOrArrayField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"authors\":[\"A\",\"B\"]}", "authors");

        assertEquals("A, B", value);
    }

    @Test
    void readTextOrArrayFieldReturnsTextWhenPresent() throws Exception {
        DraftSaveServlet servlet = new DraftSaveServlet((authorEmail, request) -> DraftSaveResult.success("ok"));
        Method method = DraftSaveServlet.class.getDeclaredMethod("readTextOrArrayField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"authors\":\"A, B\"}", "authors");

        assertEquals("A, B", value);
    }

    @Test
    void readTextOrArrayFieldReturnsNullForMissingField() throws Exception {
        DraftSaveServlet servlet = new DraftSaveServlet((authorEmail, request) -> DraftSaveResult.success("ok"));
        Method method = DraftSaveServlet.class.getDeclaredMethod("readTextOrArrayField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"keywords\":[\"k\"]}", "authors");

        assertNull(value);
    }

    @Test
    void readTextOrArrayFieldReturnsNullForNullBody() throws Exception {
        DraftSaveServlet servlet = new DraftSaveServlet((authorEmail, request) -> DraftSaveResult.success("ok"));
        Method method = DraftSaveServlet.class.getDeclaredMethod("readTextOrArrayField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, null, "authors");

        assertNull(value);
    }

    @Test
    void readTextOrArrayFieldReturnsNullForEmptyBody() throws Exception {
        DraftSaveServlet servlet = new DraftSaveServlet((authorEmail, request) -> DraftSaveResult.success("ok"));
        Method method = DraftSaveServlet.class.getDeclaredMethod("readTextOrArrayField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "", "authors");

        assertNull(value);
    }

    @Test
    void readTextOrArrayFieldReturnsNullForEmptyArray() throws Exception {
        DraftSaveServlet servlet = new DraftSaveServlet((authorEmail, request) -> DraftSaveResult.success("ok"));
        Method method = DraftSaveServlet.class.getDeclaredMethod("readTextOrArrayField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"authors\":[]}", "authors");

        assertNull(value);
    }

    private String validPayload() {
        return "{\"title\":\"T\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"author@cms.com\"}";
    }
}
