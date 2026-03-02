package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC10AcceptanceIT extends RegistrationIntegrationSupport {
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
    void AT01_acceptInvitationSuccessfully() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/71/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "71");

        ServletHttpTestSupport.ResponseCapture response = postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");

        assertEquals(200, response.getStatus());
        assertEquals(1, notificationService.invitationResponses().size());
        assertEquals("accepted", notificationService.assignmentStatus(invitationId));
        assertTrue(notificationService.editorNotifications().stream().anyMatch(n -> "accept".equals(n.getMessage())));
    }

    @Test
    void AT02_rejectInvitationSuccessfully() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/72/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "72");

        ServletHttpTestSupport.ResponseCapture response = postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"reject\"}");

        assertEquals(200, response.getStatus());
        assertEquals(1, notificationService.invitationResponses().size());
        assertEquals("rejected", notificationService.assignmentStatus(invitationId));
        assertTrue(notificationService.editorNotifications().stream().anyMatch(n -> "reject".equals(n.getMessage())));
    }

    @Test
    void AT03_attemptToRespondAfterInvitationExpired() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/73/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "73");
        notificationService.expireInvitation(invitationId);

        ServletHttpTestSupport.ResponseCapture response = postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");

        assertEquals(410, response.getStatus());
        assertEquals(0, notificationService.invitationResponses().size());
        assertEquals("pending", notificationService.assignmentStatus(invitationId));
    }

    @Test
    void AT04_databaseErrorWhenRecordingResponse() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/74/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "74");
        notificationService.invitationResponseRepository().setFailOnSave(true);

        ServletHttpTestSupport.ResponseCapture response = postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");

        assertEquals(500, response.getStatus());
        assertTrue(response.getBody().contains("retry"));
        assertEquals(0, notificationService.invitationResponses().size());
        assertEquals("pending", notificationService.assignmentStatus(invitationId));
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails)
                .map(v -> "\"" + v + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
