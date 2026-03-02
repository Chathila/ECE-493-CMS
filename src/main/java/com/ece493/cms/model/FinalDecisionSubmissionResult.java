package com.ece493.cms.model;

public class FinalDecisionSubmissionResult {
    private final int statusCode;
    private final String message;
    private final String paperId;
    private final String decision;

    private FinalDecisionSubmissionResult(int statusCode, String message, String paperId, String decision) {
        this.statusCode = statusCode;
        this.message = message;
        this.paperId = paperId;
        this.decision = decision;
    }

    public static FinalDecisionSubmissionResult created(String paperId, String decision) {
        return new FinalDecisionSubmissionResult(201, "Final decision recorded.", paperId, decision);
    }

    public static FinalDecisionSubmissionResult error(int statusCode, String message) {
        return new FinalDecisionSubmissionResult(statusCode, message, null, null);
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
}
