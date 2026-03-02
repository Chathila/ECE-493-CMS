package com.ece493.cms.service;

import com.ece493.cms.model.NotificationDeliveryFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryNotificationFailureRepository implements NotificationFailureRepository {
    private final AtomicLong idSequence = new AtomicLong(1L);
    private final List<NotificationDeliveryFailure> failures = new ArrayList<>();

    @Override
    public NotificationDeliveryFailure save(NotificationDeliveryFailure failure) {
        NotificationDeliveryFailure stored = new NotificationDeliveryFailure(
                idSequence.getAndIncrement(),
                failure.getDecisionId(),
                failure.getChannel(),
                failure.getFailedAt(),
                failure.getMessage()
        );
        failures.add(stored);
        return stored;
    }

    @Override
    public List<NotificationDeliveryFailure> findAll() {
        return List.copyOf(failures);
    }
}
