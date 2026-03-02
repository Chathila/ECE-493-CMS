package com.ece493.cms.controller;

import com.ece493.cms.model.FileValidationResult;
import com.ece493.cms.model.ManuscriptFile;
import com.ece493.cms.service.FileValidationService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileValidationServlet extends HttpServlet {
    private final FileValidationService fileValidationService;

    public FileValidationServlet(FileValidationService fileValidationService) {
        this.fileValidationService = fileValidationService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readBody(req);
        String filename = readTextField(body, "filename");
        String contentBase64 = readTextField(body, "content_base64");

        HttpSession session = req.getSession(false);
        String authorEmail = session == null ? null : (String) session.getAttribute("user_email");

        FileValidationResult result = fileValidationService.validate(authorEmail, new ManuscriptFile(filename, contentBase64));

        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"message\":\"" + escapeJson(result.getMessage()) + "\"}");
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

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
