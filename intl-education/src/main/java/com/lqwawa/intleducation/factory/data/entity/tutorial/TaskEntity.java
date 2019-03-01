package com.lqwawa.intleducation.factory.data.entity.tutorial;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 帮辅功能作业列表的实体
 */
public class TaskEntity implements Serializable {

    private int Id;
    private int AssistStudent_Id;
    private String AssMemberId;
    private String StuMemberId;
    private String StuNickName;
    private String StuRealName;
    private String StuHeadPicUrl;
    private String StuAssistTime;
    private String Title;
    private String ResId;
    private String ResUrl;
    private int ReviewState;
    private String ReviewTime;
    private String ExpireTime;
    private int T_TaskId;
    private int T_TaskType;
    private int T_CommitTaskId;
    private int T_CommitTaskOnlineId;
    private String T_EQId;
    private int T_AirClassId;
    private String T_ClassId;
    private String T_ClassName;
    private int T_CourseId;
    private String T_CourseName;
    private int T_ResCourseId;
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

    public int getAssistStudent_Id() {
        return AssistStudent_Id;
    }

    public void setAssistStudent_Id(int AssistStudent_Id) {
        this.AssistStudent_Id = AssistStudent_Id;
    }

    public String getAssMemberId() {
        return AssMemberId;
    }

    public void setAssMemberId(String AssMemberId) {
        this.AssMemberId = AssMemberId;
    }

    public String getStuMemberId() {
        return StuMemberId;
    }

    public void setStuMemberId(String StuMemberId) {
        this.StuMemberId = StuMemberId;
    }

    public String getStuNickName() {
        return StuNickName;
    }

    public void setStuNickName(String StuNickName) {
        this.StuNickName = StuNickName;
    }

    public String getStuRealName() {
        return StuRealName;
    }

    public void setStuRealName(String StuRealName) {
        this.StuRealName = StuRealName;
    }

    public String getStuHeadPicUrl() {
        return StuHeadPicUrl;
    }

    public void setStuHeadPicUrl(String StuHeadPicUrl) {
        this.StuHeadPicUrl = StuHeadPicUrl;
    }

    public String getStuAssistTime() {
        return StuAssistTime;
    }

    public void setStuAssistTime(String StuAssistTime) {
        this.StuAssistTime = StuAssistTime;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getResId() {
        return ResId;
    }

    public void setResId(String ResId) {
        this.ResId = ResId;
    }

    public String getResUrl() {
        return ResUrl;
    }

    public void setResUrl(String ResUrl) {
        this.ResUrl = ResUrl;
    }

    public int getReviewState() {
        return ReviewState;
    }

    public void setReviewState(int ReviewState) {
        this.ReviewState = ReviewState;
    }

    public String getReviewTime() {
        return ReviewTime;
    }

    public void setReviewTime(String ReviewTime) {
        this.ReviewTime = ReviewTime;
    }

    public String getExpireTime() {
        return ExpireTime;
    }

    public void setExpireTime(String ExpireTime) {
        this.ExpireTime = ExpireTime;
    }

    public int getT_TaskId() {
        return T_TaskId;
    }

    public void setT_TaskId(int T_TaskId) {
        this.T_TaskId = T_TaskId;
    }

    public int getT_TaskType() {
        return T_TaskType;
    }

    public void setT_TaskType(int T_TaskType) {
        this.T_TaskType = T_TaskType;
    }

    public int getT_CommitTaskId() {
        return T_CommitTaskId;
    }

    public void setT_CommitTaskId(int T_CommitTaskId) {
        this.T_CommitTaskId = T_CommitTaskId;
    }

    public int getT_CommitTaskOnlineId() {
        return T_CommitTaskOnlineId;
    }

    public void setT_CommitTaskOnlineId(int T_CommitTaskOnlineId) {
        this.T_CommitTaskOnlineId = T_CommitTaskOnlineId;
    }

    public String getT_EQId() {
        return T_EQId;
    }

    public void setT_EQId(String T_EQId) {
        this.T_EQId = T_EQId;
    }

    public int getT_AirClassId() {
        return T_AirClassId;
    }

    public void setT_AirClassId(int T_AirClassId) {
        this.T_AirClassId = T_AirClassId;
    }

    public String getT_ClassId() {
        return T_ClassId;
    }

    public void setT_ClassId(String T_ClassId) {
        this.T_ClassId = T_ClassId;
    }

    public String getT_ClassName() {
        return T_ClassName;
    }

    public void setT_ClassName(String T_ClassName) {
        this.T_ClassName = T_ClassName;
    }

    public int getT_CourseId() {
        return T_CourseId;
    }

    public void setT_CourseId(int T_CourseId) {
        this.T_CourseId = T_CourseId;
    }

    public String getT_CourseName() {
        return T_CourseName;
    }

    public void setT_CourseName(String T_CourseName) {
        this.T_CourseName = T_CourseName;
    }

    public int getT_ResCourseId() {
        return T_ResCourseId;
    }

    public void setT_ResCourseId(int T_ResCourseId) {
        this.T_ResCourseId = T_ResCourseId;
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
