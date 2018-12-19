package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.ResourceResult;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;

import java.util.List;

/**
 * Created by Administrator on 2016/8/23.
 */
public class CourseImageListResult extends ResourceResult<List<String>> {
    private List<CourseData> course;

    public CourseImageListResult() {
    }

    public CourseImageListResult(List<CourseData> course) {
        this.course = course;
    }

    public List<CourseData> getCourse() {
        return course;
    }

    public void setCourse(List<CourseData> course) {
        this.course = course;
    }
}
