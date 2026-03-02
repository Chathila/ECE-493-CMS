package com.ece493.cms.unit;

import com.ece493.cms.model.FinalDecision;
import com.ece493.cms.service.EmailDeliveryService;
import com.ece493.cms.service.FinalDecisionNotificationService;
import com.ece493.cms.service.InMemoryEditorNotificationService;
import com.ece493.cms.service.InMemoryEmailDeliveryService;
import com.ece493.cms.service.InMemoryNotificationFailureRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinalDecisionNotificationServiceTest {
    @Test
    void dispatchesEmailAndHandlesFailureLogging() {
        InMemoryEmailDeliveryService email = new InMemoryEmailDeliveryService();
        InMemoryNotificationFailureRepository failureRepository = new InMemoryNotificationFailureRepository();
        InMemoryEditorNotificationService editorNotifications = new InMemoryEditorNotificationService();
        FinalDecisionNotificationService service = new FinalDecisionNotificationService(
                email,
                failureRepository,
                editorNotifications
        );

        FinalDecision decision = new FinalDecision(1L, "41", "editor@cms.com", "author@cms.com", "accept", Instant.now(), "queued");
        assertTrue(service.dispatch(decision));
        assertEquals(1, email.sentEmails().size());
        assertEquals(0, failureRepository.findAll().size());

        email.setAvailable(false);
        assertFalse(service.dispatch(decision));
        assertEquals(1, failureRepository.findAll().size());
        assertEquals("email", failureRepository.findAll().get(0).getChannel());
        assertEquals("delivery failed", editorNotifications.notifications().get(0).getMessage());
    }

    @Test
    void defaultsFailureMessageWhenExceptionMessageIsNull() {
        InMemoryNotificationFailureRepository failureRepository = new InMemoryNotificationFailureRepository();
        InMemoryEditorNotificationService editorNotifications = new InMemoryEditorNotificationService();
        FinalDecisionNotificationService service = new FinalDecisionNotificationService(
                new EmailDeliveryService() {
                    @Override
                    public void send(String recipientEmail, String subject, String body) {
                        throw new IllegalStateException();
                    }
                },
                failureRepository,
                editorNotifications
        );

        boolean delivered = service.dispatch(new FinalDecision(
                2L,
                "41",
                "editor@cms.com",
                "author@cms.com",
                "reject",
                Instant.now(),
                "queued"
        ));

        assertFalse(delivered);
        assertEquals("delivery failed", failureRepository.findAll().get(0).getMessage());
    }
}
