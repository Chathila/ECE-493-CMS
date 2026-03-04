package com.ece493.cms.model;

import java.time.Instant;

public class TicketDeliveryFailure {
    private final String failureId;
    private final String ticketId;
    private final String channel;
    private final Instant failedAt;
    private final String message;

    public TicketDeliveryFailure(String failureId, String ticketId, String channel, Instant failedAt, String message) {
        this.failureId = failureId;
        this.ticketId = ticketId;
        this.channel = channel;
        this.failedAt = failedAt;
        this.message = message;
    }

    public String getFailureId() {
        return failureId;
    }

    public String getTicketId() {
        return ticketId;
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
