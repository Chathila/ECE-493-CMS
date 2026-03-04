package com.ece493.cms.service;

import com.ece493.cms.model.TicketDeliveryFailure;

import java.util.List;

public interface TicketFailureRepository {
    void save(TicketDeliveryFailure failure);

    List<TicketDeliveryFailure> findByTicketId(String ticketId);
}
