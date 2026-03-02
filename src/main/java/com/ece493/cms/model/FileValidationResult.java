package com.ece493.cms.model;

public class FileValidationResult {
    private final int statusCode;
    private final String message;

    private FileValidationResult(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static FileValidationResult success(String message) {
        return new FileValidationResult(200, message);
    }

    public static FileValidationResult error(int statusCode, String message) {
        return new FileValidationResult(statusCode, message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
