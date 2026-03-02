package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.AcceptedPaper;
import com.ece493.cms.model.Room;
import com.ece493.cms.model.TimeSlot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC15AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_generateScheduleSuccessfully() throws Exception {
        schedulingDataRepository.setAcceptedPapers(List.of(new AcceptedPaper("1", "P1"), new AcceptedPaper("2", "P2")));
        schedulingDataRepository.setRooms(List.of(new Room("R1", "Room 1"), new Room("R2", "Room 2")));
        schedulingDataRepository.setTimeSlots(List.of(new TimeSlot("T1", "09:00", "10:00"), new TimeSlot("T2", "10:00", "11:00")));

        ServletHttpTestSupport.ResponseCapture response = postScheduleGenerate(loggedInSession("admin@cms.com"));

        assertEquals(201, response.getStatus());
        assertEquals(1L, scheduleRepository.countAll());
        assertTrue(response.getBody().contains("\"status\":\"generated\""));
    }

    @Test
    void AT02_missingSchedulingData() throws Exception {
        schedulingDataRepository.setAcceptedPapers(List.of(new AcceptedPaper("1", "P1")));

        ServletHttpTestSupport.ResponseCapture response = postScheduleGenerate(loggedInSession("admin@cms.com"));

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("rooms"));
        assertEquals(0L, scheduleRepository.countAll());
    }

    @Test
    void AT03_algorithmFailure() throws Exception {
        schedulingDataRepository.setAcceptedPapers(List.of(new AcceptedPaper("1", "P1")));
        schedulingDataRepository.setRooms(List.of(new Room("R1", "Room 1")));
        schedulingDataRepository.setTimeSlots(List.of(new TimeSlot("T1", "09:00", "10:00")));
        schedulingAlgorithm.setFail(true);

        ServletHttpTestSupport.ResponseCapture response = postScheduleGenerate(loggedInSession("admin@cms.com"));

        assertEquals(500, response.getStatus());
        assertEquals(1, scheduleGenerationService.failureLog().size());
        assertEquals(0L, scheduleRepository.countAll());
    }
}
