package com.ece493.cms.service;

import com.ece493.cms.model.Payment;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryPaymentRepository implements PaymentRepository {
    private final Map<String, Payment> payments = new LinkedHashMap<>();
    private long sequence = 1L;
    private boolean failOnSave;

    @Override
    public Payment saveConfirmed(String attendeeEmail, String registrationType, double amount) {
        if (failOnSave) {
            throw new IllegalStateException("Payment persistence failed");
        }
        String paymentId = String.valueOf(sequence++);
        Payment payment = new Payment(paymentId, attendeeEmail, registrationType, amount, "confirmed", Instant.now());
        payments.put(paymentId, payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    public void setFailOnSave(boolean failOnSave) {
        this.failOnSave = failOnSave;
    }

    public int count() {
        return payments.size();
    }
}
