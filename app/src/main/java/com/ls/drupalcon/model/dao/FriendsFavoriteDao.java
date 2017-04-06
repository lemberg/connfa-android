package com.ls.drupalcon.model.dao;

import android.content.Context;

import com.ls.drupalcon.R;
import com.ls.drupalcon.model.AppDatabaseInfo;
import com.ls.drupalcon.model.data.Event;
import com.ls.drupalcon.model.data.Speaker;
import com.ls.drupalcon.model.database.AbstractEntityDAO;

import java.util.List;


public class FriendsFavoriteDao extends AbstractEntityDAO<Event, Long> {

    public static final String TABLE_NAME = "favorite_events_added";
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
    protected Event newInstance() {
        return new Event();
    }

    @Override
    protected String[] getKeyColumns() {
        return new String[0];
    }

//    public List<Event> getSpeakerById(long speakerId) {
//        String query = "SELECT * FROM table_speaker WHERE _id =" + speakerId;
//
//        return getDataBySqlQuerySafe(query, null);
//    }
//
//    public List<Event> getSpeakersByEventId(long eventId) {
//        String query = "SELECT * FROM table_speaker WHERE _id IN (" +
//                "SELECT _speaker_id FROM table_event_and_speaker WHERE _event_id = " + eventId + ")";
//        return getDataBySqlQuerySafe(query, null);
//    }
//
//    public List<Event> selectSpeakersOrderedByName() {
//        String query = mContext.getString(R.string.select_speakers_ordered_by_name);
//        return getDataBySqlQuerySafe(query, null);
//    }
}
