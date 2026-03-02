package com.ece493.cms.service;

import com.ece493.cms.model.ReviewerWorkload;
import com.ece493.cms.repository.RefereeAssignmentRepository;

public class DefaultWorkloadCheckService implements WorkloadCheckService {
    private final RefereeAssignmentRepository refereeAssignmentRepository;
    private final long workloadLimit;

    public DefaultWorkloadCheckService(RefereeAssignmentRepository refereeAssignmentRepository, long workloadLimit) {
        this.refereeAssignmentRepository = refereeAssignmentRepository;
        this.workloadLimit = workloadLimit;
    }

    @Override
    public ReviewerWorkload getReviewerWorkload(String reviewerEmail) {
        if (reviewerEmail == null || reviewerEmail.trim().isEmpty()) {
            throw new IllegalStateException("Referee workload data is unavailable.");
        }

        long assignedCount;
        try {
            assignedCount = refereeAssignmentRepository.countAssignmentsByRefereeEmail(reviewerEmail);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Referee workload data is unavailable.", e);
        }

        if (assignedCount < 0) {
            throw new IllegalStateException("Referee workload data is unavailable.");
        }

        return new ReviewerWorkload(reviewerEmail, assignedCount, workloadLimit);
    }
}
