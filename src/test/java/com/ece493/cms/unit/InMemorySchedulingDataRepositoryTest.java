package com.ece493.cms.unit;

import com.ece493.cms.service.InMemorySchedulingDataRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemorySchedulingDataRepositoryTest {
    @Test
    void supportsNullAndNonNullSetters() {
        InMemorySchedulingDataRepository repository = new InMemorySchedulingDataRepository();

        repository.setAcceptedPapers(null);
        repository.setRooms(null);
        repository.setTimeSlots(null);

        assertTrue(repository.acceptedPapers().isEmpty());
        assertTrue(repository.rooms().isEmpty());
        assertTrue(repository.timeSlots().isEmpty());
    }
}
