package com.ece493.cms.unit;

import com.ece493.cms.service.InMemoryNotificationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryNotificationServiceTest {
    @Test
    void sendsAndStoresInvitationContent() {
        InMemoryNotificationService service = new InMemoryNotificationService();

        service.sendReviewInvitations("editor@cms.com", "10", List.of("ref1@cms.com"));

        assertEquals(1L, service.sentInvitationCount());
        assertTrue(service.hasInvitationFor("ref1@cms.com", "10"));
        assertNotNull(service.invitationContent("ref1@cms.com", "10"));
    }

    @Test
    void suppressesDuplicateInvitations() {
        InMemoryNotificationService service = new InMemoryNotificationService();

        service.sendReviewInvitations("editor@cms.com", "11", List.of("ref1@cms.com"));
        service.sendReviewInvitations("editor@cms.com", "11", List.of("ref1@cms.com"));

        assertEquals(1L, service.sentInvitationCount());
    }

    @Test
    void throwsAndLogsOnDeliveryFailure() {
        InMemoryNotificationService service = new InMemoryNotificationService();
        service.setAvailable(false);

        assertThrows(IllegalStateException.class,
                () -> service.sendReviewInvitations("editor@cms.com", "12", List.of("ref1@cms.com")));
        assertEquals(1, service.failureRecords().size());
        assertEquals(1, service.editorNotifications().size());
    }

    @Test
    void throwsAndLogsOnInvalidEmail() {
        InMemoryNotificationService service = new InMemoryNotificationService();

        assertThrows(IllegalStateException.class,
                () -> service.sendReviewInvitations("editor@cms.com", "13", List.of("invalid-email")));
        assertEquals(1, service.failureRecords().size());
        assertTrue(service.editorNotifications().get(0).getMessage().contains("update"));
    }

    @Test
    void keepsFirstFailureMessageWhenMultipleFailuresOccur() {
        InMemoryNotificationService service = new InMemoryNotificationService();

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.sendReviewInvitations("editor@cms.com", "14", List.of("invalid-email", "bad-email")));

        assertTrue(error.getMessage().contains("Invalid referee email"));
        assertEquals(2, service.failureRecords().size());
    }
}
