package com.ece493.cms.model;

public class ReviewFormAccessResult {
    private final int statusCode;
    private final String message;
    private final ReviewForm reviewForm;
    private final PaperSubmission paperSubmission;

    private ReviewFormAccessResult(int statusCode, String message, ReviewForm reviewForm, PaperSubmission paperSubmission) {
        this.statusCode = statusCode;
        this.message = message;
        this.reviewForm = reviewForm;
        this.paperSubmission = paperSubmission;
    }

    public static ReviewFormAccessResult success(ReviewForm reviewForm, PaperSubmission paperSubmission) {
        return new ReviewFormAccessResult(200, "Review form loaded.", reviewForm, paperSubmission);
    }

    public static ReviewFormAccessResult error(int statusCode, String message) {
        return new ReviewFormAccessResult(statusCode, message, null, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public ReviewForm getReviewForm() {
        return reviewForm;
    }

    public PaperSubmission getPaperSubmission() {
        return paperSubmission;
    }
}
