package com.ls.drupalcon.model.managers;

import com.ls.drupalcon.app.App;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.dao.EventDao;
import com.ls.drupalcon.model.dao.FriendsFavoriteDao;
import com.ls.drupalcon.model.data.Event;
import com.ls.drupalcon.model.data.FriendsFavorite;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.List;

public class FriendsFavoriteManager {

    private FriendsFavoriteDao mFriendsTestDao;

    public FriendsFavoriteDao getFriendsTestDao() {
        return mFriendsTestDao;
    }

    public FriendsFavoriteManager() {
        mFriendsTestDao = new FriendsFavoriteDao(App.getContext());
    }

    public List<FriendsFavorite> getAllFriendsFavorite() {
        return mFriendsTestDao.getAllSafe();
    }

    public List<Long> getFavoriteEventIds() {
        List<Long> favoriteEventIds = new ArrayList<>();
        for (FriendsFavorite favorite : getAllFriendsFavorite()) {
            favoriteEventIds.add(favorite.getEventId());
        }
        return favoriteEventIds;
    }

    public List<Event> getAllFriendsFavoriteEvent() {
        EventManager eventManager = Model.instance().getEventManager();
        EventDao eventDao = eventManager.getEventDao();
        return eventDao.selectEventsByIdsSafe(getFavoriteEventIds());
    }


}
