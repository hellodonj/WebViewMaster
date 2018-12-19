package com.oosic.apps.share;

import com.umeng.socialize.media.UMediaObject;

public class ShareInfo {
    private String title;
    private String content;
    private String targetUrl;
    private UMediaObject uMediaObject;
    private SharedResource sharedResource;
    /////////////////
    private boolean isPublicRescourse = true;
    private String parentId;
    private WawaShareResource wawaShareResource;
    public ShareInfo() {

    }

    public ShareInfo(
        String title, String content, String targetUrl, UMediaObject uMediaObject) {
        this.title = title;
        this.content = content;
        this.targetUrl = targetUrl;
        this.uMediaObject = uMediaObject;
    }

    public ShareInfo(
        String title, String content, String targetUrl, UMediaObject uMediaObject,
        SharedResource sharedResource) {
        this.title = title;
        this.content = content;
        this.targetUrl = targetUrl;
        this.uMediaObject = uMediaObject;
        this.sharedResource = sharedResource;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isPublicRescourse() {
        return isPublicRescourse;
    }

    public void setIsPublicRescourse(boolean publicRescourse) {
        isPublicRescourse = publicRescourse;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public UMediaObject getuMediaObject() {
        return uMediaObject;
    }

    public void setuMediaObject(UMediaObject uMediaObject) {
        this.uMediaObject = uMediaObject;
    }

    public SharedResource getSharedResource() {
        return sharedResource;
    }

    public void setSharedResource(SharedResource sharedResource) {
        this.sharedResource = sharedResource;
    }

    public WawaShareResource getWawaShareResource() {
        return wawaShareResource;
    }

    public void setWawaShareResource(WawaShareResource wawaShareResource) {
        this.wawaShareResource = wawaShareResource;
    }

    @Override
    public String toString() {
        return "ShareInfo{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", targetUrl='" + targetUrl + '\'' +
                ", uMediaObject=" + uMediaObject +
                ", sharedResource=" + sharedResource +
                ", isPublicRescourse=" + isPublicRescourse +
                ", parentId='" + parentId + '\'' +
                ", wawaShareResource=" + wawaShareResource +
                '}';
    }
}
