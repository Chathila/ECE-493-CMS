package com.ece493.cms.service;

import com.ece493.cms.model.AcceptedPaper;
import com.ece493.cms.model.Room;
import com.ece493.cms.model.Session;
import com.ece493.cms.model.TimeSlot;

import java.util.List;

public interface SchedulingAlgorithm {
    List<Session> generate(List<AcceptedPaper> acceptedPapers, List<Room> rooms, List<TimeSlot> timeSlots);
}
