package com.galaxyschool.app.wawaschool.pojo;

import android.os.Parcel;

import com.lqwawa.client.pojo.ResourceInfo;

import java.util.List;

/**看课件支持多类型
 */
public class ResourceInfoTag extends ResourceInfo {

    List<ResourceInfo> SplitInfoList;
    private String taskId;
    private String id;
    private boolean hasRead;

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResourceInfoTag() {

    }

    public ResourceInfoTag(List<ResourceInfo> splitInfoList) {
        SplitInfoList = splitInfoList;
    }

    public List<ResourceInfo> getSplitInfoList() {
        return SplitInfoList;
    }

    public void setSplitInfoList(List<ResourceInfo> splitInfoList) {
        SplitInfoList = splitInfoList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(SplitInfoList);
    }

    protected ResourceInfoTag(Parcel in) {
        super(in);
        this.SplitInfoList = in.createTypedArrayList(ResourceInfo.CREATOR);
    }

    public static final Creator<ResourceInfoTag> CREATOR = new Creator<ResourceInfoTag>() {
        public ResourceInfoTag createFromParcel(Parcel source) {
            return new ResourceInfoTag(source);
        }

        public ResourceInfoTag[] newArray(int size) {
            return new ResourceInfoTag[size];
        }
    };

    public ResourceInfo toResourceInfo(){
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setImgPath(getImgPath());
        resourceInfo.setResourcePath(getResourcePath());
        resourceInfo.setShareAddress(getShareAddress());
        resourceInfo.setTitle(getTitle());
        resourceInfo.setType(getType());
        resourceInfo.setResourceType(getResourceType());
        resourceInfo.setScreenType(getScreenType());
        resourceInfo.setLeValue(getLeValue());
        resourceInfo.setFilePath(getFilePath());
        resourceInfo.setIsSelected(isSelected());
        //resId
        resourceInfo.setResId(getResId());
        //设置作者id
        resourceInfo.setAuthorId(getAuthorId());
        //设置vuid视频播放地址
        resourceInfo.setVuid(getVuid());
        resourceInfo.setLeStatus(getLeStatus());
        resourceInfo.setPoint(getPoint());
        resourceInfo.setCourseId(getCourseId());
        resourceInfo.setCourseTaskType(getCourseTaskType());
        return resourceInfo;
    }

    public NewResourceInfo toNewResourceInfo(){
        NewResourceInfo info = new NewResourceInfo();
        info.setThumbnail(getImgPath());
        info.setResourceUrl(getResourcePath());
        info.setShareAddress(getShareAddress());
        info.setTitle(getTitle());
        info.setType(getType());
        info.setResourceType(getResourceType());
        info.setScreenType(getScreenType());
        info.setLeValue(getLeValue());
        info.setIsSelect(isSelected());
        info.setResourceId(getResId());
        info.setAuthorId(getAuthorId());
        info.setLeStatus(getLeStatus());
        info.setVuid(getVuid());
        info.setMicroId(getResId());
        info.setAuthorName(getAuthorName());
        info.setCreatedTime(getCreateTime());
        return info;
    }
}
