package com.ece493.cms.unit;

import com.ece493.cms.controller.ScheduleServlet;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.model.Schedule;
import com.ece493.cms.model.ScheduleEditResult;
import com.ece493.cms.model.ScheduleGenerationResult;
import com.ece493.cms.model.ScheduleViewResult;
import com.ece493.cms.model.Session;
import com.ece493.cms.service.ScheduleEditService;
import com.ece493.cms.service.ScheduleGenerationService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleServletTest {
    @Test
    void handlesGenerateViewAndUpdateResponses() throws Exception {
        ScheduleGenerationService generationService = new ScheduleGenerationService(null, null, null, null) {
            @Override
            public ScheduleGenerationResult generateSchedule(String adminEmail) {
                return ScheduleGenerationResult.created("1");
            }
        };
        ScheduleEditService editService = new ScheduleEditService(null, null, null) {
            @Override
            public ScheduleViewResult getEditableSchedule(String editorEmail, String scheduleId) {
                return ScheduleViewResult.found(new Schedule(
                        1L,
                        "admin@cms.com",
                        Instant.now(),
                        Instant.now(),
                        "generated",
                        List.of(new Session(1L, "1", "11", "R1", "T1"))
                ));
            }

            @Override
            public ScheduleEditResult updateSchedule(String editorEmail, String scheduleId, List<Session> sessions) {
                return ScheduleEditResult.updated(scheduleId);
            }
        };

        ScheduleServlet servlet = new ScheduleServlet(generationService, editService);
        ServletHttpTestSupport.SessionCapture session = ServletHttpTestSupport.sessionCapture();
        session.asSession().setAttribute("user_email", "admin@cms.com");
        ServletHttpTestSupport.ResponseCapture generate = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture view = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture update = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.postJsonRequest("{}", session, "/schedule/generate"), generate.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(session, null, "/schedule/1"), view.asResponse());
        servlet.service(ServletHttpTestSupport.putJsonRequest("{\"sessions\":[{\"session_id\":1,\"paper_id\":\"11\",\"room_id\":\"R1\",\"time_slot_id\":\"T1\"}]}", session, "/schedule/1"), update.asResponse());

        assertEquals(201, generate.getStatus());
        assertTrue(generate.getBody().contains("\"schedule_id\":\"1\""));
        assertEquals(200, view.getStatus());
        assertTrue(view.getBody().contains("\"sessions\""));
        assertEquals(200, update.getStatus());
        assertTrue(update.getBody().contains("\"status\":\"updated\""));
    }

    @Test
    void handlesErrorsAndParsingEdges() throws Exception {
        ScheduleGenerationService generationService = new ScheduleGenerationService(null, null, null, null) {
            @Override
            public ScheduleGenerationResult generateSchedule(String adminEmail) {
                return ScheduleGenerationResult.missingData(List.of("rooms", "time_slots"));
            }
        };
        ScheduleEditService editService = new ScheduleEditService(null, null, null) {
            @Override
            public ScheduleViewResult getEditableSchedule(String editorEmail, String scheduleId) {
                return ScheduleViewResult.error(403, null);
            }

            @Override
            public ScheduleEditResult updateSchedule(String editorEmail, String scheduleId, List<Session> sessions) {
                return ScheduleEditResult.validationError(List.of("sessions[0].room_id"));
            }
        };

        ScheduleServlet servlet = new ScheduleServlet(generationService, editService);
        ServletHttpTestSupport.ResponseCapture missingRoute = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture badGet = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture deniedGet = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture badPut = ServletHttpTestSupport.responseCapture();
        ServletHttpTestSupport.ResponseCapture validation = ServletHttpTestSupport.responseCapture();

        servlet.service(ServletHttpTestSupport.postJsonRequest("{}", null, "/schedule/wrong"), missingRoute.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/schedule"), badGet.asResponse());
        servlet.service(ServletHttpTestSupport.getRequest(null, null, "/schedule/1"), deniedGet.asResponse());
        servlet.service(ServletHttpTestSupport.putJsonRequest("{}", null, "/schedule"), badPut.asResponse());
        servlet.service(ServletHttpTestSupport.putJsonRequest("{\"sessions\":[{\"session_id\":\"x\",\"paper_id\":1,\"room_id\":\"R1\"}]}", null, "/schedule/1"), validation.asResponse());

        assertEquals(404, missingRoute.getStatus());
        assertEquals(400, badGet.getStatus());
        assertEquals(403, deniedGet.getStatus());
        assertTrue(deniedGet.getBody().contains("\"message\":\"\""));
        assertEquals(400, badPut.getStatus());
        assertEquals(400, validation.getStatus());
        assertTrue(validation.getBody().contains("\"fields\""));
    }

    @Test
    void coversPrivateParserBranches() throws Exception {
        ScheduleServlet servlet = new ScheduleServlet(
                new ScheduleGenerationService(null, null, null, null),
                new ScheduleEditService(null, null, null)
        );
        Method parseScheduleId = ScheduleServlet.class.getDeclaredMethod("parseScheduleId", String.class);
        Method parseSessions = ScheduleServlet.class.getDeclaredMethod("parseSessions", String.class);
        Method parseLongOrDefault = ScheduleServlet.class.getDeclaredMethod("parseLongOrDefault", String.class, long.class);
        Method sessionsJson = ScheduleServlet.class.getDeclaredMethod("sessionsJson", List.class);
        parseScheduleId.setAccessible(true);
        parseSessions.setAccessible(true);
        parseLongOrDefault.setAccessible(true);
        sessionsJson.setAccessible(true);

        Object nullUri = parseScheduleId.invoke(servlet, new Object[]{null});
        Object missingMatch = parseScheduleId.invoke(servlet, "/schedule");
        Object nullBody = parseSessions.invoke(servlet, new Object[]{null});
        Object emptyBody = parseSessions.invoke(servlet, "");
        Object missingContainer = parseSessions.invoke(servlet, "{}");
        Object rawNull = parseLongOrDefault.invoke(servlet, null, 7L);
        String sessions = (String) sessionsJson.invoke(servlet, List.of(
                new Session(1L, "1", "1", "R1", "T1"),
                new Session(2L, "1", "2", "R2", "T2")
        ));

        assertEquals(null, nullUri);
        assertEquals(null, missingMatch);
        assertTrue(((List<?>) nullBody).isEmpty());
        assertTrue(((List<?>) emptyBody).isEmpty());
        assertTrue(((List<?>) missingContainer).isEmpty());
        assertEquals(7L, rawNull);
        assertTrue(sessions.contains("},{"));
    }
}
