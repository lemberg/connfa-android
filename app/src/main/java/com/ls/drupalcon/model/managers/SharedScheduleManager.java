package com.ls.drupalcon.model.managers;

import android.widget.Toast;

import com.ls.drupalcon.app.App;
import com.ls.drupalcon.model.dao.FriendsFavoriteDao;
import com.ls.drupalcon.model.dao.SharedScheduleDao;
import com.ls.drupalcon.model.data.SharedSchedule;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.List;

public class SharedScheduleManager {
    private SharedScheduleDao sharedScheduleDao;
    private FriendsFavoriteDao mFriendsDao;
    private List<SharedSchedule> list = new ArrayList<>();
    //    private int scheduleNumber;
    private SharedSchedule currentSchedule;

    public SharedScheduleManager() {
        this.sharedScheduleDao = new SharedScheduleDao();
        this.mFriendsDao = new FriendsFavoriteDao();
        this.sharedScheduleDao.saveDataSafe(new SharedSchedule(0000, "My schedule"));
        this.list.addAll(this.sharedScheduleDao.getAllSafe());
    }

    public SharedScheduleDao getSharedScheduleDao() {
        return sharedScheduleDao;
    }

    public List<String> getAllSchedulesNameList() {
        List<String> result = new ArrayList<>();
        for (SharedSchedule item : list) {
            result.add(item.getScheduleName());
        }
        return result;

    }

    public void setCurrentSchedule(int scheduleOrder) {
        this.currentSchedule = list.get(scheduleOrder);
    }

    public SharedSchedule getCurrentSchedule() {
        L.e("getCurrentSchedule = " + currentSchedule);
        return currentSchedule;
    }

    public long getCurrentScheduleId() {
        return currentSchedule.getId();
    }

    public String getCurrentFriendScheduleName() {
        return getCurrentSchedule().getScheduleName();
    }


    public void addSchedule(long scheduleCode) {
        SharedSchedule sharedSchedule = new SharedSchedule(scheduleCode, "Schedule " + scheduleCode);
        if (list.contains(sharedSchedule)) {
            Toast.makeText(App.getContext(), "This schedule already exist", Toast.LENGTH_LONG).show();
        } else {
            list.add(sharedSchedule);
            this.sharedScheduleDao.saveDataSafe(sharedSchedule);
        }
    }

    public void renameSchedule(String newScheduleName) {
        SharedSchedule sharedSchedule = new SharedSchedule(getCurrentScheduleId(), "Schedule " + newScheduleName);
        list.set(list.indexOf(currentSchedule), sharedSchedule);
        this.sharedScheduleDao.saveOrUpdateSafe(sharedSchedule);

    }

    public void deleteSharedSchedule(){
        this.sharedScheduleDao.deleteDataSafe(currentSchedule.getId());
//        int currentPosition = list.indexOf(currentSchedule);
        list.remove(currentSchedule);
        this.mFriendsDao.deleteDataSafe(currentSchedule.getId());
//        this.currentSchedule = list.get(currentPosition - 1);
        this.currentSchedule = list.get(0);
    }

}
