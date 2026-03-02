package com.ece493.cms.controller;

import com.ece493.cms.model.ManuscriptFile;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.repository.PaperSubmissionRepository;
import com.ece493.cms.service.FileStorageService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaperDetailsServlet extends HttpServlet {
    private final PaperSubmissionRepository paperSubmissionRepository;
    private final FileStorageService fileStorageService;

    public PaperDetailsServlet(PaperSubmissionRepository paperSubmissionRepository, FileStorageService fileStorageService) {
        this.paperSubmissionRepository = paperSubmissionRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String userEmail = session == null ? null : (String) session.getAttribute("user_email");
        if (isBlank(userEmail)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"message\":\"You must log in to access paper details.\"}");
            return;
        }

        String uri = req.getRequestURI();
        Long submissionId = parseId(uri, "^/papers/details/([^/]+)$");
        if (submissionId != null) {
            writePaperDetails(resp, submissionId);
            return;
        }

        Long fileId = parseId(uri, "^/papers/files/([^/]+)$");
        if (fileId != null) {
            writeFileDownload(resp, fileId);
            return;
        }

        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"message\":\"Unsupported paper details path.\"}");
    }

    private void writePaperDetails(HttpServletResponse resp, long submissionId) throws IOException {
        Optional<PaperSubmission> paperOptional = paperSubmissionRepository.findBySubmissionId(submissionId);
        if (paperOptional.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"message\":\"Paper not found.\"}");
            return;
        }

        PaperSubmission paper = paperOptional.get();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json; charset=UTF-8");
        String payload = "{"
                + "\"submission_id\":" + paper.getSubmissionId() + ","
                + "\"title\":\"" + escapeJson(nonNull(paper.getTitle())) + "\","
                + "\"authors\":\"" + escapeJson(nonNull(paper.getAuthors())) + "\","
                + "\"affiliations\":\"" + escapeJson(nonNull(paper.getAffiliations())) + "\","
                + "\"abstract_text\":\"" + escapeJson(nonNull(paper.getPaperAbstract())) + "\","
                + "\"keywords\":\"" + escapeJson(nonNull(paper.getKeywords())) + "\","
                + "\"contact_details\":\"" + escapeJson(nonNull(paper.getContactDetails())) + "\","
                + "\"download_url\":\"/papers/files/" + paper.getManuscriptFileId() + "\""
                + "}";
        resp.getWriter().write(payload);
    }

    private void writeFileDownload(HttpServletResponse resp, long fileId) throws IOException {
        Optional<ManuscriptFile> manuscriptFile = fileStorageService.findById(fileId);
        if (manuscriptFile.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"message\":\"Paper file not found.\"}");
            return;
        }

        ManuscriptFile file = manuscriptFile.get();
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(nonNull(file.getContentBase64()));
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"message\":\"Paper file could not be decoded.\"}");
            return;
        }

        String filename = nonNull(file.getFilename());
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(contentTypeFor(filename));
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + safeFilename(filename) + "\"");
        resp.getOutputStream().write(bytes);
    }

    private Long parseId(String uri, String pattern) {
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

    private String contentTypeFor(String filename) {
        String normalized = filename.toLowerCase();
        if (normalized.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (normalized.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        return "application/octet-stream";
    }

    private String safeFilename(String filename) {
        return nonNull(filename).replace("\\", "_").replace("\"", "_");
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
