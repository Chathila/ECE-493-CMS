package com.ece493.cms.model;

public class RefereeAssignmentResult {
    private final int statusCode;
    private final String message;
    private final String warning;

    private RefereeAssignmentResult(int statusCode, String message, String warning) {
        this.statusCode = statusCode;
        this.message = message;
        this.warning = warning;
    }

    public static RefereeAssignmentResult success(String message) {
        return new RefereeAssignmentResult(200, message, null);
    }

    public static RefereeAssignmentResult warning(int statusCode, String message, String warning) {
        return new RefereeAssignmentResult(statusCode, message, warning);
    }

    public static RefereeAssignmentResult error(int statusCode, String message) {
        return new RefereeAssignmentResult(statusCode, message, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getWarning() {
        return warning;
    }
}
