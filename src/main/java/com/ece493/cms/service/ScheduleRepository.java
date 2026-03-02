package com.ece493.cms.service;

import com.ece493.cms.model.Schedule;

import java.util.Optional;

public interface ScheduleRepository {
    Schedule save(Schedule schedule);

    Optional<Schedule> findById(long scheduleId);

    Schedule update(Schedule schedule);

    long countAll();
}
