package com.lqwawa.client.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Author: wangchao
 * Time: 2015/11/07 16:36
 */
public class ResourceInfo implements Parcelable,Serializable{
    String imgPath;
    String resourcePath;
    String shareAddress;
    String title;
    int type;
    int resourceType;
    int screenType;
    String leValue;
    String filePath;
    boolean isSelected;
    String resId;
    String authorId;//作者id
    String vuid; //视频播放地址
    private int LeStatus;
    String authorName;
    private String createTime;
    private String ResProperties;
    private int ResCourseId;
    private String Point;//总分
    private int completionMode = 1;//1 复述课件 2 复述课件+语音评测
    private int resPropertyMode = 2; //1 自动评测 2 人工评测

    public int getResPropertyMode() {
        return resPropertyMode;
    }

    public void setResPropertyMode(int resPropertyMode) {
        this.resPropertyMode = resPropertyMode;
    }

    public int getCompletionMode() {
        return completionMode;
    }

    public void setCompletionMode(int completionMode) {
        this.completionMode = completionMode;
    }

    public String getPoint() {
        return Point;
    }

    public void setPoint(String point) {
        Point = point;
    }

    public int getResCourseId() {
        return ResCourseId;
    }

    public void setResCourseId(int resCourseId) {
        ResCourseId = resCourseId;
    }

    public String getResProperties() {
        return ResProperties;
    }

    public void setResProperties(String resProperties) {
        ResProperties = resProperties;
    }

    public ResourceInfo() {
    }


    public ResourceInfo(String filePath, boolean isSelected) {
        this.filePath = filePath;
        this.isSelected = isSelected;
    }

    public ResourceInfo(String imgPath, String resourcePath, String shareAddress, String title,
                        int type, int resourceType, int screenType, boolean isSelected) {
        this.imgPath = imgPath;
        this.resourcePath = resourcePath;
        this.shareAddress = shareAddress;
        this.title = title;
        this.type = type;
        this.resourceType = resourceType;
        this.screenType = screenType;
        this.isSelected = isSelected;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getLeStatus() {
        return LeStatus;
    }

    public void setLeStatus(int leStatus) {
        LeStatus = leStatus;
    }

    public void setVuid(String vuid) {
        this.vuid = vuid;
    }

    public String getVuid() {
        return vuid;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getShareAddress() {
        return shareAddress;
    }

    public void setShareAddress(String shareAddress) {
        this.shareAddress = shareAddress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public String getLeValue() {
        return leValue;
    }

    public void setLeValue(String leValue) {
        this.leValue = leValue;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imgPath);
        dest.writeString(this.resourcePath);
        dest.writeString(this.shareAddress);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeInt(this.resourceType);
        dest.writeInt(this.screenType);
        dest.writeString(this.leValue);
        dest.writeString(this.filePath);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
        dest.writeString(this.resId);
        dest.writeString(authorId);
        dest.writeString(vuid);
        dest.writeString(authorName);
        dest.writeString(createTime);
        dest.writeString(ResProperties);
        dest.writeInt(this.ResCourseId);
        dest.writeString(Point);
        dest.writeInt(this.completionMode);
        dest.writeInt(this.resPropertyMode);
    }

    protected ResourceInfo(Parcel in) {
        this.imgPath = in.readString();
        this.resourcePath = in.readString();
        this.shareAddress = in.readString();
        this.title = in.readString();
        this.type = in.readInt();
        this.resourceType = in.readInt();
        this.screenType = in.readInt();
        this.leValue = in.readString();
        this.filePath = in.readString();
        this.isSelected = in.readByte() != 0;
        this.resId = in.readString();
        this.authorId = in.readString();
        this.vuid = in.readString();
        this.authorName = in.readString();
        this.createTime = in.readString();
        this.ResProperties = in.readString();
        this.ResCourseId = in.readInt();
        this.Point = in.readString();
        this.completionMode = in.readInt();
        this.resPropertyMode = in.readInt();
    }

    public static final Creator<ResourceInfo> CREATOR = new Creator<ResourceInfo>() {
        public ResourceInfo createFromParcel(Parcel source) {
            return new ResourceInfo(source);
        }

        public ResourceInfo[] newArray(int size) {
            return new ResourceInfo[size];
        }
    };
}
