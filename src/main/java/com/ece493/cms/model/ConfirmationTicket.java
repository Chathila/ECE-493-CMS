package com.ece493.cms.model;

import java.time.Instant;

public class ConfirmationTicket {
    private final String ticketId;
    private final String attendeeEmail;
    private final String paymentId;
    private final Instant issuedAt;
    private final String content;

    public ConfirmationTicket(String ticketId, String attendeeEmail, String paymentId, Instant issuedAt, String content) {
        this.ticketId = ticketId;
        this.attendeeEmail = attendeeEmail;
        this.paymentId = paymentId;
        this.issuedAt = issuedAt;
        this.content = content;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getAttendeeEmail() {
        return attendeeEmail;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public String getContent() {
        return content;
    }
}
