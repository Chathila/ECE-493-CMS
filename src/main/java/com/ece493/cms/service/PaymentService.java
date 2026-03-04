package com.ece493.cms.service;

public interface PaymentService {
    PaymentServiceDecision processPayment(String registrationType, String paymentDetailsPayload);
}
