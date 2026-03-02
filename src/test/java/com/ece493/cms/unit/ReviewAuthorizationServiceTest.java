package com.ece493.cms.unit;

import com.ece493.cms.model.ReviewAssignmentRecord;
import com.ece493.cms.service.ReviewAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewAuthorizationServiceTest {
    @Test
    void enforcesAccessAndSubmissionRules() {
        ReviewAuthorizationService service = new ReviewAuthorizationService();
        ReviewAssignmentRecord accepted = new ReviewAssignmentRecord(1L, "10", "ref@cms.com", "ed@cms.com", "accepted", false);
        ReviewAssignmentRecord approved = new ReviewAssignmentRecord(1L, "10", "ref@cms.com", "ed@cms.com", "approved", false);
        ReviewAssignmentRecord submitted = new ReviewAssignmentRecord(1L, "10", "ref@cms.com", "ed@cms.com", "submitted", false);
        ReviewAssignmentRecord pending = new ReviewAssignmentRecord(1L, "10", "ref@cms.com", "ed@cms.com", "pending", false);
        ReviewAssignmentRecord expired = new ReviewAssignmentRecord(1L, "10", "ref@cms.com", "ed@cms.com", "accepted", true);

        assertTrue(service.canAccessForm("ref@cms.com", accepted));
        assertTrue(service.canAccessForm("ref@cms.com", approved));
        assertTrue(service.canAccessForm("ref@cms.com", submitted));
        assertFalse(service.canAccessForm("ref@cms.com", pending));
        assertFalse(service.canAccessForm("other@cms.com", accepted));
        assertFalse(service.canAccessForm("ref@cms.com", expired));
        assertFalse(service.canAccessForm(" ", accepted));
        assertFalse(service.canAccessForm("ref@cms.com", null));

        assertTrue(service.canSubmitReview("ref@cms.com", accepted));
        assertTrue(service.canSubmitReview("ref@cms.com", approved));
        assertFalse(service.canSubmitReview("ref@cms.com", submitted));
        assertFalse(service.canSubmitReview("ref@cms.com", pending));
        assertFalse(service.canSubmitReview("other@cms.com", accepted));
        assertFalse(service.canSubmitReview("ref@cms.com", expired));
        assertFalse(service.canSubmitReview(null, accepted));
        assertFalse(service.canSubmitReview("ref@cms.com", null));
    }
}
