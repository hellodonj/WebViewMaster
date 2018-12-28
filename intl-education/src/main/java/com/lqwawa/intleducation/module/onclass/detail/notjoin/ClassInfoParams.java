package com.lqwawa.intleducation.module.onclass.detail.notjoin;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;

/**
 * @author mrmedici
 * @desc 班级信息的实体,用来传参给ClassDetailActivity，用来判断班级已加入未加入
 */
public class ClassInfoParams extends BaseVo{
    // 班级实体
    private OnlineClassEntity classEntity;
    // 是否从机构主页进来的
    private boolean isSchoolEnter;
    // 是否从LQ学程进来的
    private boolean isCourseEnter;
    // 家长查看孩子的
    private boolean isParent;
    // 孩子的MemberId
    private String childMemberId;

    // 是否推送跳转过来
    private boolean pushEnter;
    // 是否推送从主页面过来
    private boolean isHome;

    public ClassInfoParams(OnlineClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    public ClassInfoParams(OnlineClassEntity classEntity, boolean isSchoolEnter, boolean isCourseEnter) {
        this.classEntity = classEntity;
        this.isSchoolEnter = isSchoolEnter;
        this.isCourseEnter = isCourseEnter;
    }

    public OnlineClassEntity getClassEntity() {
        return classEntity;
    }

    public boolean isSchoolEnter() {
        return isSchoolEnter;
    }

    public boolean isCourseEnter() {
        return isCourseEnter;
    }

    public boolean isParent() {
        return isParent;
    }

    public String getChildMemberId() {
        return childMemberId;
    }

    public void setParent(boolean isParent, @NonNull String childMemberId) {
        this.isParent = isParent;
        this.childMemberId = childMemberId;
    }

    public boolean isPushEnter() {
        return pushEnter;
    }

    public void setPushEnter(boolean pushEnter) {
        this.pushEnter = pushEnter;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }
}
