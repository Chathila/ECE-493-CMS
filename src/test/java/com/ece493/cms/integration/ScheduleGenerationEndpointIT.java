package com.ece493.cms.integration;

import com.ece493.cms.model.AcceptedPaper;
import com.ece493.cms.model.Room;
import com.ece493.cms.model.TimeSlot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleGenerationEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void generatesScheduleWhenDataIsAvailable() throws Exception {
        schedulingDataRepository.setAcceptedPapers(List.of(new AcceptedPaper("1", "P1"), new AcceptedPaper("2", "P2")));
        schedulingDataRepository.setRooms(List.of(new Room("R1", "Room 1")));
        schedulingDataRepository.setTimeSlots(List.of(new TimeSlot("T1", "09:00", "10:00")));

        ServletHttpTestSupport.ResponseCapture response = postScheduleGenerate(loggedInSession("admin@cms.com"));

        assertEquals(201, response.getStatus());
        assertTrue(response.getBody().contains("\"status\":\"generated\""));
        assertEquals(1L, scheduleRepository.countAll());
    }

    @Test
    void returnsMissingDataAndAlgorithmFailure() throws Exception {
        ServletHttpTestSupport.ResponseCapture missing = postScheduleGenerate(loggedInSession("admin@cms.com"));
        assertEquals(400, missing.getStatus());
        assertTrue(missing.getBody().contains("rooms"));

        schedulingDataRepository.setAcceptedPapers(List.of(new AcceptedPaper("1", "P1")));
        schedulingDataRepository.setRooms(List.of(new Room("R1", "Room 1")));
        schedulingDataRepository.setTimeSlots(List.of(new TimeSlot("T1", "09:00", "10:00")));
        schedulingAlgorithm.setFail(true);

        ServletHttpTestSupport.ResponseCapture failed = postScheduleGenerate(loggedInSession("admin@cms.com"));
        assertEquals(500, failed.getStatus());
        assertEquals(1, scheduleGenerationService.failureLog().size());
    }
}
