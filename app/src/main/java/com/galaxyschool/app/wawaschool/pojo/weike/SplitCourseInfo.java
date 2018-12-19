package com.galaxyschool.app.wawaschool.pojo.weike;

import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;

/**
 * Created by wangchao on 1/21/16.
 */
public class SplitCourseInfo {
    int id;
    int parentId;
    String createTime;
    String playUrl;
    String shareUrl;
    String subResName;
    String fullResName;
    int subResType;
    int screenType;
    String thumbUrl;
    String createName;
    String memberId;
    int fileSize;

    public SplitCourseInfo() {
    }

    public SplitCourseInfo(int id, int parentId, String createTime, String playUrl, String shareUrl,
                           String subResName, String fullResName, int subResType, int screenType,
                           String thumbUrl, String createName, String memberId) {
        this.id = id;
        this.parentId = parentId;
        this.createTime = createTime;
        this.playUrl = playUrl;
        this.shareUrl = shareUrl;
        this.subResName = subResName;
        this.fullResName = fullResName;
        this.subResType = subResType;
        this.screenType = screenType;
        this.thumbUrl = thumbUrl;
        this.createName = createName;
        this.memberId = memberId;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public String getSubResName() {
        return subResName;
    }

    public void setSubResName(String subResName) {
        this.subResName = subResName;
    }

    public String getFullResName() {
        return fullResName;
    }

    public void setFullResName(String fullResName) {
        this.fullResName = fullResName;
    }

    public int getSubResType() {
        return subResType;
    }

    public void setSubResType(int subResType) {
        this.subResType = subResType;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public CourseInfo getCourseInfo() {
        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setNickname(fullResName);
        courseInfo.setId(id);
        courseInfo.setImgurl(thumbUrl);
        courseInfo.setCreatename(createName);
        courseInfo.setCode(memberId);
        courseInfo.setResourceurl(playUrl);
        courseInfo.setPrimaryKey("");
//        courseInfo.setResourceType(subResType);
        courseInfo.setType(subResType);
        courseInfo.setCreatetime(createTime);
        courseInfo.setUpdateTime(createTime);
        courseInfo.setShareAddress(shareUrl);
        courseInfo.setIsSlide(false);
        int resType = subResType % ResType.RES_TYPE_BASE;
        if(resType == ResType.RES_TYPE_COURSE_SPEAKER) {
            courseInfo.setIsSlide(true);
        }
        courseInfo.setIsSplitCourse(true);
        courseInfo.setScreenType(screenType);
        courseInfo.setSize(fileSize);
        courseInfo.setParentId(String.valueOf(parentId));
        return courseInfo;
    }

    public NewResourceInfo getNewResourceInfo() {
        NewResourceInfo info = new NewResourceInfo();
        info.setTitle(fullResName);
        info.setMicroId(String.valueOf(id));
        info.setThumbnail(thumbUrl);
        info.setAuthorName(createName);
        info.setAuthorId(memberId);
        info.setResourceUrl(playUrl);
        info.setUpdatedTime(createTime);
        info.setShareAddress(shareUrl);
        info.setResourceType(subResType);
        info.setScreenType(screenType);
        info.setFileSize(fileSize);
        info.setParentId(String.valueOf(parentId));
        return info;
    }

    public CourseData getCourseData() {
        CourseData data = new CourseData();
        data.nickname = fullResName;
        data.id = id;
        data.thumbnailurl = thumbUrl;
        data.createname = createName;
        data.code = memberId;
        data.resourceurl = playUrl;
        data.createtime = createTime;
        data.shareurl = shareUrl;
        data.type = subResType;
        data.screentype = screenType;
        data.size = fileSize;
        data.parentid = parentId;

        return data;
    }
}
