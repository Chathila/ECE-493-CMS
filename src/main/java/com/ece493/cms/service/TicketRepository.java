package com.ece493.cms.service;

import com.ece493.cms.model.ConfirmationTicket;

import java.util.Optional;

public interface TicketRepository {
    ConfirmationTicket save(String attendeeEmail, String paymentId, String content);

    Optional<ConfirmationTicket> findById(String ticketId);
}
