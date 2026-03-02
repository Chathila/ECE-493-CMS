package com.ece493.cms.controller;

import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.model.ReviewForm;
import com.ece493.cms.model.ReviewFormAccessResult;
import com.ece493.cms.model.ReviewSubmissionResult;
import com.ece493.cms.service.ReviewFormService;
import com.ece493.cms.service.ReviewSubmissionService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReviewWorkflowServlet extends HttpServlet {
    private final ReviewFormService reviewFormService;
    private final ReviewSubmissionService reviewSubmissionService;

    public ReviewWorkflowServlet(ReviewFormService reviewFormService, ReviewSubmissionService reviewSubmissionService) {
        this.reviewFormService = reviewFormService;
        this.reviewSubmissionService = reviewSubmissionService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long assignmentId = parseAssignmentId(req.getRequestURI(), "^/assignments/([^/]+)/review-form$");
        if (assignmentId == null) {
            writeError(resp, 400, "Assignment id is required.");
            return;
        }

        String refereeEmail = sessionEmail(req.getSession(false));
        ReviewFormAccessResult result = reviewFormService.accessReviewForm(assignmentId, refereeEmail);
        writeFormResponse(resp, result);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long assignmentId = parseAssignmentId(req.getRequestURI(), "^/assignments/([^/]+)/reviews$");
        if (assignmentId == null) {
            writeError(resp, 400, "Assignment id is required.");
            return;
        }

        String refereeEmail = sessionEmail(req.getSession(false));
        Map<String, String> responses = parseResponses(readBody(req));
        ReviewSubmissionResult result = reviewSubmissionService.submitReview(assignmentId, refereeEmail, responses);
        writeSubmissionResponse(resp, result);
    }

    private void writeFormResponse(HttpServletResponse resp, ReviewFormAccessResult result) throws IOException {
        if (result.getStatusCode() != 200) {
            writeError(resp, result.getStatusCode(), result.getMessage());
            return;
        }

        ReviewForm form = result.getReviewForm();
        PaperSubmission paper = result.getPaperSubmission();
        resp.setStatus(200);
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{"
                + "\"form\":{"
                + "\"form_id\":\"" + escapeJson(nonNull(form.getFormId())) + "\","
                + "\"version\":\"" + escapeJson(nonNull(form.getVersion())) + "\","
                + "\"required_fields\":" + jsonArray(form.getRequiredFields())
                + "},"
                + "\"paper\":{"
                + "\"submission_id\":" + paper.getSubmissionId() + ","
                + "\"title\":\"" + escapeJson(nonNull(paper.getTitle())) + "\","
                + "\"abstract\":\"" + escapeJson(nonNull(paper.getPaperAbstract())) + "\","
                + "\"authors\":\"" + escapeJson(nonNull(paper.getAuthors())) + "\","
                + "\"download_url\":\"/papers/files/" + paper.getManuscriptFileId() + "\""
                + "}"
                + "}";
        resp.getWriter().write(payload);
    }

    private void writeSubmissionResponse(HttpServletResponse resp, ReviewSubmissionResult result) throws IOException {
        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{\"message\":\"" + escapeJson(result.getMessage()) + "\"";
        if (result.getReviewId() != null) {
            payload += ",\"review_id\":" + result.getReviewId();
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

    private void writeError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"message\":\"" + escapeJson(nonNull(message)) + "\"}");
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

    private Map<String, String> parseResponses(String body) {
        if (body == null || body.isEmpty()) {
            return Map.of();
        }
        Matcher container = Pattern.compile("\\\"responses\\\"\\s*:\\s*\\{(.*?)\\}").matcher(body);
        if (!container.find()) {
            return Map.of();
        }
        String content = container.group(1);
        Matcher fieldMatcher = Pattern.compile("\\\"([^\\\"]+)\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(content);
        Map<String, String> values = new LinkedHashMap<>();
        while (fieldMatcher.find()) {
            values.put(fieldMatcher.group(1), fieldMatcher.group(2));
        }
        return values;
    }

    private Long parseAssignmentId(String uri, String pattern) {
        if (uri == null) {
            return null;
        }
        Matcher matcher = Pattern.compile(pattern).matcher(uri);
        if (!matcher.find()) {
            return null;
        }
        try {
            return Long.parseLong(matcher.group(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String sessionEmail(HttpSession session) {
        return session == null ? null : (String) session.getAttribute("user_email");
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

    private String nonNull(String value) {
        return value == null ? "" : value;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
