package com.lqwawa.intleducation.module.user.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2016/11/30.
 * email:man0fchina@foxmail.com
 */

public class PersonalInfoVo extends BaseVo{
    private int code;
    private String message;

    private List<PersonalInfo> user;
    private String collectCount;
    private String messageCount;
    private String orderCount;
    private int newNoticeCount;

    public int getNewNoticeCount() {
        return newNoticeCount;
    }

    public void setNewNoticeCount(int newNoticeCount) {
        this.newNoticeCount = newNoticeCount;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<PersonalInfo> getUser() {
        return user;
    }

    public void setUser(List<PersonalInfo> user) {
        this.user = user;
    }

    public String getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(String collectCount) {
        this.collectCount = collectCount;
    }

    public String getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(String messageCount) {
        this.messageCount = messageCount;
    }

    public String getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(String orderCount) {
        this.orderCount = orderCount;
    }
}
