package com.ece493.cms.model;

public class FinalDecisionStatusResult {
    private final int statusCode;
    private final String message;
    private final String paperId;
    private final String decision;
    private final String notificationStatus;

    private FinalDecisionStatusResult(int statusCode, String message, String paperId, String decision, String notificationStatus) {
        this.statusCode = statusCode;
        this.message = message;
        this.paperId = paperId;
        this.decision = decision;
        this.notificationStatus = notificationStatus;
    }

    public static FinalDecisionStatusResult found(String paperId, String decision, String notificationStatus) {
        return new FinalDecisionStatusResult(200, "Decision status returned.", paperId, decision, notificationStatus);
    }

    public static FinalDecisionStatusResult error(int statusCode, String message) {
        return new FinalDecisionStatusResult(statusCode, message, null, null, null);
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

    public String getDecision() {
        return decision;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }
}
