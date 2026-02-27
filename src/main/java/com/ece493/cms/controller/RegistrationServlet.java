package com.ece493.cms.controller;

import com.ece493.cms.model.RegistrationResult;
import com.ece493.cms.service.RegistrationService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationServlet extends HttpServlet {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\\"email\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("\\\"password\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");

    private final RegistrationService registrationService;
    private final String registerPageHtml;

    public RegistrationServlet(RegistrationService registrationService, String registerPageHtml) {
        this.registrationService = registrationService;
        this.registerPageHtml = registerPageHtml;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().write(registerPageHtml);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readBody(req);
        String email = readField(body, EMAIL_PATTERN);
        String password = readField(body, PASSWORD_PATTERN);

        RegistrationResult result = registrationService.register(email, password);
        if (result.isRedirect()) {
            resp.setStatus(HttpServletResponse.SC_FOUND);
            resp.setHeader("Location", result.getRedirectLocation());
            return;
        }

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

    private String readField(String body, Pattern pattern) {
        if (body == null || body.isEmpty()) {
            return null;
        }
        Matcher matcher = pattern.matcher(body);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
