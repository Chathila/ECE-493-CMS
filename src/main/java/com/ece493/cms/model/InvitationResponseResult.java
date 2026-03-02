package com.ece493.cms.model;

public class InvitationResponseResult {
    private final int statusCode;
    private final String message;
    private final String assignmentStatus;

    private InvitationResponseResult(int statusCode, String message, String assignmentStatus) {
        this.statusCode = statusCode;
        this.message = message;
        this.assignmentStatus = assignmentStatus;
    }

    public static InvitationResponseResult success(String assignmentStatus) {
        return new InvitationResponseResult(200, "Invitation response recorded.", assignmentStatus);
    }

    public static InvitationResponseResult success(String message, String assignmentStatus) {
        return new InvitationResponseResult(200, message, assignmentStatus);
    }

    public static InvitationResponseResult error(int statusCode, String message) {
        return new InvitationResponseResult(statusCode, message, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }
}
