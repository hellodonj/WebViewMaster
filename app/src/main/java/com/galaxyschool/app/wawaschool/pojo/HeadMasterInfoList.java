package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/2/2 10:39
 * E-Mail Address:863378689@qq.com
 * Describe:单个机构中角色信息为班主任的班级信息
 * ======================================================
 */

public class HeadMasterInfoList implements Serializable {
    private String ClassMailId;
    private String ClassId;
    private String ClassMailName;
    private String HeadPicUrl;
    private String HeadMaster;
    private String GroupId;
    private String ClassMailDetailId;
    private int Type;
    private int Role;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getClassMailId() {
        return ClassMailId;
    }

    public void setClassMailId(String classMailId) {
        ClassMailId = classMailId;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getClassMailName() {
        return ClassMailName;
    }

    public void setClassMailName(String classMailName) {
        ClassMailName = classMailName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public String getHeadMaster() {
        return HeadMaster;
    }

    public void setHeadMaster(String headMaster) {
        HeadMaster = headMaster;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getClassMailDetailId() {
        return ClassMailDetailId;
    }

    public void setClassMailDetailId(String classMailDetailId) {
        ClassMailDetailId = classMailDetailId;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }
}
