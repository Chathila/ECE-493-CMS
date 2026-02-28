package com.ece493.cms.acceptance;

import com.ece493.cms.integration.RegistrationIntegrationSupport;
import com.ece493.cms.integration.ServletHttpTestSupport;
import com.ece493.cms.repository.JdbcPaperSubmissionDraftRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UC05AcceptanceIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void AT01_successfullySaveDraft() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postDraftSave(validPayload(), loggedInSession("author@cms.com"));

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Draft saved successfully"));
        assertEquals(1L, new JdbcPaperSubmissionDraftRepository(dataSource).countAll());
    }

    @Test
    void AT02_invalidOrIncompleteDraftData() throws Exception {
        String payload = "{\"title\":\"\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"\"}";

        ServletHttpTestSupport.ResponseCapture response = postDraftSave(payload, loggedInSession("author@cms.com"));

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Invalid or incomplete"));
        assertEquals(0L, new JdbcPaperSubmissionDraftRepository(dataSource).countAll());
    }

    @Test
    void AT03_databaseStorageErrorDuringSave() throws Exception {
        draftSaveServlet = new com.ece493.cms.controller.DraftSaveServlet(
                (authorEmail, request) -> com.ece493.cms.model.DraftSaveResult.error(500, "Draft could not be saved due to a storage error. Please try again.")
        );

        ServletHttpTestSupport.ResponseCapture response = postDraftSave(validPayload(), loggedInSession("author@cms.com"));

        assertEquals(500, response.getStatus());
        assertTrue(response.getBody().contains("could not be saved"));
        assertEquals(0L, new JdbcPaperSubmissionDraftRepository(dataSource).countAll());
    }

    private String validPayload() {
        return "{\"title\":\"Draft Title\",\"authors\":[\"Alice\"],\"affiliations\":[\"U1\"],\"abstract\":\"A\",\"keywords\":[\"k1\"],\"contact_details\":\"author@cms.com\"}";
    }
}
