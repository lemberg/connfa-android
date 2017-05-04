package com.ls.drupalcon.model.managers;

import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.PreferencesManager;
import com.ls.drupalcon.model.dao.EventDao;
import com.ls.drupalcon.model.dao.SharedFavoritesDao;
import com.ls.drupalcon.model.dao.SharedScheduleDao;
import com.ls.drupalcon.model.data.Event;
import com.ls.drupalcon.model.data.FriendsFavoriteItem;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.List;

public class SharedFavoritesManager {
    private SharedFavoritesDao sharedFavoritesDao;

    public SharedFavoritesManager() {
        sharedFavoritesDao = new SharedFavoritesDao();
    }

    private List<FriendsFavoriteItem> getAllFriendsFavorite() {
        List<FriendsFavoriteItem> allSafe = sharedFavoritesDao.getAllSafe();
        return allSafe;
    }

    public ArrayList<Long> getFavoriteEventIds() {

        ArrayList<Long> favoriteEventIds = new ArrayList<>();
        for (FriendsFavoriteItem favorite : getAllFriendsFavorite()) {
            if (favorite.getSharedScheduleCode() == (Model.instance().getSharedScheduleManager().getCurrentScheduleId()))
                favoriteEventIds.add(favorite.getEventId());
        }
        L.e("getFavoriteEventIds = " + favoriteEventIds);
        return favoriteEventIds;
    }

    public List<Long> getMyEventIds() {

//        ArrayList<Long> favoriteEventIds = new ArrayList<>();
//        for (Long id : Model.instance().getFavoriteManager().getFavoriteEventsSafe()) {
//            favoriteEventIds.add(id);
//        }
//        L.e("getMyEventIds = " + favoriteEventIds);
        return Model.instance().getFavoriteManager().getFavoriteEventsSafe();
    }

    public List<Event> getAllFriendsFavoriteEvent() {
        EventManager eventManager = Model.instance().getEventManager();
        EventDao eventDao = eventManager.getEventDao();
        List<Event> events = eventDao.selectEventsByIdsSafe(getFavoriteEventIds());
        return events;
    }

    public void saveFavoritesSafe(ArrayList<FriendsFavoriteItem> items) {
        sharedFavoritesDao.saveDataSafe(items);
    }

    public void saveFavoritesSafe1(ArrayList<FriendsFavoriteItem> items) {
        sharedFavoritesDao.saveOrUpdateDataSafe(items);
    }

    public void deleteAll() {
        sharedFavoritesDao.deleteAllSafe();
    }


    public List<FriendsFavoriteItem> getAllFavoritesSafe() {
        return sharedFavoritesDao.getAllSafe();
    }
}
