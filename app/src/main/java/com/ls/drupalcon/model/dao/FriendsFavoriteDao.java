package com.ls.drupalcon.model.dao;

import android.content.Context;

import com.ls.drupalcon.model.AppDatabaseInfo;
import com.ls.drupalcon.model.data.FriendsFavorite;
import com.ls.drupalcon.model.database.AbstractEntityDAO;


public class FriendsFavoriteDao extends AbstractEntityDAO<FriendsFavorite, Long> {

    public static final String TABLE_NAME = "table_friends_favorite_events";
    private final Context mContext;

    public FriendsFavoriteDao(Context context) {
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
    protected FriendsFavorite newInstance() {
        return new FriendsFavorite();
    }

    @Override
    protected String[] getKeyColumns() {
        return new String[0];
    }


}
