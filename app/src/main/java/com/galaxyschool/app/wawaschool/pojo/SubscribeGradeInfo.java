package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

public class SubscribeGradeInfo {

    private String LevelGId;
    private String LevelGName;
    private int ClassMailType;
    private List<SubscribeClassInfo> ClassList;

    public String getLevelGId() {
        return LevelGId;
    }

    public void setLevelGId(String levelGId) {
        LevelGId = levelGId;
    }

    public String getLevelGName() {
        return LevelGName;
    }

    public void setLevelGName(String levelGName) {
        LevelGName = levelGName;
    }

    public int getClassMailType() {
        return ClassMailType;
    }

    public void setClassMailType(int classMailType) {
        ClassMailType = classMailType;
    }

    public List<SubscribeClassInfo> getClassList() {
        return ClassList;
    }

    public void setClassList(List<SubscribeClassInfo> classList) {
        ClassList = classList;
    }

}
