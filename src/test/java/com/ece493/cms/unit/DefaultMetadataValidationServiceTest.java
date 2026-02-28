package com.ece493.cms.unit;

import com.ece493.cms.model.ManuscriptFile;
import com.ece493.cms.model.MetadataValidationResult;
import com.ece493.cms.model.PaperSubmissionRequest;
import com.ece493.cms.service.DefaultMetadataValidationService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class DefaultMetadataValidationServiceTest {
    private final DefaultMetadataValidationService service = new DefaultMetadataValidationService();

    @Test
    void rejectsMissingRequiredMetadata() {
        MetadataValidationResult result = service.validate(new PaperSubmissionRequest(
                "",
                "A",
                "U",
                "Abstract",
                "k1",
                "author@cms.com",
                new ManuscriptFile("paper.pdf", "ZGF0YQ==")
        ));

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Missing required"));
    }

    @Test
    void rejectsNullRequest() {
        MetadataValidationResult result = service.validate(null);

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Missing required"));
    }

    @Test
    void rejectsMissingAuthors() {
        MetadataValidationResult result = service.validate(new PaperSubmissionRequest(
                "Valid title",
                "",
                "Uni",
                "Abstract",
                "k1",
                "author@cms.com",
                new ManuscriptFile("paper.pdf", "ZGF0YQ==")
        ));

        assertFalse(result.isValid());
    }

    @Test
    void rejectsMissingAffiliations() {
        MetadataValidationResult result = service.validate(new PaperSubmissionRequest(
                "Valid title",
                "Author",
                "",
                "Abstract",
                "k1",
                "author@cms.com",
                new ManuscriptFile("paper.pdf", "ZGF0YQ==")
        ));

        assertFalse(result.isValid());
    }

    @Test
    void rejectsMissingAbstract() {
        MetadataValidationResult result = service.validate(new PaperSubmissionRequest(
                "Valid title",
                "Author",
                "Uni",
                "",
                "k1",
                "author@cms.com",
                new ManuscriptFile("paper.pdf", "ZGF0YQ==")
        ));

        assertFalse(result.isValid());
    }

    @Test
    void rejectsMissingKeywords() {
        MetadataValidationResult result = service.validate(new PaperSubmissionRequest(
                "Valid title",
                "Author",
                "Uni",
                "Abstract",
                "",
                "author@cms.com",
                new ManuscriptFile("paper.pdf", "ZGF0YQ==")
        ));

        assertFalse(result.isValid());
    }

    @Test
    void rejectsMissingContactDetails() {
        MetadataValidationResult result = service.validate(new PaperSubmissionRequest(
                "Valid title",
                "Author",
                "Uni",
                "Abstract",
                "k1",
                "",
                new ManuscriptFile("paper.pdf", "ZGF0YQ==")
        ));

        assertFalse(result.isValid());
    }

    @Test
    void rejectsInvalidCharacters() {
        MetadataValidationResult result = service.validate(new PaperSubmissionRequest(
                "<bad>",
                "Author",
                "Uni",
                "Abstract",
                "k1",
                "author@cms.com",
                new ManuscriptFile("paper.pdf", "ZGF0YQ==")
        ));

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Invalid metadata format"));
    }

    @Test
    void rejectsInvalidCharactersInAuthors() {
        MetadataValidationResult result = service.validate(new PaperSubmissionRequest(
                "Valid title",
                "Author > bad",
                "Uni",
                "Abstract",
                "k1",
                "author@cms.com",
                new ManuscriptFile("paper.pdf", "ZGF0YQ==")
        ));

        assertFalse(result.isValid());
    }

    @Test
    void helperIsBlankHandlesNull() throws Exception {
        Method method = DefaultMetadataValidationService.class.getDeclaredMethod("isBlank", String.class);
        method.setAccessible(true);

        Object result = method.invoke(service, new Object[]{null});

        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void helperContainsInvalidCharactersHandlesNull() throws Exception {
        Method method = DefaultMetadataValidationService.class.getDeclaredMethod("containsInvalidCharacters", String.class);
        method.setAccessible(true);

        Object result = method.invoke(service, new Object[]{null});

        assertEquals(Boolean.FALSE, result);
    }

    @Test
    void rejectsInvalidContactEmail() {
        MetadataValidationResult result = service.validate(new PaperSubmissionRequest(
                "Valid title",
                "Author",
                "Uni",
                "Abstract",
                "k1",
                "not-an-email",
                new ManuscriptFile("paper.pdf", "ZGF0YQ==")
        ));

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("contact email"));
    }

    @Test
    void acceptsValidMetadata() {
        MetadataValidationResult result = service.validate(new PaperSubmissionRequest(
                "Valid title",
                "Author",
                "Uni",
                "Abstract",
                "k1",
                "author@cms.com",
                new ManuscriptFile("paper.pdf", "ZGF0YQ==")
        ));

        assertTrue(result.isValid());
        assertNull(result.getMessage());
    }
}
