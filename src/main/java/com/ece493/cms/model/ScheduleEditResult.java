package com.ece493.cms.model;

import java.util.List;

public class ScheduleEditResult {
    private final int statusCode;
    private final String message;
    private final String scheduleId;
    private final String status;
    private final List<String> fields;

    private ScheduleEditResult(int statusCode, String message, String scheduleId, String status, List<String> fields) {
        this.statusCode = statusCode;
        this.message = message;
        this.scheduleId = scheduleId;
        this.status = status;
        this.fields = fields == null ? List.of() : List.copyOf(fields);
    }

    public static ScheduleEditResult updated(String scheduleId) {
        return new ScheduleEditResult(200, "Schedule updated.", scheduleId, "updated", List.of());
    }

    public static ScheduleEditResult validationError(List<String> fields) {
        return new ScheduleEditResult(400, "Schedule validation failed.", null, null, fields);
    }

    public static ScheduleEditResult error(int statusCode, String message) {
        return new ScheduleEditResult(statusCode, message, null, null, List.of());
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getFields() {
        return fields;
    }
}
