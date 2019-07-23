package com.galaxyschool.app.wawaschool.pojo;

import android.text.TextUtils;

import java.io.Serializable;

public class SubscribeClassInfo implements Serializable {

    private String ClassId;
    private String ClassMailListId;
    private String ClassName;
    private String HeadPicUrl;
    private int Type; // 0班级通讯录 1老师通讯录
    private boolean Isjoin;
    private String GroupId;
    private String SchoolId;
    private int Status;
    private String schoolName;
    private String gradeId;
    private String gradeName;
    private int Role; // 1老师
    private boolean HeadMaster;
    private int IsHistory;
    private boolean isSelect;
    private String Roles; //0老师，1学生，2家长
    private int IsHeader;
    private String ClassQRCode;
    private boolean isInClass;
    //用来区分在班级中进行跳转---通知、秀秀等等具体跳转细节区分
    private boolean isTempData;
    private int Price;

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public void setIsTempData(boolean  isTempData){
        this.isTempData=isTempData;
    }
    public boolean isTempData(){
        return this.isTempData;
    }
    public void setIsHeader(int isHeader) {
        IsHeader = isHeader;
    }

    public int getIsHeader() {
        return IsHeader;
    }

    public void setRoles(String roles) {
        Roles = roles;
    }

    public String getRoles() {
        return Roles;
    }

    public boolean isTeacherByRoles() {
        return (Roles != null && Roles.contains(String.valueOf(RoleType.ROLE_TYPE_TEACHER)));
    }
    public boolean isParentByRoles() {
        return (Roles != null && Roles.contains(String.valueOf(RoleType.ROLE_TYPE_PARENT)));
    }


    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public String getClassMailListId() {
        return ClassMailListId;
    }

    public void setClassMailListId(String classMailListId) {
        ClassMailListId = classMailListId;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public boolean isClass() {
        return Type == 0;
    }

    public boolean isSchool() {
        return Type == 1;
    }

    public boolean getIsjoin() {
        return Isjoin;
    }

    public void setIsjoin(boolean isjoin) {
        Isjoin = isjoin;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public boolean isTeacher() {
        return Role == 1;
    }

    public boolean isjoin() {
        return Isjoin;
    }

    public boolean isHeadMaster() {
        return HeadMaster;
    }

    public void setHeadMaster(boolean headMaster) {
        HeadMaster = headMaster;
    }

    public int getIsHistory() {
        return IsHistory;
    }

    public void setIsHistory(int isHistory) {
        IsHistory = isHistory;
    }

    public boolean isHistory() {
        return IsHistory == 0;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getClassQRCode() {
        return ClassQRCode;
    }

    public void setClassQRCode(String classQRCode) {
        ClassQRCode = classQRCode;
    }

    public boolean isInClass() {
        return isInClass;
    }

    public void setInClass(boolean inClass) {
        isInClass = inClass;
    }

    public int getRoleType() {
        int roleType = -1;
        if(!TextUtils.isEmpty(Roles)) {
            if(Roles.contains(String.valueOf(RoleType.ROLE_TYPE_STUDENT))) {
                roleType = RoleType.ROLE_TYPE_STUDENT;
            } else if(Roles.contains(String.valueOf(RoleType.ROLE_TYPE_PARENT))) {
                roleType = RoleType.ROLE_TYPE_PARENT;
            } else if(Roles.contains(String.valueOf(RoleType.ROLE_TYPE_TEACHER))) {
                roleType = RoleType.ROLE_TYPE_TEACHER;
            }
        }
        return roleType;
    }

    public  boolean isOnlyTeacher(){
        if(TextUtils.isEmpty(Roles)) return false;
        if(Roles.equals(String.valueOf(RoleType.ROLE_TYPE_TEACHER))){
            return  true;
        }
        return false;
    }
    public boolean isOnlyParent(){
        if(TextUtils.isEmpty(Roles)) return false;
        if(Roles.equals(String.valueOf(RoleType.ROLE_TYPE_PARENT))){
            return  true;
        }
        return false;
    }
}
