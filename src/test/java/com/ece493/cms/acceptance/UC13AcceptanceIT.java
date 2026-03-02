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

class UC13AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
        seedUser("editor@cms.com", "pw", "ACTIVE", "EDITOR");
        seedUser("ref1@cms.com", "pw", "ACTIVE", "REFEREE");
        new JdbcPaperSubmissionRepository(dataSource).save(new PaperSubmission(
                0L, "author@cms.com", "Decision Paper", "Author", "Uni", "Abs", "k", "author@cms.com", 10L, Instant.now()
        ));
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_recordAcceptanceDecisionSuccessfully() throws Exception {
        prepareCompletedReview();

        ServletHttpTestSupport.ResponseCapture response = postFinalDecision("1", "{\"decision\":\"accept\"}", loggedInSession("editor@cms.com"));

        assertEquals(201, response.getStatus());
        assertTrue(response.getBody().contains("\"decision\":\"accept\""));
        assertEquals(1L, finalDecisionRepository.countAll());
        assertTrue(finalDecisionEmailDeliveryService.sentEmails().stream().anyMatch(v -> "accept".equals(v.getBody())));
    }

    @Test
    void AT02_recordRejectionDecisionSuccessfully() throws Exception {
        prepareCompletedReview();

        ServletHttpTestSupport.ResponseCapture response = postFinalDecision("1", "{\"decision\":\"reject\"}", loggedInSession("editor@cms.com"));

        assertEquals(201, response.getStatus());
        assertTrue(response.getBody().contains("\"decision\":\"reject\""));
        assertTrue(finalDecisionEmailDeliveryService.sentEmails().stream().anyMatch(v -> "reject".equals(v.getBody())));
    }

    @Test
    void AT03_attemptDecisionBeforeAllReviewsCompleted() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/1/referees/assign");

        ServletHttpTestSupport.ResponseCapture response = postFinalDecision("1", "{\"decision\":\"accept\"}", loggedInSession("editor@cms.com"));

        assertEquals(409, response.getStatus());
        assertEquals(0L, finalDecisionRepository.countAll());
        assertEquals(0, finalDecisionEmailDeliveryService.sentEmails().size());
    }

    @Test
    void AT04_databaseErrorWhileRecordingDecision() throws Exception {
        prepareCompletedReview();
        finalDecisionRepository.setFailOnSave(true);

        ServletHttpTestSupport.ResponseCapture response = postFinalDecision("1", "{\"decision\":\"accept\"}", loggedInSession("editor@cms.com"));

        assertEquals(500, response.getStatus());
        assertTrue(response.getBody().contains("retry later"));
        assertEquals(0, finalDecisionEmailDeliveryService.sentEmails().size());
    }

    private void prepareCompletedReview() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/1/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "1");
        postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");
        postReviewSubmission(String.valueOf(invitationId), "{\"responses\":{\"score\":\"8\",\"recommendation\":\"accept\",\"comments\":\"ok\"}}", loggedInSession("ref1@cms.com"));
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails)
                .map(v -> "\"" + v + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
