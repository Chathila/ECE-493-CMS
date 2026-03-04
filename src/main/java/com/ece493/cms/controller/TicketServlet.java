package com.ece493.cms.controller;

import com.ece493.cms.model.TicketStatusResult;
import com.ece493.cms.model.TicketViewResult;
import com.ece493.cms.service.TicketService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicketServlet extends HttpServlet {
    private final TicketService ticketService;

    public TicketServlet(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String paymentId = parsePath(req.getRequestURI(), "^/payments/([^/]+)/ticket$");
        if (paymentId == null) {
            writeError(resp, 404, "Endpoint not found.");
            return;
        }
        TicketStatusResult result = ticketService.generateAndDeliver(paymentId);
        writeStatus(resp, result);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ticketId = parsePath(req.getRequestURI(), "^/tickets/([^/]+)$");
        if (ticketId == null) {
            writeError(resp, 404, "Endpoint not found.");
            return;
        }
        TicketViewResult result = ticketService.viewTicket(ticketId);
        writeView(resp, result);
    }

    private void writeStatus(HttpServletResponse resp, TicketStatusResult result) throws IOException {
        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{\"message\":\"" + escapeJson(nonNull(result.getMessage())) + "\"";
        if (result.getTicketId() != null) {
            payload += ",\"ticket_id\":\"" + escapeJson(result.getTicketId()) + "\"";
        }
        if (result.getStatus() != null) {
            payload += ",\"status\":\"" + escapeJson(result.getStatus()) + "\"";
        }
        payload += "}";
        resp.getWriter().write(payload);
    }

    private void writeView(HttpServletResponse resp, TicketViewResult result) throws IOException {
        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{\"message\":\"" + escapeJson(nonNull(result.getMessage())) + "\"";
        if (result.getTicketId() != null) {
            payload += ",\"ticket_id\":\"" + escapeJson(result.getTicketId()) + "\"";
        }
        if (result.getContent() != null) {
            payload += ",\"content\":\"" + escapeJson(result.getContent()) + "\"";
        }
        payload += "}";
        resp.getWriter().write(payload);
    }

    private void writeError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"message\":\"" + escapeJson(nonNull(message)) + "\"}");
    }

    private String parsePath(String path, String expression) {
        if (path == null) {
            return null;
        }
        Matcher matcher = Pattern.compile(expression).matcher(path);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String nonNull(String value) {
        return value == null ? "" : value;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
