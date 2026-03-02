package com.ece493.cms.model;

public class ReviewerWorkload {
    private final String reviewerEmail;
    private final long assignedCount;
    private final long workloadLimit;

    public ReviewerWorkload(String reviewerEmail, long assignedCount, long workloadLimit) {
        this.reviewerEmail = reviewerEmail;
        this.assignedCount = assignedCount;
        this.workloadLimit = workloadLimit;
    }

    public String getReviewerEmail() {
        return reviewerEmail;
    }

    public long getAssignedCount() {
        return assignedCount;
    }

    public long getWorkloadLimit() {
        return workloadLimit;
    }

    public boolean canAssign() {
        return assignedCount < workloadLimit;
    }
}
