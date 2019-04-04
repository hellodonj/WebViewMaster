package com.lqwawa.intleducation.factory.data.entity.tutorial;

import java.io.Serializable;


/**
 * @author mrmedic
 * @desc 助教拉取帮辅的学生实体
 */
public class AssistStudentEntity implements Serializable {
    private int Id;
    private String AssMemberId;
    private String StuMemberId;
    private String StuNickName;
    private String StuRealName;
    private String StuHeadPicUrl;
    private int TotalTaskNum;
    private int ReviewedTaskNum;

    private String CreateId;
    private String CreateName;
    private String CreateTime;
    private String UpdateId;
    private String UpdateName;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getAssMemberId() {
        return AssMemberId;
    }

    public void setAssMemberId(String assMemberId) {
        AssMemberId = assMemberId;
    }

    public String getStuMemberId() {
        return StuMemberId;
    }

    public void setStuMemberId(String stuMemberId) {
        StuMemberId = stuMemberId;
    }

    public String getStuNickName() {
        return StuNickName;
    }

    public void setStuNickName(String stuNickName) {
        StuNickName = stuNickName;
    }

    public String getStuRealName() {
        return StuRealName;
    }

    public void setStuRealName(String stuRealName) {
        StuRealName = stuRealName;
    }

    public String getStuHeadPicUrl() {
        return StuHeadPicUrl;
    }

    public void setStuHeadPicUrl(String stuHeadPicUrl) {
        StuHeadPicUrl = stuHeadPicUrl;
    }

    public int getTotalTaskNum() {
        return TotalTaskNum;
    }

    public void setTotalTaskNum(int totalTaskNum) {
        TotalTaskNum = totalTaskNum;
    }

    public int getReviewedTaskNum() {
        return ReviewedTaskNum;
    }

    public void setReviewedTaskNum(int reviewedTaskNum) {
        ReviewedTaskNum = reviewedTaskNum;
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
}
