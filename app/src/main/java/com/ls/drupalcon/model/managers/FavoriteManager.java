package com.ls.drupalcon.model.managers;

import com.ls.drupalcon.app.App;
import com.ls.drupalcon.model.dao.EventDao;
import com.ls.utils.L;

import java.util.List;

public class FavoriteManager {

    private EventDao mEventDao;

    public FavoriteManager() {
        mEventDao = new EventDao(App.getContext());
    }

    public List<Long> getFavoriteEventDays() {
        return mEventDao.selectDistrictFavoriteDateSafe();
    }

    public List<Long> getFavoriteEventsSafe() {
        L.e("All me Favorites = " + mEventDao.selectFavoriteEventsSafe());
        return mEventDao.selectFavoriteEventsSafe();
    }

    public void setFavoriteEvent(long eventId, boolean isFavorite) {
        mEventDao.setFavoriteSafe(eventId, isFavorite);
    }
}
