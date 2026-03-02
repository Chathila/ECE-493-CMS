package com.ece493.cms.unit;

import com.ece493.cms.controller.ReviewDashboardServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.model.ReviewInvitation;
import com.ece493.cms.repository.PaperSubmissionRepository;
import com.ece493.cms.service.InMemoryReviewInvitationRepository;
import com.ece493.cms.service.ReviewAssignmentService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewDashboardServletTest {
    @Test
    void returnsUnauthorizedWhenSessionMissing() throws Exception {
        ReviewDashboardServlet servlet = new ReviewDashboardServlet(
                new InMemoryReviewInvitationRepository(),
                new ReviewAssignmentService(),
                repo(List.of())
        );
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/reviews/dashboard"), response.asResponse());

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("log in"));
    }

    @Test
    void returnsUnauthorizedWhenSessionEmailBlank() throws Exception {
        ReviewDashboardServlet servlet = new ReviewDashboardServlet(
                new InMemoryReviewInvitationRepository(),
                new ReviewAssignmentService(),
                repo(List.of())
        );
        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", " ");

        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/reviews/dashboard"), response.asResponse());

        assertEquals(401, response.getStatus());
    }

    @Test
    void returnsReviewerRequestsWithStatusesAndPaperDetails() throws Exception {
        InMemoryReviewInvitationRepository invitationRepository = new InMemoryReviewInvitationRepository();
        ReviewAssignmentService assignmentService = new ReviewAssignmentService();
        ReviewInvitation accepted = invitationRepository.save(new ReviewInvitation(
                0L, "a-1", "editor@cms.com", "ref@cms.com", "41", "sent", "content", Instant.now(), Instant.now().plusSeconds(3600)
        ));
        assignmentService.markPending(accepted.getInvitationId());
        assignmentService.updateFromDecision(accepted.getInvitationId(), "accept");

        invitationRepository.save(new ReviewInvitation(
                0L, "a-2", "editor@cms.com", "ref@cms.com", "bad-paper-id", "expired", "content", Instant.now(), Instant.now().minusSeconds(5)
        ));

        ReviewDashboardServlet servlet = new ReviewDashboardServlet(
                invitationRepository,
                assignmentService,
                repo(List.of(new PaperSubmission(
                        41L, "author@cms.com", "Paper \"A\"", "Author 1", "Uni", "Abstract", "k", "contact@cms.com", 9L, Instant.now()
                )))
        );

        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "ref@cms.com");

        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/reviews/dashboard"), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"review_requests\":["));
        assertTrue(response.getBody().contains("\"status\":\"accepted\""));
        assertTrue(response.getBody().contains("\"status\":\"expired\""));
        assertTrue(response.getBody().contains("\"paper_title\":\"Paper \\\"A\\\"\""));
        assertTrue(response.getBody().contains("\"download_url\":\"/papers/files/9\""));
        assertTrue(response.getBody().contains("\"paper_title\":\"\""));
    }

    @Test
    void returnsEditorPaperCardsWithApprovedReviewerList() throws Exception {
        InMemoryReviewInvitationRepository invitationRepository = new InMemoryReviewInvitationRepository();
        ReviewAssignmentService assignmentService = new ReviewAssignmentService();
        ReviewInvitation accepted = invitationRepository.save(new ReviewInvitation(
                0L, "a-1", "editor@cms.com", "ref1@cms.com", "52", "sent", "content", Instant.now(), Instant.now().plusSeconds(3600)
        ));
        assignmentService.markPending(accepted.getInvitationId());
        assignmentService.updateFromDecision(accepted.getInvitationId(), "accept");
        assignmentService.markApproved(accepted.getInvitationId());

        ReviewInvitation rejected = invitationRepository.save(new ReviewInvitation(
                0L, "a-2", "editor@cms.com", "ref2@cms.com", "52", "sent", "content", Instant.now(), Instant.now().plusSeconds(3600)
        ));
        assignmentService.markPending(rejected.getInvitationId());
        assignmentService.updateFromDecision(rejected.getInvitationId(), "reject");

        ReviewInvitation accepted2 = invitationRepository.save(new ReviewInvitation(
                0L, "a-3", "editor@cms.com", "ref3@cms.com", "52", "sent", "content", Instant.now(), Instant.now().plusSeconds(3600)
        ));
        assignmentService.markPending(accepted2.getInvitationId());
        assignmentService.updateFromDecision(accepted2.getInvitationId(), "accept");
        assignmentService.markApproved(accepted2.getInvitationId());

        invitationRepository.save(new ReviewInvitation(
                0L, "a-4", "editor@cms.com", "ref4@cms.com", "53", "sent", "content", Instant.now(), Instant.now().plusSeconds(3600)
        ));
        invitationRepository.save(new ReviewInvitation(
                0L, "a-5", "editor@cms.com", "ref5@cms.com", "", "sent", "content", Instant.now(), Instant.now().plusSeconds(3600)
        ));
        ReviewInvitation expiredByTime = invitationRepository.save(new ReviewInvitation(
                0L, "a-6", "editor@cms.com", "ref6@cms.com", "53", "sent", "content", Instant.now(), Instant.now().minusSeconds(1)
        ));
        assignmentService.markPending(expiredByTime.getInvitationId());

        ReviewDashboardServlet servlet = new ReviewDashboardServlet(
                invitationRepository,
                assignmentService,
                repo(List.of(
                        new PaperSubmission(52L, "author@cms.com", "Decision Paper", "Author 1", "Uni", "Abstract", "k", "contact@cms.com", 10L, Instant.now()),
                        new PaperSubmission(53L, "author@cms.com", "Another Paper", "Author 2", "Uni", "Abstract", "k", "contact@cms.com", 11L, Instant.now())
                ))
        );

        ServletHttpTestSupport.ResponseCapture response = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "editor@cms.com");

        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/reviews/dashboard"), response.asResponse());

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"editor_papers\":["));
        assertTrue(response.getBody().contains("\"paper_title\":\"Decision Paper\""));
        assertTrue(response.getBody().contains("\"approved_reviewers\":[\"ref1@cms.com\",\"ref3@cms.com\"]"));
        assertTrue(response.getBody().contains("\"email\":\"ref1@cms.com\",\"status\":\"approved\""));
        assertTrue(response.getBody().contains("\"email\":\"ref2@cms.com\",\"status\":\"rejected\""));
        assertTrue(response.getBody().contains("\"paper_title\":\"Another Paper\""));
        assertTrue(response.getBody().contains("\"status\":\"expired\""));
    }

    private PaperSubmissionRepository repo(List<PaperSubmission> submissions) {
        return new PaperSubmissionRepository() {
            @Override
            public long save(PaperSubmission paperSubmission) {
                return 0;
            }

            @Override
            public List<PaperSubmission> findAllByAuthorEmail(String authorEmail) {
                return submissions;
            }

            @Override
            public Optional<PaperSubmission> findBySubmissionId(long submissionId) {
                return submissions.stream()
                        .filter(submission -> submission.getSubmissionId() == submissionId)
                        .findFirst();
            }

            @Override
            public long countAll() {
                return submissions.size();
            }
        };
    }
}
