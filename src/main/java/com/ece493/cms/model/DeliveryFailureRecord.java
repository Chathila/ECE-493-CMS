package com.ece493.cms.model;

import java.time.Instant;

public class DeliveryFailureRecord {
    private final long failureId;
    private final long invitationId;
    private final String reason;
    private final Instant failedAt;

    public DeliveryFailureRecord(long failureId, long invitationId, String reason, Instant failedAt) {
        this.failureId = failureId;
        this.invitationId = invitationId;
        this.reason = reason;
        this.failedAt = failedAt;
    }

    public long getFailureId() {
        return failureId;
    }

    public long getInvitationId() {
        return invitationId;
    }

    public String getReason() {
        return reason;
    }

    public Instant getFailedAt() {
        return failedAt;
    }
}
