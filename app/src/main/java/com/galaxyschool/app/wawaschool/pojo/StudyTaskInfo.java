package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by KnIghT on 16-6-15.
 */
public class StudyTaskInfo implements Serializable {
    private String TaskId;//任务ID
    private int TaskType;//任务类型 0-看微课,1-看课件,2看作业,3-交作业,4-讨论话题
    private String TaskTitle;//任务标题
    private String TaskContent;//任务内容 当任务类型为讨论时才有内容
    private int TaskNum;//任总作业数目
    private int FinishTaskCount;//已完成作业数目
    private String StartTime;//布置时间 格式:"yyyy-MM-dd"
    private String EndTime;//完成时间 格式:"yyyy-MM-dd"
    private int CommentCount;//讨论数目
    private String ResId; //资源Id
    private String WorkOrderId;

    //手动添加的字段
    private int roleType; // 角色类型
    private String studentId; //孩子信息
    private UserInfo userInfo;//角色信息
    private boolean fromStudyTask;//来自学习任务
    //资源地址
    private String ResUrl;

    //新版看课件资源list
    private List<LookResDto> LookResList;
    private String CollectSchoolId;
    private int ST_StudyGroupId;//班级小组的id
    private String ST_StudyGroupName;//班级小组的名称
    private int AirClassId;//空中课堂直播的id
    private String TaskCreateId;//任务创建者的id
    private boolean NeedCommit;

    public boolean isNeedCommit() {
        return NeedCommit;
    }

    public void setNeedCommit(boolean needCommit) {
        NeedCommit = needCommit;
    }

    public HomeworkListInfo toHomeworkListInfo(String memberId,String nickName){
        HomeworkListInfo info = new HomeworkListInfo();
        info.setTaskId(TaskId);
        info.setTaskType(String.valueOf(TaskType));
        info.setTaskTitle(TaskTitle);
        info.setTaskCreateId(memberId);
        info.setTaskCreateName(nickName);
        info.setStartTime(StartTime);
        info.setEndTime(EndTime);
        info.setCommentCount(String.valueOf(CommentCount));
        info.setTaskNum(TaskNum);
        info.setFinishTaskCount(FinishTaskCount);
        info.setCreateTime(StartTime);
        info.setResId(ResId);
        info.setDiscussContent(TaskContent);
        info.setStudentIsRead(false);
        info.setIsSelect(false);
        info.setWorkOrderId(WorkOrderId);
        info.setLookResList(LookResList);
        info.setAirClassId(AirClassId);
        info.setTaskCreateId(TaskCreateId);
        return info;
    }

    public String getTaskCreateId() {
        return TaskCreateId;
    }

    public void setTaskCreateId(String taskCreateId) {
        TaskCreateId = taskCreateId;
    }

    public int getAirClassId() {
        return AirClassId;
    }

    public void setAirClassId(int airClassId) {
        this.AirClassId = airClassId;
    }

    public int getST_StudyGroupId() {
        return ST_StudyGroupId;
    }

    public void setST_StudyGroupId(int ST_StudyGroupId) {
        this.ST_StudyGroupId = ST_StudyGroupId;
    }

    public String getST_StudyGroupName() {
        return ST_StudyGroupName;
    }

    public void setST_StudyGroupName(String ST_StudyGroupName) {
        this.ST_StudyGroupName = ST_StudyGroupName;
    }

    public String getCollectSchoolId() {
        return CollectSchoolId;
    }

    public void setCollectSchoolId(String collectSchoolId) {
        CollectSchoolId = collectSchoolId;
    }

    public void setLookResList(List<LookResDto> lookResList) {
        LookResList = lookResList;
    }

    public List<LookResDto> getLookResList() {
        return LookResList;
    }


    public void setResUrl(String resUrl) {
        ResUrl = resUrl;
    }

    public String getResUrl() {
        return ResUrl;
    }

    public void setFromStudyTask(boolean fromStudyTask) {
        this.fromStudyTask = fromStudyTask;
    }

    public boolean getIsFromStudyTask(){
        return fromStudyTask;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }

    public int getRoleType() {
        return roleType;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getWorkOrderId(){
        return  WorkOrderId;
    }
    public void setWorkOrderId(String workOrderId){
        this.WorkOrderId=workOrderId;
    }
    public String getTaskId() {
        return TaskId;
    }

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public int getTaskType() {
        return TaskType;
    }

    public void setTaskType(int taskType) {
        TaskType = taskType;
    }

    public String getTaskTitle() {
        return TaskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        TaskTitle = taskTitle;
    }

    public String getTaskContent() {
        return TaskContent;
    }

    public void setTaskContent(String taskContent) {
        TaskContent = taskContent;
    }

    public int getTaskNum() {
        return TaskNum;
    }

    public void setTaskNum(int taskNum) {
        TaskNum = taskNum;
    }

    public int getFinishTaskCount() {
        return FinishTaskCount;
    }

    public void setFinishTaskCount(int finishTaskCount) {
        FinishTaskCount = finishTaskCount;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public int getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(int commentCount) {
        CommentCount = commentCount;
    }

    public String getResId() {
        return ResId;
    }

    public void setResId(String resId) {
        ResId = resId;
    }
}

