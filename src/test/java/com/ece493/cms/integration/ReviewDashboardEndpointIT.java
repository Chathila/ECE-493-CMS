package com.ece493.cms.integration;

import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.repository.JdbcPaperSubmissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewDashboardEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
        seedUser("editor@cms.com", "pw", "ACTIVE", "EDITOR");
        seedUser("ref1@cms.com", "pw", "ACTIVE", "REFEREE");
        seedUser("ref2@cms.com", "pw", "ACTIVE", "REFEREE");
        new JdbcPaperSubmissionRepository(dataSource).save(new PaperSubmission(
                0L,
                "author@cms.com",
                "Observable Paper",
                "Author One",
                "University A",
                "Paper abstract",
                "k1,k2",
                "author@cms.com",
                33L,
                Instant.now()
        ));
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void showsReviewerAndEditorViewsWithCurrentDecisions() throws Exception {
        postRefereeAssignment(payload("ref1@cms.com", "ref2@cms.com"), loggedInSession("editor@cms.com"), "/papers/1/referees/assign");
        long ref1InvitationId = notificationService.invitationId("ref1@cms.com", "1");
        postInvitationResponse(String.valueOf(ref1InvitationId), "{\"decision\":\"accept\"}");
        postInvitationApproval(String.valueOf(ref1InvitationId));

        ServletHttpTestSupport.ResponseCapture reviewer = getReviewDashboard(loggedInSession("ref1@cms.com"));
        assertEquals(200, reviewer.getStatus());
        assertTrue(reviewer.getBody().contains("\"paper_title\":\"Observable Paper\""));
        assertTrue(reviewer.getBody().contains("\"status\":\"approved\""));

        ServletHttpTestSupport.ResponseCapture editor = getReviewDashboard(loggedInSession("editor@cms.com"));
        assertEquals(200, editor.getStatus());
        assertTrue(editor.getBody().contains("\"approved_reviewers\":[\"ref1@cms.com\"]"));
        assertTrue(editor.getBody().contains("\"email\":\"ref2@cms.com\",\"status\":\"pending\""));
    }

    @Test
    void requiresLoginToViewDashboard() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = getReviewDashboard(null);

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("log in"));
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails)
                .map(v -> "\"" + v + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
