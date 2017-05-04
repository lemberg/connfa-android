package com.ls.drupalcon.model.managers;

import com.ls.drupal.AbstractBaseDrupalEntity;
import com.ls.drupal.DrupalClient;
import com.ls.drupalcon.model.dao.TypeDao;
import com.ls.drupalcon.model.data.Schedule;
import com.ls.drupalcon.model.data.Type;
import com.ls.drupalcon.model.requests.ScheduleRequest;
import com.ls.drupalcon.model.requests.TypesRequest;
import com.ls.utils.L;

import java.util.List;

public class ScheduleManager extends SynchronousItemManager<Schedule.Holder, Object, String> {

    public ScheduleManager(DrupalClient client) {
        super(client);
    }

    @Override
    protected AbstractBaseDrupalEntity getEntityToFetch(DrupalClient client, Object requestParams) {
        return new ScheduleRequest(client);
    }

    @Override
    protected String getEntityRequestTag(Object params) {
        return "scheduleGetEntityRequestTag";
    }

    @Override
    protected boolean storeResponse(Schedule.Holder requestResponse, String tag) {
        L.e("ScheduleManager Schedule.Holder = " + requestResponse.toString());
        return true;
    }

}
