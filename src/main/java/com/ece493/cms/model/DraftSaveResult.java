package com.ece493.cms.model;

public class DraftSaveResult {
    private final int statusCode;
    private final String message;

    private DraftSaveResult(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static DraftSaveResult success(String message) {
        return new DraftSaveResult(200, message);
    }

    public static DraftSaveResult error(int statusCode, String message) {
        return new DraftSaveResult(statusCode, message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
