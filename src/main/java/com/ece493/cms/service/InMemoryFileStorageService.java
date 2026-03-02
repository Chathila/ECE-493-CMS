package com.ece493.cms.service;

import com.ece493.cms.model.ManuscriptFile;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryFileStorageService implements FileStorageService {
    public static final long MAX_FILE_SIZE_BYTES = 20L * 1024L * 1024L;

    private final Map<Long, ManuscriptFile> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0L);
    private volatile boolean available = true;

    @Override
    public boolean isSupportedFormat(String filename) {
        if (filename == null) {
            return false;
        }
        String normalized = filename.trim().toLowerCase();
        return normalized.endsWith(".pdf") || normalized.endsWith(".docx");
    }

    @Override
    public boolean isWithinSizeLimit(long sizeBytes) {
        return sizeBytes >= 0 && sizeBytes <= MAX_FILE_SIZE_BYTES;
    }

    @Override
    public long computeFileSizeBytes(String contentBase64) {
        if (contentBase64 == null) {
            return -1;
        }
        try {
            return Base64.getDecoder().decode(contentBase64).length;
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    @Override
    public long store(ManuscriptFile manuscriptFile) {
        if (!available) {
            throw new IllegalStateException("File upload/storage unavailable");
        }
        long id = sequence.incrementAndGet();
        storage.put(id, manuscriptFile);
        return id;
    }

    @Override
    public Optional<ManuscriptFile> findById(long fileId) {
        return Optional.ofNullable(storage.get(fileId));
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public long storedFileCount() {
        return storage.size();
    }
}
