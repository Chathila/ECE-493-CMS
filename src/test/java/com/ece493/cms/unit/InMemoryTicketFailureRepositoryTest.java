package com.ece493.cms.unit;

import com.ece493.cms.model.TicketDeliveryFailure;
import com.ece493.cms.service.InMemoryTicketFailureRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTicketFailureRepositoryTest {
    @Test
    void filtersByTicketIdAndHandlesNullLookup() {
        InMemoryTicketFailureRepository repository = new InMemoryTicketFailureRepository();
        repository.save(new TicketDeliveryFailure("1", "10", "email", Instant.now(), "failed"));
        repository.save(new TicketDeliveryFailure("2", "20", "in-system", Instant.now(), "failed"));

        assertEquals(1, repository.findByTicketId("10").size());
        assertEquals(0, repository.findByTicketId("99").size());
        assertEquals(0, repository.findByTicketId(null).size());
    }
}
