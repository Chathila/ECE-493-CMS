package com.ece493.cms.repository;

import com.ece493.cms.model.PaperSubmission;

public interface PaperSubmissionRepository {
    void save(PaperSubmission paperSubmission);

    long countAll();
}
