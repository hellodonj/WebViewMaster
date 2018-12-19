package com.lqwawa.lqbaselib.pojo;

import android.os.Bundle;

import java.io.Serializable;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/7/9 0009 16:55
 * E-Mail Address:863378689@qq.com
 * Describe: EventBus消息传递类
 * ======================================================
 */
public class MessageEvent implements Serializable{
    private Bundle bundle;
    private String updateAction;

    public MessageEvent(String updateAction) {
        this.updateAction = updateAction;
    }

    public MessageEvent(Bundle bundle, String updateAction) {
        this.bundle = bundle;
        this.updateAction = updateAction;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public String getUpdateAction() {
        return updateAction;
    }

    public void setUpdateAction(String updateAction) {
        this.updateAction = updateAction;
    }
}
