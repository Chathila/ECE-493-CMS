package com.ece493.cms.service;

import com.ece493.cms.model.ManuscriptFile;
import com.ece493.cms.model.MetadataValidationResult;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.model.PaperSubmissionRequest;
import com.ece493.cms.model.PaperSubmissionResult;
import com.ece493.cms.repository.PaperSubmissionRepository;

import java.time.Instant;

public class PaperSubmissionServiceImpl implements PaperSubmissionService {
    private final PaperSubmissionRepository paperSubmissionRepository;
    private final MetadataValidationService metadataValidationService;
    private final FileStorageService fileStorageService;

    public PaperSubmissionServiceImpl(
            PaperSubmissionRepository paperSubmissionRepository,
            MetadataValidationService metadataValidationService,
            FileStorageService fileStorageService
    ) {
        this.paperSubmissionRepository = paperSubmissionRepository;
        this.metadataValidationService = metadataValidationService;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public PaperSubmissionResult submit(String authorEmail, PaperSubmissionRequest request) {
        if (isBlank(authorEmail)) {
            return PaperSubmissionResult.error(401, "You must log in to submit a paper.");
        }

        MetadataValidationResult metadata = metadataValidationService.validate(request);
        if (!metadata.isValid()) {
            return PaperSubmissionResult.error(400, metadata.getMessage());
        }

        ManuscriptFile manuscriptFile = request.getManuscriptFile();
        if (manuscriptFile == null || isBlank(manuscriptFile.getFilename()) || isBlank(manuscriptFile.getContentBase64())) {
            return PaperSubmissionResult.error(400, "Missing required information. Manuscript file is required.");
        }

        if (!fileStorageService.isSupportedFormat(manuscriptFile.getFilename())) {
            return PaperSubmissionResult.error(415, "Unsupported file format. Allowed formats: PDF and DOCX.");
        }

        long sizeBytes = fileStorageService.computeFileSizeBytes(manuscriptFile.getContentBase64());
        if (!fileStorageService.isWithinSizeLimit(sizeBytes)) {
            return PaperSubmissionResult.error(413, "File size exceeds maximum limit of 20 MB.");
        }

        long fileId;
        try {
            fileId = fileStorageService.store(manuscriptFile);
        } catch (IllegalStateException e) {
            return PaperSubmissionResult.error(503, "Manuscript upload failed. Please try again.");
        }

        paperSubmissionRepository.save(new PaperSubmission(
                0L,
                authorEmail,
                request.getTitle(),
                request.getAuthors(),
                request.getAffiliations(),
                request.getPaperAbstract(),
                request.getKeywords(),
                request.getContactDetails(),
                fileId,
                Instant.now()
        ));

        return PaperSubmissionResult.success("Paper submitted successfully.", "/home");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
