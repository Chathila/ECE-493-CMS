package com.ece493.cms.controller;

import com.ece493.cms.model.PaymentProcessingResult;
import com.ece493.cms.service.PaymentProcessingService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationPaymentServlet extends HttpServlet {
    private final PaymentProcessingService paymentProcessingService;

    public RegistrationPaymentServlet(PaymentProcessingService paymentProcessingService) {
        this.paymentProcessingService = paymentProcessingService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!"/registration/payments".equals(req.getRequestURI())) {
            writeError(resp, 404, "Endpoint not found.");
            return;
        }
        String attendeeEmail = sessionEmail(req.getSession(false));
        if (isBlank(attendeeEmail)) {
            writeError(resp, 401, "You must log in to pay registration fee.");
            return;
        }

        String body = readBody(req);
        String registrationType = parseTextField(body, "registration_type");
        String paymentDetails = parseObject(body, "payment_details");
        PaymentProcessingResult result = paymentProcessingService.process(attendeeEmail, registrationType, paymentDetails);
        writeResult(resp, result);
    }

    private void writeResult(HttpServletResponse resp, PaymentProcessingResult result) throws IOException {
        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{\"message\":\"" + escapeJson(nonNull(result.getMessage())) + "\"";
        if (result.getPaymentId() != null) {
            payload += ",\"payment_id\":\"" + escapeJson(result.getPaymentId()) + "\"";
        }
        if (result.getStatus() != null) {
            payload += ",\"status\":\"" + escapeJson(result.getStatus()) + "\"";
        }
        payload += "}";
        resp.getWriter().write(payload);
    }

    private void writeError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"message\":\"" + escapeJson(nonNull(message)) + "\"}");
    }

    private String parseTextField(String body, String fieldName) {
        if (body == null) {
            return null;
        }
        Matcher matcher = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"")
                .matcher(body);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String parseObject(String body, String fieldName) {
        if (body == null) {
            return null;
        }
        Matcher matcher = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*(\\{.*\\})")
                .matcher(body);
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

    private String sessionEmail(HttpSession session) {
        return session == null ? null : (String) session.getAttribute("user_email");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String nonNull(String value) {
        return value == null ? "" : value;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
