package com.ece493.cms.unit;

import com.ece493.cms.model.ReviewInvitation;
import com.ece493.cms.service.InMemoryReviewAssignmentRepository;
import com.ece493.cms.service.InMemoryReviewInvitationRepository;
import com.ece493.cms.service.ReviewAssignmentService;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryReviewAssignmentRepositoryTest {
    @Test
    void mapsInvitationToAssignmentRecordAndMarksSubmitted() {
        InMemoryReviewInvitationRepository invitationRepository = new InMemoryReviewInvitationRepository();
        ReviewAssignmentService assignmentService = new ReviewAssignmentService();
        ReviewInvitation invitation = invitationRepository.save(new ReviewInvitation(
                0L, "a-1", "editor@cms.com", "ref@cms.com", "41", "sent", "body", Instant.now(), Instant.now().plusSeconds(300)
        ));
        assignmentService.markPending(invitation.getInvitationId());
        assignmentService.updateFromDecision(invitation.getInvitationId(), "accept");
        InMemoryReviewAssignmentRepository repository = new InMemoryReviewAssignmentRepository(invitationRepository, assignmentService);

        assertTrue(repository.findByAssignmentId(invitation.getInvitationId()).isPresent());
        assertEquals("accepted", repository.findByAssignmentId(invitation.getInvitationId()).orElseThrow().getStatus());
        assertTrue(repository.markSubmitted(invitation.getInvitationId()));
        assertEquals("submitted", assignmentService.getStatus(invitation.getInvitationId()));
        assertTrue(repository.findByAssignmentId(999L).isEmpty());
    }

    @Test
    void marksExpiredAssignmentAndHandlesUnsubmittableStatus() {
        InMemoryReviewInvitationRepository invitationRepository = new InMemoryReviewInvitationRepository();
        ReviewAssignmentService assignmentService = new ReviewAssignmentService();
        ReviewInvitation invitation = invitationRepository.save(new ReviewInvitation(
                0L, "a-1", "editor@cms.com", "ref@cms.com", "41", "expired", "body", Instant.now(), Instant.now().minusSeconds(1)
        ));
        InMemoryReviewAssignmentRepository repository = new InMemoryReviewAssignmentRepository(invitationRepository, assignmentService);

        assertTrue(repository.findByAssignmentId(invitation.getInvitationId()).orElseThrow().isExpired());
        assertFalse(repository.markSubmitted(invitation.getInvitationId()));
    }

    @Test
    void marksExpiredWhenStatusIsExpiredEvenBeforeDeadline() {
        InMemoryReviewInvitationRepository invitationRepository = new InMemoryReviewInvitationRepository();
        ReviewAssignmentService assignmentService = new ReviewAssignmentService();
        ReviewInvitation invitation = invitationRepository.save(new ReviewInvitation(
                0L, "a-1", "editor@cms.com", "ref@cms.com", "41", "expired", "body", Instant.now(), Instant.now().plusSeconds(3600)
        ));
        InMemoryReviewAssignmentRepository repository = new InMemoryReviewAssignmentRepository(invitationRepository, assignmentService);

        assertTrue(repository.findByAssignmentId(invitation.getInvitationId()).orElseThrow().isExpired());
    }
}
