package com.ece493.cms.service;

import com.ece493.cms.model.NotificationDeliveryFailure;

import java.util.List;

public interface NotificationFailureRepository {
    NotificationDeliveryFailure save(NotificationDeliveryFailure failure);

    List<NotificationDeliveryFailure> findAll();
}
