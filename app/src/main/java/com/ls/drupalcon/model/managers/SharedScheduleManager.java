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
    private List<SharedSchedule> schedulesTemp;
    private SharedSchedule scheduleTemp;
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
        L.e("All Schedules Name List = " + list);
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


    public void setNewScheduleCode(long scheduleCode) {
        this.newSchedule = new SharedSchedule();
        this.newSchedule.setScheduleCode(scheduleCode);
    }

    public int getItemPosition(){
       return list.indexOf(currentSchedule);
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

    public void deleteSharedSchedule() {
        schedulesTemp = new ArrayList<>(list);
        scheduleTemp = currentSchedule;
        L.e("Temp = " + schedulesTemp);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sharedScheduleDao.deleteDataSafe(currentSchedule.getId());
                mFriendsDao.deleteDataSafe(currentSchedule.getId());
            }
        }, 2000);


        list.remove(currentSchedule);
        this.currentSchedule = list.get(0);

    }

    public void restoreSchedule() {
        list = schedulesTemp;
        currentSchedule = scheduleTemp;
        timer.cancel();
        timer = new Timer();
//        list.clear();
//        list.addAll(schedulesTemp);
//        timer.cancel();
    }

}
