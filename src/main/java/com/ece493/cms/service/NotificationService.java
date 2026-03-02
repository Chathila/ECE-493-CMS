package com.ece493.cms.service;

import java.util.List;

public interface NotificationService {
    default void sendReviewInvitations(String paperId, List<String> refereeEmails) {
        sendReviewInvitations(null, paperId, refereeEmails);
    }

    void sendReviewInvitations(String editorEmail, String paperId, List<String> refereeEmails);
}
