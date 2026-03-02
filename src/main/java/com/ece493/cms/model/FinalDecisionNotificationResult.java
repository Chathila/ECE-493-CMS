package com.ece493.cms.model;

public class FinalDecisionNotificationResult {
    private final int statusCode;
    private final String message;
    private final String paperId;
    private final String status;

    private FinalDecisionNotificationResult(int statusCode, String message, String paperId, String status) {
        this.statusCode = statusCode;
        this.message = message;
        this.paperId = paperId;
        this.status = status;
    }

    public static FinalDecisionNotificationResult sent(String paperId) {
        return new FinalDecisionNotificationResult(202, "Notification dispatched.", paperId, "sent");
    }

    public static FinalDecisionNotificationResult failed(String paperId) {
        return new FinalDecisionNotificationResult(500, "Notification delivery failed.", paperId, "failed");
    }

    public static FinalDecisionNotificationResult error(int statusCode, String message) {
        return new FinalDecisionNotificationResult(statusCode, message, null, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getPaperId() {
        return paperId;
    }

    public String getStatus() {
        return status;
    }
}
