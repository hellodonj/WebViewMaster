package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2018/1/15.
 * email:man0fchina@foxmail.com
 */

public class EmceeListVo extends BaseVo{
    /**
     * Id : 3266
     * ExtId : 1861
     * Type : 0
     * MemberId : 283220d6-7d77-4b7b-8e74-ca5b86739f7d
     * NickName : zhangke
     * RealName : 张珂
     * HeadPicUrl : 20170623095639/283220d6-7d77-4b7b-8e74-ca5b86739f7d/d84ccbe0-0eac-4798-bcc3-f3353f2ef78d.png
     * SchoolIds : null
     * ClassIds : null
     * CreateId : 283220d6-7d77-4b7b-8e74-ca5b86739f7d
     * CreateName : 张珂
     * CreateTime : 2018-01-05 15:28:49
     * UpdateId : 283220d6-7d77-4b7b-8e74-ca5b86739f7d
     * UpdateName : 张珂
     * UpdateTime : 2018-01-05 15:28:49
     * Deleted : false
     */

    private int Id;
    private int ExtId;
    private int Type;
    private String MemberId;
    private String NickName;
    private String RealName;
    private String HeadPicUrl;
    private Object SchoolIds;
    private Object ClassIds;
    private String CreateId;
    private String CreateName;
    private String CreateTime;
    private String UpdateId;
    private String UpdateName;
    private String UpdateTime;
    private boolean Deleted;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getExtId() {
        return ExtId;
    }

    public void setExtId(int ExtId) {
        this.ExtId = ExtId;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String MemberId) {
        this.MemberId = MemberId;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String RealName) {
        this.RealName = RealName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String HeadPicUrl) {
        this.HeadPicUrl = HeadPicUrl;
    }

    public Object getSchoolIds() {
        return SchoolIds;
    }

    public void setSchoolIds(Object SchoolIds) {
        this.SchoolIds = SchoolIds;
    }

    public Object getClassIds() {
        return ClassIds;
    }

    public void setClassIds(Object ClassIds) {
        this.ClassIds = ClassIds;
    }

    public String getCreateId() {
        return CreateId;
    }

    public void setCreateId(String CreateId) {
        this.CreateId = CreateId;
    }

    public String getCreateName() {
        return CreateName;
    }

    public void setCreateName(String CreateName) {
        this.CreateName = CreateName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public String getUpdateId() {
        return UpdateId;
    }

    public void setUpdateId(String UpdateId) {
        this.UpdateId = UpdateId;
    }

    public String getUpdateName() {
        return UpdateName;
    }

    public void setUpdateName(String UpdateName) {
        this.UpdateName = UpdateName;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String UpdateTime) {
        this.UpdateTime = UpdateTime;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean Deleted) {
        this.Deleted = Deleted;
    }
}
