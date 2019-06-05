package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * 描述: 用来接收星级评价的实体
 * 作者|时间: djj on 2019/6/4 0004 下午 6:19
 * starLevel":1.2,"studentNum":64,"courseNum":1
 */
public class TutorStarLevelEntity extends BaseVo {
    //老师星级
    private float starLevel;
    //学生数
    private int studentNum;
    //帮扶的课程数
    private int courseNum;

    public float getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(float starLevel) {
        this.starLevel = starLevel;
    }

    public int getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(int studentNum) {
        this.studentNum = studentNum;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(int courseNum) {
        this.courseNum = courseNum;
    }
}
