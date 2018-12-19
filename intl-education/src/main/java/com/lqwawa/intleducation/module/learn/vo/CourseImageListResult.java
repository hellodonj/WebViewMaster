package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2017/4/13.
 * email:man0fchina@foxmail.com
 */

public class CourseImageListResult extends BaseVo {
    List<CourseData> course;
    List<String> data;
    int code;

    public List<CourseData> getCourse() {
        return course;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public void setCourse(List<CourseData> course) {
        this.course = course;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
