package com.ece493.cms.model;

public class DraftSaveResult {
    private final int statusCode;
    private final String message;
    private final Long draftId;

    private DraftSaveResult(int statusCode, String message, Long draftId) {
        this.statusCode = statusCode;
        this.message = message;
        this.draftId = draftId;
    }

    public static DraftSaveResult success(String message) {
        return new DraftSaveResult(200, message, null);
    }

    public static DraftSaveResult success(String message, Long draftId) {
        return new DraftSaveResult(200, message, draftId);
    }

    public static DraftSaveResult error(int statusCode, String message) {
        return new DraftSaveResult(statusCode, message, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Long getDraftId() {
        return draftId;
    }
}
