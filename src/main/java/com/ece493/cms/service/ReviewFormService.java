package com.ece493.cms.service;

import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.model.ReviewAssignmentRecord;
import com.ece493.cms.model.ReviewForm;
import com.ece493.cms.model.ReviewFormAccessResult;
import com.ece493.cms.repository.PaperSubmissionRepository;

import java.util.Optional;

public class ReviewFormService {
    private final ReviewAssignmentRepository reviewAssignmentRepository;
    private final ReviewFormRepository reviewFormRepository;
    private final PaperSubmissionRepository paperSubmissionRepository;
    private final ReviewAuthorizationService reviewAuthorizationService;

    public ReviewFormService(
            ReviewAssignmentRepository reviewAssignmentRepository,
            ReviewFormRepository reviewFormRepository,
            PaperSubmissionRepository paperSubmissionRepository,
            ReviewAuthorizationService reviewAuthorizationService
    ) {
        this.reviewAssignmentRepository = reviewAssignmentRepository;
        this.reviewFormRepository = reviewFormRepository;
        this.paperSubmissionRepository = paperSubmissionRepository;
        this.reviewAuthorizationService = reviewAuthorizationService;
    }

    public ReviewFormAccessResult accessReviewForm(long assignmentId, String refereeEmail) {
        Optional<ReviewAssignmentRecord> assignmentOptional;
        try {
            assignmentOptional = reviewAssignmentRepository.findByAssignmentId(assignmentId);
        } catch (IllegalStateException e) {
            return ReviewFormAccessResult.error(500, "Review form unavailable.");
        }
        if (assignmentOptional.isEmpty()) {
            return ReviewFormAccessResult.error(403, "Access denied.");
        }

        ReviewAssignmentRecord assignment = assignmentOptional.get();
        if (!reviewAuthorizationService.canAccessForm(refereeEmail, assignment)) {
            return ReviewFormAccessResult.error(403, "Access denied.");
        }

        Optional<ReviewForm> formOptional;
        try {
            formOptional = reviewFormRepository.findByAssignmentId(assignmentId);
        } catch (IllegalStateException e) {
            return ReviewFormAccessResult.error(500, "Review form unavailable.");
        }
        if (formOptional.isEmpty()) {
            return ReviewFormAccessResult.error(404, "Review form unavailable.");
        }

        long paperId;
        try {
            paperId = Long.parseLong(assignment.getPaperId());
        } catch (NumberFormatException e) {
            return ReviewFormAccessResult.error(404, "Review form unavailable.");
        }

        Optional<PaperSubmission> paperOptional;
        try {
            paperOptional = paperSubmissionRepository.findBySubmissionId(paperId);
        } catch (IllegalStateException e) {
            return ReviewFormAccessResult.error(500, "Review form unavailable.");
        }
        if (paperOptional.isEmpty()) {
            return ReviewFormAccessResult.error(404, "Review form unavailable.");
        }

        return ReviewFormAccessResult.success(formOptional.get(), paperOptional.get());
    }
}
