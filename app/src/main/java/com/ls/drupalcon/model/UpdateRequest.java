package com.ls.drupalcon.model;

import com.google.gson.annotations.SerializedName;

public enum UpdateRequest {
    @SerializedName("0")
    SETTINGS(0),
    @SerializedName("1")
    TYPES(1),
    @SerializedName("2")
    LEVELS(2),
    @SerializedName("3")
    TRACKS(3),
    @SerializedName("4")
    SPEAKERS(4),
    @SerializedName("5")
    LOCATIONS(5),
    @SerializedName("6")
    FLOOR_PLANS(6),
    @SerializedName("7")
    PROGRAMS(7),
    @SerializedName("8")
    BOFS(8),
    @SerializedName("9")
    SOCIALS(9),
    @SerializedName("10")
    POIS(10),
    @SerializedName("11")
    INFO(11),
    @SerializedName("12")
    SCHEDULE(12);

    private int requestId;

    UpdateRequest(int id) {
        this.requestId = id;
    }

}
