package com.ls.drupalcon.model.managers;

import android.widget.Toast;

import com.ls.drupalcon.app.App;
import com.ls.drupalcon.model.dao.SharedScheduleDao;
import com.ls.drupalcon.model.data.SharedSchedule;

import java.util.ArrayList;
import java.util.List;

public class SharedScheduleManager {
    private SharedScheduleDao sharedScheduleDao;
    private List<SharedSchedule> list = new ArrayList<>();
    private int scheduleNumber;

    public SharedScheduleManager() {
        this.sharedScheduleDao = new SharedScheduleDao();
        this.sharedScheduleDao.saveDataSafe(new SharedSchedule("My schedule", "My schedule"));
        this.list.addAll(this.sharedScheduleDao.getAllSafe());
    }

    public SharedScheduleDao getSharedScheduleDao() {
        return sharedScheduleDao;
    }

    public List<String> getAllScheduleList() {
        List<String> result = new ArrayList<>();
        for (SharedSchedule item : list) {
            result.add(item.getScheduleName());
        }
        return result;

    }

    public void setScheduleNumber(int scheduleNumber) {
        this.scheduleNumber = scheduleNumber;
    }

    public SharedSchedule getCurrentSchedule() {
        return list.get(scheduleNumber);
    }

    public String getCurrentFriendId() {
        return getCurrentSchedule().getId();
    }

    public String getCurrentFriendScheduleName() {
        return getCurrentSchedule().getScheduleName();
    }


    public void addSchedule(String friendId) {
        SharedSchedule sharedSchedule = new SharedSchedule(friendId, "Schedule " + friendId);
        if (list.contains(sharedSchedule)) {
            Toast.makeText(App.getContext(), "This schedule already exist", Toast.LENGTH_LONG).show();
        } else {
            list.add(sharedSchedule);
            this.sharedScheduleDao.saveDataSafe(sharedSchedule);
        }
    }

    public void renameSchedule(String friendId) {
        SharedSchedule sharedSchedule = new SharedSchedule(getCurrentFriendId(), "Schedule " + friendId);
        this.sharedScheduleDao.saveOrUpdateSafe(sharedSchedule);

    }

}
