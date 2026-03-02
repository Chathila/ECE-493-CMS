package com.ece493.cms.service;

import com.ece493.cms.model.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryScheduleRepository implements ScheduleRepository {
    private final AtomicLong idSequence = new AtomicLong(1L);
    private final List<Schedule> schedules = new ArrayList<>();
    private boolean failOnSave;
    private boolean failOnUpdate;

    @Override
    public Schedule save(Schedule schedule) {
        if (failOnSave) {
            throw new IllegalStateException("Failed to store schedule");
        }
        Schedule stored = new Schedule(
                idSequence.getAndIncrement(),
                schedule.getGeneratedBy(),
                schedule.getGeneratedAt(),
                schedule.getUpdatedAt(),
                schedule.getStatus(),
                schedule.getSessions()
        );
        schedules.add(stored);
        return stored;
    }

    @Override
    public Optional<Schedule> findById(long scheduleId) {
        return schedules.stream().filter(v -> v.getScheduleId() == scheduleId).findFirst();
    }

    @Override
    public Schedule update(Schedule schedule) {
        if (failOnUpdate) {
            throw new IllegalStateException("Failed to update schedule");
        }
        for (int i = 0; i < schedules.size(); i++) {
            if (schedules.get(i).getScheduleId() == schedule.getScheduleId()) {
                schedules.set(i, schedule);
                return schedule;
            }
        }
        throw new IllegalStateException("Schedule not found");
    }

    @Override
    public long countAll() {
        return schedules.size();
    }

    public void setFailOnSave(boolean failOnSave) {
        this.failOnSave = failOnSave;
    }

    public void setFailOnUpdate(boolean failOnUpdate) {
        this.failOnUpdate = failOnUpdate;
    }
}
