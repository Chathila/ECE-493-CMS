package com.ece493.cms.repository;

import com.ece493.cms.model.PaperSubmission;

import java.util.List;
import java.util.Optional;

public interface PaperSubmissionRepository {
    long save(PaperSubmission paperSubmission);

    List<PaperSubmission> findAllByAuthorEmail(String authorEmail);

    Optional<PaperSubmission> findBySubmissionId(long submissionId);

    long countAll();
}
