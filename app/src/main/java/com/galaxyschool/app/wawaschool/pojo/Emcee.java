package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;
import java.util.List;

public class Emcee implements Serializable {
    private int Id;

    private String SchoolId;

    private String SchoolName;

    private String ClassId;

    private String ClassName;

    private String Title;

    private String AcCreateId;

    private String AcCreateRealName;

    private String AcCreateNickName;

    private String StartTime;

    private String EndTime;

    private String Intro;

    private String CoverUrl;

    private int State;

    private String LiveId;

    private String DemandId;

    private String CreateId;

    private String CreateName;

    private String CreateTime;

    private String UpdateId;

    private String UpdateName;

    private String UpdateTime;

    private boolean Deleted;

    private String EmceeMemberIdStr;

    private String StartTimeStr;

    private String EndTimeStr;



    private String courseId;

    private String courseIds;


    private List<com.galaxyschool.app.wawaschool.pojo.EmceeList> EmceeList;

    private List<PublishClass> PublishClassList;
    private String ShareUrl;
    private String emceeNames;

    //浏览数
    private int BrowseCount;
    //参加人数
    private int joinCount;

    private int payType;

    private int price;
    //是不是板书直播
    private boolean IsEbanshuLive;
    //板书直播的教室id
    private int RoomId;
    //实时的在线人数
    private int OnlineNum;
    //是否加入到我的直播
    private boolean addMyLived;
    private int LiveType;//校本资源库的视频
    private String ResTitle;//资源的title

    public int getLiveType() {
        return LiveType;
    }

    public void setLiveType(int liveType) {
        LiveType = liveType;
    }

    public String getResTitle() {
        return ResTitle;
    }

    public void setResTitle(String resTitle) {
        ResTitle = resTitle;
    }

    public boolean isAddMyLived() {
        return addMyLived;
    }

    public void setAddMyLived(boolean addMyLived) {
        this.addMyLived = addMyLived;
    }

    public int getOnlineNum() {
        return OnlineNum;
    }

    public void setOnlineNum(int onlineNum) {
        OnlineNum = onlineNum;
    }

    public boolean isEbanshuLive() {
        return IsEbanshuLive;
    }

    public void setIsEbanshuLive(boolean ebanshuLive) {
        IsEbanshuLive = ebanshuLive;
    }

    public int getRoomId() {
        return RoomId;
    }

    public void setRoomId(int roomId) {
        RoomId = roomId;
    }

    private boolean fromMyLive;

    public boolean isFromMyLive() {
        return fromMyLive;
    }

    public void setFromMyLive(boolean fromMyLive) {
        this.fromMyLive = fromMyLive;
    }

    public int getBrowseCount() {
        return BrowseCount;
    }

    public void setBrowseCount(int browseCount) {
        BrowseCount = browseCount;
    }


    public String getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(String courseIds) {
        this.courseIds = courseIds;
    }

    public String getCourseId() {

        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }


    public String getEmceeNames() {
        return emceeNames;
    }

    public void setEmceeNames(String emceeNames) {
        this.emceeNames = emceeNames;
    }

    public Emcee(){

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

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAcCreateId() {
        return AcCreateId;
    }

    public void setAcCreateId(String acCreateId) {
        AcCreateId = acCreateId;
    }

    public String getAcCreateRealName() {
        return AcCreateRealName;
    }

    public void setAcCreateRealName(String acCreateRealName) {
        AcCreateRealName = acCreateRealName;
    }

    public String getAcCreateNickName() {
        return AcCreateNickName;
    }

    public void setAcCreateNickName(String acCreateNickName) {
        AcCreateNickName = acCreateNickName;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getIntro() {
        return Intro;
    }

    public void setIntro(String intro) {
        Intro = intro;
    }

    public String getCoverUrl() {
        return CoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        CoverUrl = coverUrl;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public String getLiveId() {
        return LiveId;
    }

    public void setLiveId(String liveId) {
        LiveId = liveId;
    }

    public String getDemandId() {
        return DemandId;
    }

    public void setDemandId(String demandId) {
        DemandId = demandId;
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

    public void setDeleted(boolean deleted) {
        Deleted = deleted;
    }

    public String getEmceeMemberIdStr() {
        return EmceeMemberIdStr;
    }

    public void setEmceeMemberIdStr(String emceeMemberIdStr) {
        EmceeMemberIdStr = emceeMemberIdStr;
    }

    public String getStartTimeStr() {
        return StartTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        StartTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return EndTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        EndTimeStr = endTimeStr;
    }

    public List<com.galaxyschool.app.wawaschool.pojo.EmceeList> getEmceeList() {
        return EmceeList;
    }

    public void setEmceeList(List<com.galaxyschool.app.wawaschool.pojo.EmceeList> emceeList) {
        EmceeList = emceeList;
    }

    public List<PublishClass> getPublishClassList() {
        return PublishClassList;
    }

    public void setPublishClassList(List<PublishClass> publishClassList) {
        PublishClassList = publishClassList;
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

    public int getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(int joinCount) {
        this.joinCount = joinCount;
    }
}
