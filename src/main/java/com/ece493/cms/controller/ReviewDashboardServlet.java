package com.ece493.cms.controller;

import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.model.ReviewInvitation;
import com.ece493.cms.repository.PaperSubmissionRepository;
import com.ece493.cms.service.ReviewAssignmentService;
import com.ece493.cms.service.ReviewInvitationRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReviewDashboardServlet extends HttpServlet {
    private final ReviewInvitationRepository reviewInvitationRepository;
    private final ReviewAssignmentService reviewAssignmentService;
    private final PaperSubmissionRepository paperSubmissionRepository;

    public ReviewDashboardServlet(
            ReviewInvitationRepository reviewInvitationRepository,
            ReviewAssignmentService reviewAssignmentService,
            PaperSubmissionRepository paperSubmissionRepository
    ) {
        this.reviewInvitationRepository = reviewInvitationRepository;
        this.reviewAssignmentService = reviewAssignmentService;
        this.paperSubmissionRepository = paperSubmissionRepository;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String userEmail = session == null ? null : (String) session.getAttribute("user_email");
        if (isBlank(userEmail)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"message\":\"You must log in to view review requests.\"}");
            return;
        }

        List<ReviewInvitation> invitations = reviewInvitationRepository.findAll();
        List<ReviewInvitation> reviewerInvitations = new ArrayList<>();
        List<ReviewInvitation> editorInvitations = new ArrayList<>();
        for (ReviewInvitation invitation : invitations) {
            if (userEmail.equalsIgnoreCase(invitation.getRefereeEmail())) {
                reviewerInvitations.add(invitation);
            }
            if (userEmail.equalsIgnoreCase(invitation.getEditorEmail())) {
                editorInvitations.add(invitation);
            }
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json; charset=UTF-8");
        String body = "{"
                + "\"review_requests\":" + reviewerRequestsJson(reviewerInvitations) + ","
                + "\"editor_papers\":" + editorPapersJson(editorInvitations)
                + "}";
        resp.getWriter().write(body);
    }

    private String reviewerRequestsJson(List<ReviewInvitation> invitations) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < invitations.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            ReviewInvitation invitation = invitations.get(i);
            Optional<PaperSubmission> paper = findPaper(invitation.getPaperId());
            json.append("{")
                    .append("\"invitation_id\":").append(invitation.getInvitationId()).append(",")
                    .append("\"paper_id\":\"").append(escapeJson(nonNull(invitation.getPaperId()))).append("\",")
                    .append("\"status\":\"").append(escapeJson(resolveStatus(invitation))).append("\",")
                    .append("\"paper_title\":\"").append(escapeJson(nonNull(paper.map(PaperSubmission::getTitle).orElse(null)))).append("\",")
                    .append("\"authors\":\"").append(escapeJson(nonNull(paper.map(PaperSubmission::getAuthors).orElse(null)))).append("\",")
                    .append("\"affiliations\":\"").append(escapeJson(nonNull(paper.map(PaperSubmission::getAffiliations).orElse(null)))).append("\",")
                    .append("\"abstract_text\":\"").append(escapeJson(nonNull(paper.map(PaperSubmission::getPaperAbstract).orElse(null)))).append("\",")
                    .append("\"keywords\":\"").append(escapeJson(nonNull(paper.map(PaperSubmission::getKeywords).orElse(null)))).append("\",")
                    .append("\"contact_details\":\"").append(escapeJson(nonNull(paper.map(PaperSubmission::getContactDetails).orElse(null)))).append("\",")
                    .append("\"download_url\":\"").append(escapeJson(paper.map(v -> "/papers/files/" + v.getManuscriptFileId()).orElse(""))).append("\"")
                    .append("}");
        }
        json.append("]");
        return json.toString();
    }

    private String editorPapersJson(List<ReviewInvitation> invitations) {
        Map<String, List<ReviewInvitation>> grouped = new LinkedHashMap<>();
        for (ReviewInvitation invitation : invitations) {
            grouped.computeIfAbsent(invitation.getPaperId(), key -> new ArrayList<>()).add(invitation);
        }

        StringBuilder json = new StringBuilder("[");
        int i = 0;
        for (Map.Entry<String, List<ReviewInvitation>> entry : grouped.entrySet()) {
            if (i > 0) {
                json.append(",");
            }
            i++;

            String paperId = entry.getKey();
            List<ReviewInvitation> paperInvitations = entry.getValue();
            Optional<PaperSubmission> paper = findPaper(paperId);

            List<String> approvedReviewers = new ArrayList<>();
            StringBuilder reviewersJson = new StringBuilder("[");
            for (int j = 0; j < paperInvitations.size(); j++) {
                if (j > 0) {
                    reviewersJson.append(",");
                }
                ReviewInvitation invitation = paperInvitations.get(j);
                String status = resolveStatus(invitation);
                if ("approved".equals(status)) {
                    approvedReviewers.add(invitation.getRefereeEmail());
                }
                reviewersJson.append("{")
                        .append("\"email\":\"").append(escapeJson(nonNull(invitation.getRefereeEmail()))).append("\",")
                        .append("\"status\":\"").append(escapeJson(status)).append("\"")
                        .append("}");
            }
            reviewersJson.append("]");

            json.append("{")
                    .append("\"paper_id\":\"").append(escapeJson(nonNull(paperId))).append("\",")
                    .append("\"paper_title\":\"").append(escapeJson(nonNull(paper.map(PaperSubmission::getTitle).orElse(null)))).append("\",")
                    .append("\"authors\":\"").append(escapeJson(nonNull(paper.map(PaperSubmission::getAuthors).orElse(null)))).append("\",")
                    .append("\"affiliations\":\"").append(escapeJson(nonNull(paper.map(PaperSubmission::getAffiliations).orElse(null)))).append("\",")
                    .append("\"abstract_text\":\"").append(escapeJson(nonNull(paper.map(PaperSubmission::getPaperAbstract).orElse(null)))).append("\",")
                    .append("\"keywords\":\"").append(escapeJson(nonNull(paper.map(PaperSubmission::getKeywords).orElse(null)))).append("\",")
                    .append("\"contact_details\":\"").append(escapeJson(nonNull(paper.map(PaperSubmission::getContactDetails).orElse(null)))).append("\",")
                    .append("\"approved_reviewers\":").append(stringArrayJson(approvedReviewers)).append(",")
                    .append("\"reviewers\":").append(reviewersJson)
                    .append("}");
        }
        json.append("]");
        return json.toString();
    }

    private String resolveStatus(ReviewInvitation invitation) {
        if ("expired".equalsIgnoreCase(invitation.getStatus()) || invitation.isExpired(Instant.now())) {
            return "expired";
        }
        return reviewAssignmentService.getStatus(invitation.getInvitationId());
    }

    private Optional<PaperSubmission> findPaper(String paperId) {
        if (isBlank(paperId)) {
            return Optional.empty();
        }
        try {
            long submissionId = Long.parseLong(paperId);
            return paperSubmissionRepository.findBySubmissionId(submissionId);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private String stringArrayJson(List<String> values) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append("\"").append(escapeJson(nonNull(values.get(i)))).append("\"");
        }
        json.append("]");
        return json.toString();
    }

    private String nonNull(String value) {
        return value == null ? "" : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
