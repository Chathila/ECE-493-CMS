package com.ece493.cms.model;

public class PaperSubmissionRequest {
    private final Long draftId;
    private final String title;
    private final String authors;
    private final String affiliations;
    private final String paperAbstract;
    private final String keywords;
    private final String contactDetails;
    private final ManuscriptFile manuscriptFile;

    public PaperSubmissionRequest(
            String title,
            String authors,
            String affiliations,
            String paperAbstract,
            String keywords,
            String contactDetails,
            ManuscriptFile manuscriptFile
    ) {
        this(null, title, authors, affiliations, paperAbstract, keywords, contactDetails, manuscriptFile);
    }

    public PaperSubmissionRequest(
            Long draftId,
            String title,
            String authors,
            String affiliations,
            String paperAbstract,
            String keywords,
            String contactDetails,
            ManuscriptFile manuscriptFile
    ) {
        this.draftId = draftId;
        this.title = title;
        this.authors = authors;
        this.affiliations = affiliations;
        this.paperAbstract = paperAbstract;
        this.keywords = keywords;
        this.contactDetails = contactDetails;
        this.manuscriptFile = manuscriptFile;
    }

    public Long getDraftId() {
        return draftId;
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

    public ManuscriptFile getManuscriptFile() {
        return manuscriptFile;
    }
}
