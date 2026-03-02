package com.ece493.cms.unit;

import com.ece493.cms.model.Schedule;
import com.ece493.cms.model.Session;
import com.ece493.cms.service.InMemoryScheduleRepository;
import com.ece493.cms.service.InMemorySessionRepository;
import com.ece493.cms.service.ScheduleEditService;
import com.ece493.cms.service.ScheduleValidationService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleEditServiceTest {
    @Test
    void supportsEditValidationAndSaveFailure() {
        InMemoryScheduleRepository scheduleRepository = new InMemoryScheduleRepository();
        InMemorySessionRepository sessionRepository = new InMemorySessionRepository();
        ScheduleEditService service = new ScheduleEditService(scheduleRepository, sessionRepository, new ScheduleValidationService());
        Schedule stored = scheduleRepository.save(new Schedule(0L, "admin@cms.com", Instant.now(), Instant.now(), "generated", List.of()));

        assertEquals(401, service.getEditableSchedule(null, String.valueOf(stored.getScheduleId())).getStatusCode());
        assertEquals(401, service.getEditableSchedule(" ", String.valueOf(stored.getScheduleId())).getStatusCode());
        assertEquals(400, service.getEditableSchedule("editor@cms.com", "x").getStatusCode());
        assertEquals(404, service.getEditableSchedule("editor@cms.com", "99").getStatusCode());
        assertEquals(200, service.getEditableSchedule("editor@cms.com", String.valueOf(stored.getScheduleId())).getStatusCode());

        assertEquals(401, service.updateSchedule(null, String.valueOf(stored.getScheduleId()), List.of()).getStatusCode());
        assertEquals(401, service.updateSchedule(" ", String.valueOf(stored.getScheduleId()), List.of()).getStatusCode());
        assertEquals(400, service.updateSchedule("editor@cms.com", "x", List.of()).getStatusCode());
        assertEquals(404, service.updateSchedule("editor@cms.com", "99", List.of()).getStatusCode());

        assertEquals(400, service.updateSchedule("editor@cms.com", String.valueOf(stored.getScheduleId()), List.of()).getStatusCode());

        List<Session> conflict = List.of(
                new Session(1L, null, "1", "R1", "T1"),
                new Session(2L, null, "2", "R1", "T1")
        );
        assertTrue(service.updateSchedule("editor@cms.com", String.valueOf(stored.getScheduleId()), conflict)
                .getFields().contains("sessions[1].conflict"));

        List<Session> valid = List.of(new Session(1L, null, "1", "R1", "T1"));
        assertEquals(200, service.updateSchedule("editor@cms.com", String.valueOf(stored.getScheduleId()), valid).getStatusCode());

        scheduleRepository.setFailOnUpdate(true);
        assertEquals(500, service.updateSchedule("editor@cms.com", String.valueOf(stored.getScheduleId()), valid).getStatusCode());
    }
}
