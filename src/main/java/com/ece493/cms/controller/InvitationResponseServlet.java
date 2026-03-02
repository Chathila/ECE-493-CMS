package com.ece493.cms.controller;

import com.ece493.cms.model.InvitationResponseResult;
import com.ece493.cms.service.InvitationResponseService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvitationResponseServlet extends HttpServlet {
    private final InvitationResponseService invitationResponseService;

    public InvitationResponseServlet(InvitationResponseService invitationResponseService) {
        this.invitationResponseService = invitationResponseService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long invitationId = parseInvitationIdForView(req.getRequestURI());
        if (invitationId == null) {
            writeResponse(resp, InvitationResponseResult.error(400, "Invitation id is required."));
            return;
        }
        writeResponse(resp, invitationResponseService.viewInvitation(invitationId));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestUri = req.getRequestURI();
        Long invitationId = parseInvitationIdForResponse(requestUri);
        boolean approvalPath = false;
        if (invitationId == null) {
            invitationId = parseInvitationIdForApproval(requestUri);
            approvalPath = invitationId != null;
        }
        if (invitationId == null) {
            writeResponse(resp, InvitationResponseResult.error(400, "Invitation id is required."));
            return;
        }

        InvitationResponseResult result;
        if (approvalPath) {
            result = invitationResponseService.submitApproval(invitationId);
        } else {
            String decision = readDecision(readBody(req));
            result = invitationResponseService.submitResponse(invitationId, decision);
        }
        writeResponse(resp, result);
    }

    private void writeResponse(HttpServletResponse resp, InvitationResponseResult result) throws IOException {
        resp.setStatus(result.getStatusCode());
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{\"message\":\"" + escapeJson(result.getMessage()) + "\"";
        if (result.getAssignmentStatus() != null) {
            payload += ",\"assignment_status\":\"" + escapeJson(result.getAssignmentStatus()) + "\"";
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

    private String readDecision(String body) {
        if (body == null || body.isEmpty()) {
            return null;
        }
        Matcher matcher = Pattern.compile("\\\"decision\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(body);
        if (!matcher.find()) {
            return null;
        }
        String decision = matcher.group(1);
        return decision.trim().toLowerCase();
    }

    private Long parseInvitationIdForResponse(String uri) {
        return parseInvitationId(uri, "^/invitations/([^/]+)/response$");
    }

    private Long parseInvitationIdForView(String uri) {
        return parseInvitationId(uri, "^/invitations/([^/]+)$");
    }

    private Long parseInvitationIdForApproval(String uri) {
        return parseInvitationId(uri, "^/invitations/([^/]+)/approval$");
    }

    private Long parseInvitationId(String uri, String pattern) {
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

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
