package com.ls.drupalcon.model.managers;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.ls.drupal.DrupalClient;
import com.ls.drupalcon.R;
import com.ls.drupalcon.app.App;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.PreferencesManager;
import com.ls.drupalcon.model.UpdatesManager;
import com.ls.drupalcon.model.dao.FriendsFavoriteDao;
import com.ls.drupalcon.model.dao.SharedScheduleDao;
import com.ls.drupalcon.model.data.Data;
import com.ls.drupalcon.model.data.PostResponse;
import com.ls.drupalcon.model.data.Schedule;
import com.ls.drupalcon.model.data.SharedSchedule;
import com.ls.drupalcon.model.data.UpdateDate;
import com.ls.http.base.BaseRequest;
import com.ls.http.base.RequestConfig;
import com.ls.http.base.ResponseData;
import com.ls.utils.L;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public int getItemPosition() {
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

    public void postData() {
        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setResponseFormat(BaseRequest.ResponseFormat.JSON);
        requestConfig.setRequestFormat(BaseRequest.RequestFormat.JSON);
        requestConfig.setResponseClassSpecifier(PostResponse.class);
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(19);
        ids.add(32);
        Map<String, ArrayList<Integer>> objectToPost = new HashMap<>();
        objectToPost.put("data", ids);

        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.POST, App.getContext().getString(R.string.api_value_base_url) + "createSchedule", requestConfig);
        request.setObjectToPost(objectToPost);

        DrupalClient client = Model.instance().getClient();
        client.performRequest(request, "post", new DrupalClient.OnResponseListener() {
            @Override
            public void onResponseReceived(ResponseData data, Object tag) {
                PostResponse response = (PostResponse) data.getData();
                L.e("ResponseData = " + response.toString() + " Tag = " + tag);
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

    public void getAllSharedSchedule() {
        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setResponseFormat(BaseRequest.ResponseFormat.JSON);
        requestConfig.setRequestFormat(BaseRequest.RequestFormat.JSON);
        requestConfig.setResponseClassSpecifier(Schedule.Holder.class);

        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.GET, "http://connfa-integration.uat.link/api/v2/euna-mcdermott-dds/getSchedules?codes[]=3320", requestConfig);
        String lastDate = PreferencesManager.getInstance().getLastUpdateDate();
        request.addRequestHeader(UpdatesManager.IF_MODIFIED_SINCE_HEADER, lastDate);

        DrupalClient client = Model.instance().getClient();
        ResponseData schedule = client.performRequest(request, "Schedule", new DrupalClient.OnResponseListener() {
            @Override
            public void onResponseReceived(ResponseData data, Object tag) {
//                Schedule.Holder response = (Schedule.Holder) data.getData();
//                L.e("getAllSharedSchedule = " + data.toString()+  " Tag = " + tag);
                L.e("Object = " + tag);
                L.e("getAllSharedSchedule = " + data.getData().toString());
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

        L.e("ResponseData getAllSharedSchedule = " + schedule.toString());
    }

    public void getTest() {
        final String url = "http://connfa-integration.uat.link/api/v2/euna-mcdermott-dds/getSchedules?codes[]=1373";
        RequestQueue queue = Volley.newRequestQueue(App.getContext());
// prepare the Request
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // display response
                L.e("Response = " + response.toString());
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.getMessage());
            }
        };

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, listener, errorListener);

// add it to the RequestQueue
        queue.add(getRequest);
    }

    public void updateData() {
        RequestConfig requestConfig = new RequestConfig();
        requestConfig.setResponseFormat(BaseRequest.ResponseFormat.JSON);
        requestConfig.setRequestFormat(BaseRequest.RequestFormat.JSON);
        requestConfig.setResponseClassSpecifier(PostResponse.class);
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(22);
        ids.add(23);
        Map<String, ArrayList<Integer>> objectToPost = new HashMap<>();
        objectToPost.put("data", ids);

        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.PUT, App.getContext().getString(R.string.api_value_base_url) + "updateSchedule/1373", requestConfig);
        request.setObjectToPost(objectToPost);

        DrupalClient client = Model.instance().getClient();
        client.performRequest(request, "update", new DrupalClient.OnResponseListener() {
            @Override
            public void onResponseReceived(ResponseData data, Object tag) {
                PostResponse response = (PostResponse) data.getData();
                L.e("update ResponseData = " + response.toString() + " Tag = " + tag);
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

}
