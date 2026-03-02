package com.ece493.cms.integration;

import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.repository.JdbcPaperSubmissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinalDecisionNotificationEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() throws Exception {
        startApp();
        seedUser("editor@cms.com", "pw", "ACTIVE", "EDITOR");
        seedUser("ref1@cms.com", "pw", "ACTIVE", "REFEREE");
        new JdbcPaperSubmissionRepository(dataSource).save(new PaperSubmission(
                0L, "author@cms.com", "Paper 14", "Author", "Uni", "Abs", "k", "author@cms.com", 11L, Instant.now()
        ));

        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/1/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "1");
        postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");
        postReviewSubmission(String.valueOf(invitationId), "{\"responses\":{\"score\":\"8\",\"recommendation\":\"accept\",\"comments\":\"ok\"}}", loggedInSession("ref1@cms.com"));
        postFinalDecision("1", "{\"decision\":\"accept\"}", loggedInSession("editor@cms.com"));
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void sendsNotificationAndReturnsDecisionStatus() throws Exception {
        ServletHttpTestSupport.ResponseCapture notify = postFinalDecisionNotify("1", loggedInSession("editor@cms.com"));
        ServletHttpTestSupport.ResponseCapture status = getFinalDecisionStatus("1", loggedInSession("author@cms.com"));

        assertEquals(202, notify.getStatus());
        assertTrue(notify.getBody().contains("\"status\":\"sent\""));
        assertEquals(200, status.getStatus());
        assertTrue(status.getBody().contains("\"decision\":\"accept\""));
    }

    @Test
    void logsFailureAndNotifiesEditorWhenDeliveryFails() throws Exception {
        finalDecisionEmailDeliveryService.setAvailable(false);

        ServletHttpTestSupport.ResponseCapture notify = postFinalDecisionNotify("1", loggedInSession("editor@cms.com"));

        assertEquals(500, notify.getStatus());
        assertTrue(notify.getBody().contains("failed"));
        assertEquals(1, notificationFailureRepository.findAll().size());
        assertEquals(1, finalDecisionEditorNotificationService.notifications().size());
    }

    @Test
    void deniesUnauthorizedDecisionStatusView() throws Exception {
        ServletHttpTestSupport.ResponseCapture denied = getFinalDecisionStatus("1", loggedInSession("intruder@cms.com"));
        ServletHttpTestSupport.ResponseCapture unauthorized = getFinalDecisionStatus("1", null);

        assertEquals(403, denied.getStatus());
        assertEquals(401, unauthorized.getStatus());
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails)
                .map(v -> "\"" + v + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
