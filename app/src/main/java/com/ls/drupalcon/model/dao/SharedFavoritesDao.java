package com.ls.drupalcon.model.dao;

import com.ls.drupalcon.model.AppDatabaseInfo;
import com.ls.drupalcon.model.data.FriendsFavoriteItem;
import com.ls.drupalcon.model.data.Speaker;
import com.ls.drupalcon.model.database.AbstractEntityDAO;

import java.util.List;


public class SharedFavoritesDao extends AbstractEntityDAO<FriendsFavoriteItem, Long> {

    public static final String TABLE_NAME = "table_friends_favorite_events";

    @Override
    protected String getSearchCondition() {
        return "_id=?";
    }

    @Override
    protected String[] getSearchConditionArguments(Long theId) {
        return new String[]{theId.toString()};
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getDatabaseName() {
        return AppDatabaseInfo.DATABASE_NAME;
    }

    @Override
    protected FriendsFavoriteItem newInstance() {
        return new FriendsFavoriteItem();
    }

    @Override
    protected String[] getKeyColumns() {
        return new String[0];
    }

    public List<FriendsFavoriteItem> getFavoritesById(long eventId) {
        String query = "SELECT * FROM table_friends_favorite_events WHERE _event_id =" + eventId;

        return getDataBySqlQuerySafe(query, null);
    }


}
