package com.ece493.cms.service;

import com.ece493.cms.model.InvitationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryInvitationResponseRepository implements InvitationResponseRepository {
    private final AtomicLong idSequence = new AtomicLong(1L);
    private final List<InvitationResponse> responses = new ArrayList<>();
    private volatile boolean failOnSave;

    @Override
    public InvitationResponse save(InvitationResponse response) {
        if (failOnSave) {
            throw new IllegalStateException("Database error while saving invitation response.");
        }
        InvitationResponse stored = new InvitationResponse(
                idSequence.getAndIncrement(),
                response.getInvitationId(),
                response.getDecision(),
                response.getRespondedAt()
        );
        responses.add(stored);
        return stored;
    }

    @Override
    public Optional<InvitationResponse> findByInvitationId(long invitationId) {
        return responses.stream()
                .filter(response -> response.getInvitationId() == invitationId)
                .findFirst();
    }

    @Override
    public List<InvitationResponse> findAll() {
        return List.copyOf(responses);
    }

    public void setFailOnSave(boolean failOnSave) {
        this.failOnSave = failOnSave;
    }
}
