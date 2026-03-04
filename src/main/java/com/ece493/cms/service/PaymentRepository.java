package com.ece493.cms.service;

import com.ece493.cms.model.Payment;

import java.util.Optional;

public interface PaymentRepository {
    Payment saveConfirmed(String attendeeEmail, String registrationType, double amount);

    Optional<Payment> findById(String paymentId);
}
