package com.ece493.cms.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryNotificationService implements NotificationService {
    private final AtomicLong sentInvitationCount = new AtomicLong();
    private volatile boolean available = true;

    @Override
    public void sendReviewInvitations(String paperId, List<String> refereeEmails) {
        if (!available) {
            throw new IllegalStateException("Notification service unavailable");
        }
        sentInvitationCount.addAndGet(refereeEmails.size());
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public long sentInvitationCount() {
        return sentInvitationCount.get();
    }
}
