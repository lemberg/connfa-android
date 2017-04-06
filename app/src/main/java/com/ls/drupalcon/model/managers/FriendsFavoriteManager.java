package com.ls.drupalcon.model.managers;

import com.ls.drupalcon.app.App;
import com.ls.drupalcon.model.dao.EventDao;
import com.ls.drupalcon.model.dao.FriendsFavoriteDao;
import com.ls.drupalcon.model.dao.FriendsTestDao;
import com.ls.drupalcon.model.data.Event;

import java.util.List;

public class FriendsFavoriteManager {

    private FriendsFavoriteDao mFriendsFavoriteDao;
    private EventDao mEventDao;
    private FriendsTestDao mFriendsTestDao;

    public FriendsTestDao getmFriendsTestDao() {
        return mFriendsTestDao;
    }

    public FriendsFavoriteManager() {
        mFriendsFavoriteDao = new FriendsFavoriteDao(App.getContext());
        mEventDao = new EventDao(App.getContext());
        mFriendsTestDao = new FriendsTestDao(App.getContext());
    }

    public void setFavoriteEvent(List<Event> favoriteEvents) {
        mFriendsFavoriteDao.saveDataSafe(favoriteEvents);
    }

//    public List<Long> getFavoriteEventDays() {
//        return mEventDao.selectDistrictFavoriteDateSafe();
//    }

    public List<Long> getFavoriteEventsSafe() {
        return mEventDao.selectFriendsFavoriteEventsSafe();
    }

    public void setFriendsFavoriteSafe(long eventId, boolean isFavorite) {
        mEventDao.setFriendsFavoriteSafe(eventId, isFavorite);
    }
}
