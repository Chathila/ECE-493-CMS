package com.ece493.cms.service;

public class InvitationComposer {
    public String compose(String paperId) {
        String title = "Paper " + paperId;
        String paperAbstract = "Abstract for paper " + paperId + ".";
        String instructions = "Please accept or reject this review request.";
        return "Title: " + title + "\n"
                + "Abstract: " + paperAbstract + "\n"
                + instructions;
    }
}
