package com.ls.drupalcon.model.managers;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.dao.EventDao;
import com.ls.drupalcon.model.dao.FriendsFavoriteDao;
import com.ls.drupalcon.model.data.Event;
import com.ls.drupalcon.model.data.FriendsFavoriteItem;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.List;

public class FriendsFavoriteManager {

    private FriendsFavoriteDao mFriendsDao;

    public FriendsFavoriteManager() {
        mFriendsDao = new FriendsFavoriteDao();
    }

    public List<FriendsFavoriteItem> getAllFriendsFavorite() {
        List<FriendsFavoriteItem> allSafe = mFriendsDao.getAllSafe();
        L.e("getAllFriendsFavorite = " + allSafe.toString());
        return allSafe;
    }

    public List<Long> getFavoriteEventIds() {

        List<Long> favoriteEventIds = new ArrayList<>();
        for (FriendsFavoriteItem favorite : getAllFriendsFavorite()) {
            if (favorite.getSharedScheduleCode() == (Model.instance().getSharedScheduleManager().getCurrentScheduleId()))
                favoriteEventIds.add(favorite.getEventId());
        }
        return favoriteEventIds;
    }

    public List<Event> getAllFriendsFavoriteEvent() {
        EventManager eventManager = Model.instance().getEventManager();
        EventDao eventDao = eventManager.getEventDao();
        List<Event> events = eventDao.selectEventsByIdsSafe(getFavoriteEventIds());
        L.e("getAllFriendsFavoriteEvent = " + events.toString());
        return events;
    }

    public void saveFavorite(long id) {
        SharedScheduleManager sharedScheduleManager = Model.instance().getSharedScheduleManager();
        long currentScheduleId = sharedScheduleManager.getCurrentScheduleId();
        mFriendsDao.saveDataSafe(new FriendsFavoriteItem(id, currentScheduleId));
    }


}
