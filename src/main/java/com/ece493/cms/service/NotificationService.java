package com.ece493.cms.service;

import java.util.List;

public interface NotificationService {
    void sendReviewInvitations(String paperId, List<String> refereeEmails);
}
