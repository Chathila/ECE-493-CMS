package com.ece493.cms.unit;

import com.ece493.cms.model.Review;
import com.ece493.cms.model.ReviewForm;
import com.ece493.cms.model.ReviewSubmissionResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewModelNullCollectionsTest {
    @Test
    void normalizesNullCollectionsToEmpty() {
        Review review = new Review(1L, 2L, Instant.now(), "submitted", null);
        ReviewForm form = new ReviewForm("f", "v1", null);
        ReviewSubmissionResult validation = ReviewSubmissionResult.validationError(null);

        assertTrue(review.getResponses().isEmpty());
        assertTrue(form.getRequiredFields().isEmpty());
        assertTrue(validation.getFields().isEmpty());
    }
}
