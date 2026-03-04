package com.ece493.cms.service;

import com.ece493.cms.model.TicketDeliveryFailure;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTicketFailureRepository implements TicketFailureRepository {
    private final List<TicketDeliveryFailure> failures = new ArrayList<>();

    @Override
    public void save(TicketDeliveryFailure failure) {
        failures.add(failure);
    }

    @Override
    public List<TicketDeliveryFailure> findByTicketId(String ticketId) {
        return failures.stream()
                .filter(failure -> ticketId != null && ticketId.equals(failure.getTicketId()))
                .toList();
    }
}
