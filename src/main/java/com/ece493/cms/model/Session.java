package com.ece493.cms.model;

public class Session {
    private final long sessionId;
    private final String scheduleId;
    private final String paperId;
    private final String roomId;
    private final String timeSlotId;

    public Session(long sessionId, String scheduleId, String paperId, String roomId, String timeSlotId) {
        this.sessionId = sessionId;
        this.scheduleId = scheduleId;
        this.paperId = paperId;
        this.roomId = roomId;
        this.timeSlotId = timeSlotId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getPaperId() {
        return paperId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getTimeSlotId() {
        return timeSlotId;
    }
}
