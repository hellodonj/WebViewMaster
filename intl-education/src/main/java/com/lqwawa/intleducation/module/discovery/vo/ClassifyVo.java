package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2016/11/7.
 * email:man0fchina@foxmail.com
 */

public class ClassifyVo extends BaseVo {
    private int type;
    private String createTime;
    private String containChild;
    private String configType;
    private List<ClassifyVo> childList;
    private String createName;
    private int id;
    private String isDelete;
    private int parentId;
    private String configValue;
    private String level;
    private String levelName;
    private String deleteTime;
    private String parentName;
    private String createId;
    private String thumbnail;
    private int labelId;
    private boolean haveVipPermissions;
    public ClassifyVo(){

    }
    public ClassifyVo(ClassifyVo vo){
        this.level = vo.getLevel();
        this.configValue = vo.getConfigValue();
        this.labelId = vo.getLabelId();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<ClassifyVo> getChildList() {
        return childList;
    }

    public void setChildList(List<ClassifyVo> childList) {
        this.childList = childList;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getContainChild() {
        return containChild;
    }

    public void setContainChild(String containChild) {
        this.containChild = containChild;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }

    public boolean isHaveVipPermissions() {
        return haveVipPermissions;
    }

    public void setHaveVipPermissions(boolean haveVipPermissions) {
        this.haveVipPermissions = haveVipPermissions;
    }
}
