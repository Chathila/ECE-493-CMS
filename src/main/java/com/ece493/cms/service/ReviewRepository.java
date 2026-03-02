package com.ece493.cms.service;

import com.ece493.cms.model.Review;

import java.util.Optional;

public interface ReviewRepository {
    Review save(Review review);

    Optional<Review> findByAssignmentId(long assignmentId);

    long countAll();
}
