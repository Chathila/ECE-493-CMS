package com.ece493.cms.service;

import com.ece493.cms.model.DraftSaveRequest;

public class DefaultDraftValidationService implements DraftValidationService {
    @Override
    public String validate(DraftSaveRequest request) {
        if (request == null) {
            return "Invalid or incomplete draft data. Title and corresponding author email are required.";
        }
        if (isBlank(request.getTitle()) || isBlank(request.getContactDetails())) {
            return "Invalid or incomplete draft data. Title and corresponding author email are required.";
        }
        if (!isLikelyEmail(request.getContactDetails())) {
            return "Invalid or incomplete draft data. Please provide a valid corresponding author email.";
        }
        if (hasInvalidCharacters(request.getTitle())
                || hasInvalidCharacters(request.getAuthors())
                || hasInvalidCharacters(request.getAffiliations())
                || hasInvalidCharacters(request.getPaperAbstract())
                || hasInvalidCharacters(request.getKeywords())) {
            return "Invalid or incomplete draft data. Remove unsupported characters from draft fields.";
        }
        return null;
    }

    private boolean hasInvalidCharacters(String value) {
        return value != null && (value.contains("<") || value.contains(">"));
    }

    private boolean isLikelyEmail(String value) {
        String trimmed = value.trim();
        int at = trimmed.indexOf('@');
        int dot = trimmed.lastIndexOf('.');
        return at > 0 && dot > at + 1 && dot < trimmed.length() - 1;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
