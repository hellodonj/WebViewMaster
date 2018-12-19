package com.galaxyschool.app.wawaschool.pojo;

public class PersonInfo {

    private String MemberId;
    private String RealName;
    private String HeadPicUrl;
    private String FirstLetter;
    private int isFriend; //0否 1是

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public String getFirstLetter() {
        return FirstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        FirstLetter = firstLetter;
    }

    public boolean isFriend() {
        return isFriend == 1;
    }

    public void setFriend(boolean isFriend) {
        this.isFriend = isFriend ? 1 : 0;
    }

}
