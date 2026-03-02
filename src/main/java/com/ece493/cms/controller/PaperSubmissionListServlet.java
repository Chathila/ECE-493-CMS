package com.ece493.cms.controller;

import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.repository.PaperSubmissionRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class PaperSubmissionListServlet extends HttpServlet {
    private final PaperSubmissionRepository paperSubmissionRepository;

    public PaperSubmissionListServlet(PaperSubmissionRepository paperSubmissionRepository) {
        this.paperSubmissionRepository = paperSubmissionRepository;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String authorEmail = session == null ? null : (String) session.getAttribute("user_email");
        if (authorEmail == null || authorEmail.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"message\":\"You must log in to view submissions.\"}");
            return;
        }

        List<PaperSubmission> submissions = paperSubmissionRepository.findAllByAuthorEmail(authorEmail);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json; charset=UTF-8");
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < submissions.size(); i++) {
            PaperSubmission submission = submissions.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{")
                    .append("\"submission_id\":").append(submission.getSubmissionId()).append(",")
                    .append("\"title\":\"").append(escapeJson(nonNull(submission.getTitle()))).append("\",")
                    .append("\"contact_details\":\"").append(escapeJson(nonNull(submission.getContactDetails()))).append("\",")
                    .append("\"manuscript_file_id\":").append(submission.getManuscriptFileId())
                    .append("}");
        }
        json.append("]");
        resp.getWriter().write(json.toString());
    }

    private String nonNull(String value) {
        return value == null ? "" : value;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
