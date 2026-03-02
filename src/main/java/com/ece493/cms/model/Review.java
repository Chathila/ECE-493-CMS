package com.ece493.cms.model;

import java.time.Instant;
import java.util.Map;

public class Review {
    private final long reviewId;
    private final long assignmentId;
    private final Instant submittedAt;
    private final String status;
    private final Map<String, String> responses;

    public Review(long reviewId, long assignmentId, Instant submittedAt, String status, Map<String, String> responses) {
        this.reviewId = reviewId;
        this.assignmentId = assignmentId;
        this.submittedAt = submittedAt;
        this.status = status;
        this.responses = responses == null ? Map.of() : Map.copyOf(responses);
    }

    public long getReviewId() {
        return reviewId;
    }

    public long getAssignmentId() {
        return assignmentId;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, String> getResponses() {
        return responses;
    }
}
