package com.ls.ui.drawer;

import com.ls.drupalcon.R;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.UpdateRequest;
import com.ls.drupalcon.model.data.Event;
import com.ls.drupalcon.model.managers.SharedScheduleManager;

import java.util.ArrayList;
import java.util.List;

public class FriendFavoritesStrategy implements EventHolderFragmentStrategy {
    private static UpdateRequest request = UpdateRequest.SCHEDULES;

    @Override
    public List<Long> getDayList() {
        SharedScheduleManager sharedScheduleManager = Model.instance().getSharedScheduleManager();
        List<Event> allFriendsFavoriteEvent = sharedScheduleManager.getAllFriendsFavoriteEvent();
        List<Long> dayList = new ArrayList<>();

        for (Event event : allFriendsFavoriteEvent) {
            long date = event.getTimeRange().getDate();
            if (!dayList.contains(date)) {
                dayList.add(date);
            }

        }
        return dayList;
    }

    @Override
    public int getTextResId() {
        return R.string.placeholder_shared_schedule;
    }

    @Override
    public int getImageResId() {
        return R.drawable.ic_no_my_schedule;
    }

    @Override
    public boolean updateFavorites() {
        return true;
    }

    @Override
    public boolean update(List<UpdateRequest> requests) {
        return requests.contains(request);
    }

    @Override
    public EventMode getEventMode() {
        return EventMode.SharedSchedules;
    }
}
