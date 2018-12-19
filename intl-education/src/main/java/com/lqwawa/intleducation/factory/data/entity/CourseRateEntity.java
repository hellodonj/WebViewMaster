package com.lqwawa.intleducation.factory.data.entity;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 用来解析课程学习进度信息
 * @date 2018/04/09 13:55
 * @history v1.0
 * **********************************
 */
public class CourseRateEntity extends BaseEntity{

    private int learnRate;

    public int getLearnRate() {
        return learnRate;
    }

    public void setLearnRate(int learnRate) {
        this.learnRate = learnRate;
    }
}
