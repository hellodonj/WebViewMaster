package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2018/1/21.
 * email:man0fchina@foxmail.com
 */

public class CourseProcessVo extends BaseVo {

    /**
     * learned : true
     * week : 1
     */

    private boolean learned;
    private String week;
    private int status;

    public boolean isIsLearned() {
        return learned;
    }

    public void setIsLearned(boolean learned) {
        this.learned = learned;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
