package com.ls.drupalcon.model.managers;

import android.os.AsyncTask;
import android.widget.Toast;

import com.ls.drupal.DrupalClient;
import com.ls.drupalcon.R;
import com.ls.drupalcon.app.App;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.PreferencesManager;
import com.ls.drupalcon.model.dao.EventDao;
import com.ls.drupalcon.model.dao.SharedEventsDao;
import com.ls.drupalcon.model.dao.SharedScheduleDao;
import com.ls.drupalcon.model.data.Event;
import com.ls.drupalcon.model.data.SharedEvents;
import com.ls.drupalcon.model.data.PostResponse;
import com.ls.drupalcon.model.data.Schedule;
import com.ls.drupalcon.model.data.SharedSchedule;
import com.ls.http.base.BaseRequest;
import com.ls.http.base.RequestConfig;
import com.ls.http.base.ResponseData;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SharedScheduleManager {
    public static final long MY_DEFAULT_SCHEDULE_CODE = -1;
    private boolean isInitialized;
    private SharedScheduleDao sharedScheduleDao;
    private SharedEventsDao sharedEventsDao;
    private List<SharedEvents> sharedEvents = new ArrayList<>();
    private List<SharedSchedule> schedules = new ArrayList<>();
    private SharedSchedule currentSchedule;
    private List<SharedSchedule> schedulesTemp;
    private SharedSchedule scheduleTemp;
    private Timer timer = new Timer();
    private ArrayList<OnEventsLoadedListener> onEventsLoadedListeners = new ArrayList<>();

    public interface OnEventsLoadedListener {
        void sharedEventsLoadedCompleted();
    }

    public void setOnEventsLoaded(OnEventsLoadedListener onEventsLoadedListener) {
        this.onEventsLoadedListeners.add(onEventsLoadedListener);
    }

    public void removeOnEventsLoaded(OnEventsLoadedListener onEventsLoadedListener) {
        this.onEventsLoadedListeners.remove(onEventsLoadedListener);
    }

    public SharedScheduleManager() {
        this.sharedScheduleDao = new SharedScheduleDao();
        this.sharedEventsDao = new SharedEventsDao();
    }

    //must called in background
    public void initialize() {
        if (!isInitialized) {
            isInitialized = true;
            this.currentSchedule = new SharedSchedule(MY_DEFAULT_SCHEDULE_CODE, App.getContext().getString(R.string.my_schedule));
            List<SharedSchedule> allSchedules = sharedScheduleDao.getAllSafe();
            sharedEvents = sharedEventsDao.getAllSafe();
            if (!allSchedules.contains(currentSchedule)) {
                schedules.add(currentSchedule);
                sharedScheduleDao.saveOrUpdateSafe(currentSchedule);
            }
            schedules.addAll(allSchedules);
        }
    }

    public List<SharedSchedule> getSchedules() {
        return schedules;
    }

    public List<String> getAllSchedulesNameList() {
        List<String> result = new ArrayList<>();
        for (SharedSchedule item : schedules) {
            result.add(item.getScheduleName());
        }
        L.e("All Schedules Name List = " + schedules);
        return result;
    }

    public void setCurrentSchedule(int scheduleOrder) {
        this.currentSchedule = schedules.get(scheduleOrder);
    }

    private SharedSchedule getCurrentSchedule() {
        return currentSchedule;
    }

    public long getCurrentScheduleId() {
        return currentSchedule.getId();
    }

    public String getCurrentFriendScheduleName() {
        return getCurrentSchedule().getScheduleName();
    }

    public int getItemPosition() {
        return schedules.indexOf(currentSchedule);
    }

    public void saveNewSharedSchedule(long scheduleCode, String name) {
        SharedSchedule schedule = generateSchedule(scheduleCode, name);

        if (schedules.contains(schedule)) {
            Toast.makeText(App.getContext(), "This schedule already exist", Toast.LENGTH_LONG).show();
        } else {
            schedules.add(schedule);
            this.sharedScheduleDao.saveDataSafe(schedule);
        }
        currentSchedule = schedule;
    }

    public void renameSchedule(String newScheduleName) {
        int currentItemIndex = schedules.indexOf(currentSchedule);
        SharedSchedule schedule = schedules.get(currentItemIndex);
        schedule.setScheduleName(newScheduleName);

        currentSchedule = schedule;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                sharedScheduleDao.saveOrUpdateSafe(currentSchedule);
            }
        });


    }

    public void deleteSharedSchedule() {
        schedulesTemp = new ArrayList<>(schedules);
        scheduleTemp = currentSchedule;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sharedScheduleDao.deleteDataSafe(scheduleTemp.getId());
                sharedEventsDao.deleteDataSafe(scheduleTemp.getId());
            }
        }, 2000);
        schedules.remove(currentSchedule);
        currentSchedule = schedules.get(0);

    }

    public void restoreSchedule() {
        schedules = schedulesTemp;
        currentSchedule = scheduleTemp;
        timer.cancel();
        timer = new Timer();
    }


    public List<SharedEvents> getAllFriendsFavorite() {
        return sharedEvents;
    }

    public ArrayList<Long> getFriendsFavoriteEventIds() {

        ArrayList<Long> favoriteEventIds = new ArrayList<>();
        for (SharedEvents favorite : getAllFriendsFavorite()) {
            if (favorite.getSharedScheduleCode() == (Model.instance().getSharedScheduleManager().getCurrentScheduleId()))
                favoriteEventIds.add(favorite.getEventId());
        }
        L.e("getFriendsFavoriteEventIds = " + favoriteEventIds);
        return favoriteEventIds;
    }

    private List<Long> getMyFavoriteEventIds() {
        return Model.instance().getFavoriteManager().getFavoriteEventsSafe();
    }

    public List<Event> getAllFriendsFavoriteEvent() {
        EventDao eventDao = Model.instance().getEventManager().getEventDao();
        return eventDao.selectEventsByIdsSafe(getFriendsFavoriteEventIds());
    }

    public void saveSharedEvents(ArrayList<SharedEvents> items) {
        sharedEventsDao.deleteAllSafe();
        sharedEventsDao.saveDataSafe(items);

        sharedEvents.clear();
        sharedEvents.addAll(items);
    }

    private void saveFavoriteEventsSafe(ArrayList<SharedEvents> items) {
        sharedEvents.addAll(items);
        sharedEventsDao.saveDataSafe(items);

        if (onEventsLoadedListeners.isEmpty()) {
            for(OnEventsLoadedListener eventsLoaded: onEventsLoadedListeners)
                eventsLoaded.sharedEventsLoadedCompleted();
        }

    }

    public SharedEventsDao getSharedEventsDao() {
        return sharedEventsDao;
    }

    private List<SharedSchedule> getSharedSchedulesById(long eventId) {
        return sharedScheduleDao.getScheduleNameId(eventId);
    }

    public ArrayList<String> getSharedSchedulesNamesById(long eventId) {
        List<SharedSchedule> list = getSharedSchedulesById(eventId);
        ArrayList<String> namesList = new ArrayList<>();
        for (SharedSchedule schedule : list) {
            namesList.add(schedule.getScheduleName());
        }
        L.e("getSharedSchedulesNamesById = " + namesList.toString());
        return namesList;
    }


    public void postAllSchedules() {
        PreferencesManager instance = PreferencesManager.getInstance();
        if (instance.getMyScheduleCode() == MY_DEFAULT_SCHEDULE_CODE) {
            postSchedules();
        } else {
            updateSchedules();
        }
    }

    private Map<String, List<Long>> getObjectToPost(List<Long> ids) {
        Map<String, List<Long>> objectToPost = new HashMap<>();
        objectToPost.put("data", ids);
        L.e("Object to post = " + objectToPost.toString());
        return objectToPost;
    }

    public String getPath() {
        List<SharedSchedule> schedules = Model.instance().getSharedScheduleManager().getSchedules();

        StringBuilder url = new StringBuilder();
        url.append("getSchedules?");
        url.append("codes[]=");

        for (SharedSchedule schedule : schedules) {
            Long id = schedule.getId();
            if (id != MY_DEFAULT_SCHEDULE_CODE) {
                url.append(schedule.getId());
                url.append("&codes[]=");
            }
        }

        L.e("Get URL = " + url.toString());
        return url.toString();

    }

    public Long getMyScheduleCode() {
        return Model.instance().getPreferencesManager().getMyScheduleCode();
    }

    private SharedSchedule generateSchedule(long scheduleCode, String name) {
        return new SharedSchedule(scheduleCode, name);
    }

    private void initializeError() {
        if (!isInitialized) {
            throw new Error("SharedScheduleManager should be initialized asynchronously");
        }
    }


    private void updateSchedules() {
        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setResponseFormat(BaseRequest.ResponseFormat.JSON);
        requestConfig.setRequestFormat(BaseRequest.RequestFormat.JSON);
        requestConfig.setResponseClassSpecifier(PostResponse.class);
        final PreferencesManager instance = PreferencesManager.getInstance();

        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.PUT, App.getContext().getString(R.string.api_value_base_url) + "updateSchedule/" + instance.getMyScheduleCode(), requestConfig);
        request.setObjectToPost(getObjectToPost(getMyFavoriteEventIds()));

        DrupalClient client = Model.instance().getClient();
        client.performRequest(request, false);
    }

    private void postSchedules() {
        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setResponseFormat(BaseRequest.ResponseFormat.JSON);
        requestConfig.setRequestFormat(BaseRequest.RequestFormat.JSON);
        requestConfig.setResponseClassSpecifier(PostResponse.class);

        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.POST, App.getContext().getString(R.string.api_value_base_url) + "createSchedule", requestConfig);
        request.setObjectToPost(getObjectToPost(getMyFavoriteEventIds()));

        DrupalClient client = Model.instance().getClient();
        client.performRequest(request, "post", new DrupalClient.OnResponseListener() {
            @Override
            public void onResponseReceived(ResponseData data, Object tag) {
                PostResponse response = (PostResponse) data.getData();
                L.e("Schedule Code  = " + response.getCode() + " Tag = " + tag);
                Long code = response.getCode();
                PreferencesManager.getInstance().saveMyScheduleCode(code);
            }

            @Override
            public void onError(ResponseData data, Object tag) {
                L.e("Update Error = " + data);
            }

            @Override
            public void onCancel(Object tag) {
                L.e("Update Cancel = " + tag);
            }
        }, false);
    }


    public void fetchSharedEventsByCode(final long scheduleCode) {
        sharedEventsDao.deleteDataSafe(scheduleCode);

        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setResponseFormat(BaseRequest.ResponseFormat.JSON);
        requestConfig.setRequestFormat(BaseRequest.RequestFormat.JSON);
        requestConfig.setResponseClassSpecifier(Schedule.Holder.class);


        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.GET, App.getContext().getString(R.string.api_value_base_url) + "getSchedules?codes[]=" + scheduleCode, requestConfig);

        DrupalClient client = Model.instance().getClient();
        client.performRequest(request, "Fetch Shared Events By Code", new DrupalClient.OnResponseListener() {
            @Override
            public void onResponseReceived(ResponseData data, Object tag) {
                Schedule.Holder holder = (Schedule.Holder) data.getData();
                ArrayList<SharedEvents> sharedSchedules = new ArrayList<>();
                List<Schedule> schedules = holder.getSchedules();
                for (Schedule schedule : schedules) {
                    for (Long eventId : schedule.getEvents()) {
                        sharedSchedules.add(new SharedEvents(eventId, schedule.getCode()));
                    }
                }
                saveFavoriteEventsSafe(sharedSchedules);
            }

            @Override
            public void onError(ResponseData data, Object tag) {
                L.e("Update Error = " + data);
            }

            @Override
            public void onCancel(Object tag) {
                L.e("Update Cancel = " + tag);
            }
        }, false);
    }


}
