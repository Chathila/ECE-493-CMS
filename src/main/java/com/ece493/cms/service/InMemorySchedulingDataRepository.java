package com.ece493.cms.service;

import com.ece493.cms.model.AcceptedPaper;
import com.ece493.cms.model.Room;
import com.ece493.cms.model.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public class InMemorySchedulingDataRepository implements SchedulingDataRepository {
    private List<AcceptedPaper> acceptedPapers = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
    private List<TimeSlot> timeSlots = new ArrayList<>();

    @Override
    public List<AcceptedPaper> acceptedPapers() {
        return List.copyOf(acceptedPapers);
    }

    @Override
    public List<Room> rooms() {
        return List.copyOf(rooms);
    }

    @Override
    public List<TimeSlot> timeSlots() {
        return List.copyOf(timeSlots);
    }

    public void setAcceptedPapers(List<AcceptedPaper> acceptedPapers) {
        this.acceptedPapers = acceptedPapers == null ? List.of() : new ArrayList<>(acceptedPapers);
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms == null ? List.of() : new ArrayList<>(rooms);
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots == null ? List.of() : new ArrayList<>(timeSlots);
    }
}
