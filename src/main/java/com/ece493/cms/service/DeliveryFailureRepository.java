package com.ece493.cms.service;

import com.ece493.cms.model.DeliveryFailureRecord;

import java.util.List;

public interface DeliveryFailureRepository {
    DeliveryFailureRecord save(DeliveryFailureRecord record);

    List<DeliveryFailureRecord> findAll();
}
