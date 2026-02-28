package com.ece493.cms.unit;

import com.ece493.cms.controller.PaperSubmissionServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.PaperSubmissionResult;
import com.ece493.cms.service.PaperSubmissionService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class PaperSubmissionServletTest {
    @Test
    void getReturnsHtml() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html>Submit</html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Submit"));
    }

    @Test
    void postReturnsJsonWithRedirectWhenSuccessful() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.success("ok", "/home");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "author@cms.com");

        servlet.service(ServletHttpTestSupport.postJsonRequest(validPayload(), session), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("redirect"));
    }

    @Test
    void postReturnsJsonError() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(415, "Unsupported file format");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.postJsonRequest(validPayload()), response.asResponse());

        assertEquals(415, response.getStatus());
        assertTrue(response.getBody().contains("Unsupported"));
    }

    @Test
    void readTextOrArrayFieldReadsArrayWhenTextMissing() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");

        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readTextOrArrayField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"authors\":[\"A\",\"B\"]}", "authors");

        assertEquals("A, B", value);
    }

    @Test
    void readTextFieldReturnsNullForNullBody() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readTextField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, null, "title");

        assertNull(value);
    }

    @Test
    void readTextFieldReturnsNullWhenMissing() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readTextField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"other\":\"x\"}", "title");

        assertNull(value);
    }

    @Test
    void readTextFieldReturnsNullForEmptyBody() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readTextField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "", "title");

        assertNull(value);
    }

    @Test
    void readTextOrArrayReturnsTextWhenPresent() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readTextOrArrayField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"authors\":\"A, B\"}", "authors");

        assertEquals("A, B", value);
    }

    @Test
    void readTextOrArrayReturnsNullForMissingArrayField() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readTextOrArrayField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"keywords\":[\"k1\"]}", "authors");

        assertNull(value);
    }

    @Test
    void readTextOrArrayReturnsNullForNullBody() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readTextOrArrayField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, null, "authors");

        assertNull(value);
    }

    @Test
    void readTextOrArrayReturnsNullForEmptyBody() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readTextOrArrayField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "", "authors");

        assertNull(value);
    }

    @Test
    void readTextOrArrayReturnsNullForEmptyArray() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readTextOrArrayField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"authors\":[]}", "authors");

        assertNull(value);
    }

    private String validPayload() {
        return "{\"title\":\"T\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"ZGF0YQ==\"}}";
    }
}
