package com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.course;

import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;

import java.io.Serializable;

/**
 * @authorr mrmedici
 * @desc 课程统计参数
 */
public class CourseStatisticsParams implements Serializable {

    private String classId;
    private String courseId;
    private String courseName;
    private CourseDetailParams courseParams;

    public CourseStatisticsParams(String classId, String courseId, String courseName) {
        this.classId = classId;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public String getClassId() {
        return classId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public CourseDetailParams getCourseParams() {
        return courseParams;
    }

    public void setCourseParams(CourseDetailParams courseParams) {
        this.courseParams = courseParams;
    }
}
