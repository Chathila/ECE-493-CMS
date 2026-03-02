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

class UC17AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_viewPublishedFinalScheduleSuccessfully() throws Exception {
        Schedule schedule = scheduleRepository.save(new Schedule(0L, "admin@cms.com", Instant.now(), Instant.now(), "published", List.of()));
        sessionRepository.saveAll(schedule.getScheduleId(), List.of(new Session(1L, null, "Session A", "Main Hall", "09:00")));

        ServletHttpTestSupport.ResponseCapture response = getFinalSchedule();

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Session A"));
    }

    @Test
    void AT02_finalScheduleNotPublished() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = getFinalSchedule();

        assertEquals(404, response.getStatus());
        assertTrue(response.getBody().contains("not published yet"));
    }

    @Test
    void AT03_databaseErrorDuringScheduleRetrieval() throws Exception {
        scheduleRepository.setFailOnFindPublished(true);

        ServletHttpTestSupport.ResponseCapture response = getFinalSchedule();

        assertEquals(500, response.getStatus());
        assertTrue(response.getBody().contains("try again later"));
    }
}
