package com.ece493.cms.service;

import com.ece493.cms.model.ConfirmationTicket;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTicketDeliveryService implements TicketDeliveryService {
    private boolean failEmail;
    private boolean failInSystem;
    private final List<String> emailDeliveries = new ArrayList<>();
    private final List<String> inSystemDeliveries = new ArrayList<>();

    @Override
    public TicketDeliveryAttemptResult deliver(ConfirmationTicket ticket) {
        boolean emailDelivered = !failEmail;
        boolean inSystemDelivered = !failInSystem;
        if (emailDelivered) {
            emailDeliveries.add(ticket.getTicketId());
        }
        if (inSystemDelivered) {
            inSystemDeliveries.add(ticket.getTicketId());
        }
        return new TicketDeliveryAttemptResult(emailDelivered, inSystemDelivered);
    }

    public void setFailEmail(boolean failEmail) {
        this.failEmail = failEmail;
    }

    public void setFailInSystem(boolean failInSystem) {
        this.failInSystem = failInSystem;
    }

    public List<String> getEmailDeliveries() {
        return List.copyOf(emailDeliveries);
    }

    public List<String> getInSystemDeliveries() {
        return List.copyOf(inSystemDeliveries);
    }
}
