package com.ece493.cms.unit;

import com.ece493.cms.model.FinalDecisionNotificationResult;
import com.ece493.cms.model.FinalDecisionStatusResult;
import com.ece493.cms.model.FinalDecisionSubmissionResult;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.model.ReviewInvitation;
import com.ece493.cms.repository.PaperSubmissionRepository;
import com.ece493.cms.service.FinalDecisionNotificationService;
import com.ece493.cms.service.FinalDecisionService;
import com.ece493.cms.service.InMemoryEditorNotificationService;
import com.ece493.cms.service.InMemoryEmailDeliveryService;
import com.ece493.cms.service.InMemoryFinalDecisionRepository;
import com.ece493.cms.service.InMemoryNotificationFailureRepository;
import com.ece493.cms.service.InMemoryReviewInvitationRepository;
import com.ece493.cms.service.ReviewAssignmentService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinalDecisionServiceTest {
    @Test
    void submitDecisionCoversValidationAndStorageBranches() {
        InMemoryFinalDecisionRepository finalDecisionRepository = new InMemoryFinalDecisionRepository();
        InMemoryReviewInvitationRepository invitationRepository = new InMemoryReviewInvitationRepository();
        ReviewAssignmentService assignmentService = new ReviewAssignmentService();
        InMemoryEmailDeliveryService email = new InMemoryEmailDeliveryService();
        FinalDecisionService service = new FinalDecisionService(
                finalDecisionRepository,
                paperRepo(List.of(new PaperSubmission(1L, "author@cms.com", "P", "A", "U", "Abs", "k", "c", 1L, Instant.now()))),
                invitationRepository,
                assignmentService,
                new FinalDecisionNotificationService(email, new InMemoryNotificationFailureRepository(), new InMemoryEditorNotificationService())
        );

        assertEquals(401, service.submitDecision(null, "1", "accept").getStatusCode());
        assertEquals(400, service.submitDecision("editor@cms.com", " ", "accept").getStatusCode());
        assertEquals(400, service.submitDecision("editor@cms.com", "1", null).getStatusCode());
        assertEquals(400, service.submitDecision("editor@cms.com", "1", "maybe").getStatusCode());
        assertEquals(404, service.submitDecision("editor@cms.com", "bad", "accept").getStatusCode());
        assertEquals(409, service.submitDecision("editor@cms.com", "1", "accept").getStatusCode());

        ReviewInvitation invitation = invitationRepository.save(new ReviewInvitation(
                0L, "a-1", "editor@cms.com", "ref@cms.com", "1", "sent", "x", Instant.now(), Instant.now().plusSeconds(60)
        ));
        assignmentService.markPending(invitation.getInvitationId());
        assertEquals(409, service.submitDecision("editor@cms.com", "1", "accept").getStatusCode());

        assignmentService.updateFromDecision(invitation.getInvitationId(), "accept");
        assignmentService.markReviewSubmitted(invitation.getInvitationId());
        FinalDecisionSubmissionResult created = service.submitDecision("editor@cms.com", "1", "reject");
        assertEquals(201, created.getStatusCode());
        assertEquals("reject", created.getDecision());
        assertEquals(1, email.sentEmails().size());

        email.setAvailable(false);
        FinalDecisionSubmissionResult createdWithFailedDelivery = service.submitDecision("editor@cms.com", "1", "accept");
        assertEquals(201, createdWithFailedDelivery.getStatusCode());
        assertEquals("failed", service.getDecisionStatus("author@cms.com", "1").getNotificationStatus());

        finalDecisionRepository.setFailOnSave(true);
        assertEquals(500, service.submitDecision("editor@cms.com", "1", "accept").getStatusCode());
    }

    @Test
    void notifyAndStatusCoverFailureAndAuthorizationBranches() {
        InMemoryFinalDecisionRepository finalDecisionRepository = new InMemoryFinalDecisionRepository();
        InMemoryReviewInvitationRepository invitationRepository = new InMemoryReviewInvitationRepository();
        ReviewAssignmentService assignmentService = new ReviewAssignmentService();
        InMemoryEmailDeliveryService email = new InMemoryEmailDeliveryService();
        InMemoryNotificationFailureRepository failureRepository = new InMemoryNotificationFailureRepository();
        InMemoryEditorNotificationService editorNotifications = new InMemoryEditorNotificationService();
        FinalDecisionService service = new FinalDecisionService(
                finalDecisionRepository,
                paperRepo(List.of(new PaperSubmission(1L, "author@cms.com", "P", "A", "U", "Abs", "k", "c", 1L, Instant.now()))),
                invitationRepository,
                assignmentService,
                new FinalDecisionNotificationService(email, failureRepository, editorNotifications)
        );

        ReviewInvitation invitation = invitationRepository.save(new ReviewInvitation(
                0L, "a-1", "editor@cms.com", "ref@cms.com", "1", "sent", "x", Instant.now(), Instant.now().plusSeconds(60)
        ));
        assignmentService.markPending(invitation.getInvitationId());
        assignmentService.updateFromDecision(invitation.getInvitationId(), "accept");
        assignmentService.markReviewSubmitted(invitation.getInvitationId());
        service.submitDecision("editor@cms.com", "1", "accept");

        assertEquals(401, service.notifyDecision(null, "1").getStatusCode());
        assertEquals(400, service.notifyDecision("editor@cms.com", " ").getStatusCode());
        assertEquals(404, service.notifyDecision("editor@cms.com", "999").getStatusCode());

        FinalDecisionNotificationResult sent = service.notifyDecision("editor@cms.com", "1");
        assertEquals(202, sent.getStatusCode());

        email.setAvailable(false);
        FinalDecisionNotificationResult failed = service.notifyDecision("editor@cms.com", "1");
        assertEquals(500, failed.getStatusCode());
        assertEquals(1, failureRepository.findAll().size());
        assertEquals(1, editorNotifications.notifications().size());

        assertEquals(401, service.getDecisionStatus(null, "1").getStatusCode());
        assertEquals(400, service.getDecisionStatus("author@cms.com", " ").getStatusCode());
        assertEquals(404, service.getDecisionStatus("author@cms.com", "2").getStatusCode());
        assertEquals(403, service.getDecisionStatus("intruder@cms.com", "1").getStatusCode());

        FinalDecisionStatusResult authorView = service.getDecisionStatus("author@cms.com", "1");
        assertEquals(200, authorView.getStatusCode());
        assertEquals("accept", authorView.getDecision());

        FinalDecisionStatusResult editorView = service.getDecisionStatus("editor@cms.com", "1");
        assertEquals(200, editorView.getStatusCode());
    }

    private PaperSubmissionRepository paperRepo(List<PaperSubmission> submissions) {
        return new PaperSubmissionRepository() {
            @Override
            public long save(PaperSubmission paperSubmission) {
                return 0;
            }

            @Override
            public List<PaperSubmission> findAllByAuthorEmail(String authorEmail) {
                return submissions;
            }

            @Override
            public Optional<PaperSubmission> findBySubmissionId(long submissionId) {
                return submissions.stream().filter(v -> v.getSubmissionId() == submissionId).findFirst();
            }

            @Override
            public long countAll() {
                return submissions.size();
            }
        };
    }
}
