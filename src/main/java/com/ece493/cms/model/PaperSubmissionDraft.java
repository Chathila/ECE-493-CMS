package com.ece493.cms.model;

import java.time.Instant;

public class PaperSubmissionDraft {
    private final long draftId;
    private final String authorEmail;
    private final String title;
    private final String authors;
    private final String affiliations;
    private final String paperAbstract;
    private final String keywords;
    private final String contactDetails;
    private final Instant updatedAt;

    public PaperSubmissionDraft(
            long draftId,
            String authorEmail,
            String title,
            String authors,
            String affiliations,
            String paperAbstract,
            String keywords,
            String contactDetails,
            Instant updatedAt
    ) {
        this.draftId = draftId;
        this.authorEmail = authorEmail;
        this.title = title;
        this.authors = authors;
        this.affiliations = affiliations;
        this.paperAbstract = paperAbstract;
        this.keywords = keywords;
        this.contactDetails = contactDetails;
        this.updatedAt = updatedAt;
    }

    public long getDraftId() {
        return draftId;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
