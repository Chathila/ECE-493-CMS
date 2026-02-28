package com.ece493.cms.integration;

import com.ece493.cms.repository.JdbcPaperSubmissionDraftRepository;
import com.ece493.cms.repository.PaperSubmissionDraftRepository;
import com.ece493.cms.model.PaperSubmissionDraft;
import com.ece493.cms.service.DraftSaveService;
import com.ece493.cms.service.DraftSaveServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DraftSaveEndpointIT extends RegistrationIntegrationSupport {
    @BeforeEach
    void setUp() {
        startApp();
    }

    @AfterEach
    void tearDown() {
        stopApp();
    }

    @Test
    void postDraftSaveSuccessStoresDraft() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = postDraftSave(validPayload(), loggedInSession("author@cms.com"));

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Draft saved successfully"));
        assertEquals(1L, new JdbcPaperSubmissionDraftRepository(dataSource).countAll());
    }

    @Test
    void postDraftSaveInvalidDataReturns400() throws Exception {
        String payload = "{\"title\":\"\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"\"}";

        ServletHttpTestSupport.ResponseCapture response = postDraftSave(payload, loggedInSession("author@cms.com"));

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Invalid or incomplete"));
        assertEquals(0L, new JdbcPaperSubmissionDraftRepository(dataSource).countAll());
    }

    @Test
    void postDraftSaveStorageFailureReturns500() throws Exception {
        DraftSaveService failingService = new DraftSaveServiceImpl(
                new PaperSubmissionDraftRepository() {
                    @Override
                    public java.util.Optional<PaperSubmissionDraft> findByAuthorEmail(String authorEmail) {
                        return java.util.Optional.empty();
                    }

                    @Override
                    public void saveOrUpdate(PaperSubmissionDraft draft) {
                        throw new IllegalStateException("down");
                    }

                    @Override
                    public long countAll() {
                        return 0;
                    }
                },
                request -> null
        );
        draftSaveServlet = new com.ece493.cms.controller.DraftSaveServlet(failingService);

        ServletHttpTestSupport.ResponseCapture response = postDraftSave(validPayload(), loggedInSession("author@cms.com"));

        assertEquals(500, response.getStatus());
        assertTrue(response.getBody().contains("could not be saved"));
    }

    @Test
    void getDraftReturnsSavedDraftForAuthor() throws Exception {
        postDraftSave(validPayload(), loggedInSession("author@cms.com"));

        ServletHttpTestSupport.ResponseCapture response = getDraft(loggedInSession("author@cms.com"));

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"title\":\"Draft Title\""));
        assertTrue(response.getBody().contains("\"contact_details\":\"author@cms.com\""));
    }

    @Test
    void getDraftReturns404WhenNoDraftExists() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = getDraft(loggedInSession("author@cms.com"));

        assertEquals(404, response.getStatus());
        assertTrue(response.getBody().contains("No draft found"));
    }

    @Test
    void getDraftReturns401WithoutSession() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = getDraft(null);

        assertEquals(401, response.getStatus());
        assertTrue(response.getBody().contains("log in"));
    }

    private String validPayload() {
        return "{\"title\":\"Draft Title\",\"authors\":[\"Alice\"],\"affiliations\":[\"U1\"],\"abstract\":\"A\",\"keywords\":[\"k1\"],\"contact_details\":\"author@cms.com\"}";
    }
}
