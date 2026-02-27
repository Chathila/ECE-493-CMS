package com.ece493.cms.model;

public class LoginSubmission {
    private final String email;
    private final String password;

    public LoginSubmission(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
