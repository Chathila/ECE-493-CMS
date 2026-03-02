package com.ece493.cms.service;

import com.ece493.cms.model.Session;

import java.util.List;

public interface SessionRepository {
    List<Session> saveAll(long scheduleId, List<Session> sessions);

    List<Session> findByScheduleId(long scheduleId);
}
