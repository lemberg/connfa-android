package com.ls.drupalcon.model;

public enum UpdateRequest {
    SETTINGS(0),
    TYPES(1),
    LEVELS(2),
    TRACKS(3),
    SPEAKERS(4),
    LOCATIONS(5),
    FLOOR_PLANS(6),
    PROGRAMS(7),
    BOFS(8),
    SOCIALS(9),
    POIS(10),
    INFO(11);

    private int requestId;

    UpdateRequest(int id) {
        this.requestId = id;
    }

}
