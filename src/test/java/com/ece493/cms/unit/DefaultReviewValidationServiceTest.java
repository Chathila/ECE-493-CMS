package com.ece493.cms.unit;

import com.ece493.cms.service.DefaultReviewValidationService;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultReviewValidationServiceTest {
    @Test
    void validatesRequiredFields() {
        DefaultReviewValidationService service = new DefaultReviewValidationService();

        assertEquals(java.util.List.of("score", "recommendation", "comments"), service.validate(null));
        assertEquals(java.util.List.of("recommendation", "comments"), service.validate(Map.of("score", "8")));
        assertEquals(java.util.List.of("score"), service.validate(Map.of("score", " ", "recommendation", "accept", "comments", "ok")));
        assertEquals(java.util.List.of(), service.validate(Map.of("score", "8", "recommendation", "accept", "comments", "ok")));
    }
}
