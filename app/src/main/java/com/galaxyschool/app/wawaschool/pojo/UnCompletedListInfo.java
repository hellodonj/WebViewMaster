package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by Administrator on 2016.06.30.
 */
public class UnCompletedListInfo {

    private String StudentId;
    private String StudentName;
    private String CommitTime;
    private boolean IsRead;
    private String HeadPicUrl;

    private int CommitTaskId;
    private String ReadTime;
    private String ResId;
    private String ThumbnailUrl;
    private int TaskId;
    private int TaskType;

    public int getCommitTaskId() {
        return CommitTaskId;
    }

    public void setCommitTaskId(int commitTaskId) {
        CommitTaskId = commitTaskId;
    }

    public String getReadTime() {
        return ReadTime;
    }

    public void setReadTime(String readTime) {
        ReadTime = readTime;
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

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getCommitTime() {
        return CommitTime;
    }

    public void setCommitTime(String commitTime) {
        CommitTime = commitTime;
    }

    public boolean isRead() {
        return IsRead;
    }

    public void setIsRead(boolean isRead) {
        IsRead = isRead;
    }

    public CompletedListInfo toCompleteListInfo(){
        CompletedListInfo info = new CompletedListInfo();
        info.setStudentId(StudentId);
        info.setStudentName(StudentName);
        info.setCommitTime(CommitTime);
        info.setIsRead(IsRead);
        info.setHeadPicUrl(HeadPicUrl);
        info.setCommitTaskId(CommitTaskId);
        info.setResId(ResId);
        info.setReadTime(ReadTime);
        info.setThumbnailUrl(ThumbnailUrl);
        info.setTaskId(TaskId);
        info.setTaskType(TaskType);
        return info;
    }
}
