package com.ece493.cms.controller;

import com.ece493.cms.model.RefereeAssignmentResult;
import com.ece493.cms.service.RefereeAssignmentService;
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

public class RefereeAssignmentServlet extends HttpServlet {
    private final RefereeAssignmentService refereeAssignmentService;
    private final String assignRefereesHtml;

    public RefereeAssignmentServlet(RefereeAssignmentService refereeAssignmentService, String assignRefereesHtml) {
        this.refereeAssignmentService = refereeAssignmentService;
        this.assignRefereesHtml = assignRefereesHtml;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().write(assignRefereesHtml);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readBody(req);
        String paperId = parsePaperId(req);
        if (paperId == null) {
            paperId = readTextField(body, "paper_id");
        }
        List<String> refereeEmails = readArrayField(body, "referee_emails");

        HttpSession session = req.getSession(false);
        String editorEmail = session == null ? null : (String) session.getAttribute("user_email");

        RefereeAssignmentResult result = refereeAssignmentService.assignReferees(editorEmail, paperId, refereeEmails);

        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{\"message\":\"" + escapeJson(result.getMessage()) + "\"";
        if (result.getWarning() != null) {
            payload += ",\"warning\":\"" + escapeJson(result.getWarning()) + "\"";
        }
        payload += "}";
        resp.getWriter().write(payload);
    }

    private String parsePaperId(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if (uri == null) {
            return null;
        }
        Matcher matcher = Pattern.compile("^/papers/([^/]+)/referees/assign$").matcher(uri);
        return matcher.find() ? matcher.group(1) : null;
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

    private String readTextField(String body, String fieldName) {
        if (body == null || body.isEmpty()) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");
        Matcher matcher = pattern.matcher(body);
        return matcher.find() ? matcher.group(1) : null;
    }

    private List<String> readArrayField(String body, String fieldName) {
        if (body == null || body.isEmpty()) {
            return List.of();
        }
        Pattern arrayPattern = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*\\[(.*?)\\]");
        Matcher arrayMatcher = arrayPattern.matcher(body);
        if (!arrayMatcher.find()) {
            String csv = readTextField(body, fieldName);
            if (csv == null) {
                return List.of();
            }
            String[] items = csv.split(",");
            List<String> values = new ArrayList<>();
            for (String item : items) {
                values.add(item.trim());
            }
            return values;
        }

        String rawItems = arrayMatcher.group(1);
        Matcher itemMatcher = Pattern.compile("\\\"([^\\\"]*)\\\"").matcher(rawItems);
        List<String> values = new ArrayList<>();
        while (itemMatcher.find()) {
            values.add(itemMatcher.group(1));
        }
        return values;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
