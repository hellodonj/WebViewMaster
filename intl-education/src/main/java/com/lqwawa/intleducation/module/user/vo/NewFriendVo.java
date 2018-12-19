package com.lqwawa.intleducation.module.user.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2016/12/6.
 * email:man0fchina@foxmail.com
 */

public class NewFriendVo extends BaseVo {

    /**
     * isDelete : false
     * id : 125
     * dealTime :
     * content : 你好
     * createTime : 2016-11-15 09:41:52
     * dealFlag : 0
     * comeName : boyifs04
     * comeId : 64
     * toName : 苏粟
     * toId : 8
     * type : 0
     * createName : boyifs04
     * deleteTime :
     * createId : 64
     */

    private boolean isDelete;
    private long id;
    private String dealTime;
    private String content;
    private String createTime;
    private int dealFlag;
    private String comeName;
    private long comeId;
    private String toName;
    private long toId;
    private int type;
    private String createName;
    private String deleteTime;
    private long createId;
    private String thumbnail;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getDealFlag() {
        return dealFlag;
    }

    public void setDealFlag(int dealFlag) {
        this.dealFlag = dealFlag;
    }

    public String getComeName() {
        return comeName;
    }

    public void setComeName(String comeName) {
        this.comeName = comeName;
    }

    public long getComeId() {
        return comeId;
    }

    public void setComeId(long comeId) {
        this.comeId = comeId;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public long getToId() {
        return toId;
    }

    public void setToId(long toId) {
        this.toId = toId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public long getCreateId() {
        return createId;
    }

    public void setCreateId(long createId) {
        this.createId = createId;
    }
}
