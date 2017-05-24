package com.ls.drupalcon.model.dao;

import com.ls.drupalcon.model.AppDatabaseInfo;
import com.ls.drupalcon.model.data.SharedSchedule;
import com.ls.drupalcon.model.database.AbstractEntityDAO;

import java.util.List;


public class SharedScheduleDao extends AbstractEntityDAO<SharedSchedule, Long> {

    public static final String TABLE_NAME = "table_shared_schedules";

    @Override
    protected String getSearchCondition() {
        return SharedSchedule.COLUMN_ID + "=?";
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
    protected SharedSchedule newInstance() {
        return new SharedSchedule();
    }

    @Override
    protected String[] getKeyColumns() {
        return new String[0];
    }

//    public List<SharedEvents> getSharedSchedulesById(long eventId) {
//        String query = "SELECT * FROM table_friends_favorite_events WHERE _event_id =" + eventId;
//
//        return getDataBySqlQuerySafe(query, null);
//    }

//    public List<SharedSchedule> getSpeakersByEventId(long eventId) {
//        String query = "SELECT * FROM table_speaker WHERE _id IN (" +
//                "SELECT _speaker_id FROM table_event_and_speaker WHERE _event_id = " + eventId + ")";
//        return getDataBySqlQuerySafe(query, null);
//    }

    public List<SharedSchedule> getScheduleNameId(long eventId) {
        String query = "SELECT * FROM table_shared_schedules WHERE _id IN (" +
                "SELECT _id FROM table_friends_favorite_events WHERE _event_id = " + eventId + ")";
        return getDataBySqlQuerySafe(query, null);
    }

}
