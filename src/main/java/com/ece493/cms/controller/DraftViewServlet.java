package com.ece493.cms.controller;

import com.ece493.cms.model.PaperSubmissionDraft;
import com.ece493.cms.repository.PaperSubmissionDraftRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

public class DraftViewServlet extends HttpServlet {
    private final PaperSubmissionDraftRepository draftRepository;

    public DraftViewServlet(PaperSubmissionDraftRepository draftRepository) {
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

        Optional<PaperSubmissionDraft> draft = draftRepository.findByAuthorEmail(authorEmail);
        if (draft.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeJsonMessage(resp, "No draft found.");
            return;
        }

        PaperSubmissionDraft value = draft.get();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(
                "{"
                        + "\"title\":\"" + escapeJson(value.getTitle()) + "\","
                        + "\"authors\":\"" + escapeJson(nullToEmpty(value.getAuthors())) + "\","
                        + "\"affiliations\":\"" + escapeJson(nullToEmpty(value.getAffiliations())) + "\","
                        + "\"abstract\":\"" + escapeJson(nullToEmpty(value.getPaperAbstract())) + "\","
                        + "\"keywords\":\"" + escapeJson(nullToEmpty(value.getKeywords())) + "\","
                        + "\"contact_details\":\"" + escapeJson(value.getContactDetails()) + "\""
                        + "}"
        );
    }

    private void writeJsonMessage(HttpServletResponse resp, String message) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"message\":\"" + escapeJson(message) + "\"}");
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
