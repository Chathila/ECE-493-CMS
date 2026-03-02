package com.ece493.cms.service;

import com.ece493.cms.model.FinalDecision;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryFinalDecisionRepository implements FinalDecisionRepository {
    private final AtomicLong idSequence = new AtomicLong(1L);
    private final List<FinalDecision> decisions = new ArrayList<>();
    private boolean failOnSave;

    @Override
    public FinalDecision save(FinalDecision decision) {
        if (failOnSave) {
            throw new IllegalStateException("Decision store unavailable");
        }
        FinalDecision stored = new FinalDecision(
                idSequence.getAndIncrement(),
                decision.getPaperId(),
                decision.getEditorEmail(),
                decision.getAuthorEmail(),
                decision.getDecision(),
                decision.getDecidedAt(),
                decision.getNotificationStatus()
        );
        decisions.add(stored);
        return stored;
    }

    @Override
    public Optional<FinalDecision> findLatestByPaperId(String paperId) {
        for (int i = decisions.size() - 1; i >= 0; i--) {
            FinalDecision decision = decisions.get(i);
            if (decision.getPaperId().equals(paperId)) {
                return Optional.of(decision);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean updateNotificationStatus(long decisionId, String notificationStatus) {
        for (int i = 0; i < decisions.size(); i++) {
            FinalDecision decision = decisions.get(i);
            if (decision.getDecisionId() == decisionId) {
                decisions.set(i, new FinalDecision(
                        decision.getDecisionId(),
                        decision.getPaperId(),
                        decision.getEditorEmail(),
                        decision.getAuthorEmail(),
                        decision.getDecision(),
                        decision.getDecidedAt(),
                        notificationStatus
                ));
                return true;
            }
        }
        return false;
    }

    @Override
    public long countAll() {
        return decisions.size();
    }

    public void setFailOnSave(boolean failOnSave) {
        this.failOnSave = failOnSave;
    }
}
