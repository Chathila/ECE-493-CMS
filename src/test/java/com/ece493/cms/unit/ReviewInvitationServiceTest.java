package com.ece493.cms.unit;

import com.ece493.cms.service.DefaultInvitationValidationService;
import com.ece493.cms.service.InMemoryDeliveryFailureRepository;
import com.ece493.cms.service.InMemoryEditorNotificationService;
import com.ece493.cms.service.InMemoryEmailDeliveryService;
import com.ece493.cms.service.InMemoryReviewInvitationRepository;
import com.ece493.cms.service.InvitationComposer;
import com.ece493.cms.service.ReviewInvitationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewInvitationServiceTest {
    @Test
    void sendsInvitationWithRequiredContent() {
        InMemoryReviewInvitationRepository invitationRepository = new InMemoryReviewInvitationRepository();
        InMemoryDeliveryFailureRepository failureRepository = new InMemoryDeliveryFailureRepository();
        InMemoryEmailDeliveryService emailService = new InMemoryEmailDeliveryService();
        InMemoryEditorNotificationService editorNotificationService = new InMemoryEditorNotificationService();
        ReviewInvitationService service = new ReviewInvitationService(
                invitationRepository,
                failureRepository,
                emailService,
                editorNotificationService,
                new DefaultInvitationValidationService(),
                new InvitationComposer()
        );

        ReviewInvitationService.SendResult result = service.sendInvitation("editor@cms.com", "assign-1", "1", "ref1@cms.com");

        assertEquals(ReviewInvitationService.SendStatus.SENT, result.getStatus());
        assertEquals(1, emailService.sentEmails().size());
        String body = emailService.sentEmails().get(0).getBody();
        assertTrue(body.contains("Title:"));
        assertTrue(body.contains("Abstract:"));
        assertTrue(body.contains("accept or reject"));
    }

    @Test
    void recordsAndNotifiesOnDeliveryFailure() {
        InMemoryReviewInvitationRepository invitationRepository = new InMemoryReviewInvitationRepository();
        InMemoryDeliveryFailureRepository failureRepository = new InMemoryDeliveryFailureRepository();
        InMemoryEmailDeliveryService emailService = new InMemoryEmailDeliveryService();
        emailService.setAvailable(false);
        InMemoryEditorNotificationService editorNotificationService = new InMemoryEditorNotificationService();
        ReviewInvitationService service = new ReviewInvitationService(
                invitationRepository,
                failureRepository,
                emailService,
                editorNotificationService,
                new DefaultInvitationValidationService(),
                new InvitationComposer()
        );

        ReviewInvitationService.SendResult result = service.sendInvitation("editor@cms.com", "assign-2", "2", "ref1@cms.com");

        assertEquals(ReviewInvitationService.SendStatus.DELIVERY_FAILURE, result.getStatus());
        assertEquals(1, failureRepository.findAll().size());
        assertTrue(failureRepository.findAll().get(0).getReason().contains("unavailable"));
        assertEquals(1, editorNotificationService.notifications().size());
        assertTrue(editorNotificationService.notifications().get(0).getMessage().contains("delivery issue"));
    }

    @Test
    void recordsAndNotifiesOnInvalidEmail() {
        InMemoryReviewInvitationRepository invitationRepository = new InMemoryReviewInvitationRepository();
        InMemoryDeliveryFailureRepository failureRepository = new InMemoryDeliveryFailureRepository();
        InMemoryEmailDeliveryService emailService = new InMemoryEmailDeliveryService();
        InMemoryEditorNotificationService editorNotificationService = new InMemoryEditorNotificationService();
        ReviewInvitationService service = new ReviewInvitationService(
                invitationRepository,
                failureRepository,
                emailService,
                editorNotificationService,
                new DefaultInvitationValidationService(),
                new InvitationComposer()
        );

        ReviewInvitationService.SendResult result = service.sendInvitation("editor@cms.com", "assign-3", "3", "invalid-email");

        assertEquals(ReviewInvitationService.SendStatus.INVALID_EMAIL, result.getStatus());
        assertEquals(1, failureRepository.findAll().size());
        assertTrue(failureRepository.findAll().get(0).getReason().contains("Invalid referee email"));
        assertEquals(1, editorNotificationService.notifications().size());
        assertTrue(editorNotificationService.notifications().get(0).getMessage().contains("update"));
        assertEquals(0, emailService.sentEmails().size());
    }

    @Test
    void suppressesDuplicateInvitation() {
        InMemoryReviewInvitationRepository invitationRepository = new InMemoryReviewInvitationRepository();
        InMemoryDeliveryFailureRepository failureRepository = new InMemoryDeliveryFailureRepository();
        InMemoryEmailDeliveryService emailService = new InMemoryEmailDeliveryService();
        InMemoryEditorNotificationService editorNotificationService = new InMemoryEditorNotificationService();
        ReviewInvitationService service = new ReviewInvitationService(
                invitationRepository,
                failureRepository,
                emailService,
                editorNotificationService,
                new DefaultInvitationValidationService(),
                new InvitationComposer()
        );

        ReviewInvitationService.SendResult first = service.sendInvitation("editor@cms.com", "assign-4", "4", "ref1@cms.com");
        ReviewInvitationService.SendResult second = service.sendInvitation("editor@cms.com", "assign-4", "4", "ref1@cms.com");

        assertEquals(ReviewInvitationService.SendStatus.SENT, first.getStatus());
        assertEquals(ReviewInvitationService.SendStatus.DUPLICATE_SUPPRESSED, second.getStatus());
        assertEquals(1, emailService.sentEmails().size());
    }
}
