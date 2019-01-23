package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * ======================================================
 * Describe:点评统计实体类
 * ======================================================
 */
public class TeacherReviewStatisInfo implements Serializable {
    private String TaskCreateId;
    private String ClassId;
    private String StartTimeBegin;
    private String StartTimeEnd;
    private List<ReviewInfo> HasReviewList;
    private List<ReviewInfo> UnReviewList;

    public String getTaskCreateId() {
        return TaskCreateId;
    }

    public void setTaskCreateId(String taskCreateId) {
        TaskCreateId = taskCreateId;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getStartTimeBegin() {
        return StartTimeBegin;
    }

    public void setStartTimeBegin(String startTimeBegin) {
        StartTimeBegin = startTimeBegin;
    }

    public String getStartTimeEnd() {
        return StartTimeEnd;
    }

    public void setStartTimeEnd(String startTimeEnd) {
        StartTimeEnd = startTimeEnd;
    }

    public List<ReviewInfo> getHasReviewList() {
        return HasReviewList;
    }

    public void setHasReviewList(List<ReviewInfo> hasReviewList) {
        HasReviewList = hasReviewList;
    }

    public List<ReviewInfo> getUnReviewList() {
        return UnReviewList;
    }

    public void setUnReviewList(List<ReviewInfo> unReviewList) {
        UnReviewList = unReviewList;
    }
}
