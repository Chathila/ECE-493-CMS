package com.ece493.cms.service;

import com.ece493.cms.model.AcceptedPaper;
import com.ece493.cms.model.Room;
import com.ece493.cms.model.TimeSlot;

import java.util.List;

public interface SchedulingDataRepository {
    List<AcceptedPaper> acceptedPapers();

    List<Room> rooms();

    List<TimeSlot> timeSlots();
}
