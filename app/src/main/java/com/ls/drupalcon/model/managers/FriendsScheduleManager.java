package com.ls.drupalcon.model.managers;

import com.ls.drupalcon.model.data.FriendsFavoriteSchedule;

import java.util.ArrayList;
import java.util.List;

public class FriendsScheduleManager {
    private List<FriendsFavoriteSchedule> list = new ArrayList<>();
    private int scheduleNumber;

    public FriendsScheduleManager() {
        this.list.add(new FriendsFavoriteSchedule("My schedule", "My schedule"));
//        this.list.add(new FriendsFavoriteSchedule("Test schedule 1", "Test schedule 1"));
    }

    public List<String> getAllScheduleList() {
        List<String> result = new ArrayList<>();
        for (FriendsFavoriteSchedule item : list) {
            result.add(item.getScheduleName());
        }
        return result;

    }

    public void setScheduleNumber(int scheduleNumber) {
        this.scheduleNumber = scheduleNumber;
    }

    public String getCurrentFriendId() {
        return list.get(scheduleNumber).getFriendId();
    }

    public void addSchedule(String friendId) {
        list.add(new FriendsFavoriteSchedule(friendId, "Schedule" + friendId));
    }

}
