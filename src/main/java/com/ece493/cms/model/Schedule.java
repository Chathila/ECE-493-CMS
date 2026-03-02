package com.ece493.cms.model;

import java.time.Instant;
import java.util.List;

public class Schedule {
    private final long scheduleId;
    private final String generatedBy;
    private final Instant generatedAt;
    private final Instant updatedAt;
    private final String status;
    private final List<Session> sessions;

    public Schedule(
            long scheduleId,
            String generatedBy,
            Instant generatedAt,
            Instant updatedAt,
            String status,
            List<Session> sessions
    ) {
        this.scheduleId = scheduleId;
        this.generatedBy = generatedBy;
        this.generatedAt = generatedAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.sessions = sessions == null ? List.of() : List.copyOf(sessions);
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public List<Session> getSessions() {
        return sessions;
    }
}
