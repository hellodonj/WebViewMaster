package com.lqwawa.intleducation.module.discovery.vo;

import android.text.TextUtils;

import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2017/11/22.
 * email:man0fchina@foxmail.com
 */

public class LiveInfoVo extends BaseVo {

    /**
     * id : 1003
     * state : 0
     * startTime : 2017-11-15 15:19:00
     * browseCount : 0
     * emceeNames : 张珂
     * intro : 添加直播练习
     * shareUrl : http://platformtestop.lqwawa.com/mobileHtml/ShareAirClass.aspx?Id=1003
     * endTime : 2017-11-23 15:19:00
     * order : null
     * courseId : 0
     * createId : 283220d6-7d77-4b7b-8e74-ca5b86739f7d
     * createName : 张珂
     * emceeIds : 283220d6-7d77-4b7b-8e74-ca5b86739f7d
     * title : 直播添加练习
     * coverUrl : http://filetestop.lqwawa.com/uploadfiles//airclass/283220d6-7d77-4b7b-8e74-ca5b86739f7d/20171113031947/8714a091-95a7-4bc9-a316-2acbf64a7c51.png
     * schoolId : 3583ae72-bfdf-40ef-82e0-9d430b31ce45
     * schoolName : 青岛两栖蛙蛙信息技术有限公司
     * courseLiveId : null
     * courseIds : null
     * leVuid :
     * leUuid : b68e945493
     * leAcid :
     * acCreateId : null
     * sysType : 0
     * payType : 0
     * price : 0
     */

    private String id;
    private int state;
    private String startTime;
    private int browseCount;
    private String emceeNames;
    private String intro;
    private String shareUrl;
    private String endTime;
    private String order;
    private String courseId;
    private String createId;
    private String createName;
    private String emceeIds;
    private String title;
    private String coverUrl;
    private String schoolId;
    private String schoolName;
    private String courseLiveId;
    private String courseIds;
    private String leVuid;
    private String leUuid;
    private String leAcid;
    private String acCreateId;
    private int sysType;
    private int payType;
    private int price;
    private boolean isDelete;
    private int roomId;
    private boolean isEbanshuLive;
    private int joinCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getBrowseCount() {
        return browseCount;
    }

    public void setBrowseCount(int browseCount) {
        this.browseCount = browseCount;
    }

    public String getEmceeNames() {
        return emceeNames;
    }

    public void setEmceeNames(String emceeNames) {
        this.emceeNames = emceeNames;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getEmceeIds() {
        return emceeIds;
    }

    public void setEmceeIds(String emceeIds) {
        this.emceeIds = emceeIds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getCourseLiveId() {
        return courseLiveId;
    }

    public void setCourseLiveId(String courseLiveId) {
        this.courseLiveId = courseLiveId;
    }

    public String getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(String courseIds) {
        this.courseIds = courseIds;
    }

    public String getLeVuid() {
        return leVuid;
    }

    public void setLeVuid(String leVuid) {
        this.leVuid = leVuid;
    }

    public String getLeUuid() {
        return leUuid;
    }

    public void setLeUuid(String leUuid) {
        this.leUuid = leUuid;
    }

    public String getLeAcid() {
        return leAcid;
    }

    public void setLeAcid(String leAcid) {
        this.leAcid = leAcid;
    }

    public String getAcCreateId() {
        if(StringUtils.isValidString(acCreateId)) {
            return acCreateId;
        }else {
            return createId;
        }
    }

    public void setAcCreateId(String acCreateId) {
        this.acCreateId = acCreateId;
    }

    public int getSysType() {
        return sysType;
    }

    public void setSysType(int sysType) {
        this.sysType = sysType;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean delete) {
        isDelete = delete;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public boolean isIsEbanshuLive() {
        return isEbanshuLive;
    }

    public void setIsEbanshuLive(boolean ebanshuLive) {
        isEbanshuLive = ebanshuLive;
    }

    public int getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(int joinCount) {
        this.joinCount = joinCount;
    }
}
