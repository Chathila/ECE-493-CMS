package com.ece493.cms.service;

import com.ece493.cms.model.ReviewInvitation;

import java.util.ArrayList;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryReviewInvitationRepository implements ReviewInvitationRepository {
    private final AtomicLong idSequence = new AtomicLong(1L);
    private final List<ReviewInvitation> invitations = new ArrayList<>();

    @Override
    public ReviewInvitation save(ReviewInvitation invitation) {
        ReviewInvitation stored = new ReviewInvitation(
                idSequence.getAndIncrement(),
                invitation.getAssignmentId(),
                invitation.getEditorEmail(),
                invitation.getRefereeEmail(),
                invitation.getPaperId(),
                invitation.getStatus(),
                invitation.getContent(),
                invitation.getSentAt(),
                invitation.getExpiresAt()
        );
        invitations.add(stored);
        return stored;
    }

    @Override
    public Optional<ReviewInvitation> findByPaperIdAndRefereeEmail(String paperId, String refereeEmail) {
        return invitations.stream()
                .filter(invitation -> paperId.equals(invitation.getPaperId()) && refereeEmail.equals(invitation.getRefereeEmail()))
                .findFirst();
    }

    @Override
    public Optional<ReviewInvitation> findByInvitationId(long invitationId) {
        return invitations.stream()
                .filter(invitation -> invitation.getInvitationId() == invitationId)
                .findFirst();
    }

    @Override
    public boolean updateStatusAndExpiry(long invitationId, String status, Instant expiresAt) {
        for (int i = 0; i < invitations.size(); i++) {
            ReviewInvitation invitation = invitations.get(i);
            if (invitation.getInvitationId() == invitationId) {
                invitations.set(i, new ReviewInvitation(
                        invitation.getInvitationId(),
                        invitation.getAssignmentId(),
                        invitation.getEditorEmail(),
                        invitation.getRefereeEmail(),
                        invitation.getPaperId(),
                        status,
                        invitation.getContent(),
                        invitation.getSentAt(),
                        expiresAt
                ));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ReviewInvitation> findAll() {
        return List.copyOf(invitations);
    }
}
