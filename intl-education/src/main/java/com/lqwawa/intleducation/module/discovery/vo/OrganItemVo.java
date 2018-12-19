package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2016/11/10.
 * email:man0fchina@foxmail.com
 */

public class OrganItemVo extends BaseVo {
    private String organId;// 1,
    private String isDelete;// false,
    private String id;// 15,
    private String fee;// 1000,
    private String createTime;// "2016-10-28 17:41:24",
    private String thumbnail;// "http://192.168.99.181/image/2016/10/28/f56e888c-42dc-4d9a-b7a8-cc2a81b8d0bf.jpg",
    private String thumbnailUrl;
    private String status;// 1,
    private String name;// "英语基础",
    private String createName;// "合肥大学管理员",
    private String organName;// "合肥大学",
    private String introduction;// "英语基础",
    private String deleteTime;// "",
    private String achieveCondition;// "",
    private String createId;// 13
    private String teachersName;
    public String getTeachersName() {
        return teachersName;
    }

    public void setTeachersName(String teachersName) {
        this.teachersName = teachersName;
    }


    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAchieveCondition() {
        return achieveCondition;
    }

    public void setAchieveCondition(String achieveCondition) {
        this.achieveCondition = achieveCondition;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
