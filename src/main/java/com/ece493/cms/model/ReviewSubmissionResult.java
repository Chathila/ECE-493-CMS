package com.ece493.cms.model;

import java.util.List;

public class ReviewSubmissionResult {
    private final int statusCode;
    private final String message;
    private final Long reviewId;
    private final String status;
    private final List<String> fields;

    private ReviewSubmissionResult(int statusCode, String message, Long reviewId, String status, List<String> fields) {
        this.statusCode = statusCode;
        this.message = message;
        this.reviewId = reviewId;
        this.status = status;
        this.fields = fields == null ? List.of() : List.copyOf(fields);
    }

    public static ReviewSubmissionResult created(long reviewId) {
        return new ReviewSubmissionResult(201, "Review submitted.", reviewId, "submitted", List.of());
    }

    public static ReviewSubmissionResult validationError(List<String> fields) {
        return new ReviewSubmissionResult(400, "Review validation failed.", null, null, fields);
    }

    public static ReviewSubmissionResult error(int statusCode, String message) {
        return new ReviewSubmissionResult(statusCode, message, null, null, List.of());
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getFields() {
        return fields;
    }
}
