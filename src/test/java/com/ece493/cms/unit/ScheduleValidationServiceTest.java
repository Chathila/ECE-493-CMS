package com.ece493.cms.unit;

import com.ece493.cms.model.Session;
import com.ece493.cms.service.ScheduleValidationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleValidationServiceTest {
    @Test
    void validatesMissingAndConflictFields() {
        ScheduleValidationService service = new ScheduleValidationService();

        assertEquals(List.of("sessions"), service.validate(null));
        assertEquals(List.of("sessions"), service.validate(List.of()));

        List<String> invalid = service.validate(List.of(
                new Session(1L, "1", null, "", "T1"),
                new Session(2L, "1", "2", "R1", "T1"),
                new Session(3L, "1", "3", "R1", "T1")
        ));

        assertTrue(invalid.contains("sessions[0].paper_id"));
        assertTrue(invalid.contains("sessions[0].room_id"));
        assertTrue(invalid.contains("sessions[0].time_slot_id") || service.validate(List.of(
                new Session(1L, "1", "1", "R1", "")
        )).contains("sessions[0].time_slot_id"));
        assertTrue(invalid.contains("sessions[2].conflict"));
        assertTrue(service.validate(List.of(new Session(1L, "1", "1", "R1", "T1"))).isEmpty());
    }
}
