package com.ece493.cms.unit;

import com.ece493.cms.controller.InvitationResponseServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.InvitationResponseResult;
import com.ece493.cms.service.InvitationResponseService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvitationResponseServletTest {
    @Test
    void getReturnsExpiredWhenServiceRejectsView() throws Exception {
        InvitationResponseService service = new InvitationResponseService(null, null, null, null) {
            @Override
            public InvitationResponseResult viewInvitation(long invitationId) {
                return InvitationResponseResult.error(410, "Invitation has expired.");
            }
        };
        InvitationResponseServlet servlet = new InvitationResponseServlet(service);
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/invitations/10"), response.asResponse());

        assertEquals(410, response.getStatus());
        assertTrue(response.getBody().contains("expired"));
    }

    @Test
    void postReturnsSuccessWithAssignmentStatus() throws Exception {
        InvitationResponseService service = new InvitationResponseService(null, null, null, null) {
            @Override
            public InvitationResponseResult submitResponse(long invitationId, String decision) {
                return InvitationResponseResult.success("accepted");
            }
        };
        InvitationResponseServlet servlet = new InvitationResponseServlet(service);
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{\"decision\":\"accept\"}", null, "/invitations/11/response"),
                response.asResponse()
        );

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"assignment_status\":\"accepted\""));
    }

    @Test
    void postApprovalPathReturnsSuccess() throws Exception {
        InvitationResponseService service = new InvitationResponseService(null, null, null, null) {
            @Override
            public InvitationResponseResult submitApproval(long invitationId) {
                return InvitationResponseResult.success("Review approval submitted.", "approved");
            }
        };
        InvitationResponseServlet servlet = new InvitationResponseServlet(service);
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{}", null, "/invitations/11/approval"),
                response.asResponse()
        );

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"assignment_status\":\"approved\""));
    }

    @Test
    void postHandlesInvalidInvitationPathAndMissingDecision() throws Exception {
        InvitationResponseService service = new InvitationResponseService(null, null, null, null) {
            @Override
            public InvitationResponseResult submitResponse(long invitationId, String decision) {
                return InvitationResponseResult.error(400, "Decision must be either accept or reject.");
            }
        };
        InvitationResponseServlet servlet = new InvitationResponseServlet(service);
        ServletHttpTestSupport.ResponseCapture badPath = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture missingDecision = ServletHttpTestSupport.responseCapture();

        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{\"decision\":\"accept\"}", null, "/invitations/x/response"),
                badPath.asResponse()
        );
        servlet.service(
                ServletHttpTestSupport.postJsonRequest("{\"x\":\"y\"}", null, "/invitations/12/response"),
                missingDecision.asResponse()
        );

        assertEquals(400, badPath.getStatus());
        assertEquals(400, missingDecision.getStatus());
    }

    @Test
    void getHandlesInvalidInvitationPath() throws Exception {
        InvitationResponseService service = new InvitationResponseService(null, null, null, null) {
            @Override
            public InvitationResponseResult viewInvitation(long invitationId) {
                return InvitationResponseResult.success("pending");
            }
        };
        InvitationResponseServlet servlet = new InvitationResponseServlet(service);
        ServletHttpTestSupport.ResponseCapture badPath = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture nullUri = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/invitations/not-a-number"), badPath.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(), nullUri.asResponse());

        assertEquals(400, badPath.getStatus());
        assertEquals(400, nullUri.getStatus());
    }

    @Test
    void privateParsersHandleNullEmptyAndInvalidBodies() throws Exception {
        InvitationResponseServlet servlet = new InvitationResponseServlet(new InvitationResponseService(null, null, null, null));
        Method readDecision = InvitationResponseServlet.class.getDeclaredMethod("readDecision", String.class);
        Method parseForGet = InvitationResponseServlet.class.getDeclaredMethod("parseInvitationIdForView", String.class);
        Method parseForPost = InvitationResponseServlet.class.getDeclaredMethod("parseInvitationIdForResponse", String.class);
        Method parseForApproval = InvitationResponseServlet.class.getDeclaredMethod("parseInvitationIdForApproval", String.class);
        readDecision.setAccessible(true);
        parseForGet.setAccessible(true);
        parseForPost.setAccessible(true);
        parseForApproval.setAccessible(true);

        assertNull(readDecision.invoke(servlet, (Object) null));
        assertNull(readDecision.invoke(servlet, ""));
        assertNull(readDecision.invoke(servlet, "{\"x\":\"y\"}"));
        assertEquals("accept", readDecision.invoke(servlet, "{\"decision\":\" Accept \"}"));
        assertNull(parseForGet.invoke(servlet, (Object) null));
        assertNull(parseForGet.invoke(servlet, "/invitations/not-number"));
        assertEquals(15L, parseForGet.invoke(servlet, "/invitations/15"));
        assertNull(parseForPost.invoke(servlet, "/invitations/15"));
        assertEquals(15L, parseForPost.invoke(servlet, "/invitations/15/response"));
        assertNull(parseForApproval.invoke(servlet, "/invitations/15/response"));
        assertEquals(15L, parseForApproval.invoke(servlet, "/invitations/15/approval"));
    }
}
