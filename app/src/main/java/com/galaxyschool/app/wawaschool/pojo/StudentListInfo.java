package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by Administrator on 2016.07.22.
 */
public class StudentListInfo {

    private String Id;
    private String MemberId;
    private String RealName;
    private String Nickname;
    private int Role;
    private String HeadPicUrl;
    private String QRCode;
    private String Telephone;
    private String Email;
    private String Memo;
    private String Relation;
    private int WorkingState;
    private boolean GagMark;
    private int ParentType;
    private String LQ_ClassMailListId;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getLQ_ClassMailListId() {
        return LQ_ClassMailListId;
    }

    public void setLQ_ClassMailListId(String LQ_ClassMailListId) {
        this.LQ_ClassMailListId = LQ_ClassMailListId;
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

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
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

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public String getRelation() {
        return Relation;
    }

    public void setRelation(String relation) {
        Relation = relation;
    }

    public int getWorkingState() {
        return WorkingState;
    }

    public void setWorkingState(int workingState) {
        WorkingState = workingState;
    }

    public boolean isGagMark() {
        return GagMark;
    }

    public void setGagMark(boolean gagMark) {
        GagMark = gagMark;
    }

    public int getParentType() {
        return ParentType;
    }

    public void setParentType(int parentType) {
        ParentType = parentType;
    }
}
