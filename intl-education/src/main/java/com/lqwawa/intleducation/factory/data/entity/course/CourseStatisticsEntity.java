package com.lqwawa.intleducation.factory.data.entity.course;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 课程学习进度的实体
 */
public class CourseStatisticsEntity implements Serializable {

    private int count;
    private String name;
    private int type;
    private int color;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
