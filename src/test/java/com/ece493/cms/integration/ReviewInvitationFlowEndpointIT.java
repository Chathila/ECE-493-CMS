package com.ece493.cms.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewInvitationFlowEndpointIT extends RegistrationIntegrationSupport {
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
    void invitationContainsRequiredDetails() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("ref1@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/51/referees/assign"
        );

        assertEquals(200, response.getStatus());
        String content = notificationService.invitationContent("ref1@cms.com", "51");
        assertTrue(content.contains("Title:"));
        assertTrue(content.contains("Abstract:"));
        assertTrue(content.contains("accept or reject"));
    }

    @Test
    void failureCreatesRecordAndEditorAlert() throws Exception {
        notificationService.setAvailable(false);

        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("ref1@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/52/referees/assign"
        );

        assertEquals(503, response.getStatus());
        assertEquals(1, notificationService.failureRecords().size());
        assertEquals(1, notificationService.editorNotifications().size());
    }

    @Test
    void invalidEmailCreatesFailureAndAlert() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("invalid-email"),
                loggedInSession("editor@cms.com"),
                "/papers/53/referees/assign"
        );

        assertEquals(503, response.getStatus());
        assertTrue(notificationService.failureRecords().get(0).getReason().contains("Invalid referee email"));
        assertTrue(notificationService.editorNotifications().get(0).getMessage().contains("update"));
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails)
                .map(v -> "\"" + v + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
