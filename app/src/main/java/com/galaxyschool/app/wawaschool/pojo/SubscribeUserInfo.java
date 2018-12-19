package com.galaxyschool.app.wawaschool.pojo;

public class SubscribeUserInfo {

    public static final int USER_TYPE_PERSON = 1;
    public static final int USER_TYPE_SCHOOL = 2;

    public static final int USER_STATE_STRANGE = 0;
    public static final int USER_STATE_SUBSCRIBED = 1;
    public static final int USER_STATE_JOINED = 2;

    private String Id;
    private String Name;
    private String Thumbnail;
    private int State;
    private int Type;
    private boolean IsOnlineSchool;

    public boolean isOnlineSchool() {
        return IsOnlineSchool;
    }

    public void setIsOnlineSchool(boolean onlineSchool) {
        IsOnlineSchool = onlineSchool;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public boolean hasSubscribed() {
        return State == USER_STATE_SUBSCRIBED || State == USER_STATE_JOINED;
    }

    public boolean isFriend() {
        return isPerson() && State == USER_STATE_JOINED;
    }

    public boolean hasJoinedSchool() {
        return isSchool() && State == USER_STATE_JOINED;
    }

    public boolean isPerson() {
        return Type == USER_TYPE_PERSON;
    }

    public boolean isSchool() {
        return Type == USER_TYPE_SCHOOL;
    }

    public boolean hasJoined (){
        return State == USER_STATE_JOINED;
    }

}
