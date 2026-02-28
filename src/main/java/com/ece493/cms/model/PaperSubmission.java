package com.ece493.cms.model;

import java.time.Instant;

public class PaperSubmission {
    private final long submissionId;
    private final String authorEmail;
    private final String title;
    private final String authors;
    private final String affiliations;
    private final String paperAbstract;
    private final String keywords;
    private final String contactDetails;
    private final long manuscriptFileId;
    private final Instant submittedAt;

    public PaperSubmission(
            long submissionId,
            String authorEmail,
            String title,
            String authors,
            String affiliations,
            String paperAbstract,
            String keywords,
            String contactDetails,
            long manuscriptFileId,
            Instant submittedAt
    ) {
        this.submissionId = submissionId;
        this.authorEmail = authorEmail;
        this.title = title;
        this.authors = authors;
        this.affiliations = affiliations;
        this.paperAbstract = paperAbstract;
        this.keywords = keywords;
        this.contactDetails = contactDetails;
        this.manuscriptFileId = manuscriptFileId;
        this.submittedAt = submittedAt;
    }

    public long getSubmissionId() {
        return submissionId;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getAffiliations() {
        return affiliations;
    }

    public String getPaperAbstract() {
        return paperAbstract;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public long getManuscriptFileId() {
        return manuscriptFileId;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }
}
