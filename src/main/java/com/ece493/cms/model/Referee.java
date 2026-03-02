package com.ece493.cms.model;

public class Referee {
    private final String email;
    private final long currentWorkload;
    private final long maxWorkload;

    public Referee(String email, long currentWorkload, long maxWorkload) {
        this.email = email;
        this.currentWorkload = currentWorkload;
        this.maxWorkload = maxWorkload;
    }

    public String getEmail() {
        return email;
    }

    public long getCurrentWorkload() {
        return currentWorkload;
    }

    public long getMaxWorkload() {
        return maxWorkload;
    }
}
