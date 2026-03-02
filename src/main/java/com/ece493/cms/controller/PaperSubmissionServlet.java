package com.ece493.cms.controller;

import com.ece493.cms.model.ManuscriptFile;
import com.ece493.cms.model.PaperSubmissionRequest;
import com.ece493.cms.model.PaperSubmissionResult;
import com.ece493.cms.service.PaperSubmissionService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaperSubmissionServlet extends HttpServlet {
    private final PaperSubmissionService paperSubmissionService;
    private final String submitPaperHtml;

    public PaperSubmissionServlet(PaperSubmissionService paperSubmissionService, String submitPaperHtml) {
        this.paperSubmissionService = paperSubmissionService;
        this.submitPaperHtml = submitPaperHtml;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().write(submitPaperHtml);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readBody(req);

        String title = readTextField(body, "title");
        String authors = readTextOrArrayField(body, "authors");
        String affiliations = readTextOrArrayField(body, "affiliations");
        String paperAbstract = readTextField(body, "abstract");
        String keywords = readTextOrArrayField(body, "keywords");
        String contactDetails = readTextField(body, "contact_details");
        Long draftId = readLongField(body, "draft_id");
        String filename = readTextField(body, "filename");
        String contentBase64 = readTextField(body, "content_base64");

        HttpSession session = req.getSession(false);
        String authorEmail = session == null ? null : (String) session.getAttribute("user_email");

        PaperSubmissionResult result = paperSubmissionService.submit(
                authorEmail,
                new PaperSubmissionRequest(
                        draftId,
                        title,
                        authors,
                        affiliations,
                        paperAbstract,
                        keywords,
                        contactDetails,
                        new ManuscriptFile(filename, contentBase64)
                )
        );

        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{\"message\":\"" + escapeJson(result.getMessage()) + "\"";
        if (result.getRedirectLocation() != null) {
            payload += ",\"redirect\":\"" + escapeJson(result.getRedirectLocation()) + "\"";
        }
        if (result.getSubmissionId() != null) {
            payload += ",\"submission_id\":" + result.getSubmissionId();
        }
        payload += "}";
        resp.getWriter().write(payload);
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

    private String readTextOrArrayField(String body, String fieldName) {
        String text = readTextField(body, fieldName);
        if (text != null) {
            return text;
        }
        if (body == null || body.isEmpty()) {
            return null;
        }

        Pattern arrayPattern = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*\\[(.*?)\\]");
        Matcher arrayMatcher = arrayPattern.matcher(body);
        if (!arrayMatcher.find()) {
            return null;
        }

        String rawItems = arrayMatcher.group(1);
        Matcher itemMatcher = Pattern.compile("\\\"([^\\\"]*)\\\"").matcher(rawItems);
        StringBuilder builder = new StringBuilder();
        while (itemMatcher.find()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(itemMatcher.group(1));
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    private Long readLongField(String body, String fieldName) {
        if (body == null || body.isEmpty()) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*(-?\\d+)");
        Matcher matcher = pattern.matcher(body);
        return matcher.find() ? Long.parseLong(matcher.group(1)) : null;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
