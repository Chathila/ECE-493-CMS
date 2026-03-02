package com.ece493.cms.unit;

import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.model.ReviewAssignmentRecord;
import com.ece493.cms.model.ReviewForm;
import com.ece493.cms.model.ReviewFormAccessResult;
import com.ece493.cms.repository.PaperSubmissionRepository;
import com.ece493.cms.service.ReviewAssignmentRepository;
import com.ece493.cms.service.ReviewAuthorizationService;
import com.ece493.cms.service.ReviewFormRepository;
import com.ece493.cms.service.ReviewFormService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewFormServiceTest {
    @Test
    void returnsFormWhenAuthorizedAndDataAvailable() {
        ReviewFormService service = new ReviewFormService(
                assignmentRepo(Optional.of(new ReviewAssignmentRecord(1L, "7", "ref@cms.com", "ed@cms.com", "accepted", false)), false),
                formRepo(Optional.of(new ReviewForm("f", "v1", List.of("score"))), false),
                paperRepo(Optional.of(new PaperSubmission(7L, "a", "T", "A", "U", "Abs", "k", "c", 1L, Instant.now())), false),
                new ReviewAuthorizationService()
        );

        ReviewFormAccessResult result = service.accessReviewForm(1L, "ref@cms.com");

        assertEquals(200, result.getStatusCode());
        assertEquals("T", result.getPaperSubmission().getTitle());
    }

    @Test
    void handlesUnauthorizedUnavailableAndFailureStates() {
        ReviewFormService unauthorized = new ReviewFormService(
                assignmentRepo(Optional.of(new ReviewAssignmentRecord(1L, "7", "other@cms.com", "ed@cms.com", "accepted", false)), false),
                formRepo(Optional.of(new ReviewForm("f", "v1", List.of("score"))), false),
                paperRepo(Optional.empty(), false),
                new ReviewAuthorizationService()
        );
        ReviewFormService missingAssignment = new ReviewFormService(
                assignmentRepo(Optional.empty(), false),
                formRepo(Optional.of(new ReviewForm("f", "v1", List.of("score"))), false),
                paperRepo(Optional.empty(), false),
                new ReviewAuthorizationService()
        );
        ReviewFormService missingForm = new ReviewFormService(
                assignmentRepo(Optional.of(new ReviewAssignmentRecord(1L, "7", "ref@cms.com", "ed@cms.com", "accepted", false)), false),
                formRepo(Optional.empty(), false),
                paperRepo(Optional.empty(), false),
                new ReviewAuthorizationService()
        );
        ReviewFormService badPaperId = new ReviewFormService(
                assignmentRepo(Optional.of(new ReviewAssignmentRecord(1L, "bad", "ref@cms.com", "ed@cms.com", "accepted", false)), false),
                formRepo(Optional.of(new ReviewForm("f", "v1", List.of("score"))), false),
                paperRepo(Optional.empty(), false),
                new ReviewAuthorizationService()
        );
        ReviewFormService missingPaper = new ReviewFormService(
                assignmentRepo(Optional.of(new ReviewAssignmentRecord(1L, "7", "ref@cms.com", "ed@cms.com", "accepted", false)), false),
                formRepo(Optional.of(new ReviewForm("f", "v1", List.of("score"))), false),
                paperRepo(Optional.empty(), false),
                new ReviewAuthorizationService()
        );
        ReviewFormService assignmentFailure = new ReviewFormService(
                assignmentRepo(Optional.empty(), true),
                formRepo(Optional.of(new ReviewForm("f", "v1", List.of("score"))), false),
                paperRepo(Optional.empty(), false),
                new ReviewAuthorizationService()
        );
        ReviewFormService formFailure = new ReviewFormService(
                assignmentRepo(Optional.of(new ReviewAssignmentRecord(1L, "7", "ref@cms.com", "ed@cms.com", "accepted", false)), false),
                formRepo(Optional.empty(), true),
                paperRepo(Optional.empty(), false),
                new ReviewAuthorizationService()
        );
        ReviewFormService paperFailure = new ReviewFormService(
                assignmentRepo(Optional.of(new ReviewAssignmentRecord(1L, "7", "ref@cms.com", "ed@cms.com", "accepted", false)), false),
                formRepo(Optional.of(new ReviewForm("f", "v1", List.of("score"))), false),
                paperRepo(Optional.empty(), true),
                new ReviewAuthorizationService()
        );

        assertEquals(403, unauthorized.accessReviewForm(1L, "ref@cms.com").getStatusCode());
        assertEquals(403, missingAssignment.accessReviewForm(1L, "ref@cms.com").getStatusCode());
        assertEquals(404, missingForm.accessReviewForm(1L, "ref@cms.com").getStatusCode());
        assertEquals(404, badPaperId.accessReviewForm(1L, "ref@cms.com").getStatusCode());
        assertEquals(404, missingPaper.accessReviewForm(1L, "ref@cms.com").getStatusCode());
        assertEquals(500, assignmentFailure.accessReviewForm(1L, "ref@cms.com").getStatusCode());
        assertEquals(500, formFailure.accessReviewForm(1L, "ref@cms.com").getStatusCode());
        assertEquals(500, paperFailure.accessReviewForm(1L, "ref@cms.com").getStatusCode());
    }

    private ReviewAssignmentRepository assignmentRepo(Optional<ReviewAssignmentRecord> assignment, boolean fail) {
        return new ReviewAssignmentRepository() {
            @Override
            public Optional<ReviewAssignmentRecord> findByAssignmentId(long assignmentId) {
                if (fail) {
                    throw new IllegalStateException("fail");
                }
                return assignment;
            }

            @Override
            public boolean markSubmitted(long assignmentId) {
                return false;
            }
        };
    }

    private ReviewFormRepository formRepo(Optional<ReviewForm> form, boolean fail) {
        return assignmentId -> {
            if (fail) {
                throw new IllegalStateException("fail");
            }
            return form;
        };
    }

    private PaperSubmissionRepository paperRepo(Optional<PaperSubmission> paper, boolean fail) {
        return new PaperSubmissionRepository() {
            @Override
            public long save(PaperSubmission paperSubmission) {
                return 0;
            }

            @Override
            public List<PaperSubmission> findAllByAuthorEmail(String authorEmail) {
                return List.of();
            }

            @Override
            public Optional<PaperSubmission> findBySubmissionId(long submissionId) {
                if (fail) {
                    throw new IllegalStateException("fail");
                }
                return paper;
            }

            @Override
            public long countAll() {
                return 0;
            }
        };
    }
}
