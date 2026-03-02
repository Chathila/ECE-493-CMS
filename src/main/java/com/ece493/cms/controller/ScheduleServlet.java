package com.ece493.cms.controller;

import com.ece493.cms.model.Schedule;
import com.ece493.cms.model.ScheduleEditResult;
import com.ece493.cms.model.ScheduleGenerationResult;
import com.ece493.cms.model.ScheduleViewResult;
import com.ece493.cms.model.Session;
import com.ece493.cms.service.ScheduleEditService;
import com.ece493.cms.service.ScheduleGenerationService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleServlet extends HttpServlet {
    private final ScheduleGenerationService scheduleGenerationService;
    private final ScheduleEditService scheduleEditService;

    public ScheduleServlet(ScheduleGenerationService scheduleGenerationService, ScheduleEditService scheduleEditService) {
        this.scheduleGenerationService = scheduleGenerationService;
        this.scheduleEditService = scheduleEditService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!"/schedule/generate".equals(req.getRequestURI())) {
            writeError(resp, 404, "Endpoint not found.");
            return;
        }

        String userEmail = sessionEmail(req.getSession(false));
        ScheduleGenerationResult result = scheduleGenerationService.generateSchedule(userEmail);
        writeGenerationResponse(resp, result);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String scheduleId = parseScheduleId(req.getRequestURI());
        if (scheduleId == null) {
            writeError(resp, 400, "Schedule id is required.");
            return;
        }

        String userEmail = sessionEmail(req.getSession(false));
        ScheduleViewResult result = scheduleEditService.getEditableSchedule(userEmail, scheduleId);
        writeViewResponse(resp, result);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String scheduleId = parseScheduleId(req.getRequestURI());
        if (scheduleId == null) {
            writeError(resp, 400, "Schedule id is required.");
            return;
        }

        String userEmail = sessionEmail(req.getSession(false));
        List<Session> sessions = parseSessions(readBody(req));
        ScheduleEditResult result = scheduleEditService.updateSchedule(userEmail, scheduleId, sessions);
        writeEditResponse(resp, result);
    }

    private String parseScheduleId(String uri) {
        if (uri == null) {
            return null;
        }
        Matcher matcher = Pattern.compile("^/schedule/([^/]+)$").matcher(uri);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }

    private void writeGenerationResponse(HttpServletResponse resp, ScheduleGenerationResult result) throws IOException {
        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{\"message\":\"" + escapeJson(nonNull(result.getMessage())) + "\"";
        if (result.getScheduleId() != null) {
            payload += ",\"schedule_id\":\"" + escapeJson(result.getScheduleId()) + "\"";
        }
        if (result.getStatus() != null) {
            payload += ",\"status\":\"" + escapeJson(result.getStatus()) + "\"";
        }
        if (!result.getMissing().isEmpty()) {
            payload += ",\"missing\":" + jsonArray(result.getMissing());
        }
        payload += "}";
        resp.getWriter().write(payload);
    }

    private void writeViewResponse(HttpServletResponse resp, ScheduleViewResult result) throws IOException {
        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{\"message\":\"" + escapeJson(nonNull(result.getMessage())) + "\"";
        if (result.getSchedule() != null) {
            Schedule schedule = result.getSchedule();
            payload += ",\"schedule\":{"
                    + "\"schedule_id\":\"" + schedule.getScheduleId() + "\"," 
                    + "\"status\":\"" + escapeJson(nonNull(schedule.getStatus())) + "\"," 
                    + "\"sessions\":" + sessionsJson(schedule.getSessions())
                    + "}";
        }
        payload += "}";
        resp.getWriter().write(payload);
    }

    private void writeEditResponse(HttpServletResponse resp, ScheduleEditResult result) throws IOException {
        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{\"message\":\"" + escapeJson(nonNull(result.getMessage())) + "\"";
        if (result.getScheduleId() != null) {
            payload += ",\"schedule_id\":\"" + escapeJson(result.getScheduleId()) + "\"";
        }
        if (result.getStatus() != null) {
            payload += ",\"status\":\"" + escapeJson(result.getStatus()) + "\"";
        }
        if (!result.getFields().isEmpty()) {
            payload += ",\"fields\":" + jsonArray(result.getFields());
        }
        payload += "}";
        resp.getWriter().write(payload);
    }

    private List<Session> parseSessions(String body) {
        if (body == null || body.isEmpty()) {
            return List.of();
        }
        Matcher container = Pattern.compile("\\\"sessions\\\"\\s*:\\s*\\[(.*)]").matcher(body);
        if (!container.find()) {
            return List.of();
        }

        String sessionsContent = container.group(1);
        Matcher objectMatcher = Pattern.compile("\\{(.*?)\\}").matcher(sessionsContent);
        List<Session> sessions = new ArrayList<>();
        while (objectMatcher.find()) {
            String objectBody = objectMatcher.group(1);
            String sessionIdRaw = readTextOrNumberField(objectBody, "session_id");
            long sessionId = parseLongOrDefault(sessionIdRaw, sessions.size() + 1L);
            sessions.add(new Session(
                    sessionId,
                    null,
                    readTextOrNumberField(objectBody, "paper_id"),
                    readTextOrNumberField(objectBody, "room_id"),
                    readTextOrNumberField(objectBody, "time_slot_id")
            ));
        }
        return sessions;
    }

    private String readTextOrNumberField(String body, String fieldName) {
        Matcher textMatcher = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(body);
        if (textMatcher.find()) {
            return textMatcher.group(1);
        }
        Matcher numberMatcher = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*([0-9]+)").matcher(body);
        if (numberMatcher.find()) {
            return numberMatcher.group(1);
        }
        return null;
    }

    private long parseLongOrDefault(String raw, long fallback) {
        if (raw == null) {
            return fallback;
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    private String sessionsJson(List<Session> sessions) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < sessions.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            Session session = sessions.get(i);
            json.append("{")
                    .append("\"session_id\":").append(session.getSessionId()).append(",")
                    .append("\"paper_id\":\"").append(escapeJson(nonNull(session.getPaperId()))).append("\",")
                    .append("\"room_id\":\"").append(escapeJson(nonNull(session.getRoomId()))).append("\",")
                    .append("\"time_slot_id\":\"").append(escapeJson(nonNull(session.getTimeSlotId()))).append("\"")
                    .append("}");
        }
        json.append("]");
        return json.toString();
    }

    private String jsonArray(List<String> values) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append("\"").append(escapeJson(nonNull(values.get(i)))).append("\"");
        }
        json.append("]");
        return json.toString();
    }

    private void writeError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"message\":\"" + escapeJson(nonNull(message)) + "\"}");
    }

    private String sessionEmail(HttpSession session) {
        return session == null ? null : (String) session.getAttribute("user_email");
    }

    private String nonNull(String value) {
        return value == null ? "" : value;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
