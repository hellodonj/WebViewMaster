package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

public class ContactsSchoolInfo {

    private String SchoolId;
    private String SchoolName;
    private String LogoUrl;
    private int Role; // 0班主任 1老师 2学生 3家长
    private boolean IsOnlineSchool;
    private List<ContactsClassInfo> ClassMailList;

    private boolean isSelected; //选择状态
    private boolean HasInspectAuth;//是否有校长助手的权限

    public boolean isOnlineSchool() {
        return IsOnlineSchool;
    }

    public void setIsOnlineSchool(boolean onlineSchool) {
        IsOnlineSchool = onlineSchool;
    }

    public boolean isHasInspectAuth() {
        return HasInspectAuth;
    }

    public void setHasInspectAuth(boolean hasInspectAuth) {
        HasInspectAuth = hasInspectAuth;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getLogoUrl() {
        return LogoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        LogoUrl = logoUrl;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public boolean isHeadTeacher() {
        return Role == 0;
    }

    public boolean isTeacher() {
        return Role == 0 || Role == 1;
    }

    public boolean isStudent() {
        return Role == 2;
    }

    public boolean isParent() {
        return Role == 3;
    }

    public List<ContactsClassInfo> getClassMailList() {
        return ClassMailList;
    }

    public void setClassMailList(List<ContactsClassInfo> classMailList) {
        ClassMailList = classMailList;
    }

}
