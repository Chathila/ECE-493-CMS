package com.ece493.cms.service;

import com.ece493.cms.model.AcceptedPaper;
import com.ece493.cms.model.Schedule;
import com.ece493.cms.model.ScheduleGenerationResult;
import com.ece493.cms.model.ScheduleViewResult;
import com.ece493.cms.model.Session;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScheduleGenerationService {
    private final ScheduleRepository scheduleRepository;
    private final SessionRepository sessionRepository;
    private final SchedulingDataRepository schedulingDataRepository;
    private final SchedulingAlgorithm schedulingAlgorithm;
    private final List<String> failureLog = new ArrayList<>();

    public ScheduleGenerationService(
            ScheduleRepository scheduleRepository,
            SessionRepository sessionRepository,
            SchedulingDataRepository schedulingDataRepository,
            SchedulingAlgorithm schedulingAlgorithm
    ) {
        this.scheduleRepository = scheduleRepository;
        this.sessionRepository = sessionRepository;
        this.schedulingDataRepository = schedulingDataRepository;
        this.schedulingAlgorithm = schedulingAlgorithm;
    }

    public ScheduleGenerationResult generateSchedule(String adminEmail) {
        if (isBlank(adminEmail)) {
            return ScheduleGenerationResult.error(401, "You must log in to generate schedule.");
        }

        List<String> missing = missingData();
        if (!missing.isEmpty()) {
            return ScheduleGenerationResult.missingData(missing);
        }

        List<Session> generatedSessions;
        try {
            generatedSessions = schedulingAlgorithm.generate(
                    schedulingDataRepository.acceptedPapers(),
                    schedulingDataRepository.rooms(),
                    schedulingDataRepository.timeSlots()
            );
        } catch (IllegalStateException e) {
            failureLog.add(e.getMessage() == null ? "schedule generation failed" : e.getMessage());
            return ScheduleGenerationResult.error(500, "Schedule generation failed.");
        }

        try {
            Schedule stored = scheduleRepository.save(new Schedule(
                    0L,
                    adminEmail,
                    Instant.now(),
                    Instant.now(),
                    "generated",
                    List.of()
            ));
            sessionRepository.saveAll(stored.getScheduleId(), generatedSessions);
            return ScheduleGenerationResult.created(String.valueOf(stored.getScheduleId()));
        } catch (IllegalStateException e) {
            return ScheduleGenerationResult.error(500, "Schedule generation failed.");
        }
    }

    public ScheduleViewResult viewSchedule(String requesterEmail, String scheduleId) {
        if (isBlank(requesterEmail)) {
            return ScheduleViewResult.error(401, "You must log in to view schedule.");
        }
        long parsedScheduleId;
        try {
            parsedScheduleId = Long.parseLong(scheduleId);
        } catch (Exception e) {
            return ScheduleViewResult.error(400, "Schedule id is invalid.");
        }

        Optional<Schedule> scheduleOptional = scheduleRepository.findById(parsedScheduleId);
        if (scheduleOptional.isEmpty()) {
            return ScheduleViewResult.error(404, "Schedule not found.");
        }

        Schedule schedule = scheduleOptional.get();
        if (!requesterEmail.equalsIgnoreCase(schedule.getGeneratedBy())) {
            return ScheduleViewResult.error(403, "Access denied.");
        }

        return ScheduleViewResult.found(new Schedule(
                schedule.getScheduleId(),
                schedule.getGeneratedBy(),
                schedule.getGeneratedAt(),
                schedule.getUpdatedAt(),
                schedule.getStatus(),
                sessionRepository.findByScheduleId(parsedScheduleId)
        ));
    }

    public List<String> failureLog() {
        return List.copyOf(failureLog);
    }

    public List<AcceptedPaper> seedAcceptedPapersFromSubmissions(List<com.ece493.cms.model.PaperSubmission> submissions) {
        List<AcceptedPaper> accepted = submissions.stream()
                .map(v -> new AcceptedPaper(String.valueOf(v.getSubmissionId()), v.getTitle()))
                .toList();
        if (schedulingDataRepository instanceof InMemorySchedulingDataRepository inMemory) {
            inMemory.setAcceptedPapers(accepted);
        }
        return accepted;
    }

    private List<String> missingData() {
        List<String> missing = new ArrayList<>();
        if (schedulingDataRepository.rooms().isEmpty()) {
            missing.add("rooms");
        }
        if (schedulingDataRepository.timeSlots().isEmpty()) {
            missing.add("time_slots");
        }
        if (schedulingDataRepository.acceptedPapers().isEmpty()) {
            missing.add("accepted_papers");
        }
        return missing;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
