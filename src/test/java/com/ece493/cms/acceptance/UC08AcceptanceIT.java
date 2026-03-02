package com.ece493.cms.acceptance;

import com.ece493.cms.controller.RefereeAssignmentServlet;
import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.repository.JdbcRefereeAssignmentRepository;
import com.ece493.cms.repository.JdbcUserAccountRepository;
import com.ece493.cms.repository.RefereeAssignmentRepository;
import com.ece493.cms.service.RefereeAssignmentService;
import com.ece493.cms.service.RefereeAssignmentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC08AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
        seedUser("editor@cms.com", "pw", "ACTIVE", "EDITOR");
        seedUser("ref1@cms.com", "pw", "ACTIVE", "REFEREE");
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_allowAssignmentWhenReviewerHasFewerThanFivePapers() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("ref1@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/21/referees/assign"
        );

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("assigned"));
        assertEquals(1L, new JdbcRefereeAssignmentRepository(dataSource).countAssignmentsByRefereeEmail("ref1@cms.com"));
    }

    @Test
    void AT02_blockAssignmentWhenReviewerAlreadyHasFivePapers() throws Exception {
        JdbcRefereeAssignmentRepository repository = new JdbcRefereeAssignmentRepository(dataSource);
        repository.saveAssignments("1", List.of("ref1@cms.com"));
        repository.saveAssignments("2", List.of("ref1@cms.com"));
        repository.saveAssignments("3", List.of("ref1@cms.com"));
        repository.saveAssignments("4", List.of("ref1@cms.com"));
        repository.saveAssignments("5", List.of("ref1@cms.com"));

        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("ref1@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/22/referees/assign"
        );

        assertEquals(409, response.getStatus());
        assertTrue(response.getBody().contains("workload limit"));
        assertEquals(0L, repository.countAssignmentsByPaperId("22"));
        assertEquals(5L, repository.countAssignmentsByRefereeEmail("ref1@cms.com"));
    }

    @Test
    void AT03_workloadCountRetrievalFailureBlocksAssignment() throws Exception {
        RefereeAssignmentRepository failingCountRepo = new RefereeAssignmentRepository() {
            @Override
            public void saveAssignments(String paperId, List<String> refereeEmails) {
            }

            @Override
            public long countAssignmentsByRefereeEmail(String refereeEmail) {
                throw new IllegalStateException("database unavailable");
            }

            @Override
            public long countAssignmentsByPaperId(String paperId) {
                return 0;
            }
        };
        RefereeAssignmentService failingService = new RefereeAssignmentServiceImpl(
                new JdbcUserAccountRepository(dataSource),
                failingCountRepo,
                notificationService
        );
        refereeAssignmentServlet = new RefereeAssignmentServlet(failingService, "<html>Assign Referees</html>");

        ServletHttpTestSupport.ResponseCapture response = postRefereeAssignment(
                payload("ref1@cms.com"),
                loggedInSession("editor@cms.com"),
                "/papers/23/referees/assign"
        );

        assertEquals(500, response.getStatus());
        assertTrue(response.getBody().contains("workload check"));
        assertEquals(0L, new JdbcRefereeAssignmentRepository(dataSource).countAssignmentsByPaperId("23"));
    }

    private String payload(String... refereeEmails) {
        String joined = java.util.Arrays.stream(refereeEmails)
                .map(v -> "\"" + v + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        return "{\"referee_emails\":[" + joined + "]}";
    }
}
