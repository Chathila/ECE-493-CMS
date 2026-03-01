package com.ece493.cms.service;

import com.ece493.cms.model.DraftSaveRequest;
import com.ece493.cms.model.DraftSaveResult;
import com.ece493.cms.model.PaperSubmissionDraft;
import com.ece493.cms.repository.PaperSubmissionDraftRepository;

import java.time.Instant;
import java.util.Optional;

public class DraftSaveServiceImpl implements DraftSaveService {
    private final PaperSubmissionDraftRepository draftRepository;
    private final DraftValidationService draftValidationService;

    public DraftSaveServiceImpl(PaperSubmissionDraftRepository draftRepository, DraftValidationService draftValidationService) {
        this.draftRepository = draftRepository;
        this.draftValidationService = draftValidationService;
    }

    @Override
    public DraftSaveResult saveDraft(String authorEmail, DraftSaveRequest request) {
        if (isBlank(authorEmail)) {
            return DraftSaveResult.error(401, "You must log in to save a draft.");
        }

        String validationMessage = draftValidationService.validate(request);
        if (validationMessage != null) {
            return DraftSaveResult.error(400, validationMessage);
        }

        Optional<PaperSubmissionDraft> existingDraft = Optional.empty();
        if (request.getDraftId() != null) {
            existingDraft = draftRepository.findByIdAndAuthorEmail(request.getDraftId(), authorEmail);
            if (existingDraft.isEmpty()) {
                return DraftSaveResult.error(404, "Draft not found for update.");
            }
        }
        boolean noChanges = existingDraft.isPresent() && matches(existingDraft.get(), request);

        PaperSubmissionDraft draft = new PaperSubmissionDraft(
                existingDraft.map(PaperSubmissionDraft::getDraftId).orElse(0L),
                authorEmail,
                request.getTitle().trim(),
                normalize(request.getAuthors()),
                normalize(request.getAffiliations()),
                normalize(request.getPaperAbstract()),
                normalize(request.getKeywords()),
                request.getContactDetails().trim(),
                Instant.now()
        );

        try {
            if (existingDraft.isPresent()) {
                draftRepository.update(draft);
            } else {
                long createdId = draftRepository.save(draft);
                return DraftSaveResult.success("Draft saved successfully.", createdId);
            }
        } catch (IllegalStateException e) {
            return DraftSaveResult.error(500, "Draft could not be saved due to a storage error. Please try again.");
        }

        if (noChanges) {
            return DraftSaveResult.success("Draft saved successfully. No changes were detected.", draft.getDraftId());
        }
        return DraftSaveResult.success("Draft saved successfully.", draft.getDraftId());
    }

    private boolean matches(PaperSubmissionDraft draft, DraftSaveRequest request) {
        String existing = String.join("|",
                normalize(draft.getTitle()),
                normalize(draft.getAuthors()),
                normalize(draft.getAffiliations()),
                normalize(draft.getPaperAbstract()),
                normalize(draft.getKeywords()),
                normalize(draft.getContactDetails())
        );
        String incoming = String.join("|",
                normalize(request.getTitle()),
                normalize(request.getAuthors()),
                normalize(request.getAffiliations()),
                normalize(request.getPaperAbstract()),
                normalize(request.getKeywords()),
                normalize(request.getContactDetails())
        );
        return existing.equals(incoming);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
