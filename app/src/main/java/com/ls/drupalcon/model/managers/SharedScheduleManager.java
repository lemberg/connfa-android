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
import com.ls.drupalcon.model.dao.SharedFavoritesDao;
import com.ls.drupalcon.model.dao.SharedScheduleDao;
import com.ls.drupalcon.model.data.Event;
import com.ls.drupalcon.model.data.FriendsFavoriteItem;
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
    private SharedScheduleDao sharedScheduleDao;
    private SharedFavoritesDao sharedFavoritesDao;
    private List<SharedSchedule> schedules = new ArrayList<>();
    private SharedSchedule currentSchedule;
    private SharedSchedule newSchedule;
    private List<SharedSchedule> schedulesTemp;
    private SharedSchedule scheduleTemp;
    private Timer timer = new Timer();

    public SharedScheduleManager() {
        SharedSchedule schedule = new SharedSchedule(-1, App.getContext().getString(R.string.my_schedule));
        this.sharedScheduleDao = new SharedScheduleDao();
//        if (PreferencesManager.getInstance().getMyScheduleCode() != -1) {
//            this.sharedScheduleDao.deleteDataSafe(-1l);
//        }
        this.sharedFavoritesDao = new SharedFavoritesDao();
        this.sharedScheduleDao.saveOrUpdateSafe(schedule);
        this.schedules.addAll(this.sharedScheduleDao.getAllSafe());
        this.currentSchedule = schedule;
    }

    public SharedScheduleDao getSharedScheduleDao() {
        return sharedScheduleDao;
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

    public void updateCurrentSchedule(Long id) {
        this.currentSchedule.setScheduleCode(id);
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

    public int getItemPosition() {
        return schedules.indexOf(currentSchedule);
    }

    public void createSchedule(String scheduleName) {
        this.newSchedule.setScheduleName(scheduleName);
        if (schedules.contains(newSchedule)) {
            Toast.makeText(App.getContext(), "This schedule already exist", Toast.LENGTH_LONG).show();
        } else {
            schedules.add(newSchedule);
            this.sharedScheduleDao.saveDataSafe(newSchedule);
        }
    }

    public void renameSchedule(String newScheduleName) {
        SharedSchedule sharedSchedule = new SharedSchedule(getCurrentScheduleId(), "Schedule " + newScheduleName);
        schedules.set(schedules.indexOf(currentSchedule), sharedSchedule);
        this.sharedScheduleDao.saveOrUpdateSafe(sharedSchedule);

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
                sharedFavoritesDao.deleteDataSafe(scheduleTemp.getId());
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


    private List<FriendsFavoriteItem> getAllFriendsFavorite() {
        List<FriendsFavoriteItem> allSafe = sharedFavoritesDao.getAllSafe();
        return allSafe;
    }

    public ArrayList<Long> getFriendsFavoriteEventIds() {

        ArrayList<Long> favoriteEventIds = new ArrayList<>();
        for (FriendsFavoriteItem favorite : getAllFriendsFavorite()) {
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

    public void saveFavoritesSafe(ArrayList<FriendsFavoriteItem> items) {
        sharedFavoritesDao.deleteAllSafe();
        sharedFavoritesDao.saveDataSafe(items);
    }

    public void saveFavoritesSafe(ArrayList<FriendsFavoriteItem> items, long ScheduleCode) {

        sharedFavoritesDao.saveDataSafe(items);
    }

    public void deleteAll() {
        sharedFavoritesDao.deleteAllSafe();
    }


    public List<FriendsFavoriteItem> getAllFavoritesSafe() {
        return sharedFavoritesDao.getAllSafe();
    }

    private List<Long> getWhoIsGoing(long eventId) {
        List<Long> results = new ArrayList<>();
        List<FriendsFavoriteItem> allFriendsFavorite = getAllFriendsFavorite();
        for (FriendsFavoriteItem item : allFriendsFavorite) {
            if (item.getEventId() == eventId) {
                results.add(item.getId());
            }
        }
        L.e("getWhoIsGoing code = " + results);
        return results;
    }


    public List<String> getSchedulesNameByCode(long eventId) {
        List<SharedSchedule> allSchedules = getAllSchedules();
        List<String> results = new ArrayList<>();

        for (SharedSchedule schedule : allSchedules) {
            for (Long code : getWhoIsGoing(eventId)) {
                if (code.equals(schedule.getId())) {
                    results.add(schedule.getScheduleName());
                }
            }
        }
        L.e("getScheduleNameByCode = " + results);
        return results;
    }

    public List<SharedSchedule> getAllSchedules() {
        List<SharedSchedule> allSchedules = sharedScheduleDao.getAllSafe();
        L.e("allSchedules = " + allSchedules);
        return allSchedules;
    }

    public  List<SharedSchedule> getFavoritesById(long eventId) {
//        List<FriendsFavoriteItem> favoritesById = sharedFavoritesDao.getFavoritesById(eventId);
        List<SharedSchedule> list = sharedScheduleDao.getScheduleNameId(eventId);
        L.e("getFavoritesById = " + list.toString());
        return list;
    }

    public ArrayList<String> getSharedSchedulesNamesById(long eventId) {
        List<SharedSchedule> list = getFavoritesById(eventId);
        ArrayList<String> namesList = new ArrayList<>();
        for(SharedSchedule schedule: list){
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
        final BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.POST, App.getContext().getString(R.string.api_value_base_url) + "createSchedule", requestConfig);
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

//    public void getAllSharedSchedule() {
//        RequestConfig requestConfig = new RequestConfig();
//        requestConfig.setResponseFormat(BaseRequest.ResponseFormat.JSON);
//        requestConfig.setRequestFormat(BaseRequest.RequestFormat.JSON);
//        requestConfig.setResponseClassSpecifier(Schedule.Holder.class);
//
//        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.GET, "http://connfa-integration.uat.link/api/v2/euna-mcdermott-dds/getSchedules?codes[]=3320", requestConfig);
//        String lastDate = PreferencesManager.getInstance().getLastUpdateDate();
//        request.addRequestHeader(UpdatesManager.IF_MODIFIED_SINCE_HEADER, lastDate);
//
//        DrupalClient client = Model.instance().getClient();
//        ResponseData schedule = client.performRequest(request, "Schedule", new DrupalClient.OnResponseListener() {
//            @Override
//            public void onResponseReceived(ResponseData data, Object tag) {
//                L.e("Object = " + tag);
//            }
//
//            @Override
//            public void onError(ResponseData data, Object tag) {
//                L.e("ResponseData = " + data);
//            }
//
//            @Override
//            public void onCancel(Object tag) {
//                L.e("Object = " + tag);
//            }
//        }, false);
//
//        L.e("ResponseData getAllSharedSchedule = " + schedule.toString());
//    }

    public void getAllSharedSchedule() {
        sharedFavoritesDao.deleteAllSafe();
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // display response
                Schedule.Holder holder = SharedGson.getGson().fromJson(response.toString(), Schedule.Holder.class);

                ArrayList<FriendsFavoriteItem> sharedSchedules = new ArrayList<>();
                List<Schedule> schedules = holder.getSchedules();
                for (Schedule schedule : schedules) {
                    for (Long eventId : schedule.getEvents()) {
                        sharedSchedules.add(new FriendsFavoriteItem(eventId, schedule.getCode()));
                    }
                }
                saveFavoritesSafe(sharedSchedules);
                L.e("Schedule.Holder.class = " + holder.toString());
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.toString());
            }
        };

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, getURL(), listener, errorListener);

        Model.instance().getQueue().add(getRequest);
    }

    public void getSharedSchedule(final long scheduleCode) {
        sharedFavoritesDao.deleteDataSafe(scheduleCode);
        final String url = "http://connfa-integration.uat.link/api/v2/euna-mcdermott-dds/getSchedules?codes[]=" + scheduleCode;

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Schedule.Holder holder = SharedGson.getGson().fromJson(response.toString(), Schedule.Holder.class);

                ArrayList<FriendsFavoriteItem> sharedSchedules = new ArrayList<>();
                List<Schedule> schedules = holder.getSchedules();
                for (Schedule schedule : schedules) {
                    for (Long eventId : schedule.getEvents()) {
                        sharedSchedules.add(new FriendsFavoriteItem(eventId, schedule.getCode()));
                    }
                }
                saveFavoritesSafe(sharedSchedules, scheduleCode);
                L.e("Schedule.Holder.class = " + holder.toString());
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.getMessage());
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
        if (instance.getMyScheduleCode() == -1) {
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

    private String getURL() {
        List<SharedSchedule> schedules = sharedScheduleDao.getAllSafe();
        L.e("All schedules = " + schedules);

        StringBuilder url = new StringBuilder();
        url.append(App.getContext().getString(R.string.api_value_base_url));
        String requestParameter = "codes[]=";
        url.append("getSchedules?");

        int counter = 0;
        for (SharedSchedule schedule : schedules) {
            Long id = schedule.getId();
            if (id != -1) {
                if (counter == 0) {
                    url.append(requestParameter);
                } else {
                    url.append("&");
                    url.append(requestParameter);
                }
                url.append(schedule.getId());
                counter++;
            }


        }

        L.e("Get URL = " + url.toString());
        return url.toString();

    }

}
