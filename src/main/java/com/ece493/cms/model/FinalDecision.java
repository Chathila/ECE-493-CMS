package com.ece493.cms.model;

import java.time.Instant;

public class FinalDecision {
    private final long decisionId;
    private final String paperId;
    private final String editorEmail;
    private final String authorEmail;
    private final String decision;
    private final Instant decidedAt;
    private final String notificationStatus;

    public FinalDecision(
            long decisionId,
            String paperId,
            String editorEmail,
            String authorEmail,
            String decision,
            Instant decidedAt,
            String notificationStatus
    ) {
        this.decisionId = decisionId;
        this.paperId = paperId;
        this.editorEmail = editorEmail;
        this.authorEmail = authorEmail;
        this.decision = decision;
        this.decidedAt = decidedAt;
        this.notificationStatus = notificationStatus;
    }

    public long getDecisionId() {
        return decisionId;
    }

    public String getPaperId() {
        return paperId;
    }

    public String getEditorEmail() {
        return editorEmail;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getDecision() {
        return decision;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }
}
