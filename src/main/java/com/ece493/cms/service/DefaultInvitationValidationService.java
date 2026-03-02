package com.ece493.cms.service;

import java.util.regex.Pattern;

public class DefaultInvitationValidationService implements InvitationValidationService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Override
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isDuplicateInvitation(String paperId, String refereeEmail, ReviewInvitationRepository repository) {
        if (paperId == null || refereeEmail == null) {
            return false;
        }
        return repository.findByPaperIdAndRefereeEmail(paperId, refereeEmail).isPresent();
    }
}
