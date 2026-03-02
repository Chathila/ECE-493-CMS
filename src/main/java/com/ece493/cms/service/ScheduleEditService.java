package com.ece493.cms.service;

import com.ece493.cms.model.Schedule;
import com.ece493.cms.model.ScheduleEditResult;
import com.ece493.cms.model.ScheduleViewResult;
import com.ece493.cms.model.Session;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class ScheduleEditService {
    private final ScheduleRepository scheduleRepository;
    private final SessionRepository sessionRepository;
    private final ScheduleValidationService scheduleValidationService;

    public ScheduleEditService(
            ScheduleRepository scheduleRepository,
            SessionRepository sessionRepository,
            ScheduleValidationService scheduleValidationService
    ) {
        this.scheduleRepository = scheduleRepository;
        this.sessionRepository = sessionRepository;
        this.scheduleValidationService = scheduleValidationService;
    }

    public ScheduleViewResult getEditableSchedule(String editorEmail, String scheduleId) {
        if (isBlank(editorEmail)) {
            return ScheduleViewResult.error(401, "You must log in to edit schedule.");
        }
        long id;
        try {
            id = Long.parseLong(scheduleId);
        } catch (Exception e) {
            return ScheduleViewResult.error(400, "Schedule id is invalid.");
        }
        Optional<Schedule> scheduleOptional = scheduleRepository.findById(id);
        if (scheduleOptional.isEmpty()) {
            return ScheduleViewResult.error(404, "Schedule not found.");
        }
        Schedule schedule = scheduleOptional.get();
        return ScheduleViewResult.found(new Schedule(
                schedule.getScheduleId(),
                schedule.getGeneratedBy(),
                schedule.getGeneratedAt(),
                schedule.getUpdatedAt(),
                schedule.getStatus(),
                sessionRepository.findByScheduleId(id)
        ));
    }

    public ScheduleEditResult updateSchedule(String editorEmail, String scheduleId, List<Session> sessions) {
        if (isBlank(editorEmail)) {
            return ScheduleEditResult.error(401, "You must log in to update schedule.");
        }
        long id;
        try {
            id = Long.parseLong(scheduleId);
        } catch (Exception e) {
            return ScheduleEditResult.error(400, "Schedule id is invalid.");
        }
        Optional<Schedule> scheduleOptional = scheduleRepository.findById(id);
        if (scheduleOptional.isEmpty()) {
            return ScheduleEditResult.error(404, "Schedule not found.");
        }

        List<String> invalidFields = scheduleValidationService.validate(sessions);
        if (!invalidFields.isEmpty()) {
            return ScheduleEditResult.validationError(invalidFields);
        }

        Schedule existing = scheduleOptional.get();
        try {
            sessionRepository.saveAll(id, sessions);
            scheduleRepository.update(new Schedule(
                    id,
                    existing.getGeneratedBy(),
                    existing.getGeneratedAt(),
                    Instant.now(),
                    "updated",
                    sessionRepository.findByScheduleId(id)
            ));
            return ScheduleEditResult.updated(String.valueOf(id));
        } catch (IllegalStateException e) {
            return ScheduleEditResult.error(500, "Could not save schedule. Please retry later.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
