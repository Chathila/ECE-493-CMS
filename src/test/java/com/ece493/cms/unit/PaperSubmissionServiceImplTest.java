package com.ece493.cms.unit;

import com.ece493.cms.model.ManuscriptFile;
import com.ece493.cms.model.MetadataValidationResult;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.model.PaperSubmissionRequest;
import com.ece493.cms.model.PaperSubmissionResult;
import com.ece493.cms.repository.PaperSubmissionDraftRepository;
import com.ece493.cms.repository.PaperSubmissionRepository;
import com.ece493.cms.service.FileStorageService;
import com.ece493.cms.service.MetadataValidationService;
import com.ece493.cms.service.PaperSubmissionServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaperSubmissionServiceImplTest {
    @Test
    void rejectsWhenUserNotLoggedIn() {
        StubRepo repo = new StubRepo();
        PaperSubmissionServiceImpl service = service(repo, new StubDraftRepo(), MetadataValidationResult.valid(), new StubStorage());

        PaperSubmissionResult result = service.submit("", validRequest());

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void rejectsWhenUserEmailNull() {
        StubRepo repo = new StubRepo();
        PaperSubmissionServiceImpl service = service(repo, new StubDraftRepo(), MetadataValidationResult.valid(), new StubStorage());

        PaperSubmissionResult result = service.submit(null, validRequest());

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void rejectsMetadataValidationErrors() {
        StubRepo repo = new StubRepo();
        StubDraftRepo draftRepo = new StubDraftRepo();
        PaperSubmissionServiceImpl service = service(repo, draftRepo, MetadataValidationResult.invalid("bad metadata"), new StubStorage());

        PaperSubmissionResult result = service.submit("author@cms.com", validRequest());

        assertEquals(400, result.getStatusCode());
        assertFalse(repo.saved);
        assertNull(draftRepo.deletedDraftId);
    }

    @Test
    void rejectsMissingFileData() {
        StubRepo repo = new StubRepo();
        PaperSubmissionServiceImpl service = service(repo, new StubDraftRepo(), MetadataValidationResult.valid(), new StubStorage());

        PaperSubmissionResult result = service.submit("author@cms.com", new PaperSubmissionRequest(
                "Title", "A", "U", "Abstract", "k", "author@cms.com", new ManuscriptFile("", "")
        ));

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsWhenManuscriptObjectMissing() {
        StubRepo repo = new StubRepo();
        PaperSubmissionServiceImpl service = service(repo, new StubDraftRepo(), MetadataValidationResult.valid(), new StubStorage());

        PaperSubmissionResult result = service.submit("author@cms.com", new PaperSubmissionRequest(
                "Title", "A", "U", "Abstract", "k", "author@cms.com", null
        ));

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsWhenContentBase64Missing() {
        StubRepo repo = new StubRepo();
        PaperSubmissionServiceImpl service = service(repo, new StubDraftRepo(), MetadataValidationResult.valid(), new StubStorage());

        PaperSubmissionResult result = service.submit("author@cms.com", new PaperSubmissionRequest(
                "Title", "A", "U", "Abstract", "k", "author@cms.com", new ManuscriptFile("paper.pdf", "")
        ));

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsUnsupportedFormat() {
        StubRepo repo = new StubRepo();
        StubDraftRepo draftRepo = new StubDraftRepo();
        StubStorage storage = new StubStorage();
        storage.supportedFormat = false;
        PaperSubmissionServiceImpl service = service(repo, draftRepo, MetadataValidationResult.valid(), storage);

        PaperSubmissionResult result = service.submit("author@cms.com", validRequest());

        assertEquals(415, result.getStatusCode());
        assertFalse(repo.saved);
        assertNull(draftRepo.deletedDraftId);
    }

    @Test
    void rejectsOversizedFile() {
        StubRepo repo = new StubRepo();
        StubDraftRepo draftRepo = new StubDraftRepo();
        StubStorage storage = new StubStorage();
        storage.withinLimit = false;
        PaperSubmissionServiceImpl service = service(repo, draftRepo, MetadataValidationResult.valid(), storage);

        PaperSubmissionResult result = service.submit("author@cms.com", validRequest());

        assertEquals(413, result.getStatusCode());
        assertNull(draftRepo.deletedDraftId);
    }

    @Test
    void rejectsUploadFailure() {
        StubRepo repo = new StubRepo();
        StubDraftRepo draftRepo = new StubDraftRepo();
        StubStorage storage = new StubStorage();
        storage.throwOnStore = true;
        PaperSubmissionServiceImpl service = service(repo, draftRepo, MetadataValidationResult.valid(), storage);

        PaperSubmissionResult result = service.submit("author@cms.com", validRequest());

        assertEquals(503, result.getStatusCode());
        assertFalse(repo.saved);
        assertNull(draftRepo.deletedDraftId);
    }

    @Test
    void storesSubmissionOnSuccess() {
        StubRepo repo = new StubRepo();
        StubDraftRepo draftRepo = new StubDraftRepo();
        StubStorage storage = new StubStorage();
        storage.nextFileId = 9L;
        PaperSubmissionServiceImpl service = service(repo, draftRepo, MetadataValidationResult.valid(), storage);

        PaperSubmissionResult result = service.submit("author@cms.com", validRequest());

        assertEquals(200, result.getStatusCode());
        assertTrue(repo.saved);
        assertEquals(9L, repo.last.getManuscriptFileId());
        assertEquals("/home", result.getRedirectLocation());
        assertNull(draftRepo.deletedDraftId);
    }

    @Test
    void removesDraftWhenSubmissionFromDraft() {
        StubRepo repo = new StubRepo();
        StubDraftRepo draftRepo = new StubDraftRepo();
        StubStorage storage = new StubStorage();
        PaperSubmissionServiceImpl service = service(repo, draftRepo, MetadataValidationResult.valid(), storage);

        PaperSubmissionResult result = service.submit("author@cms.com", validRequestWithDraft(55L));

        assertEquals(200, result.getStatusCode());
        assertEquals(55L, draftRepo.deletedDraftId);
        assertEquals("author@cms.com", draftRepo.deletedForAuthorEmail);
    }

    private PaperSubmissionServiceImpl service(StubRepo repo, StubDraftRepo draftRepo, MetadataValidationResult validationResult, StubStorage storage) {
        MetadataValidationService metadataValidationService = request -> validationResult;
        return new PaperSubmissionServiceImpl(repo, draftRepo, metadataValidationService, storage);
    }

    private PaperSubmissionRequest validRequest() {
        return new PaperSubmissionRequest(
                "Title",
                "Author One",
                "Uni",
                "Abstract",
                "k1,k2",
                "author@cms.com",
                new ManuscriptFile("paper.pdf", "ZGF0YQ==")
        );
    }

    private PaperSubmissionRequest validRequestWithDraft(long draftId) {
        return new PaperSubmissionRequest(
                draftId,
                "Title",
                "Author One",
                "Uni",
                "Abstract",
                "k1,k2",
                "author@cms.com",
                new ManuscriptFile("paper.pdf", "ZGF0YQ==")
        );
    }

    private static class StubRepo implements PaperSubmissionRepository {
        private boolean saved;
        private PaperSubmission last;

        @Override
        public void save(PaperSubmission paperSubmission) {
            saved = true;
            last = paperSubmission;
        }

        @Override
        public long countAll() {
            return saved ? 1 : 0;
        }
    }

    private static class StubDraftRepo implements PaperSubmissionDraftRepository {
        private Long deletedDraftId;
        private String deletedForAuthorEmail;

        @Override
        public java.util.Optional<com.ece493.cms.model.PaperSubmissionDraft> findByIdAndAuthorEmail(long draftId, String authorEmail) {
            return java.util.Optional.empty();
        }

        @Override
        public java.util.List<com.ece493.cms.model.PaperSubmissionDraft> findAllByAuthorEmail(String authorEmail) {
            return java.util.List.of();
        }

        @Override
        public long save(com.ece493.cms.model.PaperSubmissionDraft draft) {
            return 0;
        }

        @Override
        public boolean update(com.ece493.cms.model.PaperSubmissionDraft draft) {
            return false;
        }

        @Override
        public boolean deleteByIdAndAuthorEmail(long draftId, String authorEmail) {
            deletedDraftId = draftId;
            deletedForAuthorEmail = authorEmail;
            return true;
        }

        @Override
        public long countAll() {
            return 0;
        }
    }

    private static class StubStorage implements FileStorageService {
        private boolean supportedFormat = true;
        private boolean withinLimit = true;
        private boolean throwOnStore;
        private long nextFileId = 1L;

        @Override
        public boolean isSupportedFormat(String filename) {
            return supportedFormat;
        }

        @Override
        public boolean isWithinSizeLimit(long sizeBytes) {
            return withinLimit;
        }

        @Override
        public long computeFileSizeBytes(String contentBase64) {
            return 4L;
        }

        @Override
        public long store(ManuscriptFile manuscriptFile) {
            if (throwOnStore) {
                throw new IllegalStateException("down");
            }
            return nextFileId;
        }
    }
}
