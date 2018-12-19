package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;

public class ContactsClassMemberDetails extends Model {

    private String Id;
    private String MemberId;
    private String NoteName; //备注名称
    private String RealName; //真实名
    private String Nickname; //会员名
    private String Telephone;
    private String Email;
    private String Identity; //身份/学科
    private String HeadPicUrl;
    private int WorkingState; //0不在职/不在读 1在职/在读
    private boolean HeadTeacherState; //班主任标示
    private int IsFriend;
    private int Role;
    private int ParentType; // 0家长 1妈妈 2爸爸
    private String StudentName;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public String getNoteName() {
        return NoteName;
    }

    public void setNoteName(String noteName) {
        NoteName = noteName;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
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

    public String getIdentity() {
        return Identity;
    }

    public void setIdentity(String identity) {
        Identity = identity;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public int getWorkingState() {
        return WorkingState;
    }

    public void setWorkingState(int workingState) {
        WorkingState = workingState;
    }

    public boolean isHeadTeacherState() {
        return HeadTeacherState;
    }

    public void setHeadTeacherState(boolean headTeacherState) {
        HeadTeacherState = headTeacherState;
    }

    public int getIsFriend() {
        return IsFriend;
    }

    public void setIsFriend(int isFriend) {
        IsFriend = isFriend;
    }

    public int getParentType() {
        return ParentType;
    }

    public void setParentType(int parentType) {
        ParentType = parentType;
    }

    public boolean isFather() {
        return ParentType == 2;
    }

    public boolean isMother() {
        return ParentType == 1;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }
}
