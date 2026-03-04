package com.ece493.cms.model;

import java.time.Instant;

public class Payment {
    private final String paymentId;
    private final String attendeeEmail;
    private final String registrationType;
    private final double amount;
    private final String status;
    private final Instant confirmedAt;

    public Payment(
            String paymentId,
            String attendeeEmail,
            String registrationType,
            double amount,
            String status,
            Instant confirmedAt
    ) {
        this.paymentId = paymentId;
        this.attendeeEmail = attendeeEmail;
        this.registrationType = registrationType;
        this.amount = amount;
        this.status = status;
        this.confirmedAt = confirmedAt;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getAttendeeEmail() {
        return attendeeEmail;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }
}
