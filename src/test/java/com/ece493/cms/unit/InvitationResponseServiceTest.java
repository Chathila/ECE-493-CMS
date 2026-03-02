package com.ece493.cms.unit;

import com.ece493.cms.model.InvitationResponseResult;
import com.ece493.cms.service.InMemoryNotificationService;
import com.ece493.cms.service.InvitationResponseService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvitationResponseServiceTest {
    @Test
    void recordsAcceptResponseAndUpdatesAssignmentStatus() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "201", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "201");
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult result = service.submitResponse(invitationId, "accept");

        assertEquals(200, result.getStatusCode());
        assertEquals("accepted", result.getAssignmentStatus());
        assertEquals(1, notificationService.invitationResponses().size());
        assertTrue(notificationService.editorNotifications().stream().anyMatch(n -> "accept".equals(n.getMessage())));
    }

    @Test
    void allowsApprovalSubmissionAfterAcceptance() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "210", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "210");
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult accept = service.submitResponse(invitationId, "accept");
        InvitationResponseResult approval = service.submitApproval(invitationId);

        assertEquals(200, accept.getStatusCode());
        assertEquals(200, approval.getStatusCode());
        assertEquals("approved", approval.getAssignmentStatus());
        assertTrue(notificationService.editorNotifications().stream().anyMatch(n -> "approval_submitted".equals(n.getMessage())));
    }

    @Test
    void rejectsApprovalSubmissionWhenInvitationNotAccepted() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "211", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "211");
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult pending = service.submitApproval(invitationId);
        service.submitResponse(invitationId, "reject");
        InvitationResponseResult rejected = service.submitApproval(invitationId);

        assertEquals(409, pending.getStatusCode());
        assertEquals(409, rejected.getStatusCode());
    }

    @Test
    void rejectsApprovalForMissingAndExpiredInvitation() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "212", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "212");
        notificationService.expireInvitation(invitationId);
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult missing = service.submitApproval(99999L);
        InvitationResponseResult expired = service.submitApproval(invitationId);

        assertEquals(410, missing.getStatusCode());
        assertEquals(410, expired.getStatusCode());
    }

    @Test
    void rejectsApprovalWhenInvitationMarkedExpiredEvenIfTimestampNotPast() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "213", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "213");
        notificationService.invitationRepository().updateStatusAndExpiry(invitationId, "expired", java.time.Instant.now().plusSeconds(1000));
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult result = service.submitApproval(invitationId);

        assertEquals(410, result.getStatusCode());
    }

    @Test
    void recordsRejectResponseAndUpdatesAssignmentStatus() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "202", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "202");
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult result = service.submitResponse(invitationId, "reject");

        assertEquals(200, result.getStatusCode());
        assertEquals("rejected", result.getAssignmentStatus());
        assertTrue(notificationService.editorNotifications().stream().anyMatch(n -> "reject".equals(n.getMessage())));
    }

    @Test
    void rejectsExpiredInvitationAndDoesNotRecordResponse() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "203", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "203");
        notificationService.expireInvitation(invitationId);
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult result = service.submitResponse(invitationId, "accept");

        assertEquals(410, result.getStatusCode());
        assertEquals(0, notificationService.invitationResponses().size());
        assertEquals("pending", notificationService.assignmentStatus(invitationId));
    }

    @Test
    void reportsRetryOnRepositoryFailureWithoutChangingAssignment() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "204", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "204");
        notificationService.invitationResponseRepository().setFailOnSave(true);
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult result = service.submitResponse(invitationId, "accept");

        assertEquals(500, result.getStatusCode());
        assertTrue(result.getMessage().contains("retry"));
        assertEquals(0, notificationService.invitationResponses().size());
        assertEquals("pending", notificationService.assignmentStatus(invitationId));
    }

    @Test
    void enforcesSingleResponseLock() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "205", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "205");
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult first = service.submitResponse(invitationId, "accept");
        InvitationResponseResult second = service.submitResponse(invitationId, "reject");

        assertEquals(200, first.getStatusCode());
        assertEquals(409, second.getStatusCode());
        assertEquals(1, notificationService.invitationResponses().size());
        assertEquals("accepted", notificationService.assignmentStatus(invitationId));
    }

    @Test
    void rejectsMissingInvitationAndInvalidDecision() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "208", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "208");
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult missing = service.submitResponse(999L, "accept");
        InvitationResponseResult badDecision = service.submitResponse(invitationId, "maybe");

        assertEquals(410, missing.getStatusCode());
        assertEquals(400, badDecision.getStatusCode());
    }

    @Test
    void viewInvitationHandlesOpenAndMissing() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "206", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "206");
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult open = service.viewInvitation(invitationId);
        InvitationResponseResult missing = service.viewInvitation(12345L);

        assertEquals(200, open.getStatusCode());
        assertEquals(410, missing.getStatusCode());
    }

    @Test
    void rejectsSecondResponseWhenInvitationMarkedResponded() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "207", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "207");
        notificationService.invitationRepository().updateStatusAndExpiry(invitationId, "responded", java.time.Instant.now().plusSeconds(100));
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult result = service.submitResponse(invitationId, "accept");

        assertEquals(409, result.getStatusCode());
    }

    @Test
    void treatsExpiredStatusAsExpiredEvenWhenTimestampInFuture() {
        InMemoryNotificationService notificationService = new InMemoryNotificationService();
        notificationService.sendReviewInvitations("editor@cms.com", "209", List.of("ref1@cms.com"));
        long invitationId = notificationService.invitationId("ref1@cms.com", "209");
        notificationService.invitationRepository().updateStatusAndExpiry(invitationId, "expired", java.time.Instant.now().plusSeconds(1000));
        InvitationResponseService service = new InvitationResponseService(
                notificationService.invitationRepository(),
                notificationService.invitationResponseRepository(),
                notificationService.reviewAssignmentService(),
                notificationService
        );

        InvitationResponseResult view = service.viewInvitation(invitationId);
        InvitationResponseResult submit = service.submitResponse(invitationId, "accept");

        assertEquals(410, view.getStatusCode());
        assertEquals(410, submit.getStatusCode());
    }
}
