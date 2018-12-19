package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;
/**
 * Created by XChen on 2017/1/7.
 * email:man0fchina@foxmail.com
 */

public class MyCredentialListVo extends BaseVo {
    private MyCredentialVo certification;
    private String thumbnail;
    private String organName;
    private boolean isFinish;
    private boolean haveOrder;


    public MyCredentialVo getCertification() {
        return certification;
    }

    public void setCertification(MyCredentialVo certification) {
        this.certification = certification;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public boolean isIsFinish() {
        return isFinish;
    }

    public void setIsFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public boolean isHaveOrder() {
        return haveOrder;
    }

    public void setHaveOrder(boolean haveOrder) {
        this.haveOrder = haveOrder;
    }
}
