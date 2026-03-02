package com.ece493.cms.unit;

import com.ece493.cms.service.InMemoryReviewFormRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryReviewFormRepositoryTest {
    @Test
    void returnsDefaultFormAndSupportsAvailabilityToggle() {
        InMemoryReviewFormRepository repository = new InMemoryReviewFormRepository();

        assertTrue(repository.findByAssignmentId(1L).isPresent());
        assertEquals("review-form-default", repository.findByAssignmentId(1L).orElseThrow().getFormId());
        assertTrue(repository.findByAssignmentId(0L).isEmpty());

        repository.setAvailable(false);
        assertThrows(IllegalStateException.class, () -> repository.findByAssignmentId(1L));
    }
}
