package com.ls.drupalcon.model.requests;

import com.ls.drupal.DrupalClient;
import com.ls.drupalcon.model.data.InfoItem;
import com.ls.drupalcon.model.data.Schedule;
import com.ls.http.base.BaseRequest;

import java.util.Map;

public class ScheduleRequest extends BaseSafeConsumeContainerRequest<Schedule.Holder> {

    public ScheduleRequest(DrupalClient client) {
        super(client, new Schedule.Holder());
    }

    @Override
    protected String getPath() {
        return "getSchedules?codes[]=3320";
    }

    @Override
    protected Map<String, String> getItemRequestPostParameters() {
        return null;
    }

    @Override
    protected Map<String, Object> getItemRequestGetParameters(BaseRequest.RequestMethod method) {
        return null;
    }
}