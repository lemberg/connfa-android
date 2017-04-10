package com.ls.drupalcon.model.managers;

import com.ls.drupalcon.app.App;
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

    public FriendsFavoriteDao getFriendsTestDao() {
        return mFriendsDao;
    }

    public FriendsFavoriteManager() {
        mFriendsDao = new FriendsFavoriteDao(App.getContext());
    }

    public List<FriendsFavoriteItem> getAllFriendsFavorite() {
        List<FriendsFavoriteItem> allSafe = mFriendsDao.getAllSafe();
        L.e("getAllFriendsFavorite = " + allSafe.toString());
        return allSafe;
    }

    public List<Long> getFavoriteEventIds() {

        List<Long> favoriteEventIds = new ArrayList<>();
        for (FriendsFavoriteItem favorite : getAllFriendsFavorite()) {
            favoriteEventIds.add(favorite.getEventId());
        }
        return favoriteEventIds;
    }

    public List<Event> getAllFriendsFavoriteEvent() {
        EventManager eventManager = Model.instance().getEventManager();
        EventDao eventDao = eventManager.getEventDao();
        return eventDao.selectEventsByIdsSafe(getFavoriteEventIds());
    }

    public void saveFavorite(long id) {
        FriendsScheduleManager friendsScheduleManager = Model.instance().getFriendsScheduleManager();
        int currentFriendId = friendsScheduleManager.getCurrentFriendId();
        mFriendsDao.saveDataSafe(new FriendsFavoriteItem(id, String.valueOf(currentFriendId)));
    }


}
