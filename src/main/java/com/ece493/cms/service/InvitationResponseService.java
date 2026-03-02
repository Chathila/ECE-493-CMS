package com.ece493.cms.service;

import com.ece493.cms.model.InvitationResponse;
import com.ece493.cms.model.InvitationResponseResult;
import com.ece493.cms.model.ReviewInvitation;

import java.time.Instant;

public class InvitationResponseService {
    private final ReviewInvitationRepository invitationRepository;
    private final InvitationResponseRepository invitationResponseRepository;
    private final ReviewAssignmentService reviewAssignmentService;
    private final NotificationService notificationService;

    public InvitationResponseService(
            ReviewInvitationRepository invitationRepository,
            InvitationResponseRepository invitationResponseRepository,
            ReviewAssignmentService reviewAssignmentService,
            NotificationService notificationService
    ) {
        this.invitationRepository = invitationRepository;
        this.invitationResponseRepository = invitationResponseRepository;
        this.reviewAssignmentService = reviewAssignmentService;
        this.notificationService = notificationService;
    }

    public InvitationResponseResult viewInvitation(long invitationId) {
        ReviewInvitation invitation = invitationRepository.findByInvitationId(invitationId).orElse(null);
        if (invitation == null || invitation.isExpired(Instant.now()) || "expired".equals(invitation.getStatus())) {
            return InvitationResponseResult.error(410, "Invitation has expired.");
        }
        return InvitationResponseResult.success(reviewAssignmentService.getStatus(invitationId));
    }

    public InvitationResponseResult submitResponse(long invitationId, String decision) {
        ReviewInvitation invitation = invitationRepository.findByInvitationId(invitationId).orElse(null);
        if (invitation == null || invitation.isExpired(Instant.now()) || "expired".equals(invitation.getStatus())) {
            return InvitationResponseResult.error(410, "Invitation is no longer valid.");
        }

        if (!"accept".equals(decision) && !"reject".equals(decision)) {
            return InvitationResponseResult.error(400, "Decision must be either accept or reject.");
        }

        if (invitationResponseRepository.findByInvitationId(invitationId).isPresent()
                || "responded".equals(invitation.getStatus())) {
            return InvitationResponseResult.error(409, "Invitation response already recorded.");
        }

        try {
            invitationResponseRepository.save(new InvitationResponse(
                    0L,
                    invitationId,
                    decision,
                    Instant.now()
            ));
        } catch (IllegalStateException e) {
            return InvitationResponseResult.error(500, "Response could not be recorded. Please retry.");
        }

        invitationRepository.updateStatusAndExpiry(invitationId, "responded", invitation.getExpiresAt());
        reviewAssignmentService.updateFromDecision(invitationId, decision);
        notificationService.notifyEditorDecision(invitation.getEditorEmail(), decision);
        return InvitationResponseResult.success(reviewAssignmentService.getStatus(invitationId));
    }

    public InvitationResponseResult submitApproval(long invitationId) {
        ReviewInvitation invitation = invitationRepository.findByInvitationId(invitationId).orElse(null);
        if (invitation == null || invitation.isExpired(Instant.now()) || "expired".equals(invitation.getStatus())) {
            return InvitationResponseResult.error(410, "Invitation is no longer valid.");
        }

        if (!reviewAssignmentService.markApproved(invitationId)) {
            return InvitationResponseResult.error(409, "Invitation must be accepted before submitting approval.");
        }

        notificationService.notifyEditorDecision(invitation.getEditorEmail(), "approval_submitted");
        return InvitationResponseResult.success("Review approval submitted.", reviewAssignmentService.getStatus(invitationId));
    }
}
