package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * ======================================================
 * Describe:已点评和未点评详情
 * ======================================================
 */
public class ReviewInfo implements Serializable {
    private String StudentId;
    private String StudentName;
    private String HeadPicUrl;
    private int TeacherReviewCount;
    private String TaskIdStr;

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

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public int getTeacherReviewCount() {
        return TeacherReviewCount;
    }

    public void setTeacherReviewCount(int teacherReviewCount) {
        TeacherReviewCount = teacherReviewCount;
    }

    public String getTaskIdStr() {
        return TaskIdStr;
    }

    public void setTaskIdStr(String taskIdStr) {
        TaskIdStr = taskIdStr;
    }
}
