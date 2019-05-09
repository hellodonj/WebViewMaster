package com.lqwawa.intleducation.factory.data.entity.course;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;

/**
 * @author mrmedici
 * @desc 课程路由信息接收的实体
 */
public class CourseRouteEntity extends BaseVo {
    // 蛙蛙币购买全本
    public static int BUY_TYPE_MONEY_ALL = 1;
    // 激活码授权码全本
    public static int BUY_TYPE_AUTHORIZATION_ALL = 2;
    // 娃娃币章节购买
    public static int BUY_TYPE_MONEY_CHAPTER = 3;
    // 娃娃币章节购买后授权全部
    public static int BUY_TYPE_MONEY_CHAPTER_AUTHORIZATION_ALL = 4;

    private int buyType;
    private int price;
    private boolean isBuy;
    private String tutorId;
    private String teachersId;
    private boolean isBuyAll;
    private String counselorId;
    private boolean isExpire;
    private boolean isJoin;
    private int type;
    private boolean isLabelAuthorized;

    public int getBuyType() {
        return buyType;
    }

    public void setBuyType(int buyType) {
        this.buyType = buyType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getTeachersId() {
        return teachersId;
    }

    public void setTeachersId(String teachersId) {
        this.teachersId = teachersId;
    }

    public boolean isBuyAll() {
        return isBuyAll;
    }

    public void setBuyAll(boolean buyAll) {
        isBuyAll = buyAll;
    }

    public String getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
    }

    public boolean isExpire() {
        return isExpire;
    }

    public void setExpire(boolean expire) {
        isExpire = expire;
    }

    public boolean isJoin() {
        return isJoin;
    }

    public void setJoin(boolean join) {
        isJoin = join;
    }

    public int getType() {
        return type;
    }

    public CourseRouteEntity setType(int type) {
        this.type = type;
        return this;
    }

    public boolean isLabelAuthorized() {
        return isLabelAuthorized;
    }

    public CourseRouteEntity setLabelAuthorized(boolean labelAuthorized) {
        isLabelAuthorized = labelAuthorized;
        return this;
    }

    /**
     * 是否是课程的老师身份
     * @param memberId 用户Id
     * @return
     */
    public boolean isCourseTeacher(@NonNull String memberId){
        if(EmptyUtil.isNotEmpty(teachersId) && teachersId.contains(memberId)){
            return true;
        }

        if(EmptyUtil.isNotEmpty(tutorId) && tutorId.contains(memberId)){
            return true;
        }

        // 辅导老师中包含
        if(EmptyUtil.isNotEmpty(counselorId) && counselorId.contains(memberId)){
            return true;
        }

        return  false;
    }

    /**
     * 是否是讲师
     * @param memberId 用户的memberId
     * @return true 讲师身份
     */
    public boolean isTeacher(@NonNull String memberId){
        if(EmptyUtil.isNotEmpty(teachersId) && teachersId.contains(memberId)){
            return true;
        }
        return false;
    }

    /**
     * 是否是助教
     * @param memberId 用户的memberId
     * @return true 助教身份
     */
    public boolean isTutor(@NonNull String memberId){
        if(EmptyUtil.isNotEmpty(teachersId) && teachersId.contains(memberId)){
            return false;
        }

        // 助教中包含
        if(EmptyUtil.isNotEmpty(tutorId) && tutorId.contains(memberId)){
            return true;
        }

        return  false;
    }

    /**
     * 是否是辅导老师
     * @param memberId 用户的memberId
     * @return true 辅导老师身份
     */
    public boolean isCounselorId(@NonNull String memberId){
        if(EmptyUtil.isNotEmpty(teachersId) && teachersId.contains(memberId)){
            return false;
        }

        if(EmptyUtil.isNotEmpty(tutorId) && tutorId.contains(memberId)){
            return false;
        }

        // 辅导老师中包含
        if(EmptyUtil.isNotEmpty(counselorId) && counselorId.contains(memberId)){
            return true;
        }

        return  false;
    }
}
