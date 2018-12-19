package com.lqwawa.intleducation.module.onclass.detail.base;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;

/**
 * @author mrmedici
 * @desc 在线班级Tab参数
 */
public class OnlineTabParams extends BaseVo{

    private ClassDetailEntity detailEntity;
    private String schoolId;
    private String role;
    // 是否从LQ学程之前的在线班级入口进入
    private boolean isCourseEnter;
    // 是否已经结束授课
    private boolean isGiveFinish;
    // 是否为历史班
    private boolean isGiveHistory;
    // 是否是家长身份
    private boolean isParent;
    // 家长身份，孩子的memberId
    private String childMemberId;
    // 是否加入之后的
    private boolean isJoin;

    public OnlineTabParams(ClassDetailEntity detailEntity, String schoolId, String role,boolean isJoin) {
        this.detailEntity = detailEntity;
        this.schoolId = schoolId;
        this.role = role;
        this.isJoin = isJoin;
    }

    public ClassDetailEntity getDetailEntity() {
        return detailEntity;
    }

    public String getSchoolId() {
        return schoolId;
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

    public boolean isJoin() {
        return isJoin;
    }

    public String getChildMemberId() {
        return childMemberId;
    }

    public void setParent(boolean isParent, @NonNull String childMemberId) {
        this.isParent = isParent;
        this.childMemberId = childMemberId;
    }
}
