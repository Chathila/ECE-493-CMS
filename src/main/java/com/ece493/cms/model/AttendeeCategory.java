package com.ece493.cms.model;

public class AttendeeCategory {
    private final long categoryId;
    private final String name;

    public AttendeeCategory(long categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }
}
