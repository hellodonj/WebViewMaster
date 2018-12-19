package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

/**
 * Created by KnIghT on 16-6-15.
 */
public class StudyTaskFinishInfo {
    private int TaskId;//任务ID
    private int  TaskType;//任务类型 0-看微课,1-看课件,2看作业,3-交作业,4-讨论话题
    private String TaskTitle;//任务标题
    private int TaskNum;//任总作业数目
    private int FinishTaskCount;//已完成作业数目
    private String StartTime;//布置时间 格式:"yyyy-MM-dd"
    private String EndTime;//完成时间 格式:"yyyy-MM-dd"
    private int CommentCount;//讨论数目
    private String ClassName;
    private String ThumbnailUrl;//老师发送作业的资源缩略图2016-11-16新增
    private String TaskContent;
    private String WorkOrderId;
    private String WorkOrderThumUrl;
    private List<TaskDetail>  CompletedList;
    private List<TaskDetail>  UnCompletedList;
    private String ResId;

    //新版看课件资源list
    private List<LookResDto> LookResList;

    public void setLookResList(List<LookResDto> lookResList) {
        LookResList = lookResList;
    }

    public List<LookResDto> getLookResList() {
        return LookResList;
    }


    public String getWorkOrderThumUrl() {
        return WorkOrderThumUrl;
    }

    public void setWorkOrderThumUrl(String workOrderThumUrl) {
        WorkOrderThumUrl = workOrderThumUrl;
    }

    public String getWorkOrderId() {
        return WorkOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        WorkOrderId = workOrderId;
    }

    public String getTaskContent() {
        return TaskContent;
    }

    public void setTaskContent(String taskContent) {
        TaskContent = taskContent;
    }

    public String getThumbnailUrl() {
        return ThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        ThumbnailUrl = thumbnailUrl;
    }

    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int taskId) {
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


    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public List<TaskDetail> getCompletedList() {
        return CompletedList;
    }

    public void setCompletedList(List<TaskDetail> completedList) {
        CompletedList = completedList;
    }

    public List<TaskDetail> getUnCompletedList() {
        return UnCompletedList;
    }

    public void setUnCompletedList(List<TaskDetail> unCompletedList) {
        UnCompletedList = unCompletedList;
    }
    public String getResId() {
        return ResId;
    }

    public void setResId(String resId) {
        ResId = resId;
    }
}

