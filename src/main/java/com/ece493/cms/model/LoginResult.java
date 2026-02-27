package com.ece493.cms.model;

public class LoginResult {
    private final int statusCode;
    private final String message;
    private final String redirectLocation;

    private LoginResult(int statusCode, String message, String redirectLocation) {
        this.statusCode = statusCode;
        this.message = message;
        this.redirectLocation = redirectLocation;
    }

    public static LoginResult successRedirect(String redirectLocation) {
        return new LoginResult(302, null, redirectLocation);
    }

    public static LoginResult error(int statusCode, String message) {
        return new LoginResult(statusCode, message, null);
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
