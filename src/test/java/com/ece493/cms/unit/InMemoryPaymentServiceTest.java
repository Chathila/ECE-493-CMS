package com.ece493.cms.unit;

import com.ece493.cms.service.InMemoryPaymentService;
import com.ece493.cms.service.PaymentServiceDecision;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryPaymentServiceTest {
    @Test
    void supportsConfigurableDecisions() {
        InMemoryPaymentService service = new InMemoryPaymentService();

        assertEquals(PaymentServiceDecision.APPROVED, service.processPayment("student", "{}"));
        service.setNextDecision(PaymentServiceDecision.DECLINED);
        assertEquals(PaymentServiceDecision.DECLINED, service.processPayment("student", "{}"));
        service.setNextDecision(null);
        assertEquals(PaymentServiceDecision.APPROVED, service.processPayment("student", "{}"));
    }
}
