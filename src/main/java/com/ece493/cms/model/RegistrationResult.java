package com.ece493.cms.model;

public class RegistrationResult {
    private final int statusCode;
    private final String message;
    private final String redirectLocation;

    private RegistrationResult(int statusCode, String message, String redirectLocation) {
        this.statusCode = statusCode;
        this.message = message;
        this.redirectLocation = redirectLocation;
    }

    public static RegistrationResult successRedirect(String location) {
        return new RegistrationResult(302, "Registration successful", location);
    }

    public static RegistrationResult error(int statusCode, String message) {
        return new RegistrationResult(statusCode, message, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getRedirectLocation() {
        return redirectLocation;
    }

    public boolean isRedirect() {
        return redirectLocation != null;
    }
}
