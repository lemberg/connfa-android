package com.ls.drupalcon.model.managers;

import android.widget.Toast;

import com.ls.drupalcon.R;
import com.ls.drupalcon.app.App;
import com.ls.drupalcon.model.dao.FriendsFavoriteDao;
import com.ls.drupalcon.model.dao.SharedScheduleDao;
import com.ls.drupalcon.model.data.SharedSchedule;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SharedScheduleManager {
    private SharedScheduleDao sharedScheduleDao;
    private FriendsFavoriteDao mFriendsDao;
    private List<SharedSchedule> list = new ArrayList<>();
    private SharedSchedule currentSchedule;
    private SharedSchedule newSchedule;
    private List<SharedSchedule> scheduleTemp= new ArrayList<>();
    private SharedSchedule currentScheduleTemp;
    private Timer timer = new Timer();

    public SharedScheduleManager() {
        this.sharedScheduleDao = new SharedScheduleDao();
        this.mFriendsDao = new FriendsFavoriteDao();
        this.sharedScheduleDao.saveDataSafe(new SharedSchedule(-1, App.getContext().getString(R.string.my_schedule)));
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
        return currentSchedule;
    }

    public long getCurrentScheduleId() {
        return currentSchedule.getId();
    }

    public String getCurrentFriendScheduleName() {
        return getCurrentSchedule().getScheduleName();
    }


    public void setNewScheduleCode(long scheduleCode){
        this.newSchedule = new SharedSchedule();
        this.newSchedule.setScheduleCode(scheduleCode);
    }
    public void createSchedule(String scheduleName) {
        this.newSchedule.setScheduleName(scheduleName);
        if (list.contains(newSchedule)) {
            Toast.makeText(App.getContext(), "This schedule already exist", Toast.LENGTH_LONG).show();
        } else {
            list.add(newSchedule);
            this.sharedScheduleDao.saveDataSafe(newSchedule);
        }
    }

    public void renameSchedule(String newScheduleName) {
        SharedSchedule sharedSchedule = new SharedSchedule(getCurrentScheduleId(), "Schedule " + newScheduleName);
        list.set(list.indexOf(currentSchedule), sharedSchedule);
        this.sharedScheduleDao.saveOrUpdateSafe(sharedSchedule);

    }

//    public void deleteSharedSchedule(){
//        this.sharedScheduleDao.deleteDataSafe(currentSchedule.getId());
////        int currentPosition = list.indexOf(currentSchedule);
//        list.remove(currentSchedule);
//        L.e("Updated list = " + list.toString());
//        this.mFriendsDao.deleteDataSafe(currentSchedule.getId());
////        this.currentSchedule = list.get(currentPosition - 1);
//        this.currentSchedule = list.get(0);
//    }

    public void deleteSharedSchedule(){
        //temp
        scheduleTemp = list;
        currentScheduleTemp = currentSchedule;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                L.e("Timer");
            }
        }, 2000);
        this.sharedScheduleDao.deleteDataSafe(currentSchedule.getId());
        list.remove(currentSchedule);
        this.mFriendsDao.deleteDataSafe(currentSchedule.getId());
        this.currentSchedule = list.get(0);

    }

//    public void restoreSchedule(){
//        list.add(currentScheduleTemp);
//        this.mFriendsDao.saveOrUpdate();
//        this.sharedScheduleDao.saveDataSafe();
//    }

}
