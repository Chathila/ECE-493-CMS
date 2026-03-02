package com.ece493.cms.controller;

import com.ece493.cms.model.FinalScheduleViewResult;
import com.ece493.cms.model.Schedule;
import com.ece493.cms.model.Session;
import com.ece493.cms.service.ScheduleViewService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class FinalScheduleServlet extends HttpServlet {
    private final ScheduleViewService scheduleViewService;

    public FinalScheduleServlet(ScheduleViewService scheduleViewService) {
        this.scheduleViewService = scheduleViewService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        FinalScheduleViewResult result = scheduleViewService.viewFinalSchedule();
        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{\"message\":\"" + escapeJson(nonNull(result.getMessage())) + "\"";
        if (result.getSchedule() != null) {
            Schedule schedule = result.getSchedule();
            payload += ",\"schedule\":{"
                    + "\"schedule_id\":\"" + schedule.getScheduleId() + "\","
                    + "\"sessions\":" + sessionsJson(schedule.getSessions())
                    + "}";
        }
        payload += "}";
        resp.getWriter().write(payload);
    }

    private String sessionsJson(List<Session> sessions) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < sessions.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            Session session = sessions.get(i);
            json.append("{")
                    .append("\"time_slot\":\"").append(escapeJson(nonNull(session.getTimeSlotId()))).append("\",")
                    .append("\"location\":\"").append(escapeJson(nonNull(session.getRoomId()))).append("\",")
                    .append("\"title\":\"").append(escapeJson(nonNull(session.getPaperId()))).append("\"")
                    .append("}");
        }
        json.append("]");
        return json.toString();
    }

    private String nonNull(String value) {
        return value == null ? "" : value;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
