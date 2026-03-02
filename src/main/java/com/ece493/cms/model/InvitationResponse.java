package com.ece493.cms.model;

import java.time.Instant;

public class InvitationResponse {
    private final long responseId;
    private final long invitationId;
    private final String decision;
    private final Instant respondedAt;

    public InvitationResponse(long responseId, long invitationId, String decision, Instant respondedAt) {
        this.responseId = responseId;
        this.invitationId = invitationId;
        this.decision = decision;
        this.respondedAt = respondedAt;
    }

    public long getResponseId() {
        return responseId;
    }

    public long getInvitationId() {
        return invitationId;
    }

    public String getDecision() {
        return decision;
    }

    public Instant getRespondedAt() {
        return respondedAt;
    }
}
