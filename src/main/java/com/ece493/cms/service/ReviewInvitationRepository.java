package com.ece493.cms.service;

import com.ece493.cms.model.ReviewInvitation;

import java.util.List;
import java.util.Optional;

public interface ReviewInvitationRepository {
    ReviewInvitation save(ReviewInvitation invitation);

    Optional<ReviewInvitation> findByPaperIdAndRefereeEmail(String paperId, String refereeEmail);

    List<ReviewInvitation> findAll();
}
