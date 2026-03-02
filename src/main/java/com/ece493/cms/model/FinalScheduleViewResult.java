package com.ece493.cms.model;

public class FinalScheduleViewResult {
    private final int statusCode;
    private final String message;
    private final Schedule schedule;

    private FinalScheduleViewResult(int statusCode, String message, Schedule schedule) {
        this.statusCode = statusCode;
        this.message = message;
        this.schedule = schedule;
    }

    public static FinalScheduleViewResult found(Schedule schedule) {
        return new FinalScheduleViewResult(200, "Final schedule loaded.", schedule);
    }

    public static FinalScheduleViewResult error(int statusCode, String message) {
        return new FinalScheduleViewResult(statusCode, message, null);
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
