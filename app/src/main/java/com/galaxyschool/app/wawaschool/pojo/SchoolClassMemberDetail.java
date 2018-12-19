package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;
import java.util.List;

public class SchoolClassMemberDetail implements Serializable{
    private String SchoolId;
    private String SchoolName;
    private String ClassId;
    private String ClassName;
    private int Role;
    private String SchoolLogoUrl;
    private String ClassHeadPicUrl;
    private List<ClassMemberDetail> MemberList;

    public SchoolClassMemberDetail() {
    }

    public String getClassHeadPicUrl() {
        return ClassHeadPicUrl;
    }

    public void setClassHeadPicUrl(String classHeadPicUrl) {
        ClassHeadPicUrl = classHeadPicUrl;
    }

    public String getSchoolLogoUrl() {
        return SchoolLogoUrl;
    }

    public void setSchoolLogoUrl(String schoolLogoUrl) {
        SchoolLogoUrl = schoolLogoUrl;
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

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public List<ClassMemberDetail> getMemberList() {
        return MemberList;
    }

    public void setMemberList(List<ClassMemberDetail> memberList) {
        MemberList = memberList;
    }
}
