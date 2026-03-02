package com.ece493.cms.model;

public class AcceptedPaper {
    private final String paperId;
    private final String title;

    public AcceptedPaper(String paperId, String title) {
        this.paperId = paperId;
        this.title = title;
    }

    public String getPaperId() {
        return paperId;
    }

    public String getTitle() {
        return title;
    }
}
