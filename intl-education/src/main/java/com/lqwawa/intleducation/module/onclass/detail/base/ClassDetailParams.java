package com.lqwawa.intleducation.module.onclass.detail.base;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;

/**
 * @author mrmedici
 * @desc 未加入班级的参数定义
 */
public class ClassDetailParams extends BaseVo{
    // 班级详细信息
    private JoinClassEntity joinClassEntity;
    // 点击的班级Id
    private int id;
    // 当前用户在该班级的角色信息
    private String role;
    // 是否从LQ学程入口进入
    private boolean isCourseEnter;
    // 是否从机构主页入口进入
    private boolean isSchoolEnter;
    // 是否授课结束
    private boolean isGiveFinish;
    // 是否是历史班
    private boolean isGiveHistory;
    // 是否是家长身份进来
    private boolean isParent;
    // 家长身份进来，要传孩子的memberId
    private String childMemberId;

    public ClassDetailParams(JoinClassEntity joinClassEntity, int id, String role) {
        this.joinClassEntity = joinClassEntity;
        this.id = id;
        this.role = role;
    }

    public JoinClassEntity getJoinClassEntity() {
        return joinClassEntity;
    }

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public boolean isCourseEnter() {
        return isCourseEnter;
    }

    public void setCourseEnter(boolean courseEnter) {
        isCourseEnter = courseEnter;
    }

    public boolean isSchoolEnter() {
        return isSchoolEnter;
    }

    public void setSchoolEnter(boolean schoolEnter) {
        isSchoolEnter = schoolEnter;
    }

    public boolean isGiveFinish() {
        return isGiveFinish;
    }

    public void setGiveFinish(boolean giveFinish) {
        isGiveFinish = giveFinish;
    }

    public boolean isGiveHistory() {
        return isGiveHistory;
    }

    public void setGiveHistory(boolean giveHistory) {
        isGiveHistory = giveHistory;
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
}
