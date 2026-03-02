package com.ece493.cms.service;

import com.ece493.cms.model.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryReviewRepository implements ReviewRepository {
    private final AtomicLong sequence = new AtomicLong(1L);
    private final List<Review> reviews = new ArrayList<>();
    private volatile boolean failOnSave;

    @Override
    public Review save(Review review) {
        if (failOnSave) {
            throw new IllegalStateException("Review storage unavailable.");
        }
        Review stored = new Review(
                sequence.getAndIncrement(),
                review.getAssignmentId(),
                review.getSubmittedAt(),
                review.getStatus(),
                review.getResponses()
        );
        reviews.add(stored);
        return stored;
    }

    @Override
    public Optional<Review> findByAssignmentId(long assignmentId) {
        return reviews.stream()
                .filter(review -> review.getAssignmentId() == assignmentId)
                .findFirst();
    }

    @Override
    public long countAll() {
        return reviews.size();
    }

    public void setFailOnSave(boolean failOnSave) {
        this.failOnSave = failOnSave;
    }
}
