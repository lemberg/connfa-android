package com.ls.drupalcon.model.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.ls.drupalcon.model.database.AbstractEntity;
import com.ls.utils.CursorStringParser;

import org.jetbrains.annotations.NotNull;


public class FriendsFavoriteItem extends AbstractEntity<Long> implements Comparable<FriendsFavoriteItem> {
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_EVENT_ID = "_shared_schedule_code";


    @SerializedName("_id")
    private long eventId;

    @SerializedName("_shared_schedule_code")
    private long sharedScheduleCode;


    @Override
    public Long getId() {
        return eventId;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues result = new ContentValues();
        result.put(COLUMN_ID, eventId);
        result.put(COLUMN_EVENT_ID, sharedScheduleCode);

        return result;
    }

    @Override
    public void initialize(Cursor cursor) {
        CursorStringParser parser = new CursorStringParser(cursor);
        eventId = parser.readLong(COLUMN_ID);
        sharedScheduleCode = parser.readLong(COLUMN_EVENT_ID);
    }

    @Override
    public int compareTo(@NotNull FriendsFavoriteItem event) {

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

    public long getSharedScheduleCode() {
        return sharedScheduleCode;
    }

    public void setSharedScheduleCode(long sharedScheduleCode) {
        this.sharedScheduleCode = sharedScheduleCode;
    }

    public FriendsFavoriteItem() {
    }

    public FriendsFavoriteItem(long eventId, long friendId) {
        this.eventId = eventId;
        this.sharedScheduleCode = friendId;
    }

    @Override
    public String toString() {
        return "FriendsFavoriteItem{" +
                "eventId=" + eventId +
                ", sharedScheduleCode='" + sharedScheduleCode + '\'' +
                '}';
    }
}
