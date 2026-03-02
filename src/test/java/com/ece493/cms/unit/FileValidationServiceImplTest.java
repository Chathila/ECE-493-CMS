package com.ece493.cms.unit;

import com.ece493.cms.model.FileValidationResult;
import com.ece493.cms.model.ManuscriptFile;
import com.ece493.cms.service.FileStorageService;
import com.ece493.cms.service.FileValidationServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileValidationServiceImplTest {
    @Test
    void rejectsWhenUserNotLoggedIn() {
        FileValidationServiceImpl service = new FileValidationServiceImpl(new StubStorage());

        FileValidationResult result = service.validate("", new ManuscriptFile("paper.pdf", "ZGF0YQ=="));

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void rejectsWhenUserEmailIsNull() {
        FileValidationServiceImpl service = new FileValidationServiceImpl(new StubStorage());

        FileValidationResult result = service.validate(null, new ManuscriptFile("paper.pdf", "ZGF0YQ=="));

        assertEquals(401, result.getStatusCode());
    }

    @Test
    void rejectsWhenStorageUnavailable() {
        StubStorage storage = new StubStorage();
        storage.available = false;
        FileValidationServiceImpl service = new FileValidationServiceImpl(storage);

        FileValidationResult result = service.validate("author@cms.com", new ManuscriptFile("paper.pdf", "ZGF0YQ=="));

        assertEquals(503, result.getStatusCode());
        assertTrue(result.getMessage().contains("retry"));
    }

    @Test
    void rejectsWhenFilePayloadMissing() {
        FileValidationServiceImpl service = new FileValidationServiceImpl(new StubStorage());

        FileValidationResult result = service.validate("author@cms.com", null);

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsWhenFilenameMissing() {
        FileValidationServiceImpl service = new FileValidationServiceImpl(new StubStorage());

        FileValidationResult result = service.validate("author@cms.com", new ManuscriptFile(" ", "ZGF0YQ=="));

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsWhenFileContentMissing() {
        FileValidationServiceImpl service = new FileValidationServiceImpl(new StubStorage());

        FileValidationResult result = service.validate("author@cms.com", new ManuscriptFile("paper.pdf", ""));

        assertEquals(400, result.getStatusCode());
    }

    @Test
    void rejectsUnsupportedFormat() {
        StubStorage storage = new StubStorage();
        storage.supportedFormat = false;
        FileValidationServiceImpl service = new FileValidationServiceImpl(storage);

        FileValidationResult result = service.validate("author@cms.com", new ManuscriptFile("paper.txt", "ZGF0YQ=="));

        assertEquals(400, result.getStatusCode());
        assertTrue(result.getMessage().contains("PDF and DOCX"));
    }

    @Test
    void rejectsUnreadableFile() {
        StubStorage storage = new StubStorage();
        storage.computedSizeBytes = -1L;
        FileValidationServiceImpl service = new FileValidationServiceImpl(storage);

        FileValidationResult result = service.validate("author@cms.com", new ManuscriptFile("paper.pdf", "not-base64"));

        assertEquals(422, result.getStatusCode());
        assertTrue(result.getMessage().contains("cannot be processed"));
    }

    @Test
    void rejectsOversizeFile() {
        StubStorage storage = new StubStorage();
        storage.withinLimit = false;
        FileValidationServiceImpl service = new FileValidationServiceImpl(storage);

        FileValidationResult result = service.validate("author@cms.com", new ManuscriptFile("paper.pdf", "ZGF0YQ=="));

        assertEquals(413, result.getStatusCode());
        assertTrue(result.getMessage().contains("20 MB"));
    }

    @Test
    void allowsValidFile() {
        FileValidationServiceImpl service = new FileValidationServiceImpl(new StubStorage());

        FileValidationResult result = service.validate("author@cms.com", new ManuscriptFile("paper.pdf", "ZGF0YQ=="));

        assertEquals(200, result.getStatusCode());
        assertTrue(result.getMessage().contains("successful"));
    }

    private static class StubStorage implements FileStorageService {
        private boolean supportedFormat = true;
        private boolean withinLimit = true;
        private boolean available = true;
        private long computedSizeBytes = 4L;

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
            return computedSizeBytes;
        }

        @Override
        public long store(ManuscriptFile manuscriptFile) {
            return 1;
        }

        @Override
        public boolean isAvailable() {
            return available;
        }
    }
}
