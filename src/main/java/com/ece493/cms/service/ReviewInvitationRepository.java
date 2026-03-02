package com.ece493.cms.service;

import com.ece493.cms.model.ReviewInvitation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReviewInvitationRepository {
    ReviewInvitation save(ReviewInvitation invitation);

    Optional<ReviewInvitation> findByPaperIdAndRefereeEmail(String paperId, String refereeEmail);

    Optional<ReviewInvitation> findByInvitationId(long invitationId);

    boolean updateStatusAndExpiry(long invitationId, String status, Instant expiresAt);

    List<ReviewInvitation> findAll();
}
