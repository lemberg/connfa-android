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

    public List<Favorite> getFriendsFavoriteEvent() {
        return mFriendsTestDao.getAllSafe();
    }

    public List<Long> getFavoriteEventIds() {
        List<Long> favoriteEventIds = new ArrayList<>();
        for (Favorite favorite : getFriendsFavoriteEvent()) {
            favoriteEventIds.add(favorite.getEventId());
        }
        return favoriteEventIds;
    }

    public long getWtf() {
        EventManager eventManager = Model.instance().getEventManager();
        EventDao eventDao = eventManager.getEventDao();
        List<Event> events = eventDao.selectEventsByIdsSafe(getFavoriteEventIds());

        L.e("getWtf = " + events);
//        List<Event> allSafe = eventDao.getAllSafe();
//
//
//        for(Event event: allSafe){
//            if(event.getId() == 68){
//                L.e("Event event: allSafe = "+ event);
//                return event.getFromMillis();
//            }
//        }

        return 0;
    }


}
