package com.ece493.cms.service;

import com.ece493.cms.model.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScheduleValidationService {
    public List<String> validate(List<Session> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return List.of("sessions");
        }

        List<String> fields = new ArrayList<>();
        Set<String> roomTimePairs = new HashSet<>();
        for (int i = 0; i < sessions.size(); i++) {
            Session session = sessions.get(i);
            if (isBlank(session.getRoomId())) {
                fields.add("sessions[" + i + "].room_id");
            }
            if (isBlank(session.getTimeSlotId())) {
                fields.add("sessions[" + i + "].time_slot_id");
            }
            if (isBlank(session.getPaperId())) {
                fields.add("sessions[" + i + "].paper_id");
            }
            if (!isBlank(session.getRoomId()) && !isBlank(session.getTimeSlotId())) {
                String key = session.getRoomId() + "|" + session.getTimeSlotId();
                if (!roomTimePairs.add(key)) {
                    fields.add("sessions[" + i + "].conflict");
                }
            }
        }
        return fields;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
