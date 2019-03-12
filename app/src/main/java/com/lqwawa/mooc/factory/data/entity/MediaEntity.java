package com.lqwawa.mooc.factory.data.entity;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 接收服务Media上传的返回
 */
public class MediaEntity implements Serializable {

    private String resourceurl;

    public String getResourceurl() {
        return resourceurl;
    }

    public void setResourceurl(String resourceurl) {
        this.resourceurl = resourceurl;
    }
}
