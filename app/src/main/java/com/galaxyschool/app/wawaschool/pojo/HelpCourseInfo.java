package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

/**
 * Author: wangchao
 * Time: 2015/08/25 10:21
 */
public class HelpCourseInfo {
    String helpName;
    List<CourseInfo> list;

    public String getHelpName() {
        return helpName;
    }

    public void setHelpName(String helpName) {
        this.helpName = helpName;
    }

    public List<CourseInfo> getList() {
        return list;
    }

    public void setList(List<CourseInfo> list) {
        this.list = list;
    }
}
