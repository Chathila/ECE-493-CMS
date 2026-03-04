package com.ece493.cms.unit;

import com.ece493.cms.service.InMemoryTicketRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTicketRepositoryTest {
    @Test
    void storesAndReadsTicketsWithFailureModes() {
        InMemoryTicketRepository repository = new InMemoryTicketRepository();
        String ticketId = repository.save("attendee@cms.com", "10", "content").getTicketId();
        assertEquals("1", ticketId);
        assertEquals(1, repository.count());
        assertTrue(repository.findById(ticketId).isPresent());

        repository.setFailOnRead(true);
        assertThrows(IllegalStateException.class, () -> repository.findById(ticketId));

        repository.setFailOnRead(false);
        repository.setFailOnSave(true);
        assertThrows(IllegalStateException.class, () -> repository.save("attendee@cms.com", "11", "content"));
    }
}
