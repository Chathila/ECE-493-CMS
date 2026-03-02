package com.ece493.cms.unit;

import com.ece493.cms.model.Session;
import com.ece493.cms.service.InMemorySessionRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemorySessionRepositoryTest {
    @Test
    void replacesSessionsPerSchedule() {
        InMemorySessionRepository repository = new InMemorySessionRepository();

        repository.saveAll(1L, List.of(new Session(1L, null, "1", "R1", "T1"), new Session(2L, null, "2", "R2", "T2")));
        assertEquals(2, repository.findByScheduleId(1L).size());

        repository.saveAll(1L, List.of(new Session(3L, null, "3", "R3", "T3")));
        assertEquals(1, repository.findByScheduleId(1L).size());
        assertEquals(0, repository.findByScheduleId(2L).size());
    }
}
