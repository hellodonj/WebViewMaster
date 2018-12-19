package com.galaxyschool.app.wawaschool.pojo;

public class GroupBasicInfo {

    String GroupId;
    String GruopName;
    String HeadPicUrl;
    String LQ_SchoolId;
    String ClassId;
    int Type;
    String Id;
    String SchoolName;
    boolean GagState;
    boolean IsInGroup;

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getGruopName() {
        return GruopName;
    }

    public void setGruopName(String gruopName) {
        GruopName = gruopName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public String getLQ_SchoolId() {
        return LQ_SchoolId;
    }

    public void setLQ_SchoolId(String LQ_SchoolId) {
        this.LQ_SchoolId = LQ_SchoolId;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        this.Type = type;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public boolean isGagState() {
        return GagState;
    }

    public void setGagState(boolean gagState) {
        GagState = gagState;
    }

    public boolean isChatForbidden() {
        return GagState;
    }

    public boolean isInGroup() {
        return IsInGroup;
    }

    public void setIsInGroup(boolean isInGroup) {
        IsInGroup = isInGroup;
    }
}
