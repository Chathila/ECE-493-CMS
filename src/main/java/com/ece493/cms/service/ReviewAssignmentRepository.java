package com.ece493.cms.service;

import com.ece493.cms.model.ReviewAssignmentRecord;

import java.util.Optional;

public interface ReviewAssignmentRepository {
    Optional<ReviewAssignmentRecord> findByAssignmentId(long assignmentId);

    boolean markSubmitted(long assignmentId);
}
