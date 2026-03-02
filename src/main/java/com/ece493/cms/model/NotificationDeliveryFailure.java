package com.ece493.cms.model;

import java.time.Instant;

public class NotificationDeliveryFailure {
    private final long failureId;
    private final long decisionId;
    private final String channel;
    private final Instant failedAt;
    private final String message;

    public NotificationDeliveryFailure(long failureId, long decisionId, String channel, Instant failedAt, String message) {
        this.failureId = failureId;
        this.decisionId = decisionId;
        this.channel = channel;
        this.failedAt = failedAt;
        this.message = message;
    }

    public long getFailureId() {
        return failureId;
    }

    public long getDecisionId() {
        return decisionId;
    }

    public String getChannel() {
        return channel;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public String getMessage() {
        return message;
    }
}
