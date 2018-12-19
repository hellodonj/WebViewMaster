package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by KnIghT on 16-6-20.
 */
public class TaskDetail implements Serializable{
    private int CommitTaskId;
    private String StudentId;
    private String  StudentName;
    private String HeadPicUrl;
    private String CommitTime;
    private String ReadTime;
    private Boolean IsRead;
    private String ResId;
    private String ThumbnailUrl;
    private List<TaskDetail> StudentCommitTaskList;
    private int CommitType;
    private int TaskType;
    public void setRead(Boolean read) {
        IsRead = read;
    }

    public void setTaskType(int taskType) {
        TaskType = taskType;
    }

    public int getTaskType() {
        return TaskType;
    }

    public Boolean getRead() {
        return IsRead;
    }

    public void setCommitType(int type){
        this.CommitType=type;
    }
    public int getCommitType(){
        return CommitType;
    }

    public int getCommitTaskId() {
        return CommitTaskId;
    }

    public void setCommitTaskId(int commitTaskId) {
        CommitTaskId = commitTaskId;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public String getCommitTime() {
        return CommitTime;
    }

    public void setCommitTime(String commitTime) {
        CommitTime = commitTime;
    }

    public String getReadTime() {
        return ReadTime;
    }

    public void setReadTime(String readTime) {
        ReadTime = readTime;
    }

    public Boolean getIsRead() {
        return IsRead;
    }

    public void setIsRead(Boolean isRead) {
        IsRead = isRead;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getResId() {
        return ResId;
    }

    public void setResId(String resId) {
        ResId = resId;
    }

    public String getThumbnailUrl() {
        return ThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        ThumbnailUrl = thumbnailUrl;
    }

    public List<TaskDetail> getStudentCommitTaskList() {
        return StudentCommitTaskList;
    }

    public void setStudentCommitTaskList(List<TaskDetail> studentCommitTaskList) {
        StudentCommitTaskList = studentCommitTaskList;
    }
}
