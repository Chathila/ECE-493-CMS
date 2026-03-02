package com.ece493.cms.service;

import com.ece493.cms.model.FinalScheduleViewResult;
import com.ece493.cms.model.Schedule;

import java.util.Optional;

public class ScheduleViewService {
    private final ScheduleRepository scheduleRepository;
    private final SessionRepository sessionRepository;

    public ScheduleViewService(ScheduleRepository scheduleRepository, SessionRepository sessionRepository) {
        this.scheduleRepository = scheduleRepository;
        this.sessionRepository = sessionRepository;
    }

    public FinalScheduleViewResult viewFinalSchedule() {
        Optional<Schedule> scheduleOptional;
        try {
            scheduleOptional = scheduleRepository.findPublished();
        } catch (IllegalStateException e) {
            return FinalScheduleViewResult.error(500, "Unable to retrieve schedule; please try again later.");
        }

        if (scheduleOptional.isEmpty()) {
            return FinalScheduleViewResult.error(404, "Schedule not published yet. Please check back later.");
        }

        Schedule schedule = scheduleOptional.get();
        return FinalScheduleViewResult.found(new Schedule(
                schedule.getScheduleId(),
                schedule.getGeneratedBy(),
                schedule.getGeneratedAt(),
                schedule.getUpdatedAt(),
                schedule.getStatus(),
                sessionRepository.findByScheduleId(schedule.getScheduleId())
        ));
    }
}
