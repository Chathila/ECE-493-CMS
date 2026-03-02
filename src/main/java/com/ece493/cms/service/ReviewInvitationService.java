package com.ece493.cms.service;

import com.ece493.cms.model.DeliveryFailureRecord;
import com.ece493.cms.model.ReviewInvitation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ReviewInvitationService {
    public enum SendStatus {
        SENT,
        DUPLICATE_SUPPRESSED,
        INVALID_EMAIL,
        DELIVERY_FAILURE
    }

    public static final class SendResult {
        private final SendStatus status;
        private final String message;

        public SendResult(SendStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        public SendStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    private final ReviewInvitationRepository invitationRepository;
    private final DeliveryFailureRepository failureRepository;
    private final EmailDeliveryService emailDeliveryService;
    private final EditorNotificationService editorNotificationService;
    private final InvitationValidationService invitationValidationService;
    private final InvitationComposer invitationComposer;

    public ReviewInvitationService(
            ReviewInvitationRepository invitationRepository,
            DeliveryFailureRepository failureRepository,
            EmailDeliveryService emailDeliveryService,
            EditorNotificationService editorNotificationService,
            InvitationValidationService invitationValidationService,
            InvitationComposer invitationComposer
    ) {
        this.invitationRepository = invitationRepository;
        this.failureRepository = failureRepository;
        this.emailDeliveryService = emailDeliveryService;
        this.editorNotificationService = editorNotificationService;
        this.invitationValidationService = invitationValidationService;
        this.invitationComposer = invitationComposer;
    }

    public SendResult sendInvitation(String editorEmail, String assignmentId, String paperId, String refereeEmail) {
        if (invitationValidationService.isDuplicateInvitation(paperId, refereeEmail, invitationRepository)) {
            return new SendResult(SendStatus.DUPLICATE_SUPPRESSED, "Duplicate invitation suppressed.");
        }

        String content = invitationComposer.compose(paperId);
        if (!invitationValidationService.isValidEmail(refereeEmail)) {
            ReviewInvitation failedInvitation = invitationRepository.save(new ReviewInvitation(
                    0L,
                    assignmentId,
                    editorEmail,
                    refereeEmail,
                    paperId,
                    "failed",
                    content,
                    Instant.now(),
                    Instant.now().plus(7, ChronoUnit.DAYS)
            ));
            failureRepository.save(new DeliveryFailureRecord(
                    0L,
                    failedInvitation.getInvitationId(),
                    "Invalid referee email address.",
                    Instant.now()
            ));
            editorNotificationService.notifyEditor(
                    editorEmail,
                    "Invitation delivery failed for " + refereeEmail + ". Please update the referee contact information."
            );
            return new SendResult(SendStatus.INVALID_EMAIL, "Invalid referee email address.");
        }

        try {
            emailDeliveryService.send(refereeEmail, "Review Invitation", content);
        } catch (IllegalStateException e) {
            ReviewInvitation failedInvitation = invitationRepository.save(new ReviewInvitation(
                    0L,
                    assignmentId,
                    editorEmail,
                    refereeEmail,
                    paperId,
                    "failed",
                    content,
                    Instant.now(),
                    Instant.now().plus(7, ChronoUnit.DAYS)
            ));
            failureRepository.save(new DeliveryFailureRecord(
                    0L,
                    failedInvitation.getInvitationId(),
                    "Email delivery service unavailable.",
                    Instant.now()
            ));
            editorNotificationService.notifyEditor(
                    editorEmail,
                    "Invitation delivery issue for " + refereeEmail + "."
            );
            return new SendResult(SendStatus.DELIVERY_FAILURE, "Invitation delivery failed.");
        }

        invitationRepository.save(new ReviewInvitation(
                0L,
                assignmentId,
                editorEmail,
                refereeEmail,
                paperId,
                "open",
                content,
                Instant.now(),
                Instant.now().plus(7, ChronoUnit.DAYS)
        ));
        return new SendResult(SendStatus.SENT, "Invitation sent.");
    }
}
