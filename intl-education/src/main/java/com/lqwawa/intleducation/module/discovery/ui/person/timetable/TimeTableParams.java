package com.lqwawa.intleducation.module.discovery.ui.person.timetable;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.factory.role.LQwawaRoleType;

/**
 * 我的课程表核心参数
 */
public class TimeTableParams extends BaseVo {

    private String curMemberId;
    private int role;
    // 是否是授课结束的
    private boolean isGiveFinish;

    public TimeTableParams(String curMemberId,@LQwawaRoleType.LQwawaRoleTypeRes int role) {
        this.curMemberId = curMemberId;
        this.role = role;
    }

    public String getCurMemberId() {
        return curMemberId;
    }

    public int getRole() {
        return role;
    }

    public boolean isGiveFinish() {
        return isGiveFinish;
    }

    public void setGiveFinish(boolean giveFinish) {
        isGiveFinish = giveFinish;
    }
}
