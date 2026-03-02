package com.ece493.cms.service;

import com.ece493.cms.model.DeliveryFailureRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryDeliveryFailureRepository implements DeliveryFailureRepository {
    private final AtomicLong idSequence = new AtomicLong(1L);
    private final List<DeliveryFailureRecord> records = new ArrayList<>();

    @Override
    public DeliveryFailureRecord save(DeliveryFailureRecord record) {
        DeliveryFailureRecord stored = new DeliveryFailureRecord(
                idSequence.getAndIncrement(),
                record.getInvitationId(),
                record.getReason(),
                record.getFailedAt()
        );
        records.add(stored);
        return stored;
    }

    @Override
    public List<DeliveryFailureRecord> findAll() {
        return List.copyOf(records);
    }
}
