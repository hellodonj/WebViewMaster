package com.galaxyschool.app.wawaschool.pojo;
import java.io.Serializable;

public class ClassMemberDetail implements Serializable {
    private String Id;
    private String MemberId;
    private String RealName;
    private String Nickname;
    private String HeadPicUrl;
    private String QRCode;
    private String Telephone;
    private String Email;
    private String Memo;
    private String Relation;
    private String CreatedOn;
    private String UpdatedOn;
    private String DeletedOn;
    private String LQ_ClassMailListId;
    private String GagMark;
    private String ClassId;
    private String SchoolId;
    private int WorkingState;
    private int ParentType;
    private int Role;
    private String ClassName;
    private boolean isSelect;
    private boolean isMySelf;
    private String ClassHeadPicUrl;

    public ClassMemberDetail() {
    }

    public String getClassHeadPicUrl() {
        return ClassHeadPicUrl;
    }

    public void setClassHeadPicUrl(String classHeadPicUrl) {
        ClassHeadPicUrl = classHeadPicUrl;
    }

    public boolean isMySelf() {
        return isMySelf;
    }

    public void setIsMySelf(boolean mySelf) {
        isMySelf = mySelf;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean select) {
        isSelect = select;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

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

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String getUpdatedOn() {
        return UpdatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        UpdatedOn = updatedOn;
    }

    public String getDeletedOn() {
        return DeletedOn;
    }

    public void setDeletedOn(String deletedOn) {
        DeletedOn = deletedOn;
    }

    public String getLQ_ClassMailListId() {
        return LQ_ClassMailListId;
    }

    public void setLQ_ClassMailListId(String LQ_ClassMailListId) {
        this.LQ_ClassMailListId = LQ_ClassMailListId;
    }

    public String getGagMark() {
        return GagMark;
    }

    public void setGagMark(String gagMark) {
        GagMark = gagMark;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    public int getWorkingState() {
        return WorkingState;
    }

    public void setWorkingState(int workingState) {
        WorkingState = workingState;
    }

    public int getParentType() {
        return ParentType;
    }

    public void setParentType(int parentType) {
        ParentType = parentType;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }
}
