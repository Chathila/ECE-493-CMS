package com.ece493.cms.service;

public interface InvitationValidationService {
    boolean isValidEmail(String email);

    boolean isDuplicateInvitation(String paperId, String refereeEmail, ReviewInvitationRepository repository);
}
