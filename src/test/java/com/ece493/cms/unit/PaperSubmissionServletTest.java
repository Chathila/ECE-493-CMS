package com.ece493.cms.unit;

import com.ece493.cms.controller.PaperSubmissionServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.PaperSubmissionRequest;
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
        CapturingSubmissionService service = new CapturingSubmissionService(PaperSubmissionResult.success("ok", "/home", 11L));
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "author@cms.com");

        servlet.service(ServletHttpTestSupport.postJsonRequest(validPayloadWithDraft(), session), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("redirect"));
        assertTrue(response.getBody().contains("\"submission_id\":11"));
        assertNotNull(service.capturedRequest);
        assertEquals(10L, service.capturedRequest.getDraftId());
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

    @Test
    void readLongFieldReturnsNullForMissingField() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readLongField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"title\":\"T\"}", "draft_id");

        assertNull(value);
    }

    @Test
    void readLongFieldReturnsNullForNullBody() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readLongField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, null, "draft_id");

        assertNull(value);
    }

    @Test
    void readLongFieldReturnsNullForEmptyBody() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readLongField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "", "draft_id");

        assertNull(value);
    }

    @Test
    void readLongFieldReadsNumber() throws Exception {
        PaperSubmissionService service = (authorEmail, request) -> PaperSubmissionResult.error(400, "bad");
        PaperSubmissionServlet servlet = new PaperSubmissionServlet(service, "<html></html>");
        Method method = PaperSubmissionServlet.class.getDeclaredMethod("readLongField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"draft_id\":42}", "draft_id");

        assertEquals(42L, value);
    }

    private String validPayload() {
        return "{\"title\":\"T\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"ZGF0YQ==\"}}";
    }

    private String validPayloadWithDraft() {
        return "{\"draft_id\":10,\"title\":\"T\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"ZGF0YQ==\"}}";
    }

    private static class CapturingSubmissionService implements PaperSubmissionService {
        private final PaperSubmissionResult response;
        private PaperSubmissionRequest capturedRequest;

        private CapturingSubmissionService(PaperSubmissionResult response) {
            this.response = response;
        }

        @Override
        public PaperSubmissionResult submit(String authorEmail, PaperSubmissionRequest request) {
            this.capturedRequest = request;
            return response;
        }
    }
}
