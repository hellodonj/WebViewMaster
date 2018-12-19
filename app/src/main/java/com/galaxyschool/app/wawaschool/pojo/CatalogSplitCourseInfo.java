package com.galaxyschool.app.wawaschool.pojo;

/**
 * Author: wangchao
 * Time: 2015/12/16 11:28
 */
public class CatalogSplitCourseInfo {
    private int id;
    private int parentId;
    private String playUrl;
    private int splitRecId;
    private int status;
    private String subResName;
    private int subResType;
    private String subResUrl;
    private String thumbUrl;
    private int synResult;
    private boolean isSelect;

    public CatalogSplitCourseInfo() {

    }

    public CatalogSplitCourseInfo(
        int id, int parentId, String playUrl, int splitRecId, int status, String subResName, int subResType,
        String subResUrl, String thumbUrl, int synResult, boolean isSelect) {
        this.id = id;
        this.parentId = parentId;
        this.playUrl = playUrl;
        this.splitRecId = splitRecId;
        this.status = status;
        this.subResName = subResName;
        this.subResType = subResType;
        this.subResUrl = subResUrl;
        this.thumbUrl = thumbUrl;
        this.synResult = synResult;
        this.isSelect = isSelect;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public int getSplitRecId() {
        return splitRecId;
    }

    public void setSplitRecId(int splitRecId) {
        this.splitRecId = splitRecId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSubResName() {
        return subResName;
    }

    public void setSubResName(String subResName) {
        this.subResName = subResName;
    }

    public int getSubResType() {
        return subResType;
    }

    public void setSubResType(int subResType) {
        this.subResType = subResType;
    }

    public String getSubResUrl() {
        return subResUrl;
    }

    public void setSubResUrl(String subResUrl) {
        this.subResUrl = subResUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public int getSynResult() {
        return synResult;
    }

    public void setSynResult(int synResult) {
        this.synResult = synResult;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
