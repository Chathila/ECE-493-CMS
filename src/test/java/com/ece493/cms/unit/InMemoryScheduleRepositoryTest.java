package com.ece493.cms.unit;

import com.ece493.cms.model.Schedule;
import com.ece493.cms.service.InMemoryScheduleRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryScheduleRepositoryTest {
    @Test
    void savesFindsUpdatesAndHandlesFailures() {
        InMemoryScheduleRepository repository = new InMemoryScheduleRepository();
        Schedule saved = repository.save(new Schedule(0L, "admin@cms.com", Instant.now(), Instant.now(), "generated", List.of()));

        assertEquals(1L, saved.getScheduleId());
        assertTrue(repository.findById(saved.getScheduleId()).isPresent());

        Schedule updated = new Schedule(saved.getScheduleId(), saved.getGeneratedBy(), saved.getGeneratedAt(), Instant.now(), "updated", List.of());
        assertEquals("updated", repository.update(updated).getStatus());

        repository.setFailOnSave(true);
        assertThrows(IllegalStateException.class,
                () -> repository.save(new Schedule(0L, "admin@cms.com", Instant.now(), Instant.now(), "generated", List.of())));

        repository.setFailOnUpdate(true);
        assertThrows(IllegalStateException.class, () -> repository.update(updated));

        repository.setFailOnUpdate(false);
        assertThrows(IllegalStateException.class,
                () -> repository.update(new Schedule(999L, "admin@cms.com", Instant.now(), Instant.now(), "updated", List.of())));

        repository.setFailOnSave(false);
        repository.save(new Schedule(0L, "admin@cms.com", Instant.now(), Instant.now(), "draft", List.of()));
        repository.save(new Schedule(0L, "admin@cms.com", Instant.now(), Instant.now(), "published", List.of()));
        assertEquals("published", repository.findPublished().orElseThrow().getStatus());

        InMemoryScheduleRepository reverseCheck = new InMemoryScheduleRepository();
        reverseCheck.save(new Schedule(0L, "admin@cms.com", Instant.now(), Instant.now(), "published", List.of()));
        reverseCheck.save(new Schedule(0L, "admin@cms.com", Instant.now(), Instant.now(), "draft", List.of()));
        assertEquals("published", reverseCheck.findPublished().orElseThrow().getStatus());
    }
}
