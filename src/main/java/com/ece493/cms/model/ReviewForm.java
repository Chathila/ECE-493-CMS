package com.ece493.cms.model;

import java.util.List;

public class ReviewForm {
    private final String formId;
    private final String version;
    private final List<String> requiredFields;

    public ReviewForm(String formId, String version, List<String> requiredFields) {
        this.formId = formId;
        this.version = version;
        this.requiredFields = requiredFields == null ? List.of() : List.copyOf(requiredFields);
    }

    public String getFormId() {
        return formId;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getRequiredFields() {
        return requiredFields;
    }
}
