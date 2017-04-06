package com.ls.drupalcon.model.dao;

import android.content.Context;

import com.ls.drupalcon.model.AppDatabaseInfo;
import com.ls.drupalcon.model.data.Event;
import com.ls.drupalcon.model.data.Favorite;
import com.ls.drupalcon.model.database.AbstractEntityDAO;


public class FriendsTestDao extends AbstractEntityDAO<Favorite, Long> {

    public static final String TABLE_NAME = "table_friends_favorite_events";
    private final Context mContext;

    public FriendsTestDao(Context context) {
        mContext = context;
    }

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
    protected Favorite newInstance() {
        return new Favorite();
    }

    @Override
    protected String[] getKeyColumns() {
        return new String[0];
    }


}
