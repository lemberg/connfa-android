package com.ls.drupalcon.model.data;


import android.content.ContentValues;
import android.database.Cursor;

import com.ls.drupalcon.model.database.AbstractEntity;
import com.ls.utils.CursorStringParser;

public class SharedSchedule extends AbstractEntity<Long> {
    public final static String COLUMN_ID = "_id";
    private final static String COLUMN_SCHEDULE_NAME_ID = "_schedule_name_id";

    private Long scheduleCode;
    private String scheduleName;

    public SharedSchedule() {
    }

    public SharedSchedule(Long scheduleCode, String scheduleName) {
        this.scheduleCode = scheduleCode;
        this.scheduleName = scheduleName;
    }

    @Override
    public Long getId() {
        return scheduleCode;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues result = new ContentValues();
        result.put(COLUMN_ID, scheduleCode);
        result.put(COLUMN_SCHEDULE_NAME_ID, scheduleName);

        return result;
    }

    @Override
    public void initialize(Cursor theCursor) {
        CursorStringParser parser = new CursorStringParser(theCursor);
        scheduleCode = parser.readLong(COLUMN_ID);
        scheduleName = parser.readString(COLUMN_SCHEDULE_NAME_ID);
    }

    public SharedSchedule(long friendId, String scheduleName) {
        this.scheduleCode = friendId;
        this.scheduleName = scheduleName;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    @Override
    public String toString() {
        return "SharedSchedule{" +
                "scheduleCode='" + scheduleCode + '\'' +
                ", scheduleName='" + scheduleName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SharedSchedule schedule = (SharedSchedule) o;

        return scheduleCode != null ? scheduleCode.equals(schedule.scheduleCode) : schedule.scheduleCode == null;

    }

    @Override
    public int hashCode() {
        return scheduleCode != null ? scheduleCode.hashCode() : 0;
    }
    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        SharedSchedule that = (SharedSchedule) o;
//
//        if (scheduleCode != null ? !scheduleCode.equals(that.scheduleCode) : that.scheduleCode != null)
//            return false;
//        return scheduleName != null ? scheduleName.equals(that.scheduleName) : that.scheduleName == null;
//
//    }
//
//    @Override
//    public int hashCode() {
//        int result = scheduleCode != null ? scheduleCode.hashCode() : 0;
//        result = 31 * result + (scheduleName != null ? scheduleName.hashCode() : 0);
//        return result;
//    }

    public void setScheduleCode(Long scheduleCode) {
        this.scheduleCode = scheduleCode;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }
}
