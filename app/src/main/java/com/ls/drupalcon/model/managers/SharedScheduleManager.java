package com.ls.drupalcon.model.managers;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.ls.http.base.SharedGson;
import com.ls.utils.L;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SharedScheduleManager {
    public static final long MY_DEFAULT_SCHEDULE_CODE = -1;
    public boolean isInitialized;
    private SharedScheduleDao sharedScheduleDao;
    private SharedEventsDao sharedEventsDao;
    private List<SharedSchedule> schedules = new ArrayList<>();
    private SharedSchedule currentSchedule;
    private List<SharedSchedule> schedulesTemp;
    private SharedSchedule scheduleTemp;
    private Timer timer = new Timer();

    public SharedScheduleManager() {
        this.sharedScheduleDao = new SharedScheduleDao();
        this.sharedEventsDao = new SharedEventsDao();

    }

    //must called in background
    public void initialize() {
        if (isInitialized) {
            L.e("Please initialize SharedScheduleManager");
            return;
        } else {
            isInitialized = true;
            this.currentSchedule = new SharedSchedule(MY_DEFAULT_SCHEDULE_CODE, App.getContext().getString(R.string.my_schedule));
            List<SharedSchedule> allSchedules = sharedScheduleDao.getAllSafe();
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

    public SharedSchedule getCurrentSchedule() {
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

    public void saveNewSharedSchedule(long scheduleCode) {
        SharedSchedule schedule = generateSchedule(scheduleCode);

        if (schedules.contains(schedule)) {
            Toast.makeText(App.getContext(), "This schedule already exist", Toast.LENGTH_LONG).show();
        } else {
            schedules.add(schedule);
            this.sharedScheduleDao.saveDataSafe(schedule);
        }
        currentSchedule = schedule;
    }

    public void renameSchedule(String newScheduleName) {
        SharedSchedule schedule = schedules.get(schedules.indexOf(currentSchedule));
        schedule.setScheduleName(newScheduleName);

        currentSchedule.setScheduleName(newScheduleName);
        this.sharedScheduleDao.saveOrUpdateSafe(currentSchedule);

    }

    public void deleteSharedSchedule() {
        schedulesTemp = new ArrayList<>(schedules);
        scheduleTemp = currentSchedule;
        L.e("Temp = " + scheduleTemp);
        L.e("currentSchedule = " + currentSchedule.toString());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sharedScheduleDao.deleteDataSafe(scheduleTemp.getId());
                sharedEventsDao.deleteDataSafe(scheduleTemp.getId());
//
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


    private List<SharedEvents> getAllFriendsFavorite() {
        List<SharedEvents> allSafe = sharedEventsDao.getAllSafe();
        return allSafe;
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

    public List<Long> getMyEventIds() {

        return Model.instance().getFavoriteManager().getFavoriteEventsSafe();
    }

    public List<Event> getAllFriendsFavoriteEvent() {
        EventManager eventManager = Model.instance().getEventManager();
        EventDao eventDao = eventManager.getEventDao();
        List<Event> events = eventDao.selectEventsByIdsSafe(getFriendsFavoriteEventIds());
        return events;
    }

    public void updateFavoriteEventsSafe(ArrayList<SharedEvents> items) {
        sharedEventsDao.deleteAllSafe();
        sharedEventsDao.saveDataSafe(items);
    }

    public void saveFavoriteEventsSafe(ArrayList<SharedEvents> items) {

        sharedEventsDao.saveDataSafe(items);
    }

    public void deleteAll() {
        sharedEventsDao.deleteAllSafe();
    }

    public SharedEventsDao getSharedEventsDao() {
        return sharedEventsDao;
    }

    private List<Long> getWhoIsGoing(long eventId) {
        List<Long> results = new ArrayList<>();
        List<SharedEvents> allFriendsFavorite = getAllFriendsFavorite();
        for (SharedEvents item : allFriendsFavorite) {
            if (item.getEventId() == eventId) {
                results.add(item.getId());
            }
        }
        L.e("getWhoIsGoing code = " + results);
        return results;
    }


    public List<String> getSchedulesNameByCode(long eventId) {
        List<String> results = new ArrayList<>();

        for (SharedSchedule schedule : getAllSharedSchedules()) {
            for (Long code : getWhoIsGoing(eventId)) {
                if (code.equals(schedule.getId())) {
                    results.add(schedule.getScheduleName());
                }
            }
        }
        L.e("getScheduleNameByCode = " + results);
        return results;
    }

    public List<SharedSchedule> getAllSharedSchedules() {
        List<SharedSchedule> allSchedules = sharedScheduleDao.getAllSafe();
        L.e("allSchedules = " + allSchedules);
        return allSchedules;
    }

    public List<SharedSchedule> getFavoritesById(long eventId) {
        List<SharedSchedule> list = sharedScheduleDao.getScheduleNameId(eventId);
        L.e("getFavoritesById = " + list.toString());
        return list;
    }

    public ArrayList<String> getSharedSchedulesNamesById(long eventId) {
        List<SharedSchedule> list = getFavoritesById(eventId);
        ArrayList<String> namesList = new ArrayList<>();
        for (SharedSchedule schedule : list) {
            namesList.add(schedule.getScheduleName());
        }
        L.e("getSharedSchedulesNamesById = " + namesList.toString());
        return namesList;
    }

    public void postData(final Long eventId) {
        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setResponseFormat(BaseRequest.ResponseFormat.JSON);
        requestConfig.setRequestFormat(BaseRequest.RequestFormat.JSON);
        requestConfig.setResponseClassSpecifier(PostResponse.class);

        ArrayList<Long> favoriteEventIds = new ArrayList<>();
        favoriteEventIds.add(eventId);

        final PreferencesManager preferencesManager = PreferencesManager.getInstance();
        final BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.POST, App.getContext().getString(R.string.api_value_base_url) + "saveNewSharedSchedule", requestConfig);
        request.setObjectToPost(getObjectToPost(favoriteEventIds));

        DrupalClient client = Model.instance().getClient();
        client.performRequest(request, "post", new DrupalClient.OnResponseListener() {
            @Override
            public void onResponseReceived(ResponseData data, Object tag) {
                PostResponse response = (PostResponse) data.getData();
                L.e("Schedule Code  = " + response.getCode() + " Tag = " + tag);
                Long code = response.getCode();
//                updateCurrentSchedule(code);
                preferencesManager.saveMyScheduleCode(code);
            }

            @Override
            public void onError(ResponseData data, Object tag) {
                L.e("ResponseData = " + data);
            }

            @Override
            public void onCancel(Object tag) {
                L.e("Object = " + tag);
            }
        }, false);
    }


    public void fetchSharedEventsByCode(final long scheduleCode) {
        sharedEventsDao.deleteDataSafe(scheduleCode);
        final String url = App.getContext().getString(R.string.api_value_base_url) + "getSchedules?codes[]=" + scheduleCode;

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Schedule.Holder holder = SharedGson.getGson().fromJson(response.toString(), Schedule.Holder.class);

                ArrayList<SharedEvents> sharedSchedules = new ArrayList<>();
                List<Schedule> schedules = holder.getSchedules();
                for (Schedule schedule : schedules) {
                    for (Long eventId : schedule.getEvents()) {
                        sharedSchedules.add(new SharedEvents(eventId, schedule.getCode()));
                    }
                }
                updateFavoriteEventsSafe(sharedSchedules, scheduleCode);
                L.e("Schedule.Holder.class = " + holder.toString());
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.toString());
            }
        };

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, listener, errorListener);

        Model.instance().getQueue().add(getRequest);
    }

    private void updateData() {
        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setResponseFormat(BaseRequest.ResponseFormat.JSON);
        requestConfig.setRequestFormat(BaseRequest.RequestFormat.JSON);
        requestConfig.setResponseClassSpecifier(PostResponse.class);
        final PreferencesManager instance = PreferencesManager.getInstance();

        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.PUT, App.getContext().getString(R.string.api_value_base_url) + "updateSchedule/" + instance.getMyScheduleCode(), requestConfig);
        request.setObjectToPost(getObjectToPost(getMyEventIds()));

        DrupalClient client = Model.instance().getClient();
        client.performRequest(request, "update", new DrupalClient.OnResponseListener() {
            @Override
            public void onResponseReceived(ResponseData data, Object tag) {
                PostResponse response = (PostResponse) data.getData();
                L.e("Update = " + response.toString() + " Tag = " + tag);
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

    public void postScheduleData(Long eventId) {
        PreferencesManager instance = PreferencesManager.getInstance();
        if (instance.getMyScheduleCode() == MY_DEFAULT_SCHEDULE_CODE) {
            postData(eventId);
        } else {
            updateData();
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

    public SharedSchedule generateSchedule(long scheduleCode) {
        return new SharedSchedule(scheduleCode, App.getContext().getString(R.string.schedule) + scheduleCode);
    }


}
