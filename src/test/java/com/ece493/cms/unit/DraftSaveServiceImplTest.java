package com.ece493.cms.unit;

import com.ece493.cms.model.DraftSaveRequest;
import com.ece493.cms.model.DraftSaveResult;
import com.ece493.cms.model.PaperSubmissionDraft;
import com.ece493.cms.repository.PaperSubmissionDraftRepository;
import com.ece493.cms.service.DraftSaveServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DraftSaveServiceImplTest {
    @Test
    void rejectsWhenAuthorNotLoggedIn() {
        StubRepo repo = new StubRepo();
        DraftSaveServiceImpl service = new DraftSaveServiceImpl(repo, request -> null);

        DraftSaveResult result = service.saveDraft("", validRequest());

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void rejectsWhenAuthorEmailIsNull() {
        StubRepo repo = new StubRepo();
        DraftSaveServiceImpl service = new DraftSaveServiceImpl(repo, request -> null);

        DraftSaveResult result = service.saveDraft(null, validRequest());

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void rejectsValidationErrors() {
        StubRepo repo = new StubRepo();
        DraftSaveServiceImpl service = new DraftSaveServiceImpl(repo, request -> "bad draft");

        DraftSaveResult result = service.saveDraft("author@cms.com", validRequest());

        assertEquals(400, result.getStatusCode());
        assertEquals("bad draft", result.getMessage());
    }

    @Test
    void returnsStorageErrorWhenRepositoryFails() {
        StubRepo repo = new StubRepo();
        repo.throwOnSave = true;
        DraftSaveServiceImpl service = new DraftSaveServiceImpl(repo, request -> null);

        DraftSaveResult result = service.saveDraft("author@cms.com", validRequest());

        assertEquals(500, result.getStatusCode());
        assertTrue(result.getMessage().contains("storage error"));
    }

    @Test
    void savesDraftSuccessfullyWhenChangesExist() {
        StubRepo repo = new StubRepo();
        repo.existing = Optional.of(new PaperSubmissionDraft(
                1L, "author@cms.com", "Old Title", "A", "U", "X", "k", "author@cms.com", Instant.now()
        ));
        DraftSaveServiceImpl service = new DraftSaveServiceImpl(repo, request -> null);

        DraftSaveResult result = service.saveDraft("author@cms.com", validRequest());

        assertEquals(200, result.getStatusCode());
        assertEquals("Draft saved successfully.", result.getMessage());
    }

    @Test
    void returnsNoChangeMessageWhenPayloadMatchesExistingDraft() {
        StubRepo repo = new StubRepo();
        DraftSaveRequest request = validRequest();
        repo.existing = Optional.of(new PaperSubmissionDraft(
                1L,
                "author@cms.com",
                request.getTitle(),
                request.getAuthors(),
                request.getAffiliations(),
                request.getPaperAbstract(),
                request.getKeywords(),
                request.getContactDetails(),
                Instant.now()
        ));
        DraftSaveServiceImpl service = new DraftSaveServiceImpl(repo, requestArg -> null);

        DraftSaveResult result = service.saveDraft("author@cms.com", request);

        assertEquals(200, result.getStatusCode());
        assertTrue(result.getMessage().contains("No changes were detected"));
    }

    @Test
    void handlesNullOptionalFieldsDuringSaveAndComparison() {
        StubRepo repo = new StubRepo();
        DraftSaveRequest request = new DraftSaveRequest("Title", null, null, null, null, "author@cms.com");
        repo.existing = Optional.of(new PaperSubmissionDraft(
                1L, "author@cms.com", "Title", null, null, null, null, "author@cms.com", Instant.now()
        ));
        DraftSaveServiceImpl service = new DraftSaveServiceImpl(repo, requestArg -> null);

        DraftSaveResult result = service.saveDraft("author@cms.com", request);

        assertEquals(200, result.getStatusCode());
        assertTrue(result.getMessage().contains("No changes were detected"));
    }

    private DraftSaveRequest validRequest() {
        return new DraftSaveRequest("Title", "A", "U", "Abstract", "k1,k2", "author@cms.com");
    }

    private static class StubRepo implements PaperSubmissionDraftRepository {
        private Optional<PaperSubmissionDraft> existing = Optional.empty();
        private boolean throwOnSave;

        @Override
        public Optional<PaperSubmissionDraft> findByAuthorEmail(String authorEmail) {
            return existing;
        }

        @Override
        public void saveOrUpdate(PaperSubmissionDraft draft) {
            if (throwOnSave) {
                throw new IllegalStateException("down");
            }
            existing = Optional.of(draft);
        }

        @Override
        public long countAll() {
            return existing.isPresent() ? 1 : 0;
        }
    }
}
