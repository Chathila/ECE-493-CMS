package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.repository.JdbcRefereeAssignmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC07AcceptanceIT extends RegistrationIntegrationSupport {
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
    void AT01_successfullyAssignRefereesAndSendInvitations() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("ref1@cms.com", "ref2@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/1/referees/assign"
        );

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("assigned"));
        assertEquals(2L, new JdbcRefereeAssignmentRepository(dataSource).countAssignmentsByPaperId("1"));
        assertEquals(2L, notificationService.sentInvitationCount());
    }

    @Test
    void AT02_invalidRefereeEmailAddress() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("missing@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/2/referees/assign"
        );

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Invalid referee email"));
        assertEquals(0L, new JdbcRefereeAssignmentRepository(dataSource).countAssignmentsByPaperId("2"));
    }

    @Test
    void AT03_refereeWorkloadLimitExceeded() throws Exception {
        JdbcRefereeAssignmentRepository repository = new JdbcRefereeAssignmentRepository(dataSource);
        repository.saveAssignments("1", java.util.List.of("ref1@cms.com"));
        repository.saveAssignments("2", java.util.List.of("ref1@cms.com"));
        repository.saveAssignments("3", java.util.List.of("ref1@cms.com"));
        repository.saveAssignments("4", java.util.List.of("ref1@cms.com"));
        repository.saveAssignments("5", java.util.List.of("ref1@cms.com"));

        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("ref1@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/10/referees/assign"
        );

        assertEquals(409, response.getStatus());
        assertTrue(response.getBody().contains("workload limit"));
        assertEquals(0L, repository.countAssignmentsByPaperId("10"));
    }

    @Test
    void AT04_tooManyRefereesAssignedToPaper_conflictWithClarificationNoMaximum() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("ref1@cms.com", "ref2@cms.com", "ref1@cms.com", "ref2@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/4/referees/assign"
        );

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("assigned"));
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails).map(v -> "\"" + v + "\"").collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
