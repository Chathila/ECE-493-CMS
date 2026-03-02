package com.ece493.cms.unit;

import com.ece493.cms.model.FinalDecision;
import com.ece493.cms.service.InMemoryFinalDecisionRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryFinalDecisionRepositoryTest {
    @Test
    void savesLooksUpUpdatesAndHandlesFailures() {
        InMemoryFinalDecisionRepository repository = new InMemoryFinalDecisionRepository();
        FinalDecision saved = repository.save(new FinalDecision(
                0L,
                "42",
                "editor@cms.com",
                "author@cms.com",
                "accept",
                Instant.now(),
                "queued"
        ));

        assertEquals(1L, saved.getDecisionId());
        assertTrue(repository.findLatestByPaperId("42").isPresent());
        assertTrue(repository.updateNotificationStatus(saved.getDecisionId(), "sent"));
        assertEquals("sent", repository.findLatestByPaperId("42").orElseThrow().getNotificationStatus());
        assertFalse(repository.updateNotificationStatus(999L, "failed"));
        assertTrue(repository.findLatestByPaperId("none").isEmpty());

        repository.setFailOnSave(true);
        assertThrows(IllegalStateException.class, () -> repository.save(saved));
    }
}
