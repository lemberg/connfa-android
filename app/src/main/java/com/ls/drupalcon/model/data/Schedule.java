package com.ls.drupalcon.model.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Schedule {
    private Long code;
    private ArrayList<Long> events = new ArrayList<>();

    public Long getCode() {
        return code;
    }

    public ArrayList<Long> getEvents() {
        return events;
    }

    public static class Holder {

        @SerializedName("schedules")
        private List<Schedule> schedules = new ArrayList<>();

        public List<Schedule> getSchedules() {
            return schedules;
        }

        @Override
        public String toString() {
            return "Holder{" +
                    "schedules=" + schedules +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "code=" + code +
                ", events=" + events +
                '}';
    }
}
