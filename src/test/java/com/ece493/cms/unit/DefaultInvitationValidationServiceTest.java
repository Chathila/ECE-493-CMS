package com.ece493.cms.unit;

import com.ece493.cms.service.DefaultInvitationValidationService;
import com.ece493.cms.service.InMemoryReviewInvitationRepository;
import com.ece493.cms.service.ReviewInvitationService;
import com.ece493.cms.service.InMemoryDeliveryFailureRepository;
import com.ece493.cms.service.InMemoryEditorNotificationService;
import com.ece493.cms.service.InMemoryEmailDeliveryService;
import com.ece493.cms.service.InvitationComposer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultInvitationValidationServiceTest {
    @Test
    void validatesEmailFormats() {
        DefaultInvitationValidationService service = new DefaultInvitationValidationService();

        assertTrue(service.isValidEmail("ref@cms.com"));
        assertFalse(service.isValidEmail("invalid-email"));
        assertFalse(service.isValidEmail(null));
    }

    @Test
    void detectsDuplicateInvitationsAndHandlesNullInputs() {
        DefaultInvitationValidationService service = new DefaultInvitationValidationService();
        InMemoryReviewInvitationRepository repository = new InMemoryReviewInvitationRepository();
        ReviewInvitationService invitationService = new ReviewInvitationService(
                repository,
                new InMemoryDeliveryFailureRepository(),
                new InMemoryEmailDeliveryService(),
                new InMemoryEditorNotificationService(),
                service,
                new InvitationComposer()
        );

        invitationService.sendInvitation("editor@cms.com", "assign-1", "1", "ref1@cms.com");

        assertTrue(service.isDuplicateInvitation("1", "ref1@cms.com", repository));
        assertFalse(service.isDuplicateInvitation("2", "ref1@cms.com", repository));
        assertFalse(service.isDuplicateInvitation("1", "ref2@cms.com", repository));
        assertFalse(service.isDuplicateInvitation(null, "ref1@cms.com", repository));
        assertFalse(service.isDuplicateInvitation("1", null, repository));
    }
}
