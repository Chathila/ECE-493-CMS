package com.ece493.cms.service;

import com.ece493.cms.model.ConfirmationTicket;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTicketRepository implements TicketRepository {
    private final Map<String, ConfirmationTicket> tickets = new LinkedHashMap<>();
    private long sequence = 1L;
    private boolean failOnSave;
    private boolean failOnRead;

    @Override
    public ConfirmationTicket save(String attendeeEmail, String paymentId, String content) {
        if (failOnSave) {
            throw new IllegalStateException("Ticket persistence failed");
        }
        String ticketId = String.valueOf(sequence++);
        ConfirmationTicket ticket = new ConfirmationTicket(ticketId, attendeeEmail, paymentId, Instant.now(), content);
        tickets.put(ticketId, ticket);
        return ticket;
    }

    @Override
    public Optional<ConfirmationTicket> findById(String ticketId) {
        if (failOnRead) {
            throw new IllegalStateException("Ticket retrieval failed");
        }
        return Optional.ofNullable(tickets.get(ticketId));
    }

    public void setFailOnSave(boolean failOnSave) {
        this.failOnSave = failOnSave;
    }

    public void setFailOnRead(boolean failOnRead) {
        this.failOnRead = failOnRead;
    }

    public int count() {
        return tickets.size();
    }
}
