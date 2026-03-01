package com.ece493.cms.unit;

import com.ece493.cms.model.DraftSaveRequest;
import com.ece493.cms.service.DefaultDraftValidationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultDraftValidationServiceTest {
    @Test
    void rejectsNullRequest() {
        DefaultDraftValidationService service = new DefaultDraftValidationService();

        String message = service.validate(null);

        assertTrue(message.contains("Title and corresponding author email"));
    }

    @Test
    void rejectsMissingRequiredFields() {
        DefaultDraftValidationService service = new DefaultDraftValidationService();

        String message = service.validate(new DraftSaveRequest(null, "", "A", "U", "Abstract", "k", ""));

        assertTrue(message.contains("Title and corresponding author email"));
    }

    @Test
    void rejectsMissingRequiredFieldsWhenNull() {
        DefaultDraftValidationService service = new DefaultDraftValidationService();

        String message = service.validate(new DraftSaveRequest(null, null, "A", "U", "Abstract", "k", "a@b.com"));

        assertTrue(message.contains("Title and corresponding author email"));
    }

    @Test
    void rejectsInvalidEmail() {
        DefaultDraftValidationService service = new DefaultDraftValidationService();

        String message = service.validate(new DraftSaveRequest(null, "T", "A", "U", "Abstract", "k", "not-email"));

        assertTrue(message.contains("valid corresponding author email"));
    }

    @Test
    void rejectsInvalidCharacters() {
        DefaultDraftValidationService service = new DefaultDraftValidationService();

        String message = service.validate(new DraftSaveRequest(null, "T<bad>", "A", "U", "Abstract", "k", "a@b.com"));

        assertTrue(message.contains("unsupported characters"));
    }

    @Test
    void rejectsInvalidCharactersInAuthorsAndAffiliationsAndAbstractAndKeywords() {
        DefaultDraftValidationService service = new DefaultDraftValidationService();

        String authorMessage = service.validate(new DraftSaveRequest(null, "Title", "A<bad>", "U", "Abstract", "k", "a@b.com"));
        String affiliationMessage = service.validate(new DraftSaveRequest(null, "Title", "A", "U<bad>", "Abstract", "k", "a@b.com"));
        String abstractMessage = service.validate(new DraftSaveRequest(null, "Title", "A", "U", "Abstract>bad", "k", "a@b.com"));
        String keywordsMessage = service.validate(new DraftSaveRequest(null, "Title", "A", "U", "Abstract", "k>bad", "a@b.com"));

        assertTrue(authorMessage.contains("unsupported characters"));
        assertTrue(affiliationMessage.contains("unsupported characters"));
        assertTrue(abstractMessage.contains("unsupported characters"));
        assertTrue(keywordsMessage.contains("unsupported characters"));
    }

    @Test
    void rejectsBlankAndMalformedEmailVariants() {
        DefaultDraftValidationService service = new DefaultDraftValidationService();

        String blank = service.validate(new DraftSaveRequest(null, "Title", "A", "U", "Abstract", "k", " "));
        String missingAt = service.validate(new DraftSaveRequest(null, "Title", "A", "U", "Abstract", "k", "ab.com"));
        String missingDot = service.validate(new DraftSaveRequest(null, "Title", "A", "U", "Abstract", "k", "a@bcom"));
        String trailingDot = service.validate(new DraftSaveRequest(null, "Title", "A", "U", "Abstract", "k", "a@b."));

        assertTrue(blank.contains("Title and corresponding author email"));
        assertTrue(missingAt.contains("valid corresponding author email"));
        assertTrue(missingDot.contains("valid corresponding author email"));
        assertTrue(trailingDot.contains("valid corresponding author email"));
    }

    @Test
    void returnsNullForValidDraft() {
        DefaultDraftValidationService service = new DefaultDraftValidationService();

        String message = service.validate(new DraftSaveRequest(null, "Title", "", "", "", "", "a@b.com"));

        assertNull(message);
    }

    @Test
    void allowsNullOptionalFieldsWithoutInvalidCharacterFailure() {
        DefaultDraftValidationService service = new DefaultDraftValidationService();

        String message = service.validate(new DraftSaveRequest(null, "Title", null, null, null, null, "a@b.com"));

        assertNull(message);
    }
}
