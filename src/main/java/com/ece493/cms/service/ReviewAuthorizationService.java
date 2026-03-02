package com.ece493.cms.service;

import com.ece493.cms.model.ReviewAssignmentRecord;

public class ReviewAuthorizationService {
    public boolean canAccessForm(String refereeEmail, ReviewAssignmentRecord assignment) {
        if (isBlank(refereeEmail) || assignment == null || assignment.isExpired()) {
            return false;
        }
        if (!refereeEmail.equalsIgnoreCase(assignment.getRefereeEmail())) {
            return false;
        }
        String status = assignment.getStatus();
        return "accepted".equals(status) || "approved".equals(status) || "submitted".equals(status);
    }

    public boolean canSubmitReview(String refereeEmail, ReviewAssignmentRecord assignment) {
        if (isBlank(refereeEmail) || assignment == null || assignment.isExpired()) {
            return false;
        }
        if (!refereeEmail.equalsIgnoreCase(assignment.getRefereeEmail())) {
            return false;
        }
        String status = assignment.getStatus();
        return "accepted".equals(status) || "approved".equals(status);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
