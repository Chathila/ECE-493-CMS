package com.ece493.cms.integration;

import com.ece493.cms.repository.JdbcRefereeAssignmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefereeAssignmentEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
        seedUser("editor@cms.com", "pw", "ACTIVE", "EDITOR");
        seedUser("ref1@cms.com", "pw", "ACTIVE", "REFEREE");
        seedUser("ref2@cms.com", "pw", "ACTIVE", "REFEREE");
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void assignsRefereesSuccessfully() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                validPayload("ref1@cms.com", "ref2@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/1/referees/assign"
        );

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("assigned"));
        JdbcRefereeAssignmentRepository repository = new JdbcRefereeAssignmentRepository(dataSource);
        assertEquals(2L, repository.countAssignmentsByPaperId("1"));
        assertEquals(2L, notificationService.sentInvitationCount());
    }

    @Test
    void rejectsInvalidRefereeEmail() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                validPayload("missing@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/1/referees/assign"
        );

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Invalid referee email"));
        assertEquals(0L, new JdbcRefereeAssignmentRepository(dataSource).countAssignmentsByPaperId("1"));
    }

    @Test
    void rejectsWorkloadLimitExceeded() throws Exception {
        JdbcRefereeAssignmentRepository repository = new JdbcRefereeAssignmentRepository(dataSource);
        repository.saveAssignments("1", java.util.List.of("ref1@cms.com"));
        repository.saveAssignments("2", java.util.List.of("ref1@cms.com"));
        repository.saveAssignments("3", java.util.List.of("ref1@cms.com"));
        repository.saveAssignments("4", java.util.List.of("ref1@cms.com"));
        repository.saveAssignments("5", java.util.List.of("ref1@cms.com"));

        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                validPayload("ref1@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/10/referees/assign"
        );

        assertEquals(409, response.getStatus());
        assertTrue(response.getBody().contains("workload limit"));
        assertEquals(0L, repository.countAssignmentsByPaperId("10"));
    }

    @Test
    void returnsWarningWhenNotificationFailsButStillSaves() throws Exception {
        notificationService.setAvailable(false);
        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                validPayload("ref1@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/11/referees/assign"
        );

        assertEquals(503, response.getStatus());
        assertTrue(response.getBody().contains("warning"));
        assertEquals(1L, new JdbcRefereeAssignmentRepository(dataSource).countAssignmentsByPaperId("11"));
    }

    private String validPayload(String... emails) {
        String joined = java.util.Arrays.stream(emails).map(v -> "\"" + v + "\"").collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
