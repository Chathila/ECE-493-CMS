package com.ece493.cms.service;

import com.ece493.cms.model.ConfirmationTicket;

public interface TicketDeliveryService {
    TicketDeliveryAttemptResult deliver(ConfirmationTicket ticket);
}
