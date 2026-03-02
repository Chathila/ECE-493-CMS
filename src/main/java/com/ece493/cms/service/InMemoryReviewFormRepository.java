package com.ece493.cms.service;

import com.ece493.cms.model.ReviewForm;

import java.util.Optional;

public class InMemoryReviewFormRepository implements ReviewFormRepository {
    private volatile boolean available = true;

    @Override
    public Optional<ReviewForm> findByAssignmentId(long assignmentId) {
        if (!available) {
            throw new IllegalStateException("Review form storage unavailable.");
        }
        if (assignmentId <= 0) {
            return Optional.empty();
        }
        return Optional.of(new ReviewForm(
                "review-form-default",
                "v1",
                java.util.List.of("score", "recommendation", "comments")
        ));
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
