package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by KnIghT on 16-5-18.
 */
public class TempNewResourceInfo {
    private int type;
    private int commentnum;
    private int praisenum;
    private int viewcount;
    private int id;
    private int screentype;
    private String shareurl;
    private String description;
    private String nickname;
    private String code;
    private String createtime;
    private String resourceurl;
    private String thumbnailurl;
    private String createname;
    private int size;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(int commentnum) {
        this.commentnum = commentnum;
    }

    public int getPraisenum() {
        return praisenum;
    }

    public void setPraisenum(int praisenum) {
        this.praisenum = praisenum;
    }

    public int getViewcount() {
        return viewcount;
    }

    public void setViewcount(int viewcount) {
        this.viewcount = viewcount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScreentype() {
        return screentype;
    }

    public void setScreentype(int screentype) {
        this.screentype = screentype;
    }

    public String getShareurl() {
        return shareurl;
    }

    public void setShareurl(String shareurl) {
        this.shareurl = shareurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getResourceurl() {
        return resourceurl;
    }

    public void setResourceurl(String resourceurl) {
        this.resourceurl = resourceurl;
    }

    public String getThumbnailurl() {
        return thumbnailurl;
    }

    public void setThumbnailurl(String thumbnailurl) {
        this.thumbnailurl = thumbnailurl;
    }

    public String getCreatename() {
        return createname;
    }

    public void setCreatename(String createname) {
        this.createname = createname;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static NewResourceInfo pase2NewResourceInfo(TempNewResourceInfo tempInfo) {
        if (tempInfo == null) {
            return null;
        }
        NewResourceInfo info = new NewResourceInfo();
        info.setResourceType(tempInfo.getType());
        info.setCommentNumber(tempInfo.getCommentnum());
        info.setShareAddress(tempInfo.getShareurl());
        info.setDescription(tempInfo.getDescription());
        info.setTitle(tempInfo.getNickname());
        info.setPointNumber(tempInfo.getPraisenum());
        info.setReadNumber(tempInfo.getViewcount());
        info.setAuthorId(tempInfo.getCode());
        info.setMicroId(String.valueOf(tempInfo.getId()));
        info.setUpdatedTime(tempInfo.getCreatetime());
        info.setResourceUrl(tempInfo.getResourceurl());
        info.setThumbnail(tempInfo.getThumbnailurl());
        info.setScreenType(tempInfo.getScreentype());
        info.setAuthorName(tempInfo.getCreatename());
        info.setFileSize(tempInfo.getSize());
        return info;
    }

}
