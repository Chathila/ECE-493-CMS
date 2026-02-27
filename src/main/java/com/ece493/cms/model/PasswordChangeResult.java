package com.ece493.cms.model;

public class PasswordChangeResult {
    private final int statusCode;
    private final String message;
    private final boolean requireRelogin;

    private PasswordChangeResult(int statusCode, String message, boolean requireRelogin) {
        this.statusCode = statusCode;
        this.message = message;
        this.requireRelogin = requireRelogin;
    }

    public static PasswordChangeResult success(String message) {
        return new PasswordChangeResult(200, message, true);
    }

    public static PasswordChangeResult error(int statusCode, String message) {
        return new PasswordChangeResult(statusCode, message, false);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public boolean requiresRelogin() {
        return requireRelogin;
    }
}
