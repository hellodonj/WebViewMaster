package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by KnIghT on 16-5-18.
 */
public class TaskOrderNewResourceInfo {

    private String createName;
    private String createTime;
    private int fileSize;
    private String fullResName;
    private int id;
    private String memberId;
    private String parentId;
    private String playUrl;
    private int screenType;
    private String shareUrl;
    private int status;
    private String subResName;
    private int subResType;
    private int synResult;
    private String thumbUrl;


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

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFullResName() {
        return fullResName;
    }

    public void setFullResName(String fullResName) {
        this.fullResName = fullResName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }


    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
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

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public int getSubResType() {
        return subResType;
    }

    public void setSubResType(int subResType) {
        this.subResType = subResType;
    }

    public int getSynResult() {
        return synResult;
    }

    public void setSynResult(int synResult) {
        this.synResult = synResult;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public static NewResourceInfo pase2NewResourceInfo(TaskOrderNewResourceInfo tempInfo) {
        if (tempInfo == null) {
            return null;
        }
        NewResourceInfo info = new NewResourceInfo();
        info.setAuthorName(tempInfo.getCreateName());
        info.setCreatedTime(tempInfo.getCreateTime());
        info.setFileSize(tempInfo.getFileSize());
        info.setTitle(tempInfo.getFullResName());
        info.setResourceType(tempInfo.getSubResType());
        info.setMicroId(tempInfo.getId()+"");
        info.setAuthorId(tempInfo.getMemberId());
        info.setParentMicroId(tempInfo.getParentId());
        info.setResourceUrl(tempInfo.getPlayUrl());
        info.setScreenType(tempInfo.getScreenType());
        info.setShareAddress(tempInfo.getShareUrl());
        info.setResourceType(tempInfo.getSubResType());
        info.setThumbnail(tempInfo.getThumbUrl());
        return info;
    }

}
