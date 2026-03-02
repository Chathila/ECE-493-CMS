package com.ece493.cms.unit;

import com.ece493.cms.model.FinalDecisionNotificationResult;
import com.ece493.cms.model.FinalDecisionStatusResult;
import com.ece493.cms.model.FinalDecisionSubmissionResult;
import com.ece493.cms.controller.RefereeAssignmentServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.RefereeAssignmentResult;
import com.ece493.cms.service.FinalDecisionService;
import com.ece493.cms.service.RefereeAssignmentService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefereeAssignmentServletTest {
    @Test
    void getReturnsHtml() throws Exception {
        RefereeAssignmentService service = (editorEmail, paperId, refereeEmails) -> RefereeAssignmentResult.success("ok");
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet(service, "<html>Assign Referees</html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Assign Referees"));
    }

    @Test
    void postReturnsSuccessPayload() throws Exception {
        RefereeAssignmentService service = (editorEmail, paperId, refereeEmails) -> RefereeAssignmentResult.success("assigned");
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "editor@cms.com");

        servlet.service(
                ServletHttpTestSupport.postJsonRequest(validPayload(), session, "/papers/1/referees/assign"),
                response.asResponse()
        );

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("assigned"));
    }

    @Test
    void postReturnsWarningPayload() throws Exception {
        RefereeAssignmentService service = (editorEmail, paperId, refereeEmails) ->
                RefereeAssignmentResult.warning(503, "assigned", "warning");
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "editor@cms.com");

        servlet.service(
                ServletHttpTestSupport.postJsonRequest(validPayload(), session, "/papers/1/referees/assign"),
                response.asResponse()
        );

        assertEquals(503, response.getStatus());
        assertTrue(response.getBody().contains("\"warning\":\"warning\""));
    }

    @Test
    void postFallsBackToPaperIdInBodyWhenPathMissing() throws Exception {
        RefereeAssignmentService service = (editorEmail, paperId, refereeEmails) ->
                "25".equals(paperId) ? RefereeAssignmentResult.success("ok") : RefereeAssignmentResult.error(400, "bad paper id");
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "editor@cms.com");

        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{\"paper_id\":\"25\",\"referee_emails\":[\"ref@cms.com\"]}", session),
                response.asResponse()
        );

        assertEquals(200, response.getStatus());
    }

    @Test
    void postHandlesMissingSession() throws Exception {
        RefereeAssignmentService service = (editorEmail, paperId, refereeEmails) ->
                editorEmail == null ? RefereeAssignmentResult.error(401, "login required") : RefereeAssignmentResult.success("ok");
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet(service, "<html></html>");
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(
                ServletHttpTestSupport.postJsonRequest(validPayload(), null, "/papers/1/referees/assign"),
                response.asResponse()
        );

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("login required"));
    }

    @Test
    void parsePaperIdExtractsFromPath() throws Exception {
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet((editorEmail, paperId, refereeEmails) -> RefereeAssignmentResult.success("ok"), "<html></html>");
        Method method = RefereeAssignmentServlet.class.getDeclaredMethod("parsePaperId", jakarta.servlet.http.HttpServletRequest.class);
        method.setAccessible(true);

        Object extracted = method.invoke(servlet, ServletHttpTestSupport.postJsonRequest("{}", null, "/papers/77/referees/assign"));
        Object missing = method.invoke(servlet, ServletHttpTestSupport.postJsonRequest("{}", null, "/papers/submit"));

        assertEquals("77", extracted);
        assertNull(missing);
    }

    @Test
    void parsePaperIdReturnsNullWhenRequestUriNull() throws Exception {
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet((editorEmail, paperId, refereeEmails) -> RefereeAssignmentResult.success("ok"), "<html></html>");
        Method method = RefereeAssignmentServlet.class.getDeclaredMethod("parsePaperId", jakarta.servlet.http.HttpServletRequest.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, ServletHttpTestSupport.postJsonRequest("{}", null, null));

        assertNull(value);
    }

    @Test
    void readArrayFieldHandlesArrayCsvAndMissing() throws Exception {
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet((editorEmail, paperId, refereeEmails) -> RefereeAssignmentResult.success("ok"), "<html></html>");
        Method method = RefereeAssignmentServlet.class.getDeclaredMethod("readArrayField", String.class, String.class);
        method.setAccessible(true);

        Object array = method.invoke(servlet, "{\"referee_emails\":[\"a@cms.com\",\"b@cms.com\"]}", "referee_emails");
        Object csv = method.invoke(servlet, "{\"referee_emails\":\"a@cms.com, b@cms.com\"}", "referee_emails");
        Object missing = method.invoke(servlet, "{\"title\":\"x\"}", "referee_emails");
        Object empty = method.invoke(servlet, "", "referee_emails");
        Object nullBody = method.invoke(servlet, null, "referee_emails");

        assertEquals(List.of("a@cms.com", "b@cms.com"), array);
        assertEquals(List.of("a@cms.com", "b@cms.com"), csv);
        assertEquals(List.of(), missing);
        assertEquals(List.of(), empty);
        assertEquals(List.of(), nullBody);
    }

    @Test
    void readTextFieldReturnsNullWhenMissing() throws Exception {
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet((editorEmail, paperId, refereeEmails) -> RefereeAssignmentResult.success("ok"), "<html></html>");
        Method method = RefereeAssignmentServlet.class.getDeclaredMethod("readTextField", String.class, String.class);
        method.setAccessible(true);

        Object value = method.invoke(servlet, "{\"other\":\"x\"}", "paper_id");

        assertNull(value);
    }

    @Test
    void readTextFieldReturnsNullForNullOrEmptyBody() throws Exception {
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet((editorEmail, paperId, refereeEmails) -> RefereeAssignmentResult.success("ok"), "<html></html>");
        Method method = RefereeAssignmentServlet.class.getDeclaredMethod("readTextField", String.class, String.class);
        method.setAccessible(true);

        Object nullBody = method.invoke(servlet, null, "paper_id");
        Object emptyBody = method.invoke(servlet, "", "paper_id");

        assertNull(nullBody);
        assertNull(emptyBody);
    }

    @Test
    void routesDecisionEndpointsWhenDecisionServiceProvided() throws Exception {
        FinalDecisionService decisionService = new FinalDecisionService(null, null, null, null, null) {
            @Override
            public FinalDecisionSubmissionResult submitDecision(String editorEmail, String paperId, String decision) {
                return FinalDecisionSubmissionResult.created(paperId, decision == null ? "accept" : decision);
            }

            @Override
            public FinalDecisionNotificationResult notifyDecision(String editorEmail, String paperId) {
                return FinalDecisionNotificationResult.sent(paperId);
            }

            @Override
            public FinalDecisionStatusResult getDecisionStatus(String viewerEmail, String paperId) {
                return FinalDecisionStatusResult.found(paperId, "accept", "sent");
            }
        };
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet(
                (editorEmail, paperId, refereeEmails) -> RefereeAssignmentResult.success("ok"),
                decisionService,
                "<html></html>"
        );
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "editor@cms.com");
        ServletHttpTestSupport.ResponseCapture submit = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture notify = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture status = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture htmlFallback = ServletHttpTestSupport.responseCapture();

        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{\"decision\":\"accept\"}", session, "/papers/1/decision"),
                submit.asResponse()
        );
        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{}", session, "/papers/1/decision/notify"),
                notify.asResponse()
        );
        servlet.service(
                ServletHttpTestSupport.getRequest(session, null, "/papers/1/decision"),
                status.asResponse()
        );
        servlet.service(
                ServletHttpTestSupport.getRequest(session, null, "/papers/assign"),
                htmlFallback.asResponse()
        );

        assertEquals(201, submit.getStatus());
        assertTrue(submit.getBody().contains("\"decision\":\"accept\""));
        assertEquals(202, notify.getStatus());
        assertTrue(notify.getBody().contains("\"status\":\"sent\""));
        assertEquals(200, status.getStatus());
        assertTrue(status.getBody().contains("\"notification_status\":\"sent\""));
        assertEquals(200, htmlFallback.getStatus());
        assertTrue(htmlFallback.getBody().contains("<html>"));
    }

    @Test
    void parseDecisionPathsHandleMatchMissingAndNull() throws Exception {
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet((editorEmail, paperId, refereeEmails) -> RefereeAssignmentResult.success("ok"), "<html></html>");
        Method parseDecisionPaperId = RefereeAssignmentServlet.class.getDeclaredMethod("parseDecisionPaperId", jakarta.servlet.http.HttpServletRequest.class);
        Method parseDecisionNotifyPaperId = RefereeAssignmentServlet.class.getDeclaredMethod("parseDecisionNotifyPaperId", jakarta.servlet.http.HttpServletRequest.class);
        parseDecisionPaperId.setAccessible(true);
        parseDecisionNotifyPaperId.setAccessible(true);

        Object decision = parseDecisionPaperId.invoke(servlet, ServletHttpTestSupport.getRequest(null, null, "/papers/1/decision"));
        Object noDecision = parseDecisionPaperId.invoke(servlet, ServletHttpTestSupport.getRequest(null, null, "/papers/1/referees/assign"));
        Object nullDecision = parseDecisionPaperId.invoke(servlet, ServletHttpTestSupport.getRequest(null, null, null));
        Object notify = parseDecisionNotifyPaperId.invoke(servlet, ServletHttpTestSupport.postJsonRequest("{}", null, "/papers/1/decision/notify"));
        Object noNotify = parseDecisionNotifyPaperId.invoke(servlet, ServletHttpTestSupport.postJsonRequest("{}", null, "/papers/1/decision"));
        Object nullNotify = parseDecisionNotifyPaperId.invoke(servlet, ServletHttpTestSupport.postJsonRequest("{}", null, null));

        assertEquals("1", decision);
        assertNull(noDecision);
        assertNull(nullDecision);
        assertEquals("1", notify);
        assertNull(noNotify);
        assertNull(nullNotify);
    }

    @Test
    void writesDecisionNotifyErrorPayloadWithOnlyMessageWhenFieldsMissing() throws Exception {
        FinalDecisionService decisionService = new FinalDecisionService(null, null, null, null, null) {
            @Override
            public FinalDecisionNotificationResult notifyDecision(String editorEmail, String paperId) {
                return FinalDecisionNotificationResult.error(500, null);
            }
        };
        RefereeAssignmentServlet servlet = new RefereeAssignmentServlet(
                (editorEmail, paperId, refereeEmails) -> RefereeAssignmentResult.success("ok"),
                decisionService,
                "<html></html>"
        );
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "editor@cms.com");

        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{}", session, "/papers/1/decision/notify"),
                response.asResponse()
        );

        assertEquals(500, response.getStatus());
        assertTrue(response.getBody().contains("\"message\":\"\""));
    }

    private String validPayload() {
        return "{\"referee_emails\":[\"ref1@cms.com\"]}";
    }
}
