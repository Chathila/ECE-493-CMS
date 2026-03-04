package com.ece493.cms.service;

import com.ece493.cms.model.Payment;
import com.ece493.cms.model.PaymentProcessingResult;

public class PaymentProcessingService {
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public PaymentProcessingService(PaymentRepository paymentRepository, PaymentService paymentService) {
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }

    public PaymentProcessingResult process(String attendeeEmail, String registrationType, String paymentDetailsPayload) {
        if (isBlank(attendeeEmail)) {
            return PaymentProcessingResult.invalidDetails();
        }
        if (isBlank(registrationType) || isBlank(paymentDetailsPayload) || invalidDetailsObject(paymentDetailsPayload)) {
            return PaymentProcessingResult.invalidDetails();
        }

        PaymentServiceDecision decision = paymentService.processPayment(registrationType, paymentDetailsPayload);
        if (decision == PaymentServiceDecision.DECLINED) {
            return PaymentProcessingResult.declined();
        }
        if (decision == PaymentServiceDecision.UNAVAILABLE) {
            return PaymentProcessingResult.unavailable();
        }

        try {
            Payment payment = paymentRepository.saveConfirmed(attendeeEmail, registrationType, 0.0);
            return PaymentProcessingResult.success(payment.getPaymentId());
        } catch (IllegalStateException e) {
            return PaymentProcessingResult.unavailable();
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean invalidDetailsObject(String paymentDetailsPayload) {
        String normalized = paymentDetailsPayload.replaceAll("\\s+", "");
        return "{}".equals(normalized) || !normalized.contains(":");
    }
}
