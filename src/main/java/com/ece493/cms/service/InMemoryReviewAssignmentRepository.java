package com.ece493.cms.service;

import com.ece493.cms.model.ReviewAssignmentRecord;
import com.ece493.cms.model.ReviewInvitation;

import java.time.Instant;
import java.util.Optional;

public class InMemoryReviewAssignmentRepository implements ReviewAssignmentRepository {
    private final ReviewInvitationRepository invitationRepository;
    private final ReviewAssignmentService reviewAssignmentService;

    public InMemoryReviewAssignmentRepository(
            ReviewInvitationRepository invitationRepository,
            ReviewAssignmentService reviewAssignmentService
    ) {
        this.invitationRepository = invitationRepository;
        this.reviewAssignmentService = reviewAssignmentService;
    }

    @Override
    public Optional<ReviewAssignmentRecord> findByAssignmentId(long assignmentId) {
        Optional<ReviewInvitation> invitationOptional = invitationRepository.findByInvitationId(assignmentId);
        if (invitationOptional.isEmpty()) {
            return Optional.empty();
        }
        ReviewInvitation invitation = invitationOptional.get();
        return Optional.of(new ReviewAssignmentRecord(
                assignmentId,
                invitation.getPaperId(),
                invitation.getRefereeEmail(),
                invitation.getEditorEmail(),
                reviewAssignmentService.getStatus(assignmentId),
                invitation.isExpired(Instant.now()) || "expired".equals(invitation.getStatus())
        ));
    }

    @Override
    public boolean markSubmitted(long assignmentId) {
        return reviewAssignmentService.markReviewSubmitted(assignmentId);
    }
}
