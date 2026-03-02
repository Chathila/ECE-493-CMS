package com.ece493.cms.unit;

import com.ece493.cms.model.Review;
import com.ece493.cms.model.ReviewAssignmentRecord;
import com.ece493.cms.model.ReviewSubmissionResult;
import com.ece493.cms.service.EditorNotificationService;
import com.ece493.cms.service.ReviewAssignmentRepository;
import com.ece493.cms.service.ReviewAuthorizationService;
import com.ece493.cms.service.ReviewRepository;
import com.ece493.cms.service.ReviewSubmissionService;
import com.ece493.cms.service.ReviewValidationService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewSubmissionServiceTest {
    @Test
    void submitsReviewWhenValidAndAuthorized() {
        StubAssignmentRepo assignmentRepo = new StubAssignmentRepo(Optional.of(
                new ReviewAssignmentRecord(1L, "7", "ref@cms.com", "ed@cms.com", "accepted", false)
        ));
        StubReviewRepo reviewRepo = new StubReviewRepo();
        StubNotifier notifier = new StubNotifier();
        ReviewSubmissionService service = new ReviewSubmissionService(
                assignmentRepo,
                reviewRepo,
                responses -> List.of(),
                new ReviewAuthorizationService(),
                notifier
        );

        ReviewSubmissionResult result = service.submitReview(1L, "ref@cms.com",
                Map.of("score", "8", "recommendation", "accept", "comments", "ok"));

        assertEquals(201, result.getStatusCode());
        assertEquals(1L, result.getReviewId());
        assertTrue(assignmentRepo.markSubmittedCalled);
        assertEquals("review submitted", notifier.lastMessage);
    }

    @Test
    void handlesErrorsForAuthorizationValidationAndStorage() {
        StubReviewRepo okReviewRepo = new StubReviewRepo();
        StubNotifier notifier = new StubNotifier();
        ReviewSubmissionService missingAssignment = new ReviewSubmissionService(
                new StubAssignmentRepo(Optional.empty()),
                okReviewRepo,
                responses -> List.of(),
                new ReviewAuthorizationService(),
                notifier
        );
        ReviewSubmissionService unauthorized = new ReviewSubmissionService(
                new StubAssignmentRepo(Optional.of(new ReviewAssignmentRecord(1L, "7", "other@cms.com", "ed@cms.com", "accepted", false))),
                okReviewRepo,
                responses -> List.of(),
                new ReviewAuthorizationService(),
                notifier
        );
        ReviewSubmissionService validationError = new ReviewSubmissionService(
                new StubAssignmentRepo(Optional.of(new ReviewAssignmentRecord(1L, "7", "ref@cms.com", "ed@cms.com", "accepted", false))),
                okReviewRepo,
                responses -> List.of("score", "comments"),
                new ReviewAuthorizationService(),
                notifier
        );
        StubReviewRepo failingReviewRepo = new StubReviewRepo();
        failingReviewRepo.failOnSave = true;
        ReviewSubmissionService saveFailure = new ReviewSubmissionService(
                new StubAssignmentRepo(Optional.of(new ReviewAssignmentRecord(1L, "7", "ref@cms.com", "ed@cms.com", "accepted", false))),
                failingReviewRepo,
                responses -> List.of(),
                new ReviewAuthorizationService(),
                notifier
        );
        StubAssignmentRepo cannotMarkSubmitted = new StubAssignmentRepo(Optional.of(
                new ReviewAssignmentRecord(1L, "7", "ref@cms.com", "ed@cms.com", "accepted", false)
        ));
        cannotMarkSubmitted.markSubmittedResult = false;
        ReviewSubmissionService markFailure = new ReviewSubmissionService(
                cannotMarkSubmitted,
                okReviewRepo,
                responses -> List.of(),
                new ReviewAuthorizationService(),
                notifier
        );
        StubAssignmentRepo failingAssignmentLookup = new StubAssignmentRepo(Optional.empty());
        failingAssignmentLookup.failLookup = true;
        ReviewSubmissionService lookupFailure = new ReviewSubmissionService(
                failingAssignmentLookup,
                okReviewRepo,
                responses -> List.of(),
                new ReviewAuthorizationService(),
                notifier
        );

        assertEquals(403, missingAssignment.submitReview(1L, "ref@cms.com", Map.of()).getStatusCode());
        assertEquals(403, unauthorized.submitReview(1L, "ref@cms.com", Map.of()).getStatusCode());
        ReviewSubmissionResult validation = validationError.submitReview(1L, "ref@cms.com", Map.of());
        assertEquals(400, validation.getStatusCode());
        assertEquals(List.of("score", "comments"), validation.getFields());
        assertEquals(500, saveFailure.submitReview(1L, "ref@cms.com", Map.of("score", "8", "recommendation", "a", "comments", "c")).getStatusCode());
        assertEquals(500, markFailure.submitReview(1L, "ref@cms.com", Map.of("score", "8", "recommendation", "a", "comments", "c")).getStatusCode());
        assertEquals(500, lookupFailure.submitReview(1L, "ref@cms.com", Map.of()).getStatusCode());
    }

    private static class StubAssignmentRepo implements ReviewAssignmentRepository {
        private final Optional<ReviewAssignmentRecord> assignment;
        private boolean failLookup;
        private boolean markSubmittedCalled;
        private boolean markSubmittedResult = true;

        private StubAssignmentRepo(Optional<ReviewAssignmentRecord> assignment) {
            this.assignment = assignment;
        }

        @Override
        public Optional<ReviewAssignmentRecord> findByAssignmentId(long assignmentId) {
            if (failLookup) {
                throw new IllegalStateException("fail");
            }
            return assignment;
        }

        @Override
        public boolean markSubmitted(long assignmentId) {
            markSubmittedCalled = true;
            return markSubmittedResult;
        }
    }

    private static class StubReviewRepo implements ReviewRepository {
        private boolean failOnSave;

        @Override
        public Review save(Review review) {
            if (failOnSave) {
                throw new IllegalStateException("fail");
            }
            return new Review(1L, review.getAssignmentId(), Instant.now(), review.getStatus(), review.getResponses());
        }

        @Override
        public Optional<Review> findByAssignmentId(long assignmentId) {
            return Optional.empty();
        }

        @Override
        public long countAll() {
            return 0;
        }
    }

    private static class StubNotifier implements EditorNotificationService {
        private String lastEditor;
        private String lastMessage;

        @Override
        public void notifyEditor(String editorEmail, String message) {
            this.lastEditor = editorEmail;
            this.lastMessage = message;
        }
    }
}
