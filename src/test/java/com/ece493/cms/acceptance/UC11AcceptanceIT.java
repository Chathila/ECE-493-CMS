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

class UC11AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
        seedUser("editor@cms.com", "pw", "ACTIVE", "EDITOR");
        seedUser("ref1@cms.com", "pw", "ACTIVE", "REFEREE");
        new JdbcPaperSubmissionRepository(dataSource).save(new PaperSubmission(
                0L, "author@cms.com", "Paper 11", "Author", "Uni", "Abstract", "k", "author@cms.com", 10L, Instant.now()
        ));
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_accessReviewFormSuccessfully() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/1/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "1");
        postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");

        ServletHttpTestSupport.ResponseCapture response = getReviewForm(String.valueOf(invitationId), loggedInSession("ref1@cms.com"));

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"form\""));
        assertTrue(response.getBody().contains("\"paper\""));
    }

    @Test
    void AT02_accessDeniedForUnauthorizedPaper() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/1/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "1");

        ServletHttpTestSupport.ResponseCapture response = getReviewForm(String.valueOf(invitationId), loggedInSession("another@cms.com"));

        assertEquals(403, response.getStatus());
        assertTrue(response.getBody().contains("Access denied"));
    }

    @Test
    void AT03_reviewFormRetrievalFailure() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com"), loggedInSession("editor@cms.com"), "/papers/99/referees/assign");
        long invitationId = notificationService.invitationId("ref1@cms.com", "99");
        postInvitationResponse(String.valueOf(invitationId), "{\"decision\":\"accept\"}");

        ServletHttpTestSupport.ResponseCapture response = getReviewForm(String.valueOf(invitationId), loggedInSession("ref1@cms.com"));

        assertEquals(404, response.getStatus());
        assertTrue(response.getBody().contains("unavailable"));
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails)
                .map(v -> "\"" + v + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
