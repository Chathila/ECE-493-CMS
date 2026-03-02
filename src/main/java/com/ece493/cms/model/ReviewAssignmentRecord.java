package com.ece493.cms.model;

public class ReviewAssignmentRecord {
    private final long assignmentId;
    private final String paperId;
    private final String refereeEmail;
    private final String editorEmail;
    private final String status;
    private final boolean expired;

    public ReviewAssignmentRecord(
            long assignmentId,
            String paperId,
            String refereeEmail,
            String editorEmail,
            String status,
            boolean expired
    ) {
        this.assignmentId = assignmentId;
        this.paperId = paperId;
        this.refereeEmail = refereeEmail;
        this.editorEmail = editorEmail;
        this.status = status;
        this.expired = expired;
    }

    public long getAssignmentId() {
        return assignmentId;
    }

    public String getPaperId() {
        return paperId;
    }

    public String getRefereeEmail() {
        return refereeEmail;
    }

    public String getEditorEmail() {
        return editorEmail;
    }

    public String getStatus() {
        return status;
    }

    public boolean isExpired() {
        return expired;
    }
}
