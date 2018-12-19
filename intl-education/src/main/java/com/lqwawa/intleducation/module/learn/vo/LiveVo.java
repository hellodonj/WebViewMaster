package com.lqwawa.intleducation.module.learn.vo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.user.vo.MyOrderVo;

import java.util.List;

/**
 * Created by XChen on 2017/11/27.
 * email:man0fchina@foxmail.com
 */

public class LiveVo extends BaseVo {
    private static final long serialVersionUID = 5144359315666168115L;
    public static final int FROM_AIR_CLASSROOM = 0;
    public static final int FROM_MOOC = 1;
    /**
     * id : 620
     * state : 1
     * startTime : 2017-08-07 16:26:00
     * order : null
     * courseId : 418
     * createId : 527f4621-1d28-4c0a-9cd5-11ddb650f15d
     * endTime : null
     * courseLiveId : null
     * createName : 刘佳默
     * emceeIds : 527f4621-1d28-4c0a-9cd5-11ddb650f15d
     * title : TEST
     * coverUrl : http://file.lqwawa.com/uploadfiles//airclass/527f4621-1d28-4c0a-9cd5-11ddb650f15d/20170807042243/109d2f3f-15e1-40e9-968c-113815f8701d.jpg
     * schoolId : bfbba4e6-c98a-4160-bca4-540087fb1d89
     * schoolName : 两栖蛙蛙体验学校
     * leUuid : b68e945493
     * leVuid :
     * leAcid : A20170807000007x
     * intro : CCCCCC
     * acCreateId : null
     * emceeNames : 刘佳默
     * shareUrl=http://platformtestop.lqwawa.com/mobileHtml/ShareAirClass.aspx?Id=857
     * browseCount=0
     */

    private String id;
    private int AirLiveId;//空中课堂直播对象键值
    private int    state;
    private int    browseCount;
    private String startTime;
    private String order;
    private String courseId;
    private String courseIds;
    private String createId;
    private String endTime;
    private String courseLiveId;
    private String createName;
    private String emceeIds;
    private String title;
    private String coverUrl;
    private String schoolId;
    private String schoolName;
    private String leUuid;
    private String leVuid;
    private String leAcid;
    private String intro;
    private String acCreateId;
    private String emceeNames;
    private String shareUrl;
    private String price;
    private int    payType;
    private boolean isDelete;
    private boolean isEbanshuLive;
    private int     joinCount;
    private int ExtId;
    private String ClassId;
    private String ClassName;
    private String CreateRealName;
    private String CreateNickName;
    private int Type;
    private String CoverUrlSrc;
    private String CreateTime;
    private int RoomId;
    private int OnlineNum;
    private String LiveId;
    private String DemandId;
    private String StartTimeStr;
    private String EndTimeStr;
    private String sPublishClassDeleted;
    private List<EmceeListVo> EmceeList;
    private String AcCreateRealName;
    private String AcCreateNickName;
    private String EmceeMemberIdStr;
    private boolean IsPublishClassDeleted;
    private Object PublishClassList;
    private int LiveType;
    private String ResTitle;

    public int getAirLiveId() {
        return AirLiveId;
    }

    public void setAirLiveId(int airClassroomId) {
        AirLiveId = airClassroomId;
    }

    public Object getPublishClassList() {
        return PublishClassList;
    }

    public List<PublishClassVo> getPublishClassVoList(){
        if (PublishClassList != null){
            List<PublishClassVo> publishClasses = JSONObject.parseArray(PublishClassList.toString(), PublishClassVo.class);
            return publishClasses;
        }
        return null;
    }

    public LiveVo(){
        Type = FROM_MOOC;
    }

    public void setPublishClassList(Object publishClassList) {
        PublishClassList = publishClassList;
    }

    public String getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(String courseIds) {
        this.courseIds = courseIds;
    }

    public int getBrowseCount() {
        return browseCount;
    }

    public void setBrowseCount(int browseCount) {
        this.browseCount = browseCount;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCourseLiveId() {
        return courseLiveId;
    }

    public void setCourseLiveId(String courseLiveId) {
        this.courseLiveId = courseLiveId;
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

    public String getLeUuid() {
        return leUuid;
    }

    public void setLeUuid(String leUuid) {
        this.leUuid = leUuid;
    }

    public String getLeVuid() {
        return leVuid;
    }

    public void setLeVuid(String leVuid) {
        this.leVuid = leVuid;
    }

    public String getLeAcid() {
        return leAcid;
    }

    public void setLeAcid(String leAcid) {
        this.leAcid = leAcid;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
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

    public String getEmceeNames() {
        return emceeNames;
    }

    public void setEmceeNames(String emceeNames) {
        this.emceeNames = emceeNames;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public boolean isIsDelete() {
        return isDelete || IsPublishClassDeleted;
    }

    public void setIsDelete(boolean delete) {
        isDelete = delete;
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

    public int getExtId() {
        return ExtId;
    }

    public void setExtId(int extId) {
        ExtId = extId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isEbanshuLive() {
        return isEbanshuLive;
    }

    public void setEbanshuLive(boolean ebanshuLive) {
        isEbanshuLive = ebanshuLive;
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

    public String getCreateRealName() {
        return CreateRealName;
    }

    public void setCreateRealName(String createRealName) {
        CreateRealName = createRealName;
    }

    public String getCreateNickName() {
        return CreateNickName;
    }

    public void setCreateNickName(String createNickName) {
        CreateNickName = createNickName;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getCoverUrlSrc() {
        return CoverUrlSrc;
    }

    public void setCoverUrlSrc(String coverUrlSrc) {
        CoverUrlSrc = coverUrlSrc;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public int getRoomId() {
        return RoomId;
    }

    public void setRoomId(int roomId) {
        RoomId = roomId;
    }

    public int getOnlineNum() {
        return OnlineNum;
    }

    public void setOnlineNum(int onlineNum) {
        OnlineNum = onlineNum;
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

    public String getsPublishClassDeleted() {
        return sPublishClassDeleted;
    }

    public void setsPublishClassDeleted(String sPublishClassDeleted) {
        this.sPublishClassDeleted = sPublishClassDeleted;
    }

    public List<EmceeListVo> getEmceeList() {
        return EmceeList;
    }

    public void setEmceeList(List<EmceeListVo> emceeList) {
        EmceeList = emceeList;
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

    public String getEmceeMemberIdStr() {
        return EmceeMemberIdStr;
    }

    public void setEmceeMemberIdStr(String emceeMemberIdStr) {
        EmceeMemberIdStr = emceeMemberIdStr;
    }

    public boolean getIsPublishClassDeleted() {
        return IsPublishClassDeleted;
    }

    public void setIsPublishClassDeleted(boolean isPublishClassDeleted) {
        IsPublishClassDeleted = isPublishClassDeleted;
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

    public static LiveVo fromMyOrder(MyOrderVo orderVo){
        LiveVo vo = new LiveVo();
        vo.setSchoolId(orderVo.getOrganId());
        vo.setId("" + orderVo.getCourseId());
        vo.setCoverUrl(orderVo.getThumbnailUrl());
        vo.setTitle(orderVo.getCourseName());
        vo.setCreateName(orderVo.getTeachersName());
        vo.setPayType(orderVo.getPayType());
        vo.setPrice(orderVo.getPrice());
        vo.setEmceeIds(orderVo.getTeachersId());
        vo.setEmceeNames(orderVo.getTeachersName());
        vo.setSchoolName(orderVo.getOrganName());
        return vo;
    }

    public boolean isFromMooc(){
        return Type == FROM_MOOC;
    }
}
