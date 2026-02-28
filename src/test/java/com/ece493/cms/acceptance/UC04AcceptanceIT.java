package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.repository.JdbcPaperSubmissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UC04AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_successfulPaperSubmission() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postPaperSubmission(validPayload(), loggedInSession("author@cms.com"));

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Paper submitted successfully"));
        assertTrue(response.getBody().contains("redirect"));
        assertEquals(1L, new JdbcPaperSubmissionRepository(dataSource).countAll());
    }

    @Test
    void AT02_missingRequiredMetadata() throws Exception {
        String payload = "{\"title\":\"\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"ZGF0YQ==\"}}";

        ServletHttpTestSupport.ResponseCapture response = postPaperSubmission(payload, loggedInSession("author@cms.com"));

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Missing required"));
    }

    @Test
    void AT03_unsupportedFileFormat() throws Exception {
        String payload = "{\"title\":\"T\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.txt\",\"content_base64\":\"ZGF0YQ==\"}}";

        ServletHttpTestSupport.ResponseCapture response = postPaperSubmission(payload, loggedInSession("author@cms.com"));

        assertEquals(415, response.getStatus());
        assertTrue(response.getBody().contains("Allowed formats"));
    }

    @Test
    void AT04_fileSizeExceedsLimit() throws Exception {
        String largeBase64 = "A".repeat((20 * 1024 * 1024) + 1);
        String payload = "{\"title\":\"T\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"" + largeBase64 + "\"}}";

        ServletHttpTestSupport.ResponseCapture response = postPaperSubmission(payload, loggedInSession("author@cms.com"));

        assertEquals(413, response.getStatus());
        assertTrue(response.getBody().contains("20 MB"));
    }

    @Test
    void AT05_invalidFormData() throws Exception {
        String payload = "{\"title\":\"T\",\"authors\":[\"<bad>\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"invalid-email\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"ZGF0YQ==\"}}";

        ServletHttpTestSupport.ResponseCapture response = postPaperSubmission(payload, loggedInSession("author@cms.com"));

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Invalid metadata format"));
    }

    private String validPayload() {
        return "{\"title\":\"Valid Paper\",\"authors\":[\"Alice\",\"Bob\"],\"affiliations\":[\"U1\",\"U2\"],\"abstract\":\"A\",\"keywords\":[\"k1\",\"k2\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"ZGF0YQ==\"}}";
    }
}
