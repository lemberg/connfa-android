package com.ls.drupalcon.model.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.ls.drupalcon.model.database.AbstractEntity;
import com.ls.utils.CursorStringParser;

import org.jetbrains.annotations.NotNull;


public class SharedEvents extends AbstractEntity<Long> implements Comparable<SharedEvents> {
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_EVENT_ID= "_event_id";

    @SerializedName("_id")
    private long sharedScheduleCode;

    @SerializedName("_event_id")
    private long eventId;


    @Override
    public Long getId() {
        return sharedScheduleCode;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues result = new ContentValues();
        result.put(COLUMN_ID, sharedScheduleCode);
        result.put(COLUMN_EVENT_ID, eventId);

        return result;
    }

    @Override
    public void initialize(Cursor cursor) {
        CursorStringParser parser = new CursorStringParser(cursor);
        eventId = parser.readLong(COLUMN_EVENT_ID);
        sharedScheduleCode = parser.readLong(COLUMN_ID);
    }

    @Override
    public int compareTo(@NotNull SharedEvents event) {

        int result;
        if (eventId == event.eventId) {
            result = 0;
        } else if (eventId > event.eventId) {
            result = 1;
        } else {
            result = -1;
        }

        return result;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public void setSharedScheduleCode(long sharedScheduleCode) {
        this.sharedScheduleCode = sharedScheduleCode;
    }

    public SharedEvents() {
    }

    public SharedEvents(long eventId, long friendId) {
        this.eventId = eventId;
        this.sharedScheduleCode = friendId;
    }

    @Override
    public String toString() {
        return "SharedEvents{" +
                "eventId=" + eventId +
                ", sharedScheduleCode='" + sharedScheduleCode + '\'' +
                '}';
    }
}
