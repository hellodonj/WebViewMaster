package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

public class ContactsSearchSchoolInfo {

    private String SchoolId;
    private String SchoolName;
    private String SchoolLogo;
    private List<ContactsSearchClassInfo> ClassList;
    private int JoinState;
    private boolean first;

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

    public String getSchoolLogo() {
        return SchoolLogo;
    }

    public void setSchoolLogo(String schoolLogo) {
        SchoolLogo = schoolLogo;
    }

    public List<ContactsSearchClassInfo> getClassList() {
        return ClassList;
    }

    public void setClassList(List<ContactsSearchClassInfo> classList) {
        ClassList = classList;
    }

    public int getJoinState() {
        return JoinState;
    }

    public void setJoinState(int joinState) {
        JoinState = joinState;
    }

    public boolean hasJoined() {
        return JoinState != 0;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

}
