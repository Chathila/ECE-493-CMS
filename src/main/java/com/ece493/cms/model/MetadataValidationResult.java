package com.ece493.cms.model;

public class MetadataValidationResult {
    private final boolean valid;
    private final String message;

    private MetadataValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public static MetadataValidationResult valid() {
        return new MetadataValidationResult(true, null);
    }

    public static MetadataValidationResult invalid(String message) {
        return new MetadataValidationResult(false, message);
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
}
