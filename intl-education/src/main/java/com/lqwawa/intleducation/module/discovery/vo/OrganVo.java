package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2016/11/8.
 * email:man0fchina@foxmail.com
 */

public class OrganVo extends BaseVo {
    private String createTime;// "2016-10-28 16:51:37",
    private String businessInfo;// "",
    private String publicizeImg;// "http://file.lqwawa.com/UploadFiles/20160714104014/000000000000000/b60ce190-eb8d-4fc5-8428-3a25ef8021bd.jpg",
    private String createName;// "超级管理员",
    private String areaId;// 0,
    private String id;// 114,
    private String isDelete;// false,
    private String thumbnail;// "http://file.lqwawa.com/UploadFiles/20160714104014/000000000000000/b60ce190-eb8d-4fc5-8428-3a25ef8021bd.jpg",
    private String level;// "0",
    private String email;// "b754229874@163.com",
    private String name;// "两栖蛙蛙体验学校",
    private String levelName;// "",
    private String schoolId;// "bfbba4e6-c98a-4160-bca4-540087fb1d89",
    private String deleteTime;// "",
    private String introduction;// "都来耍哈，都来耍，嘿嘿！！！！哈哈哈哈啊哈哈",
    private String mobile;// "15375284325",
    private String createId;// 1
    private boolean attented;

    public boolean isAttented() {
        return attented;
    }

    public void setAttented(boolean attented) {
        this.attented = attented;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getBusinessInfo() {
        return businessInfo;
    }

    public void setBusinessInfo(String businessInfo) {
        this.businessInfo = businessInfo;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicizeImg() {
        return publicizeImg;
    }

    public void setPublicizeImg(String publicizeImg) {
        this.publicizeImg = publicizeImg;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
