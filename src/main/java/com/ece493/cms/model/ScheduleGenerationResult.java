package com.ece493.cms.model;

import java.util.List;

public class ScheduleGenerationResult {
    private final int statusCode;
    private final String message;
    private final String scheduleId;
    private final String status;
    private final List<String> missing;

    private ScheduleGenerationResult(int statusCode, String message, String scheduleId, String status, List<String> missing) {
        this.statusCode = statusCode;
        this.message = message;
        this.scheduleId = scheduleId;
        this.status = status;
        this.missing = missing == null ? List.of() : List.copyOf(missing);
    }

    public static ScheduleGenerationResult created(String scheduleId) {
        return new ScheduleGenerationResult(201, "Schedule generated.", scheduleId, "generated", List.of());
    }

    public static ScheduleGenerationResult missingData(List<String> missing) {
        return new ScheduleGenerationResult(400, "Missing scheduling information.", null, null, missing);
    }

    public static ScheduleGenerationResult error(int statusCode, String message) {
        return new ScheduleGenerationResult(statusCode, message, null, null, List.of());
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

    public List<String> getMissing() {
        return missing;
    }
}
