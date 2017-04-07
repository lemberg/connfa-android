package com.ls.drupalcon.model.managers;

import com.ls.drupalcon.app.App;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.dao.EventDao;
import com.ls.drupalcon.model.dao.FriendsTestDao;
import com.ls.drupalcon.model.data.Event;
import com.ls.drupalcon.model.data.Favorite;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.List;

public class FriendsFavoriteManager {

    private FriendsTestDao mFriendsTestDao;

    public FriendsTestDao getFriendsTestDao() {
        return mFriendsTestDao;
    }

    public FriendsFavoriteManager() {
        mFriendsTestDao = new FriendsTestDao(App.getContext());
    }

    public List<Favorite> getAllFriendsFavorite() {
        return mFriendsTestDao.getAllSafe();
    }

    public List<Long> getFavoriteEventIds() {
        List<Long> favoriteEventIds = new ArrayList<>();
        for (Favorite favorite : getAllFriendsFavorite()) {
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


}
