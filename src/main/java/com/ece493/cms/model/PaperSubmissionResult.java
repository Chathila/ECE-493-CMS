package com.ece493.cms.model;

public class PaperSubmissionResult {
    private final int statusCode;
    private final String message;
    private final String redirectLocation;

    private PaperSubmissionResult(int statusCode, String message, String redirectLocation) {
        this.statusCode = statusCode;
        this.message = message;
        this.redirectLocation = redirectLocation;
    }

    public static PaperSubmissionResult success(String message, String redirectLocation) {
        return new PaperSubmissionResult(200, message, redirectLocation);
    }

    public static PaperSubmissionResult error(int statusCode, String message) {
        return new PaperSubmissionResult(statusCode, message, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getRedirectLocation() {
        return redirectLocation;
    }
}
