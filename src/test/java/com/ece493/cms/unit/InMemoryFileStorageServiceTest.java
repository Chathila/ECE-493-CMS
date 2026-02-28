package com.ece493.cms.unit;

import com.ece493.cms.model.ManuscriptFile;
import com.ece493.cms.service.InMemoryFileStorageService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFileStorageServiceTest {
    @Test
    void validatesSupportedFormats() {
        InMemoryFileStorageService service = new InMemoryFileStorageService();

        assertTrue(service.isSupportedFormat("paper.pdf"));
        assertTrue(service.isSupportedFormat("paper.DOCX"));
        assertFalse(service.isSupportedFormat("paper.txt"));
        assertFalse(service.isSupportedFormat(null));
    }

    @Test
    void validatesSizeAndBase64() {
        InMemoryFileStorageService service = new InMemoryFileStorageService();

        long size = service.computeFileSizeBytes("ZGF0YQ==");

        assertEquals(4L, size);
        assertTrue(service.isWithinSizeLimit(size));
        assertFalse(service.isWithinSizeLimit(-1L));
        assertFalse(service.isWithinSizeLimit(InMemoryFileStorageService.MAX_FILE_SIZE_BYTES + 1L));
        assertEquals(-1L, service.computeFileSizeBytes(null));
        assertEquals(-1L, service.computeFileSizeBytes("not-base64"));
    }

    @Test
    void storesWhenAvailableAndFailsWhenUnavailable() {
        InMemoryFileStorageService service = new InMemoryFileStorageService();

        long id = service.store(new ManuscriptFile("paper.pdf", "ZGF0YQ=="));

        assertEquals(1L, id);
        assertEquals(1L, service.storedFileCount());

        service.setAvailable(false);
        assertThrows(IllegalStateException.class, () -> service.store(new ManuscriptFile("paper.pdf", "ZGF0YQ==")));
    }
}
