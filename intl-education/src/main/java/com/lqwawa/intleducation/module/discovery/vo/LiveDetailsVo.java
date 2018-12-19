package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2017/11/22.
 * email:man0fchina@foxmail.com
 */

public class LiveDetailsVo extends BaseVo {

    /**
     * haveCourse : true
     * isJoin : false
     * isBuy : false
     * isBuyAndDelete : false
     * isExpire : false
     */

    private boolean haveCourse;
    private boolean isJoin;
    private boolean isBuy;
    private boolean isBuyAndDelete;
    private boolean isExpire;
    private LiveInfoVo live;
    private String teacherIds;
    private String tutorIds;

    public boolean isHaveCourse() {
        return haveCourse;
    }

    public void setHaveCourse(boolean haveCourse) {
        this.haveCourse = haveCourse;
    }

    public boolean isIsJoin() {
        return isJoin;
    }

    public void setIsJoin(boolean isJoin) {
        this.isJoin = isJoin;
    }

    public boolean isIsBuy() {
        return isBuy;
    }

    public void setIsBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }

    public boolean isIsBuyAndDelete() {
        return isBuyAndDelete;
    }

    public void setIsBuyAndDelete(boolean isBuyAndDelete) {
        this.isBuyAndDelete = isBuyAndDelete;
    }

    public boolean isIsExpire() {
        return isExpire;
    }

    public void setIsExpire(boolean isExpire) {
        this.isExpire = isExpire;
    }

    public LiveInfoVo getLive() {
        return live;
    }

    public void setLive(LiveInfoVo live) {
        this.live = live;
    }

    public String getTeacherIds() {
        return teacherIds;
    }

    public void setTeacherIds(String teacherIds) {
        this.teacherIds = teacherIds;
    }

    public String getTutorIds() {
        return tutorIds;
    }

    public void setTutorIds(String tutorIds) {
        this.tutorIds = tutorIds;
    }
}
