package com.ls.drupalcon.model.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.ls.drupalcon.model.database.AbstractEntity;
import com.ls.utils.CursorStringParser;

import org.jetbrains.annotations.NotNull;


public class FriendsFavorite extends AbstractEntity<Long> implements Comparable<FriendsFavorite> {
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_EVENT_ID = "_friend_id";


    @SerializedName("_id")
    private long eventId;

    @SerializedName("_friend_id")
    private String friendId;


    @Override
    public Long getId() {
        return eventId;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues result = new ContentValues();
        result.put(COLUMN_ID, eventId);
        result.put(COLUMN_EVENT_ID, friendId);

        return result;
    }

    @Override
    public void initialize(Cursor cursor) {
        CursorStringParser parser = new CursorStringParser(cursor);
        eventId = parser.readLong(COLUMN_ID);
        friendId = parser.readString(COLUMN_EVENT_ID);
    }

    @Override
    public int compareTo(@NotNull FriendsFavorite event) {

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

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public FriendsFavorite() {
    }

    public FriendsFavorite(long eventId, String friendId) {
        this.eventId = eventId;
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return "FriendsFavorite{" +
                "eventId=" + eventId +
                ", friendId='" + friendId + '\'' +
                '}';
    }
}
