package com.ece493.cms.unit;

import com.ece493.cms.model.ReviewInvitation;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewInvitationTest {
    @Test
    void isExpiredHandlesNullEqualBeforeAndAfter() {
        Instant now = Instant.now();
        ReviewInvitation noExpiry = new ReviewInvitation(1L, "a1", "editor@cms.com", "ref@cms.com", "p1", "open", "c", now, null);
        ReviewInvitation equalExpiry = new ReviewInvitation(2L, "a2", "editor@cms.com", "ref@cms.com", "p2", "open", "c", now, now);
        ReviewInvitation beforeExpiry = new ReviewInvitation(3L, "a3", "editor@cms.com", "ref@cms.com", "p3", "open", "c", now, now.minusSeconds(1));
        ReviewInvitation afterExpiry = new ReviewInvitation(4L, "a4", "editor@cms.com", "ref@cms.com", "p4", "open", "c", now, now.plusSeconds(1));

        assertFalse(noExpiry.isExpired(now));
        assertTrue(equalExpiry.isExpired(now));
        assertTrue(beforeExpiry.isExpired(now));
        assertFalse(afterExpiry.isExpired(now));
    }
}
