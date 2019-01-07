package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/23.
 */

public class EmceeList implements Serializable{
    private int Id;

    private int ExtId;

    private int Type;

    private String MemberId;

    private String NickName;

    private String RealName;

    private String HeadPicUrl;

    private String CreateId;

    private String CreateName;

    private String CreateTime;

    private String UpdateId;

    private String UpdateName;

    private String UpdateTime;

    private boolean Deleted;

    private String ClassIds;

    private String SchoolIds;

    private boolean IsOnlineSchool;

    public EmceeList(){

    }

    public boolean isOnlineSchool() {
        return IsOnlineSchool;
    }

    public void setIsOnlineSchool(boolean onlineSchool) {
        IsOnlineSchool = onlineSchool;
    }

    public String getClassIds() {
        return ClassIds;
    }

    public void setClassIds(String classIds) {
        ClassIds = classIds;
    }

    public String getSchoolIds() {
        return SchoolIds;
    }

    public void setSchoolIds(String schoolIds) {
        SchoolIds = schoolIds;
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

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public String getCreateId() {
        return CreateId;
    }

    public void setCreateId(String createId) {
        CreateId = createId;
    }

    public String getCreateName() {
        return CreateName;
    }

    public void setCreateName(String createName) {
        CreateName = createName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getUpdateId() {
        return UpdateId;
    }

    public void setUpdateId(String updateId) {
        UpdateId = updateId;
    }

    public String getUpdateName() {
        return UpdateName;
    }

    public void setUpdateName(String updateName) {
        UpdateName = updateName;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean deleted) {
        Deleted = deleted;
    }
}
