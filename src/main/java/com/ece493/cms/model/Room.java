package com.ece493.cms.model;

public class Room {
    private final String roomId;
    private final String name;

    public Room(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }
}
