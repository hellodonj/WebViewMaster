package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2018/1/15.
 * email:man0fchina@foxmail.com
 */

public class WawaLiveListVo extends BaseVo {

    /**
     * Id : 53
     * ExtId : 1861
     * MemberId : 33c8e531-2de8-44ed-a05c-b9fcde0b2722
     * SchoolId : 3583ae72-bfdf-40ef-82e0-9d430b31ce45
     * SchoolName : 青岛两栖蛙蛙信息技术有限公司
     * ClassId : 00000000-0000-0000-0000-000000000000
     * ClassName :
     * AcCreateId : 283220d6-7d77-4b7b-8e74-ca5b86739f7d
     * AcCreateRealName : 张珂
     * AcCreateNickName : zhangke
     * Type : 1
     * LivePrice : 0
     * CourseId : 417
     * Title : 关联课时
     * StartTime : 2018-01-06 15:28:00
     * EndTime : 2018-01-07 15:28:00
     * Intro :
     * CoverUrl : /airclass/283220d6-7d77-4b7b-8e74-ca5b86739f7d/20180105032809/5c96b583-7b5f-4bbc-9af7-b6fb27cac7ca.jpg
     * CoverUrlSrc : http://filetestop.lqwawa.com/uploadfiles//airclass/283220d6-7d77-4b7b-8e74-ca5b86739f7d/20180105032809/5c96b583-7b5f-4bbc-9af7-b6fb27cac7ca.jpg
     * IsEbanshuLive : true
     * EmceeIdStr : 283220d6-7d77-4b7b-8e74-ca5b86739f7d
     * CreateId : 33c8e531-2de8-44ed-a05c-b9fcde0b2722
     * CreateName : 陈鑫
     * CreateTime : 2018-01-15 16:39:55
     * Deleted : false
     * EmceeMemberIdStr : null
     * StartTimeStr : null
     * EndTimeStr : null
     * EmceeList : [{"Id":3266,"ExtId":1861,"Type":0,"MemberId":"283220d6-7d77-4b7b-8e74-ca5b86739f7d","NickName":"zhangke","RealName":"张珂","HeadPicUrl":"20170623095639/283220d6-7d77-4b7b-8e74-ca5b86739f7d/d84ccbe0-0eac-4798-bcc3-f3353f2ef78d.png","SchoolIds":null,"ClassIds":null,"CreateId":"283220d6-7d77-4b7b-8e74-ca5b86739f7d","CreateName":"张珂","CreateTime":"2018-01-05 15:28:49","UpdateId":"283220d6-7d77-4b7b-8e74-ca5b86739f7d","UpdateName":"张珂","UpdateTime":"2018-01-05 15:28:49","Deleted":false}]
     * PublishClassList : []
     * ShareUrl : http://platformtestop.lqwawa.com/mobileHtml/ShareAirClass.aspx?Id=53
     * RoomId : 1002380
     * BrowseCount : 7
     * OnlineNum : null
     * LiveId :
     * DemandId :
     * State : 2
     * IsLiveDeleted : false
     */

    private int Id;
    private int ExtId;
    private String MemberId;
    private String SchoolId;
    private String SchoolName;
    private String ClassId;
    private String ClassName;
    private String AcCreateId;
    private String AcCreateRealName;
    private String AcCreateNickName;
    private int Type;
    private String LivePrice;
    private String CourseId;
    private String Title;
    private String StartTime;
    private String EndTime;
    private String Intro;
    private String CoverUrl;
    private String CoverUrlSrc;
    private boolean IsEbanshuLive;
    private String EmceeIdStr;
    private String CreateId;
    private String CreateName;
    private String CreateTime;
    private boolean Deleted;
    private String EmceeMemberIdStr;
    private String StartTimeStr;
    private String EndTimeStr;
    private String ShareUrl;
    private int RoomId;
    private int BrowseCount;
    private int OnlineNum;
    private String LiveId;
    private String DemandId;
    private int State;
    private boolean IsLiveDeleted;
    private boolean IsPublishClassDeleted;
    private List<EmceeListVo> EmceeList;
    private int joinCount;
    private Object PublishClassList;

    public Object getPublishClassList() {
        return PublishClassList;
    }

    public void setPublishClassList(Object publishClassList) {
        PublishClassList = publishClassList;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getExtId() {
        return ExtId;
    }

    public void setExtId(int ExtId) {
        this.ExtId = ExtId;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String MemberId) {
        this.MemberId = MemberId;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String SchoolId) {
        this.SchoolId = SchoolId;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String SchoolName) {
        this.SchoolName = SchoolName;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String ClassId) {
        this.ClassId = ClassId;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String ClassName) {
        this.ClassName = ClassName;
    }

    public String getAcCreateId() {
        return AcCreateId;
    }

    public void setAcCreateId(String AcCreateId) {
        this.AcCreateId = AcCreateId;
    }

    public String getAcCreateRealName() {
        return AcCreateRealName;
    }

    public void setAcCreateRealName(String AcCreateRealName) {
        this.AcCreateRealName = AcCreateRealName;
    }

    public String getAcCreateNickName() {
        return AcCreateNickName;
    }

    public void setAcCreateNickName(String AcCreateNickName) {
        this.AcCreateNickName = AcCreateNickName;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public String getLivePrice() {
        return LivePrice;
    }

    public void setLivePrice(String LivePrice) {
        this.LivePrice = LivePrice;
    }

    public String getCourseId() {
        return CourseId;
    }

    public void setCourseId(String CourseId) {
        this.CourseId = CourseId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    public String getIntro() {
        return Intro;
    }

    public void setIntro(String Intro) {
        this.Intro = Intro;
    }

    public String getCoverUrl() {
        return CoverUrl;
    }

    public void setCoverUrl(String CoverUrl) {
        this.CoverUrl = CoverUrl;
    }

    public String getCoverUrlSrc() {
        return CoverUrlSrc;
    }

    public void setCoverUrlSrc(String CoverUrlSrc) {
        this.CoverUrlSrc = CoverUrlSrc;
    }

    public boolean isIsEbanshuLive() {
        return IsEbanshuLive;
    }

    public void setIsEbanshuLive(boolean IsEbanshuLive) {
        this.IsEbanshuLive = IsEbanshuLive;
    }

    public String getEmceeIdStr() {
        return EmceeIdStr;
    }

    public void setEmceeIdStr(String EmceeIdStr) {
        this.EmceeIdStr = EmceeIdStr;
    }

    public String getCreateId() {
        return CreateId;
    }

    public void setCreateId(String CreateId) {
        this.CreateId = CreateId;
    }

    public String getCreateName() {
        return CreateName;
    }

    public void setCreateName(String CreateName) {
        this.CreateName = CreateName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean Deleted) {
        this.Deleted = Deleted;
    }

    public String getEmceeMemberIdStr() {
        return EmceeMemberIdStr;
    }

    public void setEmceeMemberIdStr(String EmceeMemberIdStr) {
        this.EmceeMemberIdStr = EmceeMemberIdStr;
    }

    public String getStartTimeStr() {
        return StartTimeStr;
    }

    public void setStartTimeStr(String StartTimeStr) {
        this.StartTimeStr = StartTimeStr;
    }

    public String getEndTimeStr() {
        return EndTimeStr;
    }

    public void setEndTimeStr(String EndTimeStr) {
        this.EndTimeStr = EndTimeStr;
    }

    public String getShareUrl() {
        return ShareUrl;
    }

    public void setShareUrl(String ShareUrl) {
        this.ShareUrl = ShareUrl;
    }

    public int getRoomId() {
        return RoomId;
    }

    public void setRoomId(int RoomId) {
        this.RoomId = RoomId;
    }

    public int getBrowseCount() {
        return BrowseCount;
    }

    public void setBrowseCount(int BrowseCount) {
        this.BrowseCount = BrowseCount;
    }

    public int getOnlineNum() {
        return OnlineNum;
    }

    public void setOnlineNum(int OnlineNum) {
        this.OnlineNum = OnlineNum;
    }

    public String getLiveId() {
        return LiveId;
    }

    public void setLiveId(String LiveId) {
        this.LiveId = LiveId;
    }

    public String getDemandId() {
        return DemandId;
    }

    public void setDemandId(String DemandId) {
        this.DemandId = DemandId;
    }

    public int getState() {
        return State;
    }

    public void setState(int State) {
        this.State = State;
    }

    public boolean isIsLiveDeleted() {
        return IsLiveDeleted;
    }

    public void setIsLiveDeleted(boolean IsLiveDeleted) {
        this.IsLiveDeleted = IsLiveDeleted;
    }

    public boolean isIsPublishClassDeleted() {
        return IsPublishClassDeleted;
    }

    public void setIsPublishClassDeleted(boolean IsPublishClassDeleted) {
        this.IsPublishClassDeleted = IsPublishClassDeleted;
    }

    public List<EmceeListVo> getEmceeList() {
        return EmceeList;
    }

    public void setEmceeList(List<EmceeListVo> EmceeList) {
        this.EmceeList = EmceeList;
    }

    public int getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(int joinCount) {
        this.joinCount = joinCount;
    }

    public LiveVo toLiveVo(){
        LiveVo liveVo = new LiveVo();
        liveVo.setAirLiveId(Id);
        liveVo.setId(String.valueOf(ExtId));
        liveVo.setExtId(ExtId);
        liveVo.setCreateId(MemberId);
        liveVo.setSchoolId(SchoolId);
        liveVo.setSchoolName(SchoolName);
        liveVo.setClassId(ClassId);
        liveVo.setClassName(ClassName);
        liveVo.setAcCreateId(AcCreateId);
        liveVo.setAcCreateRealName(AcCreateRealName);
        liveVo.setAcCreateNickName(AcCreateNickName);
        liveVo.setType(Type);
        liveVo.setPrice(LivePrice);
        liveVo.setPayType((StringUtils.isIntString(LivePrice)
                && Integer.parseInt(LivePrice) > 0) ? 1 : 0);
        liveVo.setCourseId(CourseId);
        liveVo.setTitle(Title);
        liveVo.setStartTime(StartTime);
        liveVo.setEndTime(EndTime);
        liveVo.setIntro(Intro);
        liveVo.setCoverUrl(CoverUrlSrc);
        liveVo.setCoverUrlSrc(CoverUrlSrc);
        liveVo.setIsEbanshuLive(IsEbanshuLive);
        liveVo.setEmceeIds(EmceeIdStr);
        liveVo.setCreateId(CreateId);
        liveVo.setCreateName(CreateName);
        liveVo.setCreateTime(CreateTime);
        liveVo.setIsDelete(Deleted);
        liveVo.setEmceeMemberIdStr(EmceeMemberIdStr);
        liveVo.setStartTimeStr(StartTimeStr);
        liveVo.setEndTimeStr(EndTimeStr);
        liveVo.setShareUrl(ShareUrl);
        liveVo.setRoomId(RoomId);
        liveVo.setBrowseCount(BrowseCount);
        liveVo.setOnlineNum(OnlineNum);
        liveVo.setLiveId(LiveId);
        liveVo.setDemandId(DemandId);
        liveVo.setState(State);
        liveVo.setIsDelete(IsLiveDeleted);
        liveVo.setIsPublishClassDeleted(IsPublishClassDeleted);
        liveVo.setEmceeList(EmceeList);
        liveVo.setJoinCount(joinCount);
        liveVo.setPublishClassList(PublishClassList);
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < getEmceeList().size(); i ++) {
            buffer.append(getEmceeList().get(i).getRealName());
            buffer.append(i >= getEmceeList().size() ? "" : " ");
        }
        liveVo.setEmceeNames(buffer.toString());

        return liveVo;
    }

    public static List<LiveVo> wawaLiveListToLiveList(List<WawaLiveListVo> list){
        List<LiveVo> liveVoList = new ArrayList<>();
        if(list != null && list.size() > 0){
            for(int i = 0; i < list.size(); i++){
                liveVoList.add(list.get(i).toLiveVo());
            }
        }
        return liveVoList;
    }
}
