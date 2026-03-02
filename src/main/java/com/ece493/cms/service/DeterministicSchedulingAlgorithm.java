package com.ece493.cms.service;

import com.ece493.cms.model.AcceptedPaper;
import com.ece493.cms.model.Room;
import com.ece493.cms.model.Session;
import com.ece493.cms.model.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public class DeterministicSchedulingAlgorithm implements SchedulingAlgorithm {
    private boolean fail;

    @Override
    public List<Session> generate(List<AcceptedPaper> acceptedPapers, List<Room> rooms, List<TimeSlot> timeSlots) {
        if (fail) {
            throw new IllegalStateException("Scheduling algorithm failed");
        }
        List<Session> sessions = new ArrayList<>();
        for (int i = 0; i < acceptedPapers.size(); i++) {
            AcceptedPaper paper = acceptedPapers.get(i);
            Room room = rooms.get(i % rooms.size());
            TimeSlot slot = timeSlots.get(i % timeSlots.size());
            sessions.add(new Session(i + 1L, null, paper.getPaperId(), room.getRoomId(), slot.getTimeSlotId()));
        }
        return sessions;
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }
}
