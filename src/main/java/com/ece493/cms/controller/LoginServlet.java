package com.ece493.cms.controller;

import com.ece493.cms.model.LoginResult;
import com.ece493.cms.model.LoginSubmission;
import com.ece493.cms.service.AuthenticationService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginServlet extends HttpServlet {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\\"email\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("\\\"password\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");

    private final AuthenticationService authenticationService;
    private final String loginPageHtml;

    public LoginServlet(AuthenticationService authenticationService, String loginPageHtml) {
        this.authenticationService = authenticationService;
        this.loginPageHtml = loginPageHtml;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().write(loginPageHtml);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readBody(req);
        String email = readField(body, EMAIL_PATTERN);
        String password = readField(body, PASSWORD_PATTERN);

        LoginResult result = authenticationService.authenticate(new LoginSubmission(email, password));
        if (result.isRedirect()) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user_email", result.getAuthenticatedEmail() == null ? email : result.getAuthenticatedEmail());
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
