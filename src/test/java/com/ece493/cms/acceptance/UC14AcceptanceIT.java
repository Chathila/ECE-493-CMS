package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.repository.JdbcPaperSubmissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC14AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() throws Exception {
        startApp();
        seedUser("editor@cms.com", "pw", "ACTIVE", "EDITOR");
        seedUser("ref1@cms.com", "pw", "ACTIVE", "REFEREE");
        new JdbcPaperSubmissionRepository(dataSource).save(new PaperSubmission(
                0L, "author@cms.com", "Notify Paper", "Author", "Uni", "Abs", "k", "author@cms.com", 10L, Instant.now()
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
    void AT01_authorReceivesDecisionNotificationSuccessfully() throws Exception {
        ServletHttpTestSupport.ResponseCapture notify = postFinalDecisionNotify("1", loggedInSession("editor@cms.com"));
        ServletHttpTestSupport.ResponseCapture status = getFinalDecisionStatus("1", loggedInSession("author@cms.com"));

        assertEquals(202, notify.getStatus());
        assertTrue(finalDecisionEmailDeliveryService.sentEmails().stream().anyMatch(v -> "accept".equals(v.getBody())));
        assertEquals(200, status.getStatus());
        assertTrue(status.getBody().contains("\"decision\":\"accept\""));
    }

    @Test
    void AT02_notificationDeliveryFailure() throws Exception {
        finalDecisionEmailDeliveryService.setAvailable(false);

        ServletHttpTestSupport.ResponseCapture notify = postFinalDecisionNotify("1", loggedInSession("editor@cms.com"));

        assertEquals(500, notify.getStatus());
        assertEquals(1, notificationFailureRepository.findAll().size());
        assertTrue(finalDecisionEditorNotificationService.notifications().stream().anyMatch(v -> "delivery failed".equals(v.getMessage())));
    }

    @Test
    void AT03_authorViewsDecisionBeforeNotificationDelivery() throws Exception {
        finalDecisionEmailDeliveryService.setAvailable(false);
        postFinalDecisionNotify("1", loggedInSession("editor@cms.com"));

        ServletHttpTestSupport.ResponseCapture status = getFinalDecisionStatus("1", loggedInSession("author@cms.com"));

        assertEquals(200, status.getStatus());
        assertTrue(status.getBody().contains("\"decision\":\"accept\""));
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails)
                .map(v -> "\"" + v + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
