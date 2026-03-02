package com.ece493.cms.model;

import java.time.Instant;

public class ReviewInvitation {
    private final long invitationId;
    private final String assignmentId;
    private final String refereeEmail;
    private final String paperId;
    private final String status;
    private final String content;
    private final Instant sentAt;

    public ReviewInvitation(
            long invitationId,
            String assignmentId,
            String refereeEmail,
            String paperId,
            String status,
            String content,
            Instant sentAt
    ) {
        this.invitationId = invitationId;
        this.assignmentId = assignmentId;
        this.refereeEmail = refereeEmail;
        this.paperId = paperId;
        this.status = status;
        this.content = content;
        this.sentAt = sentAt;
    }

    public long getInvitationId() {
        return invitationId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public String getRefereeEmail() {
        return refereeEmail;
    }

    public String getPaperId() {
        return paperId;
    }

    public String getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}
