package com.ece493.cms.unit;

import com.ece493.cms.model.PaymentProcessingResult;
import com.ece493.cms.service.InMemoryPaymentRepository;
import com.ece493.cms.service.InMemoryPaymentService;
import com.ece493.cms.service.PaymentProcessingService;
import com.ece493.cms.service.PaymentServiceDecision;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentProcessingServiceTest {
    @Test
    void handlesValidationProviderAndPersistenceOutcomes() {
        InMemoryPaymentRepository repository = new InMemoryPaymentRepository();
        InMemoryPaymentService paymentService = new InMemoryPaymentService();
        PaymentProcessingService service = new PaymentProcessingService(repository, paymentService);

        assertEquals(400, service.process(null, "student", "{\"method\":\"card\"}").getStatusCode());
        assertEquals(400, service.process("a@cms.com", "", "{\"method\":\"card\"}").getStatusCode());
        assertEquals(400, service.process("a@cms.com", "student", " ").getStatusCode());
        assertEquals(400, service.process("a@cms.com", "student", "{}").getStatusCode());
        assertEquals(400, service.process("a@cms.com", "student", "{oops}").getStatusCode());

        paymentService.setNextDecision(PaymentServiceDecision.DECLINED);
        PaymentProcessingResult declined = service.process("a@cms.com", "student", "{\"method\":\"card\"}");
        assertEquals(402, declined.getStatusCode());

        paymentService.setNextDecision(PaymentServiceDecision.UNAVAILABLE);
        PaymentProcessingResult unavailable = service.process("a@cms.com", "student", "{\"method\":\"card\"}");
        assertEquals(503, unavailable.getStatusCode());

        paymentService.setNextDecision(PaymentServiceDecision.APPROVED);
        PaymentProcessingResult success = service.process("a@cms.com", "student", "{\"method\":\"card\"}");
        assertEquals(200, success.getStatusCode());
        assertEquals("confirmed", success.getStatus());
        assertEquals(1, repository.count());

        repository.setFailOnSave(true);
        assertEquals(503, service.process("a@cms.com", "student", "{\"method\":\"card\"}").getStatusCode());
    }
}
