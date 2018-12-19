package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

/**
 * Created by Administrator on 2016.06.30.
 */
public class HomeworkFinishStatusObjectInfo {

    private String ClassName;
    private int TaskId;
    private int TaskType;
    private String TaskTitle;
    private String TaskContent;
    private int TaskNum;
    private int FinishTaskCount;
    private String StartTime;
    private String EndTime;
    private int CommentCount;
    private String ResId;

    private List<CompletedListInfo> CompletedList;
    private List<UnCompletedListInfo> UnCompletedList;


    //新版看课件资源list
    private List<LookResDto> LookResList;

    public void setLookResList(List<LookResDto> lookResList) {
        LookResList = lookResList;
    }

    public List<LookResDto> getLookResList() {
        return LookResList;
    }


    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
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

    public List<CompletedListInfo> getCompletedList() {
        return CompletedList;
    }

    public void setCompletedList(List<CompletedListInfo> completedList) {
        CompletedList = completedList;
    }

    public List<UnCompletedListInfo> getUnCompletedList() {
        return UnCompletedList;
    }

    public void setUnCompletedList(List<UnCompletedListInfo> unCompletedList) {
        UnCompletedList = unCompletedList;
    }
}
