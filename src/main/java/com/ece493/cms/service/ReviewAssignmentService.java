package com.ece493.cms.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReviewAssignmentService {
    private final Map<Long, String> assignmentStatusByInvitationId = new ConcurrentHashMap<>();

    public void markPending(long invitationId) {
        assignmentStatusByInvitationId.putIfAbsent(invitationId, "pending");
    }

    public void updateFromDecision(long invitationId, String decision) {
        if ("accept".equalsIgnoreCase(decision)) {
            assignmentStatusByInvitationId.put(invitationId, "accepted");
            return;
        }
        if ("reject".equalsIgnoreCase(decision)) {
            assignmentStatusByInvitationId.put(invitationId, "rejected");
        }
    }

    public boolean markApproved(long invitationId) {
        String currentStatus = getStatus(invitationId);
        if ("accepted".equals(currentStatus) || "approved".equals(currentStatus)) {
            assignmentStatusByInvitationId.put(invitationId, "approved");
            return true;
        }
        return false;
    }

    public boolean markReviewSubmitted(long invitationId) {
        String currentStatus = getStatus(invitationId);
        if ("accepted".equals(currentStatus) || "approved".equals(currentStatus) || "submitted".equals(currentStatus)) {
            assignmentStatusByInvitationId.put(invitationId, "submitted");
            return true;
        }
        return false;
    }

    public String getStatus(long invitationId) {
        return assignmentStatusByInvitationId.getOrDefault(invitationId, "pending");
    }
}
