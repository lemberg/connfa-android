package com.ls.drupalcon.model.data;


public class FriendsFavoriteSchedule {
    private String friendId;
    private String scheduleName;

    public FriendsFavoriteSchedule() {
    }

    public FriendsFavoriteSchedule(String friendId, String scheduleName) {
        this.friendId = friendId;
        this.scheduleName = scheduleName;
    }

    public String getFriendId() {
        return friendId;
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
}
