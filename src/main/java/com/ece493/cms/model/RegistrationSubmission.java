package com.ece493.cms.model;

import java.time.Instant;

public class RegistrationSubmission {
    private final String emailInput;
    private final String passwordInput;
    private final Instant submittedAt;

    public RegistrationSubmission(String emailInput, String passwordInput, Instant submittedAt) {
        this.emailInput = emailInput;
        this.passwordInput = passwordInput;
        this.submittedAt = submittedAt;
    }

    public String getEmailInput() {
        return emailInput;
    }

    public String getPasswordInput() {
        return passwordInput;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }
}
