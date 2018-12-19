package com.galaxyschool.app.wawaschool.pojo;

public class UserBasicInfo {

    String MemberId;
    String HXID;
    String NickName;
    String RealName;
    String HeaderPic;
    boolean WhetherFriends;
    String FriendId;

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getHXID() {
        return HXID;
    }

    public void setHXID(String HXID) {
        this.HXID = HXID;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getHeaderPic() {
        return HeaderPic;
    }

    public void setHeaderPic(String headerPic) {
        HeaderPic = headerPic;
    }

    public boolean isWhetherFriends() {
        return WhetherFriends;
    }

    public void setWhetherFriends(boolean whetherFriends) {
        WhetherFriends = whetherFriends;
    }

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
    }
}
