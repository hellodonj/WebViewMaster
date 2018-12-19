package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂班级详情通知实体
 * @date 2018/06/04 16:10
 * @history v1.0
 * **********************************
 */
public class ClassNotificationEntity extends BaseVo{

    private int Type;
    private Object GroupId;
    private int IsRead;
    private int ReadCount;
    private int NoReadCount;
    private String time;
    private String Id;
    private String Title;
    private String Thumbnail;
    private String ResourceUrl;
    private String ShareAddress;
    private String ResourceType;
    private int ScreenType;
    private String ReadNumber;
    private String CommentNumber;
    private String PointNumber;
    private String CreatedTime;
    private String UpdatedTime;
    private String MicroId;
    private String AuthorId;
    private String AuthorName;
    private int CollectionNum;
    private String Description;
    private String FileSize;
    private Object LeValue;
    private int LeStatus;
    private String HeadPicUrl;
    private Object ResProperties;

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public Object getGroupId() {
        return GroupId;
    }

    public void setGroupId(Object GroupId) {
        this.GroupId = GroupId;
    }

    public int getIsRead() {
        return IsRead;
    }

    public void setIsRead(int IsRead) {
        this.IsRead = IsRead;
    }

    public int getReadCount() {
        return ReadCount;
    }

    public void setReadCount(int ReadCount) {
        this.ReadCount = ReadCount;
    }

    public int getNoReadCount() {
        return NoReadCount;
    }

    public void setNoReadCount(int NoReadCount) {
        this.NoReadCount = NoReadCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String Thumbnail) {
        this.Thumbnail = Thumbnail;
    }

    public String getResourceUrl() {
        return ResourceUrl;
    }

    public void setResourceUrl(String ResourceUrl) {
        this.ResourceUrl = ResourceUrl;
    }

    public String getShareAddress() {
        return ShareAddress;
    }

    public void setShareAddress(String ShareAddress) {
        this.ShareAddress = ShareAddress;
    }

    public String getResourceType() {
        return ResourceType;
    }

    public void setResourceType(String ResourceType) {
        this.ResourceType = ResourceType;
    }

    public int getScreenType() {
        return ScreenType;
    }

    public void setScreenType(int ScreenType) {
        this.ScreenType = ScreenType;
    }

    public String getReadNumber() {
        return ReadNumber;
    }

    public void setReadNumber(String ReadNumber) {
        this.ReadNumber = ReadNumber;
    }

    public String getCommentNumber() {
        return CommentNumber;
    }

    public void setCommentNumber(String CommentNumber) {
        this.CommentNumber = CommentNumber;
    }

    public String getPointNumber() {
        return PointNumber;
    }

    public void setPointNumber(String PointNumber) {
        this.PointNumber = PointNumber;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(String CreatedTime) {
        this.CreatedTime = CreatedTime;
    }

    public String getUpdatedTime() {
        return UpdatedTime;
    }

    public void setUpdatedTime(String UpdatedTime) {
        this.UpdatedTime = UpdatedTime;
    }

    public String getMicroId() {
        return MicroId;
    }

    public void setMicroId(String MicroId) {
        this.MicroId = MicroId;
    }

    public String getAuthorId() {
        return AuthorId;
    }

    public void setAuthorId(String AuthorId) {
        this.AuthorId = AuthorId;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String AuthorName) {
        this.AuthorName = AuthorName;
    }

    public int getCollectionNum() {
        return CollectionNum;
    }

    public void setCollectionNum(int CollectionNum) {
        this.CollectionNum = CollectionNum;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getFileSize() {
        return FileSize;
    }

    public void setFileSize(String FileSize) {
        this.FileSize = FileSize;
    }

    public Object getLeValue() {
        return LeValue;
    }

    public void setLeValue(Object LeValue) {
        this.LeValue = LeValue;
    }

    public int getLeStatus() {
        return LeStatus;
    }

    public void setLeStatus(int LeStatus) {
        this.LeStatus = LeStatus;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String HeadPicUrl) {
        this.HeadPicUrl = HeadPicUrl;
    }

    public Object getResProperties() {
        return ResProperties;
    }

    public void setResProperties(Object ResProperties) {
        this.ResProperties = ResProperties;
    }
}
