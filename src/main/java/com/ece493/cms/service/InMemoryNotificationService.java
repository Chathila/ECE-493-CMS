package com.ece493.cms.service;

import com.ece493.cms.model.DeliveryFailureRecord;
import com.ece493.cms.model.InvitationResponse;
import com.ece493.cms.model.ReviewInvitation;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryNotificationService implements NotificationService {
    private final AtomicLong sentInvitationCount = new AtomicLong();
    private final InMemoryEmailDeliveryService emailDeliveryService = new InMemoryEmailDeliveryService();
    private final InMemoryEditorNotificationService editorNotificationService = new InMemoryEditorNotificationService();
    private final InMemoryReviewInvitationRepository reviewInvitationRepository = new InMemoryReviewInvitationRepository();
    private final InMemoryDeliveryFailureRepository deliveryFailureRepository = new InMemoryDeliveryFailureRepository();
    private final InMemoryInvitationResponseRepository invitationResponseRepository = new InMemoryInvitationResponseRepository();
    private final ReviewAssignmentService reviewAssignmentService = new ReviewAssignmentService();
    private final ReviewInvitationService reviewInvitationService = new ReviewInvitationService(
            reviewInvitationRepository,
            deliveryFailureRepository,
            emailDeliveryService,
            editorNotificationService,
            new DefaultInvitationValidationService(),
            new InvitationComposer()
    );

    @Override
    public void sendReviewInvitations(String editorEmail, String paperId, List<String> refereeEmails) {
        String assignmentId = paperId + "-assignment";
        String firstFailureMessage = null;
        for (String refereeEmail : refereeEmails) {
            ReviewInvitationService.SendResult result = reviewInvitationService.sendInvitation(
                    editorEmail,
                    assignmentId,
                    paperId,
                    refereeEmail
            );
            if (result.getStatus() == ReviewInvitationService.SendStatus.SENT) {
                sentInvitationCount.incrementAndGet();
                reviewInvitationRepository.findByPaperIdAndRefereeEmail(paperId, refereeEmail)
                        .ifPresent(invitation -> reviewAssignmentService.markPending(invitation.getInvitationId()));
            } else if (result.getStatus() == ReviewInvitationService.SendStatus.INVALID_EMAIL
                    || result.getStatus() == ReviewInvitationService.SendStatus.DELIVERY_FAILURE) {
                if (firstFailureMessage == null) {
                    firstFailureMessage = result.getMessage();
                }
            }
        }
        if (firstFailureMessage != null) {
            throw new IllegalStateException(firstFailureMessage);
        }
    }

    public void setAvailable(boolean available) {
        emailDeliveryService.setAvailable(available);
    }

    public long sentInvitationCount() {
        return sentInvitationCount.get();
    }

    public boolean hasInvitationFor(String refereeEmail, String paperId) {
        return reviewInvitationRepository.findByPaperIdAndRefereeEmail(paperId, refereeEmail).isPresent();
    }

    public String invitationContent(String refereeEmail, String paperId) {
        return reviewInvitationRepository.findByPaperIdAndRefereeEmail(paperId, refereeEmail)
                .map(ReviewInvitation::getContent)
                .orElse(null);
    }

    public List<DeliveryFailureRecord> failureRecords() {
        return deliveryFailureRepository.findAll();
    }

    public List<InMemoryEditorNotificationService.EditorNotification> editorNotifications() {
        return editorNotificationService.notifications();
    }

    @Override
    public void notifyEditorDecision(String editorEmail, String decision) {
        editorNotificationService.notifyEditor(editorEmail, decision);
    }

    public InMemoryReviewInvitationRepository invitationRepository() {
        return reviewInvitationRepository;
    }

    public InMemoryInvitationResponseRepository invitationResponseRepository() {
        return invitationResponseRepository;
    }

    public ReviewAssignmentService reviewAssignmentService() {
        return reviewAssignmentService;
    }

    public Long invitationId(String refereeEmail, String paperId) {
        return reviewInvitationRepository.findByPaperIdAndRefereeEmail(paperId, refereeEmail)
                .map(ReviewInvitation::getInvitationId)
                .orElse(null);
    }

    public boolean expireInvitation(long invitationId) {
        return reviewInvitationRepository.updateStatusAndExpiry(invitationId, "expired", Instant.now().minusSeconds(1));
    }

    public String assignmentStatus(long invitationId) {
        return reviewAssignmentService.getStatus(invitationId);
    }

    public List<InvitationResponse> invitationResponses() {
        return invitationResponseRepository.findAll();
    }
}
