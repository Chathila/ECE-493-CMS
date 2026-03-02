package com.ece493.cms.service;

import com.ece493.cms.model.ReviewForm;

import java.util.Optional;

public interface ReviewFormRepository {
    Optional<ReviewForm> findByAssignmentId(long assignmentId);
}
