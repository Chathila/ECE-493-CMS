package com.ece493.cms.integration;

import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.repository.JdbcPaperSubmissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewSubmissionEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
        seedUser("editor@cms.com", "pw", "ACTIVE", "EDITOR");
        seedUser("ref1@cms.com", "pw", "ACTIVE", "REFEREE");
        new JdbcPaperSubmissionRepository(dataSource).save(new PaperSubmission(
                0L, "author@cms.com", "Paper 1", "Author", "Uni", "Abstract", "k", "author@cms.com", 10L, Instant.now()
        ));
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void submitsReviewWhenValid() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/1/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "1");
        postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");

        ServletHttpTestSupport.ResponseCapture response = postReviewSubmission(
                String.valueOf(invitationId),
                "{\"responses\":{\"score\":\"8\",\"recommendation\":\"accept\",\"comments\":\"good\"}}",
                loggedInSession("ref1@cms.com")
        );

        assertEquals(201, response.getStatus());
        assertTrue(response.getBody().contains("\"status\":\"submitted\""));
        assertEquals("submitted", notificationService.assignmentStatus(invitationId));
    }

    @Test
    void returnsValidationFieldsForIncompleteSubmission() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/1/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "1");
        postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");

        ServletHttpTestSupport.ResponseCapture response = postReviewSubmission(
                String.valueOf(invitationId),
                "{\"responses\":{\"score\":\"\",\"recommendation\":\"\",\"comments\":\"\"}}",
                loggedInSession("ref1@cms.com")
        );

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("\"fields\""));
    }

    @Test
    void returnsRetryMessageOnStorageFailure() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/1/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "1");
        postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");
        reviewRepository.setFailOnSave(true);

        ServletHttpTestSupport.ResponseCapture response = postReviewSubmission(
                String.valueOf(invitationId),
                "{\"responses\":{\"score\":\"8\",\"recommendation\":\"accept\",\"comments\":\"good\"}}",
                loggedInSession("ref1@cms.com")
        );

        assertEquals(500, response.getStatus());
        assertTrue(response.getBody().contains("retry later"));
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails)
                .map(v -> "\"" + v + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
