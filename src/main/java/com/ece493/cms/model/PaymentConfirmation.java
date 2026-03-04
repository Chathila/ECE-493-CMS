package com.ece493.cms.model;

import java.time.Instant;

public class PaymentConfirmation {
    private final String paymentId;
    private final String attendeeEmail;
    private final double amount;
    private final Instant confirmedAt;

    public PaymentConfirmation(String paymentId, String attendeeEmail, double amount, Instant confirmedAt) {
        this.paymentId = paymentId;
        this.attendeeEmail = attendeeEmail;
        this.amount = amount;
        this.confirmedAt = confirmedAt;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getAttendeeEmail() {
        return attendeeEmail;
    }

    public double getAmount() {
        return amount;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }
}
