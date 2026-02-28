package com.ece493.cms.service;

import com.ece493.cms.model.MetadataValidationResult;
import com.ece493.cms.model.PaperSubmissionRequest;

import java.util.regex.Pattern;

public class DefaultMetadataValidationService implements MetadataValidationService {
    private static final Pattern SIMPLE_EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @Override
    public MetadataValidationResult validate(PaperSubmissionRequest request) {
        if (request == null
                || isBlank(request.getTitle())
                || isBlank(request.getAuthors())
                || isBlank(request.getAffiliations())
                || isBlank(request.getPaperAbstract())
                || isBlank(request.getKeywords())
                || isBlank(request.getContactDetails())) {
            return MetadataValidationResult.invalid("Missing required information. Fill title, authors, affiliations, abstract, keywords, and contact details.");
        }

        if (containsInvalidCharacters(request.getTitle()) || containsInvalidCharacters(request.getAuthors())) {
            return MetadataValidationResult.invalid("Invalid metadata format. Please correct highlighted fields and retry.");
        }

        if (!SIMPLE_EMAIL.matcher(request.getContactDetails().trim()).matches()) {
            return MetadataValidationResult.invalid("Invalid metadata format. Please provide a valid contact email.");
        }

        return MetadataValidationResult.valid();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean containsInvalidCharacters(String value) {
        return value != null && (value.contains("<") || value.contains(">"));
    }
}
