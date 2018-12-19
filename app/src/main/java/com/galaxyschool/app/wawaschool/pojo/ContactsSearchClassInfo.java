package com.galaxyschool.app.wawaschool.pojo;

public class ContactsSearchClassInfo {

    String ClassId;
    String ClassMailListID;
    String ClassName;
    String HeadPicUrl;
    int Type;
    int Isjoin;
    String GroupId;

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

    public String getClassMailListID() {
        return ClassMailListID;
    }

    public void setClassMailListID(String classMailListID) {
        ClassMailListID = classMailListID;
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

    public int getIsjoin() {
        return Isjoin;
    }

    public void setIsjoin(int isjoin) {
        Isjoin = isjoin;
    }

    public boolean hasJoined() {
        return Isjoin != 0;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }
}
