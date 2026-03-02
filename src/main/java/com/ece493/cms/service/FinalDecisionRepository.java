package com.ece493.cms.service;

import com.ece493.cms.model.FinalDecision;

import java.util.Optional;

public interface FinalDecisionRepository {
    FinalDecision save(FinalDecision decision);

    Optional<FinalDecision> findLatestByPaperId(String paperId);

    boolean updateNotificationStatus(long decisionId, String notificationStatus);

    long countAll();
}
