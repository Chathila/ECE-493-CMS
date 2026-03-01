package com.ece493.cms.controller;

import com.ece493.cms.model.PaperSubmissionDraft;
import com.ece493.cms.repository.PaperSubmissionDraftRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class DraftListServlet extends HttpServlet {
    private final PaperSubmissionDraftRepository draftRepository;

    public DraftListServlet(PaperSubmissionDraftRepository draftRepository) {
        this.draftRepository = draftRepository;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String authorEmail = session == null ? null : (String) session.getAttribute("user_email");
        if (authorEmail == null || authorEmail.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJsonMessage(resp, "You must log in to view drafts.");
            return;
        }

        List<PaperSubmissionDraft> drafts = draftRepository.findAllByAuthorEmail(authorEmail);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json; charset=UTF-8");
        StringBuilder payload = new StringBuilder("[");
        for (int i = 0; i < drafts.size(); i++) {
            PaperSubmissionDraft draft = drafts.get(i);
            if (i > 0) {
                payload.append(",");
            }
            payload.append("{")
                    .append("\"draft_id\":").append(draft.getDraftId()).append(",")
                    .append("\"title\":\"").append(escapeJson(draft.getTitle())).append("\",")
                    .append("\"contact_details\":\"").append(escapeJson(draft.getContactDetails())).append("\"")
                    .append("}");
        }
        payload.append("]");
        resp.getWriter().write(payload.toString());
    }

    private void writeJsonMessage(HttpServletResponse resp, String message) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"message\":\"" + escapeJson(message) + "\"}");
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
