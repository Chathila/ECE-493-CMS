package com.ece493.cms.model;

import java.time.Instant;

public class RefereeAssignment {
    private final long assignmentId;
    private final String paperId;
    private final String refereeEmail;
    private final Instant assignedAt;

    public RefereeAssignment(long assignmentId, String paperId, String refereeEmail, Instant assignedAt) {
        this.assignmentId = assignmentId;
        this.paperId = paperId;
        this.refereeEmail = refereeEmail;
        this.assignedAt = assignedAt;
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

    public Instant getAssignedAt() {
        return assignedAt;
    }
}
