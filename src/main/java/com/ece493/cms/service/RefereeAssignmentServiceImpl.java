package com.ece493.cms.service;

import com.ece493.cms.model.RefereeAssignmentResult;
import com.ece493.cms.repository.RefereeAssignmentRepository;
import com.ece493.cms.repository.UserAccountRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RefereeAssignmentServiceImpl implements RefereeAssignmentService {
    public static final long DEFAULT_REFEREE_WORKLOAD_LIMIT = 5L;

    private final UserAccountRepository userAccountRepository;
    private final RefereeAssignmentRepository refereeAssignmentRepository;
    private final NotificationService notificationService;

    public RefereeAssignmentServiceImpl(
            UserAccountRepository userAccountRepository,
            RefereeAssignmentRepository refereeAssignmentRepository,
            NotificationService notificationService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.refereeAssignmentRepository = refereeAssignmentRepository;
        this.notificationService = notificationService;
    }

    @Override
    public RefereeAssignmentResult assignReferees(String editorEmail, String paperId, List<String> refereeEmails) {
        if (isBlank(editorEmail)) {
            return RefereeAssignmentResult.error(401, "You must log in to assign referees.");
        }
        if (isBlank(paperId)) {
            return RefereeAssignmentResult.error(400, "Paper id is required.");
        }

        List<String> normalizedEmails = normalizeRefereeEmails(refereeEmails);
        if (normalizedEmails.isEmpty()) {
            return RefereeAssignmentResult.error(400, "At least one referee email is required.");
        }

        for (String refereeEmail : normalizedEmails) {
            if (!userAccountRepository.existsByEmail(refereeEmail)) {
                return RefereeAssignmentResult.error(400, "Invalid referee email address: " + refereeEmail);
            }
        }

        for (String refereeEmail : normalizedEmails) {
            long currentLoad;
            try {
                currentLoad = refereeAssignmentRepository.countAssignmentsByRefereeEmail(refereeEmail);
            } catch (IllegalStateException e) {
                return RefereeAssignmentResult.error(500, "Referee workload check could not be completed. Please retry.");
            }
            if (currentLoad >= DEFAULT_REFEREE_WORKLOAD_LIMIT) {
                return RefereeAssignmentResult.error(409, "Referee workload limit exceeded for: " + refereeEmail);
            }
        }

        refereeAssignmentRepository.saveAssignments(paperId, normalizedEmails);

        try {
            notificationService.sendReviewInvitations(paperId, normalizedEmails);
            return RefereeAssignmentResult.success("Referees assigned successfully and invitations sent.");
        } catch (IllegalStateException e) {
            return RefereeAssignmentResult.warning(
                    503,
                    "Referees assigned successfully.",
                    "Invitations were not sent due to notification service unavailability."
            );
        }
    }

    private List<String> normalizeRefereeEmails(List<String> refereeEmails) {
        if (refereeEmails == null) {
            return List.of();
        }

        Set<String> unique = new LinkedHashSet<>();
        for (String email : refereeEmails) {
            if (email == null) {
                continue;
            }
            String normalized = email.trim();
            if (!normalized.isEmpty()) {
                unique.add(normalized);
            }
        }
        return new ArrayList<>(unique);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
