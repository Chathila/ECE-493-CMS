package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.Schedule;
import com.ece493.cms.model.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC16AcceptanceIT extends RegistrationIntegrationSupport {
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
    void AT01_editAndSaveScheduleSuccessfully() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = putSchedule(
                "1",
                "{\"sessions\":[{\"session_id\":1,\"paper_id\":\"1\",\"room_id\":\"R2\",\"time_slot_id\":\"T2\"}]}",
                loggedInSession("editor@cms.com")
        );

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("updated"));
    }

    @Test
    void AT02_schedulingConflictIntroduced() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = putSchedule(
                "1",
                "{\"sessions\":[{\"session_id\":1,\"paper_id\":\"1\",\"room_id\":\"R1\",\"time_slot_id\":\"T1\"},{\"session_id\":2,\"paper_id\":\"2\",\"room_id\":\"R1\",\"time_slot_id\":\"T1\"}]}",
                loggedInSession("editor@cms.com")
        );

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("conflict"));
    }

    @Test
    void AT03_invalidOrIncompleteScheduleEdit() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = putSchedule(
                "1",
                "{\"sessions\":[{\"session_id\":1,\"paper_id\":\"\",\"room_id\":\"\",\"time_slot_id\":\"T1\"}]}",
                loggedInSession("editor@cms.com")
        );

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("fields"));
    }

    @Test
    void AT04_databaseErrorWhileSavingSchedule() throws Exception {
        scheduleRepository.setFailOnUpdate(true);

        ServletHttpTestSupport.ResponseCapture response = putSchedule(
                "1",
                "{\"sessions\":[{\"session_id\":1,\"paper_id\":\"1\",\"room_id\":\"R2\",\"time_slot_id\":\"T2\"}]}",
                loggedInSession("editor@cms.com")
        );

        assertEquals(500, response.getStatus());
        assertTrue(response.getBody().contains("retry later"));
    }
}
