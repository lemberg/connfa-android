package com.ls.drupalcon.model.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsScheduleManager {

    private Map<String, Integer> map = new HashMap<>();
    private int ScheduleNumber;

    public FriendsScheduleManager() {
        this.map.put("Test schedule 1", 1111);
        this.map.put("My schedule", 0);
    }

    public List<String> getFriendsScheduleList() {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            result.add(entry.getKey());
        }
        return result;

    }

    public void setScheduleNumber(int scheduleNumber) {
        ScheduleNumber = scheduleNumber;
    }

    public int getCurrentFriendId() {
        if (ScheduleNumber == 0) {
            return 0;
        } else {
            return 1111;
        }

    }
}
