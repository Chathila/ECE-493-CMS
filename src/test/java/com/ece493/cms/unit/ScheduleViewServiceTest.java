package com.ece493.cms.unit;

import com.ece493.cms.model.FinalScheduleViewResult;
import com.ece493.cms.model.Schedule;
import com.ece493.cms.model.Session;
import com.ece493.cms.service.InMemoryScheduleRepository;
import com.ece493.cms.service.InMemorySessionRepository;
import com.ece493.cms.service.ScheduleViewService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScheduleViewServiceTest {
    @Test
    void coversPublishedUnavailableAndFailure() {
        InMemoryScheduleRepository scheduleRepository = new InMemoryScheduleRepository();
        InMemorySessionRepository sessionRepository = new InMemorySessionRepository();
        ScheduleViewService service = new ScheduleViewService(scheduleRepository, sessionRepository);

        assertEquals(404, service.viewFinalSchedule().getStatusCode());

        Schedule published = scheduleRepository.save(new Schedule(
                0L,
                "admin@cms.com",
                Instant.now(),
                Instant.now(),
                "published",
                List.of()
        ));
        sessionRepository.saveAll(published.getScheduleId(), List.of(new Session(1L, null, "Paper", "Room", "10:00")));

        FinalScheduleViewResult found = service.viewFinalSchedule();
        assertEquals(200, found.getStatusCode());
        assertEquals(1, found.getSchedule().getSessions().size());

        scheduleRepository.setFailOnFindPublished(true);
        assertEquals(500, service.viewFinalSchedule().getStatusCode());
    }
}
