package com.ls.drupalcon.model.managers;

import com.ls.drupal.AbstractBaseDrupalEntity;
import com.ls.drupal.DrupalClient;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.data.FriendsFavoriteItem;
import com.ls.drupalcon.model.data.Schedule;
import com.ls.drupalcon.model.requests.ScheduleRequest;
import com.ls.utils.L;

import java.util.ArrayList;
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
        ArrayList<FriendsFavoriteItem> sharedSchedules = new ArrayList<>();
        List<Schedule> schedules = requestResponse.getSchedules();
        for (Schedule schedule : schedules) {
            for (Long eventId : schedule.getEvents()) {
                sharedSchedules.add(new FriendsFavoriteItem(eventId, schedule.getCode()));
            }
        }
        Model.instance().getSharedScheduleManager().saveFavoritesDeleteAndSafe(sharedSchedules);
        return true;
    }

}
