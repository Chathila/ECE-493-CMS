package com.ece493.cms.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvitationResponseEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
        seedUser("editor@cms.com", "pw", "ACTIVE", "EDITOR");
        seedUser("ref1@cms.com", "pw", "ACTIVE", "REFEREE");
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void acceptsInvitationAndUpdatesStatus() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/61/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "61");

        ServletHttpTestSupport.ResponseCapture response = postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"assignment_status\":\"accepted\""));
        assertEquals("accepted", notificationService.assignmentStatus(invitationId));
    }

    @Test
    void rejectsExpiredInvitationResponse() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/62/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "62");
        notificationService.expireInvitation(invitationId);

        ServletHttpTestSupport.ResponseCapture response = postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"reject\"}");

        assertEquals(410, response.getStatus());
        assertEquals(0, notificationService.invitationResponses().size());
        assertEquals("pending", notificationService.assignmentStatus(invitationId));
    }

    @Test
    void blocksAccessToExpiredInvitationOnGet() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/63/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "63");
        notificationService.expireInvitation(invitationId);

        ServletHttpTestSupport.ResponseCapture response = getInvitation(String.valueOf(invitationId));

        assertEquals(410, response.getStatus());
        assertTrue(response.getBody().contains("expired"));
    }

    @Test
    void allowsApprovalSubmissionAfterAcceptance() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/64/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "64");
        postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");

        ServletHttpTestSupport.ResponseCapture response = postInvitationApproval(String.valueOf(invitationId));

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"assignment_status\":\"approved\""));
        assertEquals("approved", notificationService.assignmentStatus(invitationId));
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails)
                .map(v -> "\"" + v + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
