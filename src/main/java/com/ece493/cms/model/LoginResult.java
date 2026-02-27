package com.ece493.cms.model;

public class LoginResult {
    private final int statusCode;
    private final String message;
    private final String redirectLocation;
    private final String authenticatedEmail;

    private LoginResult(int statusCode, String message, String redirectLocation, String authenticatedEmail) {
        this.statusCode = statusCode;
        this.message = message;
        this.redirectLocation = redirectLocation;
        this.authenticatedEmail = authenticatedEmail;
    }

    public static LoginResult successRedirect(String redirectLocation) {
        return new LoginResult(302, null, redirectLocation, null);
    }

    public static LoginResult successRedirect(String redirectLocation, String authenticatedEmail) {
        return new LoginResult(302, null, redirectLocation, authenticatedEmail);
    }

    public static LoginResult error(int statusCode, String message) {
        return new LoginResult(statusCode, message, null, null);
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

    public String getAuthenticatedEmail() {
        return authenticatedEmail;
    }

    public boolean isRedirect() {
        return redirectLocation != null;
    }
}
