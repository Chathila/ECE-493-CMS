package com.ece493.cms.service;

public class TicketDeliveryAttemptResult {
    private final boolean emailDelivered;
    private final boolean inSystemDelivered;

    public TicketDeliveryAttemptResult(boolean emailDelivered, boolean inSystemDelivered) {
        this.emailDelivered = emailDelivered;
        this.inSystemDelivered = inSystemDelivered;
    }

    public boolean isEmailDelivered() {
        return emailDelivered;
    }

    public boolean isInSystemDelivered() {
        return inSystemDelivered;
    }
}
