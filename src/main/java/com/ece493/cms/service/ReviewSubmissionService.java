package com.ece493.cms.service;

import com.ece493.cms.model.Review;
import com.ece493.cms.model.ReviewAssignmentRecord;
import com.ece493.cms.model.ReviewSubmissionResult;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReviewSubmissionService {
    private final ReviewAssignmentRepository reviewAssignmentRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewValidationService reviewValidationService;
    private final ReviewAuthorizationService reviewAuthorizationService;
    private final EditorNotificationService editorNotificationService;

    public ReviewSubmissionService(
            ReviewAssignmentRepository reviewAssignmentRepository,
            ReviewRepository reviewRepository,
            ReviewValidationService reviewValidationService,
            ReviewAuthorizationService reviewAuthorizationService,
            EditorNotificationService editorNotificationService
    ) {
        this.reviewAssignmentRepository = reviewAssignmentRepository;
        this.reviewRepository = reviewRepository;
        this.reviewValidationService = reviewValidationService;
        this.reviewAuthorizationService = reviewAuthorizationService;
        this.editorNotificationService = editorNotificationService;
    }

    public ReviewSubmissionResult submitReview(long assignmentId, String refereeEmail, Map<String, String> responses) {
        Optional<ReviewAssignmentRecord> assignmentOptional;
        try {
            assignmentOptional = reviewAssignmentRepository.findByAssignmentId(assignmentId);
        } catch (IllegalStateException e) {
            return ReviewSubmissionResult.error(500, "Review submission failed. Please retry later.");
        }
        if (assignmentOptional.isEmpty()) {
            return ReviewSubmissionResult.error(403, "Access denied.");
        }

        ReviewAssignmentRecord assignment = assignmentOptional.get();
        if (!reviewAuthorizationService.canSubmitReview(refereeEmail, assignment)) {
            return ReviewSubmissionResult.error(403, "Access denied.");
        }

        List<String> invalidFields = reviewValidationService.validate(responses);
        if (!invalidFields.isEmpty()) {
            return ReviewSubmissionResult.validationError(invalidFields);
        }

        Review savedReview;
        try {
            savedReview = reviewRepository.save(new Review(
                    0L,
                    assignmentId,
                    Instant.now(),
                    "submitted",
                    responses
            ));
        } catch (IllegalStateException e) {
            return ReviewSubmissionResult.error(500, "Review submission failed. Please retry later.");
        }

        if (!reviewAssignmentRepository.markSubmitted(assignmentId)) {
            return ReviewSubmissionResult.error(500, "Review submission failed. Please retry later.");
        }

        editorNotificationService.notifyEditor(assignment.getEditorEmail(), "review submitted");
        return ReviewSubmissionResult.created(savedReview.getReviewId());
    }
}
