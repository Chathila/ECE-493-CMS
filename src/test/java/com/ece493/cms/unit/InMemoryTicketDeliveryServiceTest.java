package com.ece493.cms.unit;

import com.ece493.cms.model.ConfirmationTicket;
import com.ece493.cms.service.InMemoryTicketDeliveryService;
import com.ece493.cms.service.TicketDeliveryAttemptResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InMemoryTicketDeliveryServiceTest {
    @Test
    void tracksEmailAndInSystemDeliveryAttempts() {
        InMemoryTicketDeliveryService service = new InMemoryTicketDeliveryService();
        ConfirmationTicket ticket = new ConfirmationTicket("1", "attendee@cms.com", "5", Instant.now(), "content");

        TicketDeliveryAttemptResult ok = service.deliver(ticket);
        assertEquals(true, ok.isEmailDelivered());
        assertEquals(true, ok.isInSystemDelivered());
        assertFalse(service.getEmailDeliveries().isEmpty());
        assertFalse(service.getInSystemDeliveries().isEmpty());

        service.setFailEmail(true);
        service.setFailInSystem(true);
        TicketDeliveryAttemptResult fail = service.deliver(ticket);
        assertEquals(false, fail.isEmailDelivered());
        assertEquals(false, fail.isInSystemDelivered());
    }
}
