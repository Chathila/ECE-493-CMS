package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC09AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
        seedUser("editor@cms.com", "pw", "ACTIVE", "EDITOR");
        seedUser("ref1@cms.com", "pw", "ACTIVE", "REFEREE");
        seedUser("invalid-email", "pw", "ACTIVE", "REFEREE");
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_invitationEmailSentSuccessfully() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("ref1@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/41/referees/assign"
        );

        assertEquals(200, response.getStatus());
        String content = notificationService.invitationContent("ref1@cms.com", "41");
        assertTrue(content.contains("Title:"));
        assertTrue(content.contains("Abstract:"));
        assertTrue(content.contains("accept or reject"));
    }

    @Test
    void AT02_emailServiceFailure() throws Exception {
        notificationService.setAvailable(false);

        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("ref1@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/42/referees/assign"
        );

        assertEquals(503, response.getStatus());
        assertEquals(1, notificationService.failureRecords().size());
        assertEquals(1, notificationService.editorNotifications().size());
        assertEquals(0L, notificationService.sentInvitationCount());
    }

    @Test
    void AT03_invalidRefereeEmailAddress() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("invalid-email"),
                loggedInSession("editor@cms.com"),
                "/papers/43/referees/assign"
        );

        assertEquals(503, response.getStatus());
        assertEquals(1, notificationService.failureRecords().size());
        assertTrue(notificationService.failureRecords().get(0).getReason().contains("Invalid referee email"));
        assertEquals(1, notificationService.editorNotifications().size());
        assertTrue(notificationService.editorNotifications().get(0).getMessage().contains("update"));
        assertEquals(0L, notificationService.sentInvitationCount());
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails)
                .map(v -> "\"" + v + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
