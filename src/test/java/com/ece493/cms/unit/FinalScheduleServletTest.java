package com.ece493.cms.unit;

import com.ece493.cms.controller.FinalScheduleServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.FinalScheduleViewResult;
import com.ece493.cms.model.Schedule;
import com.ece493.cms.model.Session;
import com.ece493.cms.service.ScheduleViewService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinalScheduleServletTest {
    @Test
    void rendersScheduleAndErrors() throws Exception {
        ScheduleViewService service = new ScheduleViewService(null, null) {
            @Override
            public FinalScheduleViewResult viewFinalSchedule() {
                return FinalScheduleViewResult.found(new Schedule(
                        1L,
                        "admin@cms.com",
                        Instant.now(),
                        Instant.now(),
                        "published",
                        List.of(
                                new Session(1L, "1", "Title", "Room A", "09:00"),
                                new Session(2L, "1", "Title 2", "Room B", "10:00")
                        )
                ));
            }
        };
        FinalScheduleServlet servlet = new FinalScheduleServlet(service);
        ServletHttpTestSupport.ResponseCapture ok = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/schedule/final"), ok.asResponse());

        assertEquals(200, ok.getStatus());
        assertTrue(ok.getBody().contains("\"sessions\""));
        assertTrue(ok.getBody().contains("\"location\":\"Room A\""));
        assertTrue(ok.getBody().contains("},{"));

        FinalScheduleServlet errorServlet = new FinalScheduleServlet(new ScheduleViewService(null, null) {
            @Override
            public FinalScheduleViewResult viewFinalSchedule() {
                return FinalScheduleViewResult.error(500, null);
            }
        });
        ServletHttpTestSupport.ResponseCapture error = ServletHttpTestSupport.responseCapture();
        errorServlet.service(ServletHttpTestSupport.getRequest(null, null, "/schedule/final"), error.asResponse());
        assertEquals(500, error.getStatus());
        assertTrue(error.getBody().contains("\"message\":\"\""));
    }
}
