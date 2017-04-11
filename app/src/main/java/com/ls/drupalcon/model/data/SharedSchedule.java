package com.ls.drupalcon.model.data;


import android.content.ContentValues;
import android.database.Cursor;

import com.ls.drupalcon.model.database.AbstractEntity;
import com.ls.utils.CursorStringParser;

public class SharedSchedule extends AbstractEntity<String> {
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_SCHEDULE_NAME_ID = "_schedule_name_id";

    private String friendId;
    private String scheduleName;

    public SharedSchedule() {
    }

    @Override
    public String getId() {
        return friendId;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues result = new ContentValues();
        result.put(COLUMN_ID, friendId);
        result.put(COLUMN_SCHEDULE_NAME_ID, scheduleName);

        return result;
    }

    @Override
    public void initialize(Cursor theCursor) {
        CursorStringParser parser = new CursorStringParser(theCursor);
        friendId = parser.readString(COLUMN_ID);
        scheduleName = parser.readString(COLUMN_SCHEDULE_NAME_ID);
    }

    public SharedSchedule(String friendId, String scheduleName) {
        this.friendId = friendId;
        this.scheduleName = scheduleName;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    @Override
    public String toString() {
        return "SharedSchedule{" +
                "friendId='" + friendId + '\'' +
                ", scheduleName='" + scheduleName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SharedSchedule that = (SharedSchedule) o;

        if (friendId != null ? !friendId.equals(that.friendId) : that.friendId != null)
            return false;
        return scheduleName != null ? scheduleName.equals(that.scheduleName) : that.scheduleName == null;

    }

    @Override
    public int hashCode() {
        int result = friendId != null ? friendId.hashCode() : 0;
        result = 31 * result + (scheduleName != null ? scheduleName.hashCode() : 0);
        return result;
    }
}
