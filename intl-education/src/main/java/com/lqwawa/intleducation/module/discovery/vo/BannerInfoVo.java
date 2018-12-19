package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2016/11/9.
 * email:man0fchina@foxmail.com
 */

public class BannerInfoVo extends BaseVo {
    private String isDelete;// false,
    private String id;// 205,
    private String thumbnail;// " http://192.168.99.181/image/2016/11/09/3aeb1296-d684-4d06-8cf7-58b4381603b1.jpg",
    private String linkUrl;// "",
    private String deleteTime;// ""

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
