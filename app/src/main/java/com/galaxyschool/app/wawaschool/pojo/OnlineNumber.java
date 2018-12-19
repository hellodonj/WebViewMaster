package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/1/3 17:31
 * E-Mail Address:863378689@qq.com
 * Describe:观看直播的在线人数统计
 * ======================================================
 */

public class OnlineNumber implements Serializable{
    private int Id;
    private int ExtId;
    private String ClassId;
    private String MemberId;
    private boolean IsOnline;
    private String CreateTime;
    private String UpdateTime;
    private boolean Deleted;

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean deleted) {
        Deleted = deleted;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getExtId() {
        return ExtId;
    }

    public void setExtId(int extId) {
        ExtId = extId;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public boolean isOnline() {
        return IsOnline;
    }

    public void setIsOnline(boolean online) {
        IsOnline = online;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }
}
