package com.ece493.cms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultReviewValidationService implements ReviewValidationService {
    @Override
    public List<String> validate(Map<String, String> responses) {
        List<String> fields = new ArrayList<>();
        if (isBlank(value(responses, "score"))) {
            fields.add("score");
        }
        if (isBlank(value(responses, "recommendation"))) {
            fields.add("recommendation");
        }
        if (isBlank(value(responses, "comments"))) {
            fields.add("comments");
        }
        return fields;
    }

    private String value(Map<String, String> responses, String key) {
        if (responses == null) {
            return null;
        }
        return responses.get(key);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
