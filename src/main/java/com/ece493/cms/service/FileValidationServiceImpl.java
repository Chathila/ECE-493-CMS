package com.ece493.cms.service;

import com.ece493.cms.model.FileValidationResult;
import com.ece493.cms.model.ManuscriptFile;

public class FileValidationServiceImpl implements FileValidationService {
    private final FileStorageService fileStorageService;

    public FileValidationServiceImpl(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public FileValidationResult validate(String authorEmail, ManuscriptFile manuscriptFile) {
        if (isBlank(authorEmail)) {
            return FileValidationResult.error(401, "You must log in to validate a manuscript file.");
        }

        if (!fileStorageService.isAvailable()) {
            return FileValidationResult.error(503, "Upload failed due to a system or network error. Please retry.");
        }

        if (manuscriptFile == null || isBlank(manuscriptFile.getFilename()) || isBlank(manuscriptFile.getContentBase64())) {
            return FileValidationResult.error(400, "Missing required file information. Provide filename and file content.");
        }

        if (!fileStorageService.isSupportedFormat(manuscriptFile.getFilename())) {
            return FileValidationResult.error(400, "Unsupported file format. Allowed formats: PDF and DOCX.");
        }

        long sizeBytes = fileStorageService.computeFileSizeBytes(manuscriptFile.getContentBase64());
        if (sizeBytes < 0) {
            return FileValidationResult.error(422, "File cannot be processed. Please upload a valid PDF or DOCX file.");
        }

        if (!fileStorageService.isWithinSizeLimit(sizeBytes)) {
            return FileValidationResult.error(413, "File size exceeds maximum limit of 20 MB.");
        }

        return FileValidationResult.success("File validation successful. You can proceed.");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
