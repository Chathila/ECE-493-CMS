package com.ece493.cms.unit;

import com.ece493.cms.model.ManuscriptFile;
import com.ece493.cms.model.MetadataValidationResult;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.model.PaperSubmissionRequest;
import com.ece493.cms.model.PaperSubmissionResult;
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
        PaperSubmissionServiceImpl service = service(repo, MetadataValidationResult.valid(), new StubStorage());

        PaperSubmissionResult result = service.submit("", validRequest());

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void rejectsWhenUserEmailNull() {
        StubRepo repo = new StubRepo();
        PaperSubmissionServiceImpl service = service(repo, MetadataValidationResult.valid(), new StubStorage());

        PaperSubmissionResult result = service.submit(null, validRequest());

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void rejectsMetadataValidationErrors() {
        StubRepo repo = new StubRepo();
        PaperSubmissionServiceImpl service = service(repo, MetadataValidationResult.invalid("bad metadata"), new StubStorage());

        PaperSubmissionResult result = service.submit("author@cms.com", validRequest());

        assertEquals(400, result.getStatusCode());
        assertFalse(repo.saved);
    }

    @Test
    void rejectsMissingFileData() {
        StubRepo repo = new StubRepo();
        PaperSubmissionServiceImpl service = service(repo, MetadataValidationResult.valid(), new StubStorage());

        PaperSubmissionResult result = service.submit("author@cms.com", new PaperSubmissionRequest(
                "Title", "A", "U", "Abstract", "k", "author@cms.com", new ManuscriptFile("", "")
        ));

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsWhenManuscriptObjectMissing() {
        StubRepo repo = new StubRepo();
        PaperSubmissionServiceImpl service = service(repo, MetadataValidationResult.valid(), new StubStorage());

        PaperSubmissionResult result = service.submit("author@cms.com", new PaperSubmissionRequest(
                "Title", "A", "U", "Abstract", "k", "author@cms.com", null
        ));

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsWhenContentBase64Missing() {
        StubRepo repo = new StubRepo();
        PaperSubmissionServiceImpl service = service(repo, MetadataValidationResult.valid(), new StubStorage());

        PaperSubmissionResult result = service.submit("author@cms.com", new PaperSubmissionRequest(
                "Title", "A", "U", "Abstract", "k", "author@cms.com", new ManuscriptFile("paper.pdf", "")
        ));

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsUnsupportedFormat() {
        StubRepo repo = new StubRepo();
        StubStorage storage = new StubStorage();
        storage.supportedFormat = false;
        PaperSubmissionServiceImpl service = service(repo, MetadataValidationResult.valid(), storage);

        PaperSubmissionResult result = service.submit("author@cms.com", validRequest());

        assertEquals(415, result.getStatusCode());
        assertFalse(repo.saved);
    }

    @Test
    void rejectsOversizedFile() {
        StubRepo repo = new StubRepo();
        StubStorage storage = new StubStorage();
        storage.withinLimit = false;
        PaperSubmissionServiceImpl service = service(repo, MetadataValidationResult.valid(), storage);

        PaperSubmissionResult result = service.submit("author@cms.com", validRequest());

        assertEquals(413, result.getStatusCode());
    }

    @Test
    void rejectsUploadFailure() {
        StubRepo repo = new StubRepo();
        StubStorage storage = new StubStorage();
        storage.throwOnStore = true;
        PaperSubmissionServiceImpl service = service(repo, MetadataValidationResult.valid(), storage);

        PaperSubmissionResult result = service.submit("author@cms.com", validRequest());

        assertEquals(503, result.getStatusCode());
        assertFalse(repo.saved);
    }

    @Test
    void storesSubmissionOnSuccess() {
        StubRepo repo = new StubRepo();
        StubStorage storage = new StubStorage();
        storage.nextFileId = 9L;
        PaperSubmissionServiceImpl service = service(repo, MetadataValidationResult.valid(), storage);

        PaperSubmissionResult result = service.submit("author@cms.com", validRequest());

        assertEquals(200, result.getStatusCode());
        assertTrue(repo.saved);
        assertEquals(9L, repo.last.getManuscriptFileId());
        assertEquals("/home", result.getRedirectLocation());
    }

    private PaperSubmissionServiceImpl service(StubRepo repo, MetadataValidationResult validationResult, StubStorage storage) {
        MetadataValidationService metadataValidationService = request -> validationResult;
        return new PaperSubmissionServiceImpl(repo, metadataValidationService, storage);
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
