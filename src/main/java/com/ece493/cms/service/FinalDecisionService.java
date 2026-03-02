package com.ece493.cms.service;

import com.ece493.cms.model.FinalDecision;
import com.ece493.cms.model.FinalDecisionNotificationResult;
import com.ece493.cms.model.FinalDecisionStatusResult;
import com.ece493.cms.model.FinalDecisionSubmissionResult;
import com.ece493.cms.model.PaperSubmission;
import com.ece493.cms.model.ReviewInvitation;
import com.ece493.cms.repository.PaperSubmissionRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class FinalDecisionService {
    private final FinalDecisionRepository finalDecisionRepository;
    private final PaperSubmissionRepository paperSubmissionRepository;
    private final ReviewInvitationRepository reviewInvitationRepository;
    private final ReviewAssignmentService reviewAssignmentService;
    private final FinalDecisionNotificationService finalDecisionNotificationService;

    public FinalDecisionService(
            FinalDecisionRepository finalDecisionRepository,
            PaperSubmissionRepository paperSubmissionRepository,
            ReviewInvitationRepository reviewInvitationRepository,
            ReviewAssignmentService reviewAssignmentService,
            FinalDecisionNotificationService finalDecisionNotificationService
    ) {
        this.finalDecisionRepository = finalDecisionRepository;
        this.paperSubmissionRepository = paperSubmissionRepository;
        this.reviewInvitationRepository = reviewInvitationRepository;
        this.reviewAssignmentService = reviewAssignmentService;
        this.finalDecisionNotificationService = finalDecisionNotificationService;
    }

    public FinalDecisionSubmissionResult submitDecision(String editorEmail, String paperId, String decision) {
        if (isBlank(editorEmail)) {
            return FinalDecisionSubmissionResult.error(401, "You must log in to submit a decision.");
        }
        if (isBlank(paperId)) {
            return FinalDecisionSubmissionResult.error(400, "Paper id is required.");
        }
        String normalizedDecision = normalizeDecision(decision);
        if (normalizedDecision == null) {
            return FinalDecisionSubmissionResult.error(400, "Decision must be accept or reject.");
        }

        Optional<PaperSubmission> paperOptional = lookupPaper(paperId);
        if (paperOptional.isEmpty()) {
            return FinalDecisionSubmissionResult.error(404, "Paper not found.");
        }
        if (!allReviewsCompleted(paperId)) {
            return FinalDecisionSubmissionResult.error(409, "Required reviews are incomplete.");
        }

        FinalDecision saved;
        try {
            saved = finalDecisionRepository.save(new FinalDecision(
                    0L,
                    paperId,
                    editorEmail,
                    paperOptional.get().getAuthorEmail(),
                    normalizedDecision,
                    Instant.now(),
                    "queued"
            ));
        } catch (IllegalStateException e) {
            return FinalDecisionSubmissionResult.error(500, "Could not record decision. Please retry later.");
        }

        boolean delivered = finalDecisionNotificationService.dispatch(saved);
        finalDecisionRepository.updateNotificationStatus(saved.getDecisionId(), delivered ? "sent" : "failed");
        return FinalDecisionSubmissionResult.created(paperId, normalizedDecision);
    }

    public FinalDecisionNotificationResult notifyDecision(String editorEmail, String paperId) {
        if (isBlank(editorEmail)) {
            return FinalDecisionNotificationResult.error(401, "You must log in to send notifications.");
        }
        if (isBlank(paperId)) {
            return FinalDecisionNotificationResult.error(400, "Paper id is required.");
        }

        Optional<FinalDecision> decisionOptional = finalDecisionRepository.findLatestByPaperId(paperId);
        if (decisionOptional.isEmpty()) {
            return FinalDecisionNotificationResult.error(404, "Decision not found.");
        }
        FinalDecision decision = decisionOptional.get();
        boolean delivered = finalDecisionNotificationService.dispatch(decision);
        finalDecisionRepository.updateNotificationStatus(decision.getDecisionId(), delivered ? "sent" : "failed");
        return delivered
                ? FinalDecisionNotificationResult.sent(paperId)
                : FinalDecisionNotificationResult.failed(paperId);
    }

    public FinalDecisionStatusResult getDecisionStatus(String viewerEmail, String paperId) {
        if (isBlank(viewerEmail)) {
            return FinalDecisionStatusResult.error(401, "You must log in to view decision status.");
        }
        if (isBlank(paperId)) {
            return FinalDecisionStatusResult.error(400, "Paper id is required.");
        }

        Optional<FinalDecision> decisionOptional = finalDecisionRepository.findLatestByPaperId(paperId);
        if (decisionOptional.isEmpty()) {
            return FinalDecisionStatusResult.error(404, "Decision status unavailable.");
        }

        FinalDecision decision = decisionOptional.get();
        if (!viewerEmail.equalsIgnoreCase(decision.getAuthorEmail())
                && !viewerEmail.equalsIgnoreCase(decision.getEditorEmail())) {
            return FinalDecisionStatusResult.error(403, "Access denied.");
        }

        return FinalDecisionStatusResult.found(
                decision.getPaperId(),
                decision.getDecision(),
                decision.getNotificationStatus()
        );
    }

    private Optional<PaperSubmission> lookupPaper(String paperId) {
        try {
            return paperSubmissionRepository.findBySubmissionId(Long.parseLong(paperId));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private boolean allReviewsCompleted(String paperId) {
        List<ReviewInvitation> invitations = reviewInvitationRepository.findAll().stream()
                .filter(invitation -> paperId.equals(invitation.getPaperId()))
                .toList();
        if (invitations.isEmpty()) {
            return false;
        }
        for (ReviewInvitation invitation : invitations) {
            String status = reviewAssignmentService.getStatus(invitation.getInvitationId());
            if (!"submitted".equals(status)) {
                return false;
            }
        }
        return true;
    }

    private String normalizeDecision(String decision) {
        if (decision == null) {
            return null;
        }
        String normalized = decision.trim().toLowerCase();
        if ("accept".equals(normalized) || "reject".equals(normalized)) {
            return normalized;
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
