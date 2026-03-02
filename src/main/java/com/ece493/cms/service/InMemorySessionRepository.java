package com.ece493.cms.service;

import com.ece493.cms.model.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemorySessionRepository implements SessionRepository {
    private final List<Session> sessions = new ArrayList<>();

    @Override
    public List<Session> saveAll(long scheduleId, List<Session> sessionsToStore) {
        sessions.removeIf(session -> String.valueOf(scheduleId).equals(session.getScheduleId()));
        List<Session> stored = new ArrayList<>();
        for (Session session : sessionsToStore) {
            Session normalized = new Session(
                    session.getSessionId(),
                    String.valueOf(scheduleId),
                    session.getPaperId(),
                    session.getRoomId(),
                    session.getTimeSlotId()
            );
            sessions.add(normalized);
            stored.add(normalized);
        }
        return List.copyOf(stored);
    }

    @Override
    public List<Session> findByScheduleId(long scheduleId) {
        return sessions.stream()
                .filter(session -> String.valueOf(scheduleId).equals(session.getScheduleId()))
                .collect(Collectors.toList());
    }
}
