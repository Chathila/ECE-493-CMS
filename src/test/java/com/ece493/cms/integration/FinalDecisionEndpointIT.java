package com.ece493.cms.integration;

import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.repository.JdbcPaperSubmissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinalDecisionEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
        seedUser("editor@cms.com", "pw", "ACTIVE", "EDITOR");
        seedUser("ref1@cms.com", "pw", "ACTIVE", "REFEREE");
        new JdbcPaperSubmissionRepository(dataSource).save(new PaperSubmission(
                0L, "author@cms.com", "Paper 13", "Author", "Uni", "Abs", "k", "author@cms.com", 9L, Instant.now()
        ));
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void recordsFinalDecisionWhenAllReviewsAreSubmitted() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/1/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "1");
        postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");
        postReviewSubmission(String.valueOf(invitationId), "{\"responses\":{\"score\":\"8\",\"recommendation\":\"accept\",\"comments\":\"ok\"}}", loggedInSession("ref1@cms.com"));

        ServletHttpTestSupport.ResponseCapture response = postFinalDecision("1", "{\"decision\":\"accept\"}", loggedInSession("editor@cms.com"));

        assertEquals(201, response.getStatus());
        assertTrue(response.getBody().contains("\"decision\":\"accept\""));
        assertEquals(1L, finalDecisionRepository.countAll());
    }

    @Test
    void blocksDecisionWhenReviewsIncompleteAndHandlesSaveFailure() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/1/referees/assign");

        ServletHttpTestSupport.ResponseCapture incomplete = postFinalDecision("1", "{\"decision\":\"reject\"}", loggedInSession("editor@cms.com"));
        assertEquals(409, incomplete.getStatus());

        long invitationId = notificationService.invitationId("ref1@cms.com", "1");
        postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");
        postReviewSubmission(String.valueOf(invitationId), "{\"responses\":{\"score\":\"8\",\"recommendation\":\"accept\",\"comments\":\"ok\"}}", loggedInSession("ref1@cms.com"));
        finalDecisionRepository.setFailOnSave(true);

        ServletHttpTestSupport.ResponseCapture failure = postFinalDecision("1", "{\"decision\":\"reject\"}", loggedInSession("editor@cms.com"));
        assertEquals(500, failure.getStatus());
        assertTrue(failure.getBody().contains("retry later"));
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails)
                .map(v -> "\"" + v + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
