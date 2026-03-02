package com.ece493.cms.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryEmailDeliveryService implements EmailDeliveryService {
    public static final class SentEmail {
        private final String recipientEmail;
        private final String subject;
        private final String body;

        public SentEmail(String recipientEmail, String subject, String body) {
            this.recipientEmail = recipientEmail;
            this.subject = subject;
            this.body = body;
        }

        public String getRecipientEmail() {
            return recipientEmail;
        }

        public String getSubject() {
            return subject;
        }

        public String getBody() {
            return body;
        }
    }

    private final List<SentEmail> sentEmails = new ArrayList<>();
    private volatile boolean available = true;

    @Override
    public void send(String recipientEmail, String subject, String body) {
        if (!available) {
            throw new IllegalStateException("Email service unavailable");
        }
        sentEmails.add(new SentEmail(recipientEmail, subject, body));
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<SentEmail> sentEmails() {
        return Collections.unmodifiableList(sentEmails);
    }
}
