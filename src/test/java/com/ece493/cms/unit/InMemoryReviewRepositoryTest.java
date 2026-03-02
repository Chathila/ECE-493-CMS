package com.ece493.cms.unit;

import com.ece493.cms.model.Review;
import com.ece493.cms.service.InMemoryReviewRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryReviewRepositoryTest {
    @Test
    void savesFindsCountsAndHandlesFailures() {
        InMemoryReviewRepository repository = new InMemoryReviewRepository();
        Review stored = repository.save(new Review(0L, 10L, Instant.now(), "submitted", Map.of("score", "8")));

        assertEquals(1L, stored.getReviewId());
        assertTrue(repository.findByAssignmentId(10L).isPresent());
        assertTrue(repository.findByAssignmentId(11L).isEmpty());
        assertEquals(1L, repository.countAll());

        repository.setFailOnSave(true);
        assertThrows(IllegalStateException.class,
                () -> repository.save(new Review(0L, 11L, Instant.now(), "submitted", Map.of())));
    }
}
