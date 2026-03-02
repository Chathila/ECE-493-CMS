package com.ece493.cms.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileValidationEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void validateSuccessReturns200() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postFileValidation(validPayload("paper.pdf", "ZGF0YQ=="), loggedInSession("author@cms.com"));

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("successful"));
    }

    @Test
    void validateUnsupportedFormatReturns400() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postFileValidation(validPayload("paper.txt", "ZGF0YQ=="), loggedInSession("author@cms.com"));

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("PDF and DOCX"));
    }

    @Test
    void validateOversizeReturns413() throws Exception {
        String largeBase64 = Base64.getEncoder().encodeToString(new byte[(20 * 1024 * 1024) + 1]);
        ServletHttpTestSupport.ResponseCapture response = postFileValidation(validPayload("paper.pdf", largeBase64), loggedInSession("author@cms.com"));

        assertEquals(413, response.getStatus());
        assertTrue(response.getBody().contains("20 MB"));
    }

    @Test
    void validateCorruptedReturns422() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postFileValidation(validPayload("paper.pdf", "not-base64"), loggedInSession("author@cms.com"));

        assertEquals(422, response.getStatus());
        assertTrue(response.getBody().contains("cannot be processed"));
    }

    @Test
    void validateUnavailableReturns503() throws Exception {
        fileStorageService.setAvailable(false);
        ServletHttpTestSupport.ResponseCapture response = postFileValidation(validPayload("paper.pdf", "ZGF0YQ=="), loggedInSession("author@cms.com"));

        assertEquals(503, response.getStatus());
        assertTrue(response.getBody().contains("retry"));
    }

    private String validPayload(String filename, String contentBase64) {
        return "{\"filename\":\"" + filename + "\",\"content_base64\":\"" + contentBase64 + "\"}";
    }
}
