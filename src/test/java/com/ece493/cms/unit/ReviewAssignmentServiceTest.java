package com.ece493.cms.unit;

import com.ece493.cms.service.ReviewAssignmentService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewAssignmentServiceTest {
    @Test
    void supportsPendingAcceptedRejectedApprovalAndIgnoresUnknownDecision() {
        ReviewAssignmentService service = new ReviewAssignmentService();

        assertEquals("pending", service.getStatus(1L));
        service.markPending(1L);
        assertEquals("pending", service.getStatus(1L));
        service.updateFromDecision(1L, "accept");
        assertEquals("accepted", service.getStatus(1L));
        service.updateFromDecision(2L, "reject");
        assertEquals("rejected", service.getStatus(2L));
        service.updateFromDecision(3L, "maybe");
        assertEquals("pending", service.getStatus(3L));
        org.junit.jupiter.api.Assertions.assertTrue(service.markApproved(1L));
        assertEquals("approved", service.getStatus(1L));
        org.junit.jupiter.api.Assertions.assertTrue(service.markApproved(1L));
        org.junit.jupiter.api.Assertions.assertFalse(service.markApproved(2L));
        org.junit.jupiter.api.Assertions.assertFalse(service.markApproved(3L));
        org.junit.jupiter.api.Assertions.assertTrue(service.markReviewSubmitted(1L));
        assertEquals("submitted", service.getStatus(1L));
        org.junit.jupiter.api.Assertions.assertTrue(service.markReviewSubmitted(1L));
        org.junit.jupiter.api.Assertions.assertFalse(service.markReviewSubmitted(2L));
        org.junit.jupiter.api.Assertions.assertFalse(service.markReviewSubmitted(3L));
    }
}
