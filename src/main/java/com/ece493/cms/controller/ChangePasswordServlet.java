package com.ece493.cms.controller;

import com.ece493.cms.model.PasswordChangeRequest;
import com.ece493.cms.model.PasswordChangeResult;
import com.ece493.cms.service.PasswordChangeService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordServlet extends HttpServlet {
    private static final Pattern CURRENT_PASSWORD_PATTERN = Pattern.compile("\\\"current_password\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");
    private static final Pattern NEW_PASSWORD_PATTERN = Pattern.compile("\\\"new_password\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");
    private static final Pattern CONFIRM_PASSWORD_PATTERN = Pattern.compile("\\\"confirm_password\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");

    private final PasswordChangeService passwordChangeService;
    private final String changePasswordHtml;

    public ChangePasswordServlet(PasswordChangeService passwordChangeService, String changePasswordHtml) {
        this.passwordChangeService = passwordChangeService;
        this.changePasswordHtml = changePasswordHtml;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().write(changePasswordHtml);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readBody(req);
        String currentPassword = readField(body, CURRENT_PASSWORD_PATTERN);
        String newPassword = readField(body, NEW_PASSWORD_PATTERN);
        String confirmPassword = readField(body, CONFIRM_PASSWORD_PATTERN);

        HttpSession session = req.getSession(false);
        String email = session == null ? null : (String) session.getAttribute("user_email");

        PasswordChangeResult result = passwordChangeService.changePassword(
                email,
                new PasswordChangeRequest(currentPassword, newPassword, confirmPassword)
        );

        if (result.requiresRelogin() && session != null) {
            session.invalidate();
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
