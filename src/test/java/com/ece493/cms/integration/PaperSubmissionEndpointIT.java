package com.ece493.cms.integration;

import com.ece493.cms.repository.JdbcPaperSubmissionDraftRepository;
import com.ece493.cms.repository.JdbcPaperSubmissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaperSubmissionEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void getSubmitPaperReturnsHtmlForm() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = getPaperSubmissionPage();

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Submit Paper"));
    }

    @Test
    void postSubmitPaperSuccessStoresSubmissionAndFile() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postPaperSubmission(validPayload(), loggedInSession("author@cms.com"));

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Paper submitted successfully"));
        assertTrue(response.getBody().contains("\"submission_id\":"));
        assertEquals(1L, new JdbcPaperSubmissionRepository(dataSource).countAll());
        assertEquals(1L, fileStorageService.storedFileCount());
    }

    @Test
    void postSubmitPaperWithDraftIdRemovesDraft() throws Exception {
        ServletHttpTestSupport.ResponseCapture draftCreate = postDraftSave(validDraftPayload(), loggedInSession("author@cms.com"));
        String draftId = extractDraftId(draftCreate.getBody());

        ServletHttpTestSupport.ResponseCapture response = postPaperSubmission(validPayloadWithDraftId(draftId), loggedInSession("author@cms.com"));

        assertEquals(200, response.getStatus());
        assertEquals(1L, new JdbcPaperSubmissionRepository(dataSource).countAll());
        assertEquals(0L, new JdbcPaperSubmissionDraftRepository(dataSource).countAll());
    }

    @Test
    void postSubmitPaperMissingMetadataReturns400() throws Exception {
        String payload = "{\"title\":\"\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"ZGF0YQ==\"}}";

        ServletHttpTestSupport.ResponseCapture response = postPaperSubmission(payload, loggedInSession("author@cms.com"));

        assertEquals(400, response.getStatus());
        assertEquals(0L, new JdbcPaperSubmissionRepository(dataSource).countAll());
        assertEquals(0L, fileStorageService.storedFileCount());
    }

    @Test
    void postSubmitPaperUnsupportedFormatReturns415() throws Exception {
        String payload = "{\"title\":\"T\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.txt\",\"content_base64\":\"ZGF0YQ==\"}}";

        ServletHttpTestSupport.ResponseCapture response = postPaperSubmission(payload, loggedInSession("author@cms.com"));

        assertEquals(415, response.getStatus());
        assertTrue(response.getBody().contains("PDF and DOCX"));
    }

    @Test
    void postSubmitPaperOversizeReturns413() throws Exception {
        String largeBase64 = "A".repeat((20 * 1024 * 1024) + 1);
        String payload = "{\"title\":\"T\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"" + largeBase64 + "\"}}";

        ServletHttpTestSupport.ResponseCapture response = postPaperSubmission(payload, loggedInSession("author@cms.com"));

        assertEquals(413, response.getStatus());
        assertTrue(response.getBody().contains("20 MB"));
    }

    @Test
    void postSubmitPaperInvalidMetadataReturns400() throws Exception {
        String payload = "{\"title\":\"T\",\"authors\":[\"<bad>\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"not-email\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"ZGF0YQ==\"}}";

        ServletHttpTestSupport.ResponseCapture response = postPaperSubmission(payload, loggedInSession("author@cms.com"));

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Invalid metadata format"));
    }

    @Test
    void postSubmitPaperUploadFailureReturns503() throws Exception {
        fileStorageService.setAvailable(false);

        ServletHttpTestSupport.ResponseCapture response = postPaperSubmission(validPayload(), loggedInSession("author@cms.com"));

        assertEquals(503, response.getStatus());
        assertTrue(response.getBody().contains("Please try again"));
        assertEquals(0L, new JdbcPaperSubmissionRepository(dataSource).countAll());
    }

    @Test
    void getPaperSubmissionsReturnsSubmittedPapers() throws Exception {
        postPaperSubmission(validPayload(), loggedInSession("author@cms.com"));

        ServletHttpTestSupport.ResponseCapture response = getPaperSubmissions(loggedInSession("author@cms.com"));

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Valid Paper"));
        assertTrue(response.getBody().contains("\"submission_id\":"));
    }

    private String validPayload() {
        return "{\"title\":\"Valid Paper\",\"authors\":[\"Alice\",\"Bob\"],\"affiliations\":[\"U1\",\"U2\"],\"abstract\":\"A\",\"keywords\":[\"k1\",\"k2\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"ZGF0YQ==\"}}";
    }

    private String validPayloadWithDraftId(String draftId) {
        return "{\"draft_id\":" + draftId + ",\"title\":\"Valid Paper\",\"authors\":[\"Alice\",\"Bob\"],\"affiliations\":[\"U1\",\"U2\"],\"abstract\":\"A\",\"keywords\":[\"k1\",\"k2\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"ZGF0YQ==\"}}";
    }

    private String validDraftPayload() {
        return "{\"title\":\"Draft Title\",\"authors\":[\"Alice\"],\"affiliations\":[\"U1\"],\"abstract\":\"A\",\"keywords\":[\"k1\"],\"contact_details\":\"author@cms.com\"}";
    }

    private String extractDraftId(String body) {
        int marker = body.indexOf("\"draft_id\":");
        int start = marker + "\"draft_id\":".length();
        int end = body.indexOf('}', start);
        return body.substring(start, end).trim();
    }
}
