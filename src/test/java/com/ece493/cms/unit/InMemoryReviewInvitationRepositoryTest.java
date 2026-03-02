package com.ece493.cms.unit;

import com.ece493.cms.model.ReviewInvitation;
import com.ece493.cms.service.InMemoryReviewInvitationRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryReviewInvitationRepositoryTest {
    @Test
    void findsByIdAndUpdatesStatusWhenPresent() {
        InMemoryReviewInvitationRepository repository = new InMemoryReviewInvitationRepository();
        ReviewInvitation stored = repository.save(new ReviewInvitation(
                0L, "a1", "editor@cms.com", "ref@cms.com", "p1", "open", "c", Instant.now(), Instant.now().plusSeconds(100)
        ));

        assertTrue(repository.findByInvitationId(stored.getInvitationId()).isPresent());
        assertTrue(repository.updateStatusAndExpiry(stored.getInvitationId(), "responded", Instant.now().plusSeconds(50)));
        assertTrue(repository.findByInvitationId(stored.getInvitationId()).orElseThrow().getStatus().equals("responded"));
    }

    @Test
    void updateStatusReturnsFalseWhenMissingInvitation() {
        InMemoryReviewInvitationRepository repository = new InMemoryReviewInvitationRepository();
        repository.save(new ReviewInvitation(
                0L, "a1", "editor@cms.com", "ref@cms.com", "p1", "open", "c", Instant.now(), Instant.now().plusSeconds(100)
        ));

        assertFalse(repository.updateStatusAndExpiry(999L, "expired", Instant.now()));
    }
}
