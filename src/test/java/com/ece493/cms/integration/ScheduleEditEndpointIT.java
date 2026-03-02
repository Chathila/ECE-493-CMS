package com.ece493.cms.integration;

import com.ece493.cms.model.Schedule;
import com.ece493.cms.model.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleEditEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
        Schedule schedule = scheduleRepository.save(new Schedule(0L, "editor@cms.com", Instant.now(), Instant.now(), "generated", List.of()));
        sessionRepository.saveAll(schedule.getScheduleId(), List.of(new Session(1L, null, "1", "R1", "T1")));
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void updatesScheduleAndCanRetrieveIt() throws Exception {
        ServletHttpTestSupport.ResponseCapture update = putSchedule(
                "1",
                "{\"sessions\":[{\"session_id\":1,\"paper_id\":\"1\",\"room_id\":\"R2\",\"time_slot_id\":\"T2\"}]}",
                loggedInSession("editor@cms.com")
        );
        ServletHttpTestSupport.ResponseCapture view = getSchedule("1", loggedInSession("editor@cms.com"));

        assertEquals(200, update.getStatus());
        assertEquals(200, view.getStatus());
        assertTrue(view.getBody().contains("\"room_id\":\"R2\""));
    }

    @Test
    void returnsValidationAndSaveErrors() throws Exception {
        ServletHttpTestSupport.ResponseCapture conflict = putSchedule(
                "1",
                "{\"sessions\":[{\"session_id\":1,\"paper_id\":\"1\",\"room_id\":\"R1\",\"time_slot_id\":\"T1\"},{\"session_id\":2,\"paper_id\":\"2\",\"room_id\":\"R1\",\"time_slot_id\":\"T1\"}]}",
                loggedInSession("editor@cms.com")
        );
        assertEquals(400, conflict.getStatus());

        scheduleRepository.setFailOnUpdate(true);
        ServletHttpTestSupport.ResponseCapture saveError = putSchedule(
                "1",
                "{\"sessions\":[{\"session_id\":1,\"paper_id\":\"1\",\"room_id\":\"R2\",\"time_slot_id\":\"T2\"}]}",
                loggedInSession("editor@cms.com")
        );
        assertEquals(500, saveError.getStatus());
        assertTrue(saveError.getBody().contains("retry later"));
    }
}
