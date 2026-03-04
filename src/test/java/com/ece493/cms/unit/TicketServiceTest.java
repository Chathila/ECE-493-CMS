package com.ece493.cms.unit;

import com.ece493.cms.model.TicketStatusResult;
import com.ece493.cms.model.TicketViewResult;
import com.ece493.cms.service.InMemoryPaymentRepository;
import com.ece493.cms.service.InMemoryTicketDeliveryService;
import com.ece493.cms.service.InMemoryTicketFailureRepository;
import com.ece493.cms.service.InMemoryTicketRepository;
import com.ece493.cms.service.TicketService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketServiceTest {
    @Test
    void handlesTicketGenerationDeliveryAndViewOutcomes() {
        InMemoryPaymentRepository paymentRepository = new InMemoryPaymentRepository();
        InMemoryTicketRepository ticketRepository = new InMemoryTicketRepository();
        InMemoryTicketFailureRepository failureRepository = new InMemoryTicketFailureRepository();
        InMemoryTicketDeliveryService deliveryService = new InMemoryTicketDeliveryService();
        TicketService service = new TicketService(paymentRepository, ticketRepository, failureRepository, deliveryService);

        assertEquals(400, service.generateAndDeliver(null).getStatusCode());
        assertEquals(404, service.generateAndDeliver("9").getStatusCode());

        String paymentId = paymentRepository.saveConfirmed("attendee@cms.com", "student", 100.0).getPaymentId();
        TicketStatusResult delivered = service.generateAndDeliver(paymentId);
        assertEquals(201, delivered.getStatusCode());
        assertFalse(deliveryService.getEmailDeliveries().isEmpty());
        assertFalse(deliveryService.getInSystemDeliveries().isEmpty());

        String paymentId2 = paymentRepository.saveConfirmed("attendee@cms.com", "author", 200.0).getPaymentId();
        deliveryService.setFailEmail(true);
        TicketStatusResult failed = service.generateAndDeliver(paymentId2);
        assertEquals(500, failed.getStatusCode());
        assertEquals(1, failureRepository.findByTicketId(failed.getTicketId()).size());

        TicketViewResult found = service.viewTicket(failed.getTicketId());
        assertEquals(200, found.getStatusCode());
        assertTrue(found.getContent().contains("Payment ID"));

        assertEquals(400, service.viewTicket(" ").getStatusCode());
        assertEquals(404, service.viewTicket("999").getStatusCode());

        ticketRepository.setFailOnRead(true);
        assertEquals(500, service.viewTicket(failed.getTicketId()).getStatusCode());
    }

    @Test
    void returnsErrorWhenTicketCannotBeStored() {
        InMemoryPaymentRepository paymentRepository = new InMemoryPaymentRepository();
        InMemoryTicketRepository ticketRepository = new InMemoryTicketRepository();
        ticketRepository.setFailOnSave(true);
        TicketService service = new TicketService(
                paymentRepository,
                ticketRepository,
                new InMemoryTicketFailureRepository(),
                new InMemoryTicketDeliveryService()
        );
        String paymentId = paymentRepository.saveConfirmed("attendee@cms.com", "student", 100.0).getPaymentId();
        assertEquals(500, service.generateAndDeliver(paymentId).getStatusCode());
    }
}
