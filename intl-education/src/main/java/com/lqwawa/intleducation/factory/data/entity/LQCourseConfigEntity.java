package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 记得写注释喲
 * @date 2018/04/28 11:52
 * @history v1.0
 * **********************************
 */
public class LQCourseConfigEntity extends BaseVo{


    private int configType;
    private String configValue;
    private String configValueEn;
    private boolean containChild;
    private int createId;
    private String createName;
    private String createTime;
    private String deleteTime;
    private int id;
    private boolean isDelete;
    private int labelId;
    private String level;
    private String levelName;
    private int paramTwoId;
    private int paramThreeId;
    private int parentId;
    private String parentName;
    private String picture;
    private String picturePad;
    private int sortNum;
    private String thumbnail;
    private String thumbnailPad;
    // V5.9新添加的字段
    private String entityOrganId;
    private List<CourseVo> courseList;
    private List<LQCourseConfigEntity> childList;

    // V5.11.X新添加的字段
    private boolean isAuthorized;

    public int getConfigType() {
        return configType;
    }

    public void setConfigType(int configType) {
        this.configType = configType;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigValueEn() {
        return configValueEn;
    }

    public void setConfigValueEn(String configValueEn) {
        this.configValueEn = configValueEn;
    }

    public boolean isContainChild() {
        return containChild;
    }

    public void setContainChild(boolean containChild) {
        this.containChild = containChild;
    }

    public int getCreateId() {
        return createId;
    }

    public void setCreateId(int createId) {
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

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
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

    public int getParamTwoId() {
        return paramTwoId;
    }

    public void setParamTwoId(int paramTwoId) {
        this.paramTwoId = paramTwoId;
    }

    public int getParamThreeId() {
        return paramThreeId;
    }

    public void setParamThreeId(int paramThreeId) {
        this.paramThreeId = paramThreeId;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPicturePad() {
        return picturePad;
    }

    public void setPicturePad(String picturePad) {
        this.picturePad = picturePad;
    }

    public int getSortNum() {
        return sortNum;
    }

    public void setSortNum(int sortNum) {
        this.sortNum = sortNum;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailPad() {
        return thumbnailPad;
    }

    public void setThumbnailPad(String thumbnailPad) {
        this.thumbnailPad = thumbnailPad;
    }

    public String getEntityOrganId() {
        return entityOrganId;
    }

    public void setEntityOrganId(String entityOrganId) {
        this.entityOrganId = entityOrganId;
    }

    public List<CourseVo> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<CourseVo> courseList) {
        this.courseList = courseList;
    }

    public List<LQCourseConfigEntity> getChildList() {
        return childList;
    }

    public void setChildList(List<LQCourseConfigEntity> childList) {
        this.childList = childList;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }
}
