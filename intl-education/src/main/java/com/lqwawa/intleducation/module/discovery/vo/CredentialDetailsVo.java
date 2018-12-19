package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2016/11/21.
 * email:man0fchina@foxmail.com
 */

public class CredentialDetailsVo extends BaseVo {
    private List<CredentialVo> certification;
    private List<CourseVo> courseList;
    private int code;
    private boolean isAdd;//false,
    private boolean isApply;//false,
    private boolean isPay;//false
    private boolean courseFinish;//false
    private boolean isBuy;
    private String message;

    public List<CredentialVo> getCertification() {
        return certification;
    }

    public void setCertification(List<CredentialVo> certification) {
        this.certification = certification;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<CourseVo> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<CourseVo> courseList) {
        this.courseList = courseList;
    }

    public boolean isIsAdd() {
        return isAdd;
    }

    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public boolean isIsApply() {
        return isApply;
    }

    public void setIsApply(boolean isApply) {
        this.isApply = isApply;
    }

    public boolean isIsPay() {
        return isPay;
    }

    public void setIsPay(boolean isPay) {
        this.isPay = isPay;
    }

    public boolean isCourseFinish() {
        return courseFinish;
    }

    public void setCourseFinish(boolean courseFinish) {
        this.courseFinish = courseFinish;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsBuy() {
        return isBuy;
    }

    public void setIsBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }
}
