package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by Administrator on 2016.07.26.
 */
public class FamilyMemberInfo {

    private String SchoolId;
    private String ClassId;
    private String MemberId;
    private String RealName;
    private String NickName;
    private String HeadPicUrl;
    private String Telephone;
    private String Email;
    //0父亲 1母亲  2家长 3班主任 4学生
    private int RelationType;
    private String NoteName;
    private boolean IsFriend;

    public boolean getIsFriend() {
        return IsFriend;
    }

    public void setIsFriend(boolean friend) {
        IsFriend = friend;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

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

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getRelationType() {
        return RelationType;
    }

    public void setRelationType(int relationType) {
        RelationType = relationType;
    }

    public String getNoteName() {
        return NoteName;
    }

    public void setNoteName(String noteName) {
        NoteName = noteName;
    }
}
