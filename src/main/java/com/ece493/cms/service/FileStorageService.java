package com.ece493.cms.service;

import com.ece493.cms.model.ManuscriptFile;

public interface FileStorageService {
    boolean isSupportedFormat(String filename);

    boolean isWithinSizeLimit(long sizeBytes);

    long computeFileSizeBytes(String contentBase64);

    long store(ManuscriptFile manuscriptFile);
}
