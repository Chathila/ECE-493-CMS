package com.ece493.cms.repository;

import com.ece493.cms.model.PaperSubmissionDraft;

import java.util.List;
import java.util.Optional;

public interface PaperSubmissionDraftRepository {
    Optional<PaperSubmissionDraft> findByIdAndAuthorEmail(long draftId, String authorEmail);

    List<PaperSubmissionDraft> findAllByAuthorEmail(String authorEmail);

    long save(PaperSubmissionDraft draft);

    boolean update(PaperSubmissionDraft draft);

    boolean deleteByIdAndAuthorEmail(long draftId, String authorEmail);

    long countAll();
}
