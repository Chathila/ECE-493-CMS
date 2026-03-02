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

class FinalScheduleEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void returnsPublishedSchedule() throws Exception {
        Schedule schedule = scheduleRepository.save(new Schedule(0L, "admin@cms.com", Instant.now(), Instant.now(), "published", List.of()));
        sessionRepository.saveAll(schedule.getScheduleId(), List.of(new Session(1L, null, "Paper 1", "Room 1", "10:00")));

        ServletHttpTestSupport.ResponseCapture response = getFinalSchedule();

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Paper 1"));
    }

    @Test
    void returnsNotPublishedAndRetrievalFailure() throws Exception {
        ServletHttpTestSupport.ResponseCapture unavailable = getFinalSchedule();
        assertEquals(404, unavailable.getStatus());

        scheduleRepository.setFailOnFindPublished(true);
        ServletHttpTestSupport.ResponseCapture failure = getFinalSchedule();
        assertEquals(500, failure.getStatus());
        assertTrue(failure.getBody().contains("try again later"));
    }
}
