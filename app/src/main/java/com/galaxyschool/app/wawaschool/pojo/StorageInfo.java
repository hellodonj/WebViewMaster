package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

public class StorageInfo implements Serializable {
    private String Id;
    private String MemberId;
    private long TotalKb_Personal;
    private long TotalKb_PostBar;
    private long TotalKb_Perform;
    private long TotalKb_All;
    private String UpdateTime;
    private String CreateTime;
    private String Deleted;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public long getTotalKb_Personal() {
        return TotalKb_Personal;
    }

    public void setTotalKb_Personal(long totalKb_Personal) {
        TotalKb_Personal = totalKb_Personal;
    }

    public long getTotalKb_PostBar() {
        return TotalKb_PostBar;
    }

    public void setTotalKb_PostBar(long totalKb_PostBar) {
        TotalKb_PostBar = totalKb_PostBar;
    }

    public long getTotalKb_Perform() {
        return TotalKb_Perform;
    }

    public void setTotalKb_Perform(long totalKb_Perform) {
        TotalKb_Perform = totalKb_Perform;
    }

    public long getTotalKb_All() {
        return TotalKb_All;
    }

    public void setTotalKb_All(long totalKb_All) {
        TotalKb_All = totalKb_All;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getDeleted() {
        return Deleted;
    }

    public void setDeleted(String deleted) {
        Deleted = deleted;
    }
}
