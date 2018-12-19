package com.lqwawa.intleducation.module.discovery.vo;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/14.
 * email:man0fchina@foxmail.com
 */

public class CourseIntroduceVo extends BaseVo {
    // 1课程介绍 2适用对象 3学习目标 4显示课程标准
    private int type;
    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 根据课程信息build出课程公告
     * @param isJoin 是否显示在已加入课程
     * @param vo 课程数据
     * @return 集合数据
     */
    public static List<CourseIntroduceVo> buildData(boolean isJoin, @NonNull CourseVo vo){
        List<CourseIntroduceVo> introduceArray = new ArrayList<>();
        CourseIntroduceVo introduceVo = new CourseIntroduceVo();
        introduceVo.setType(1);
        introduceVo.setInfo(vo.getIntroduction());
        introduceArray.add(introduceVo);
        introduceVo = new CourseIntroduceVo();
        introduceVo.setType(2);
        introduceVo.setInfo(vo.getSuitObj());
        introduceArray.add(introduceVo);
        introduceVo = new CourseIntroduceVo();
        introduceVo.setType(3);
        introduceVo.setInfo(vo.getLearnGoal());
        introduceArray.add(introduceVo);
        if(isJoin && false){
            // 已经加入的课程,显示课程标准
            introduceVo = new CourseIntroduceVo();
            introduceVo.setType(4);
            introduceVo.setInfo(vo.getScoreCriteria());
            introduceArray.add(introduceVo);
        }

        return introduceArray;
    }
}
