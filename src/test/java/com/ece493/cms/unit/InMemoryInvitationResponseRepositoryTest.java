package com.ece493.cms.unit;

import com.ece493.cms.model.InvitationResponse;
import com.ece493.cms.service.InMemoryInvitationResponseRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryInvitationResponseRepositoryTest {
    @Test
    void savesFindsAndHandlesMissingInvitation() {
        InMemoryInvitationResponseRepository repository = new InMemoryInvitationResponseRepository();

        repository.save(new InvitationResponse(0L, 9L, "accept", Instant.now()));

        assertTrue(repository.findByInvitationId(9L).isPresent());
        assertFalse(repository.findByInvitationId(99L).isPresent());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void throwsWhenConfiguredToFailOnSave() {
        InMemoryInvitationResponseRepository repository = new InMemoryInvitationResponseRepository();
        repository.setFailOnSave(true);

        assertThrows(IllegalStateException.class,
                () -> repository.save(new InvitationResponse(0L, 9L, "accept", Instant.now())));
    }
}
