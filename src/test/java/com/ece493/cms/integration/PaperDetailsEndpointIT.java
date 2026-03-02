package com.ece493.cms.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaperDetailsEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void returnsPaperDetailsAndDownloadForLoggedInUser() throws Exception {
        postPaperSubmission(validPayload(), loggedInSession("author@cms.com"));

        ServletHttpTestSupport.ResponseCapture details = getPaperDetails(loggedInSession("author@cms.com"), "1");
        ServletHttpTestSupport.ResponseCapture file = getPaperFile(loggedInSession("author@cms.com"), "1");

        assertEquals(200, details.getStatus());
        assertTrue(details.getBody().contains("\"title\":\"Valid Paper\""));
        assertTrue(details.getBody().contains("\"download_url\":\"/papers/files/1\""));
        assertEquals(200, file.getStatus());
        assertEquals("application/pdf", file.getContentType());
        assertTrue(file.getBody().contains("data"));
    }

    @Test
    void blocksPaperDetailsWhenLoggedOut() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = getPaperDetails(null, "1");

        assertEquals(401, response.getStatus());
    }

    private String validPayload() {
        return "{\"title\":\"Valid Paper\",\"authors\":[\"Alice\",\"Bob\"],\"affiliations\":[\"U1\",\"U2\"],\"abstract\":\"A\",\"keywords\":[\"k1\",\"k2\"],\"contact_details\":\"author@cms.com\",\"manuscript_file\":{\"filename\":\"paper.pdf\",\"content_base64\":\"ZGF0YQ==\"}}";
    }
}
