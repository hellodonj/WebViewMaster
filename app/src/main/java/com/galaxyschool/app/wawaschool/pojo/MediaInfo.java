package com.galaxyschool.app.wawaschool.pojo;

import android.text.TextUtils;
import com.galaxyschool.app.wawaschool.db.dto.MediaDTO;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.oosic.apps.share.SharedResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangchao on 12/26/15.
 */
public class MediaInfo {
    String id;
    String title;
    String subTitle;
    String path;
    String thumbnail;
    long duration;
    long createTime;
    int mediaType;
    String shareAddress;
    int resourceType;
    CourseInfo courseInfo;
    String microId;
    boolean isSelect;
    String resourceUrl;
    String updateTime;
    //added
    boolean hasSplitData;
    boolean hasCollected;
    //micro course item
//    NewResourceInfo newResourceInfo;
    LocalCourseInfo localCourseInfo;
    String Description;
    //added to
    NewResourceInfoTag newResourceInfoTag;
    int Type;
    String vuid;//乐视播放用到
    private int LeStatus;
    private String  nocCreateTime;//noc时间
    private String       nocRemark;//noc简介
    private String   nocEntryNum;//noc编号
    private String       nocOrgName;//noc来源
    private String author;//作者
    private int nocNameForType;//noc 参赛名义
    private int nocPraiseNum;//

    private String ResProperties;
    private String Point;

    public String getPoint() {
        return Point;
    }

    public void setPoint(String point) {
        Point = point;
    }

    public String getResProperties() {
        return ResProperties;
    }

    public void setResProperties(String resProperties) {
        ResProperties = resProperties;
    }

    //作者id
    private String authorId;
    public int getLeStatus() {
        return LeStatus;
    }

    public void setLeStatus(int leStatus) {
        LeStatus = leStatus;
    }

    public int getNocNameForType() {
        return nocNameForType;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setNocNameForType(int nocNameForType) {
        this.nocNameForType = nocNameForType;
    }

    public int getNocPraiseNum() {
        return nocPraiseNum;
    }

    public void setNocPraiseNum(int nocPraiseNum) {
        this.nocPraiseNum = nocPraiseNum;
    }

    public String getVuid() {
        return vuid;
    }

    public void setVuid(String vuid) {
        this.vuid = vuid;
    }

    public String getNocCreateTime() {
        return nocCreateTime;
    }

    public void setNocCreateTime(String nocCreateTime) {
        this.nocCreateTime = nocCreateTime;
    }

    public String getNocRemark() {
        return nocRemark;
    }

    public void setNocRemark(String nocRemark) {
        this.nocRemark = nocRemark;
    }

    public String getNocEntryNum() {
        return nocEntryNum;
    }

    public void setNocEntryNum(String nocEntryNum) {
        this.nocEntryNum = nocEntryNum;
    }

    public String getNocOrgName() {
        return nocOrgName;
    }

    public void setNocOrgName(String nocOrgName) {
        this.nocOrgName = nocOrgName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getType() {
        return Type;
    }

    public void setNewResourceInfoTag(NewResourceInfoTag newResourceInfoTag) {
        this.newResourceInfoTag = newResourceInfoTag;
    }

    public NewResourceInfoTag getNewResourceInfoTag() {
        return newResourceInfoTag;
    }

    public void setDescription(String description){
        this.Description=description;
    }
    public String getDescription(){
        return Description;
    }
//    public void setNewResourceInfo(NewResourceInfo newResourceInfo) {
//        this.newResourceInfo = newResourceInfo;
//    }
//
//    public NewResourceInfo getNewResourceInfo() {
//        return newResourceInfo;
//    }

    public void setHasCollected(boolean hasCollected) {
        this.hasCollected = hasCollected;
    }

    public boolean isHasCollected() {
        return hasCollected;
    }

    public void setHasSplitData(boolean hasSplitData) {
        this.hasSplitData = hasSplitData;
    }

    public boolean isHasSplitData() {
        return hasSplitData;
    }

    public MediaInfo() {
    }

    public MediaInfo(String title, String path, String thumbnail, long duration, long createTime, int mediaType) {
        this.title = title;
        this.path = path;
        this.thumbnail = thumbnail;
        this.duration = duration;
        this.createTime = createTime;
        this.mediaType = mediaType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public String getShareAddress() {
        return shareAddress;
    }

    public void setShareAddress(String shareAddress) {
        this.shareAddress = shareAddress;
    }

//    public NewResourceInfo getNewResourceInfo() {
//        return newResourceInfo;
//    }
//
//    public void setNewResourceInfo(NewResourceInfo newResourceInfo) {
//        this.newResourceInfo = newResourceInfo;
//    }


    public LocalCourseInfo getLocalCourseInfo() {
        return localCourseInfo;
    }

    public void setLocalCourseInfo(LocalCourseInfo localCourseInfo) {
        this.localCourseInfo = localCourseInfo;
    }

    public CourseInfo getCourseInfo() {
        return courseInfo;
    }

    public void setCourseInfo(CourseInfo courseInfo) {
        this.courseInfo = courseInfo;
    }

    public String getMicroId() {
        return microId;
    }

    public void setMicroId(String microId) {
        this.microId = microId;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public MediaDTO toMediaDTO() {
        return new MediaDTO(title, path, thumbnail, duration, createTime, mediaType);
    }

    public SharedResource getSharedResource() {
        SharedResource resource = new SharedResource();
        int resType = resourceType % ResType.RES_TYPE_BASE;
        if (resType == ResType.RES_TYPE_ONEPAGE) {
            resource.setType(SharedResource.RESOURCE_TYPE_FILE);
        } else if (resType == ResType.RES_TYPE_COURSE || resType == ResType.RES_TYPE_COURSE_SPEAKER) {
            resource.setType(SharedResource.RESOURCE_TYPE_STREAM);
        } else if(resType == ResType.RES_TYPE_NOTE) {
            resource.setType(SharedResource.RESOURCE_TYPE_NOTE);
        }

        resource.setId(microId);
        resource.setThumbnailUrl(thumbnail);
        resource.setTitle(title);
        resource.setUrl(path);
        resource.setShareUrl(shareAddress);
        if(courseInfo != null) {
            resource.setAuthorId(courseInfo.getCode());
            resource.setAuthorName(courseInfo.getCreatename());
        }
        resource.setDescription("");
        resource.setPrimaryKey(id);
        if (TextUtils.isEmpty(resource.getDescription())
            || resource.getTitle().equals(resource.getDescription())) {
            resource.setDescription(resource.getAuthorName());
        }
        return resource;
    }


    public NewResourceInfoTag toNewResourceInfoTag(List<MediaInfo> list) {
        NewResourceInfoTag tag = new NewResourceInfoTag();
        tag.setAuthorId(authorId);
        tag.setTitle(title);
        tag.setThumbnail(thumbnail);
        tag.setResourceUrl(resourceUrl);
        tag.setMicroId(microId);
        tag.setResourceType(resourceType);
        if (list != null && list.size() > 0){
            List<NewResourceInfo> newResourceInfoList = new ArrayList<>();
            for (MediaInfo mediaInfo : list){
                if (mediaInfo != null){
                    NewResourceInfo newResourceInfo = new NewResourceInfo();
                    newResourceInfo.setAuthorId(mediaInfo.getAuthorId());
                    newResourceInfo.setTitle(mediaInfo.getTitle());
                    newResourceInfo.setThumbnail(mediaInfo.getThumbnail());
                    newResourceInfo.setResourceUrl(mediaInfo.getResourceUrl());
                    newResourceInfo.setMicroId(mediaInfo.getMicroId());
                    newResourceInfo.setResourceType(mediaInfo.getResourceType());
                    newResourceInfoList.add(newResourceInfo);
                }
            }
            tag.setSplitInfoList(newResourceInfoList);
        }
        return tag;
    }
}
