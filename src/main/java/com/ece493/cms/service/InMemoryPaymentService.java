package com.ece493.cms.service;

public class InMemoryPaymentService implements PaymentService {
    private PaymentServiceDecision nextDecision = PaymentServiceDecision.APPROVED;

    @Override
    public PaymentServiceDecision processPayment(String registrationType, String paymentDetailsPayload) {
        return nextDecision;
    }

    public void setNextDecision(PaymentServiceDecision nextDecision) {
        this.nextDecision = nextDecision == null ? PaymentServiceDecision.APPROVED : nextDecision;
    }
}
