package com.ece493.cms.model;

import java.time.Instant;

public class ReviewInvitation {
    private final long invitationId;
    private final String assignmentId;
    private final String editorEmail;
    private final String refereeEmail;
    private final String paperId;
    private final String status;
    private final String content;
    private final Instant sentAt;
    private final Instant expiresAt;

    public ReviewInvitation(
            long invitationId,
            String assignmentId,
            String editorEmail,
            String refereeEmail,
            String paperId,
            String status,
            String content,
            Instant sentAt,
            Instant expiresAt
    ) {
        this.invitationId = invitationId;
        this.assignmentId = assignmentId;
        this.editorEmail = editorEmail;
        this.refereeEmail = refereeEmail;
        this.paperId = paperId;
        this.status = status;
        this.content = content;
        this.sentAt = sentAt;
        this.expiresAt = expiresAt;
    }

    public long getInvitationId() {
        return invitationId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public String getEditorEmail() {
        return editorEmail;
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

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired(Instant now) {
        return expiresAt != null && (expiresAt.equals(now) || expiresAt.isBefore(now));
    }
}
