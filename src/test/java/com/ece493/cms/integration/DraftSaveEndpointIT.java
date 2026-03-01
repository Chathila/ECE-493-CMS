package com.ece493.cms.integration;

import com.ece493.cms.model.PaperSubmissionDraft;
import com.ece493.cms.repository.JdbcPaperSubmissionDraftRepository;
import com.ece493.cms.repository.PaperSubmissionDraftRepository;
import com.ece493.cms.service.DraftSaveService;
import com.ece493.cms.service.DraftSaveServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

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
    void postDraftSaveSuccessCanCreateMultipleDrafts() throws Exception {
        postDraftSave(validPayload("Draft One"), loggedInSession("author@cms.com"));
        ServletHttpTestSupport.ResponseCapture response = postDraftSave(validPayload("Draft Two"), loggedInSession("author@cms.com"));

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Draft saved successfully"));
        assertEquals(2L, new JdbcPaperSubmissionDraftRepository(dataSource).countAll());
    }

    @Test
    void postDraftSaveWithIdUpdatesExistingDraft() throws Exception {
        ServletHttpTestSupport.ResponseCapture create = postDraftSave(validPayload("Original"), loggedInSession("author@cms.com"));
        String createdId = extractDraftId(create.getBody());

        ServletHttpTestSupport.ResponseCapture update = postDraftSave(validUpdatePayload(createdId, "Updated"), loggedInSession("author@cms.com"));
        ServletHttpTestSupport.ResponseCapture fetched = getDraft(loggedInSession("author@cms.com"), createdId);

        assertEquals(200, update.getStatus());
        assertEquals(200, fetched.getStatus());
        assertTrue(fetched.getBody().contains("\"title\":\"Updated\""));
        assertEquals(1L, new JdbcPaperSubmissionDraftRepository(dataSource).countAll());
    }

    @Test
    void postDraftSaveInvalidDataReturns400() throws Exception {
        String payload = "{\"title\":\"\",\"authors\":[\"A\"],\"affiliations\":[\"U\"],\"abstract\":\"X\",\"keywords\":[\"k\"],\"contact_details\":\"\"}";

        ServletHttpTestSupport.ResponseCapture response = postDraftSave(payload, loggedInSession("author@cms.com"));

        assertEquals(400, response.getStatus());
        assertTrue(response.getBody().contains("Invalid or incomplete"));
    }

    @Test
    void postDraftSaveStorageFailureReturns500() throws Exception {
        DraftSaveService failingService = new DraftSaveServiceImpl(new PaperSubmissionDraftRepository() {
            @Override
            public Optional<PaperSubmissionDraft> findByIdAndAuthorEmail(long draftId, String authorEmail) {
                return Optional.empty();
            }

            @Override
            public List<PaperSubmissionDraft> findAllByAuthorEmail(String authorEmail) {
                return List.of();
            }

            @Override
            public long save(PaperSubmissionDraft draft) {
                throw new IllegalStateException("down");
            }

            @Override
            public boolean update(PaperSubmissionDraft draft) {
                throw new IllegalStateException("down");
            }

            @Override
            public boolean deleteByIdAndAuthorEmail(long draftId, String authorEmail) {
                throw new IllegalStateException("down");
            }

            @Override
            public long countAll() {
                return 0;
            }
        }, request -> null);
        draftSaveServlet = new com.ece493.cms.controller.DraftSaveServlet(failingService);

        ServletHttpTestSupport.ResponseCapture response = postDraftSave(validPayload("Draft"), loggedInSession("author@cms.com"));

        assertEquals(500, response.getStatus());
        assertTrue(response.getBody().contains("could not be saved"));
    }

    @Test
    void getDraftReturnsSavedDraftForAuthor() throws Exception {
        ServletHttpTestSupport.ResponseCapture create = postDraftSave(validPayload("Draft Title"), loggedInSession("author@cms.com"));
        String createdId = extractDraftId(create.getBody());

        ServletHttpTestSupport.ResponseCapture response = getDraft(loggedInSession("author@cms.com"), createdId);

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("\"title\":\"Draft Title\""));
        assertTrue(response.getBody().contains("\"contact_details\":\"author@cms.com\""));
    }

    @Test
    void getDraftReturns404WhenNoDraftExists() throws Exception {
        ServletHttpTestSupport.ResponseCapture response = getDraft(loggedInSession("author@cms.com"), "999");

        assertEquals(404, response.getStatus());
        assertTrue(response.getBody().contains("No draft found"));
    }

    @Test
    void getDraftsReturnsList() throws Exception {
        postDraftSave(validPayload("Draft One"), loggedInSession("author@cms.com"));
        postDraftSave(validPayload("Draft Two"), loggedInSession("author@cms.com"));

        ServletHttpTestSupport.ResponseCapture response = getDrafts(loggedInSession("author@cms.com"));

        assertEquals(200, response.getStatus());
        assertTrue(response.getBody().contains("Draft One"));
        assertTrue(response.getBody().contains("Draft Two"));
    }

    private String validPayload(String title) {
        return "{\"title\":\"" + title + "\",\"authors\":[\"Alice\"],\"affiliations\":[\"U1\"],\"abstract\":\"A\",\"keywords\":[\"k1\"],\"contact_details\":\"author@cms.com\"}";
    }

    private String validUpdatePayload(String draftId, String title) {
        return "{\"draft_id\":" + draftId + ",\"title\":\"" + title + "\",\"authors\":[\"Alice\"],\"affiliations\":[\"U1\"],\"abstract\":\"A\",\"keywords\":[\"k1\"],\"contact_details\":\"author@cms.com\"}";
    }

    private String extractDraftId(String body) {
        int marker = body.indexOf("\"draft_id\":");
        int start = marker + "\"draft_id\":".length();
        int end = body.indexOf('}', start);
        return body.substring(start, end).trim();
    }
}
