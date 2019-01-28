package com.lqwawa.intleducation.factory.data.entity.course;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 课程学习进度的实体
 */
public class LearningProgressEntity implements Serializable {

    private String coursePercent;
    private String thumbnail;
    private String userId;
    private String userName;

    public String getCoursePercent() {
        return coursePercent;
    }

    public void setCoursePercent(String coursePercent) {
        this.coursePercent = coursePercent;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
