package com.galaxyschool.app.wawaschool.pojo.weike;

import java.io.Serializable;

/**
 * Created by wangchao on 1/19/16.
 */
public class MediaData implements Serializable{
    public int splitnum;
    public int splitflag;
    public boolean isdelete;
    public String originname;
    public int parentid;
    public int type;
    public int downloadtimes;
    public int size;
    public String createtime;
    public int id;
    public String level;
    public String thumbnailurl;
    public String createid;
    public String resourceurl;
    public String description;
    public String savename;
    public String createname;


    public String getIdType() {
        StringBuilder builder = new StringBuilder();
        builder.append(id);
        builder.append("-");
        builder.append(type);
        return builder.toString();
    }

    public CourseData toCourseData(){
        CourseData courseData = new CourseData();
        courseData.id = id;
        courseData.type = type;
        courseData.resourceurl = resourceurl;
        courseData.nickname = createname;
        return courseData;
    }
}
