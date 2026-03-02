package com.ece493.cms.unit;

import com.ece493.cms.model.AcceptedPaper;
import com.ece493.cms.model.Room;
import com.ece493.cms.model.ScheduleGenerationResult;
import com.ece493.cms.model.ScheduleViewResult;
import com.ece493.cms.model.TimeSlot;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.service.DeterministicSchedulingAlgorithm;
import com.ece493.cms.service.InMemoryScheduleRepository;
import com.ece493.cms.service.InMemorySchedulingDataRepository;
import com.ece493.cms.service.InMemorySessionRepository;
import com.ece493.cms.service.ScheduleGenerationService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleGenerationServiceTest {
    @Test
    void handlesSuccessAndFailurePaths() {
        InMemoryScheduleRepository scheduleRepository = new InMemoryScheduleRepository();
        InMemorySessionRepository sessionRepository = new InMemorySessionRepository();
        InMemorySchedulingDataRepository dataRepository = new InMemorySchedulingDataRepository();
        DeterministicSchedulingAlgorithm algorithm = new DeterministicSchedulingAlgorithm();
        ScheduleGenerationService service = new ScheduleGenerationService(
                scheduleRepository,
                sessionRepository,
                dataRepository,
                algorithm
        );

        assertEquals(401, service.generateSchedule(null).getStatusCode());
        ScheduleGenerationResult missing = service.generateSchedule("admin@cms.com");
        assertEquals(400, missing.getStatusCode());
        assertTrue(missing.getMissing().contains("rooms"));

        dataRepository.setAcceptedPapers(List.of(new AcceptedPaper("1", "p1")));
        dataRepository.setRooms(List.of(new Room("R1", "Room 1")));
        dataRepository.setTimeSlots(List.of(new TimeSlot("T1", "09:00", "10:00")));

        ScheduleGenerationResult created = service.generateSchedule("admin@cms.com");
        assertEquals(201, created.getStatusCode());
        assertEquals(1L, scheduleRepository.countAll());
        assertEquals(1, service.seedAcceptedPapersFromSubmissions(List.of(
                new PaperSubmission(1L, "a@cms.com", "P1", "A", "U", "Abs", "k", "c", 1L, Instant.now())
        )).size());

        ScheduleViewResult denied = service.viewSchedule("other@cms.com", created.getScheduleId());
        assertEquals(403, denied.getStatusCode());
        assertEquals(401, service.viewSchedule("", created.getScheduleId()).getStatusCode());
        assertEquals(400, service.viewSchedule("admin@cms.com", "x").getStatusCode());
        assertEquals(404, service.viewSchedule("admin@cms.com", "99").getStatusCode());
        assertEquals(200, service.viewSchedule("admin@cms.com", created.getScheduleId()).getStatusCode());

        algorithm.setFail(true);
        ScheduleGenerationResult failed = service.generateSchedule("admin@cms.com");
        assertEquals(500, failed.getStatusCode());
        assertEquals(1, service.failureLog().size());

        scheduleRepository.setFailOnSave(true);
        algorithm.setFail(false);
        assertEquals(500, service.generateSchedule("admin@cms.com").getStatusCode());
    }

    @Test
    void handlesNullAlgorithmFailureMessageAndNonInMemorySeedPath() {
        InMemoryScheduleRepository scheduleRepository = new InMemoryScheduleRepository();
        InMemorySessionRepository sessionRepository = new InMemorySessionRepository();
        ScheduleGenerationService service = new ScheduleGenerationService(
                scheduleRepository,
                sessionRepository,
                new com.ece493.cms.service.SchedulingDataRepository() {
                    @Override
                    public List<AcceptedPaper> acceptedPapers() {
                        return List.of(new AcceptedPaper("1", "P1"));
                    }

                    @Override
                    public List<Room> rooms() {
                        return List.of(new Room("R1", "Room 1"));
                    }

                    @Override
                    public List<TimeSlot> timeSlots() {
                        return List.of(new TimeSlot("T1", "09:00", "10:00"));
                    }
                },
                (acceptedPapers, rooms, timeSlots) -> {
                    throw new IllegalStateException();
                }
        );

        assertEquals(500, service.generateSchedule("admin@cms.com").getStatusCode());
        assertEquals("schedule generation failed", service.failureLog().get(0));
        assertEquals(0, service.seedAcceptedPapersFromSubmissions(List.of()).size());
    }
}
