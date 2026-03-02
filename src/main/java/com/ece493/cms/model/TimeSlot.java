package com.ece493.cms.model;

public class TimeSlot {
    private final String timeSlotId;
    private final String startTime;
    private final String endTime;

    public TimeSlot(String timeSlotId, String startTime, String endTime) {
        this.timeSlotId = timeSlotId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getTimeSlotId() {
        return timeSlotId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
