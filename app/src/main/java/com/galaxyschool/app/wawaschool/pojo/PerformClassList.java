package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/5/24.
 */

public class PerformClassList implements Serializable{
    private int Id;
    private int PraiseCount;
    private int PlayCount;
    private String CreatorId;
    private String CreatorRealName;
    private String CreatorNickName;
    private String CreatorHeadPicUrl;
    private String Title;
    private String ResId;
    private String ResUrl;
    private String ResThumbnail;
    private String Intro;
    private String PublishTime;
    private String CreateId;
    private String CreateName;
    private String CreateTime;
    private String UpdateId;
    private String UpdateName;
    private String UpdateTime;
    private boolean Deleted;
    private String ShareUrl;
    private String ShareUrlQrCode;
    private String LeValue;
    private int LeStatus;
    private String uuid;
    private String vuid;
    private List<CommentListInfo> CommentList;
    private List<PerformMember> PerformMemberList;

    //0普通视频  1ar视频
    private int Type;
    private int PhotoId;

    public PerformClassList() {
    }

    public int getLeStatus() {
        return LeStatus;
    }

    public void setLeStatus(int leStatus) {
        LeStatus = leStatus;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVuid() {
        return vuid;
    }

    public void setVuid(String vuid) {
        this.vuid = vuid;
    }

    public String getShareUrlQrCode() {
        return ShareUrlQrCode;
    }

    public void setShareUrlQrCode(String shareUrlQrCode) {
        ShareUrlQrCode = shareUrlQrCode;
    }

    public List<CommentListInfo> getCommentList() {
        return CommentList;
    }

    public void setCommentList(List<CommentListInfo> commentList) {
        CommentList = commentList;
    }

    public String getLeValue() {
        return LeValue;
    }

    public void setLeValue(String leValue) {
        LeValue = leValue;
    }

    public String getShareUrl() {
        return ShareUrl;
    }

    public void setShareUrl(String shareUrl) {
        ShareUrl = shareUrl;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getPraiseCount() {
        return PraiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        PraiseCount = praiseCount;
    }

    public int getPlayCount() {
        return PlayCount;
    }

    public void setPlayCount(int playCount) {
        PlayCount = playCount;
    }

    public String getCreatorId() {
        return CreatorId;
    }

    public void setCreatorId(String creatorId) {
        CreatorId = creatorId;
    }

    public String getCreatorRealName() {
        return CreatorRealName;
    }

    public void setCreatorRealName(String creatorRealName) {
        CreatorRealName = creatorRealName;
    }

    public String getCreatorNickName() {
        return CreatorNickName;
    }

    public void setCreatorNickName(String creatorNickName) {
        CreatorNickName = creatorNickName;
    }

    public String getCreatorHeadPicUrl() {
        return CreatorHeadPicUrl;
    }

    public void setCreatorHeadPicUrl(String creatorHeadPicUrl) {
        CreatorHeadPicUrl = creatorHeadPicUrl;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getResId() {
        return ResId;
    }

    public void setResId(String resId) {
        ResId = resId;
    }

    public String getResUrl() {
        return ResUrl;
    }

    public void setResUrl(String resUrl) {
        ResUrl = resUrl;
    }

    public String getResThumbnail() {
        return ResThumbnail;
    }

    public void setResThumbnail(String resThumbnail) {
        ResThumbnail = resThumbnail;
    }

    public String getIntro() {
        return Intro;
    }

    public void setIntro(String intro) {
        Intro = intro;
    }

    public String getPublishTime() {
        return PublishTime;
    }

    public void setPublishTime(String publishTime) {
        PublishTime = publishTime;
    }

    public String getCreateId() {
        return CreateId;
    }

    public void setCreateId(String createId) {
        CreateId = createId;
    }

    public String getCreateName() {
        return CreateName;
    }

    public void setCreateName(String createName) {
        CreateName = createName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getUpdateId() {
        return UpdateId;
    }

    public void setUpdateId(String updateId) {
        UpdateId = updateId;
    }

    public String getUpdateName() {
        return UpdateName;
    }

    public void setUpdateName(String updateName) {
        UpdateName = updateName;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setIsDeleted(boolean deleted) {
        Deleted = deleted;
    }

    public List<PerformMember> getPerformMemberList() {
        return PerformMemberList;
    }

    public void setPerformMemberList(List<PerformMember> performMemberList) {
        PerformMemberList = performMemberList;
    }


    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getPhotoId() {
        return PhotoId;
    }

    public void setPhotoId(int photoId) {
        PhotoId = photoId;
    }
}
