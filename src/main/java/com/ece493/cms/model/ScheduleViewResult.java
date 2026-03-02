package com.ece493.cms.model;

public class ScheduleViewResult {
    private final int statusCode;
    private final String message;
    private final Schedule schedule;

    private ScheduleViewResult(int statusCode, String message, Schedule schedule) {
        this.statusCode = statusCode;
        this.message = message;
        this.schedule = schedule;
    }

    public static ScheduleViewResult found(Schedule schedule) {
        return new ScheduleViewResult(200, "Schedule loaded.", schedule);
    }

    public static ScheduleViewResult error(int statusCode, String message) {
        return new ScheduleViewResult(statusCode, message, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Schedule getSchedule() {
        return schedule;
    }
}
