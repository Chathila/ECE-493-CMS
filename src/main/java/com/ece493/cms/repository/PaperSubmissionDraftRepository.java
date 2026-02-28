package com.ece493.cms.repository;

import com.ece493.cms.model.PaperSubmissionDraft;

import java.util.Optional;

public interface PaperSubmissionDraftRepository {
    Optional<PaperSubmissionDraft> findByAuthorEmail(String authorEmail);

    void saveOrUpdate(PaperSubmissionDraft draft);

    long countAll();
}
