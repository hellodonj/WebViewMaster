package com.lqwawa.intleducation.factory.data.entity;

import com.alibaba.fastjson.JSON;
import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;

import java.io.Serializable;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 直播数据实体
 * @date 2018/06/02 15:21
 * @history v1.0
 * **********************************
 */
public class LiveEntity extends BaseVo{

    private String AcCreateId;
    private String AcCreateNickName;
    private String AcCreateRealName;
    private String ClassId;
    private String ClassName;
    private String CourseId;
    private String CoverUrl;
    private String CoverUrlSrc;
    private String CreateId;
    private String CreateName;
    private String CreateTime;
    private boolean Deleted;
    private String DemandId;
    private String EndTime;
    private int Id;
    private String Intro;
    private boolean IsEbanshuLive;
    private int JoinCount;
    private String LiveId;
    private String LivePrice;
    private int RoomId;
    private String SchoolId;
    private String SchoolName;
    private String ShareUrl;
    private String StartTime;
    private int State;
    private String Title;
    private int Type;
    private String UpdateId;
    private String UpdateName;
    private String UpdateTime;
    private int BrowseCount;
    private int OnlineNum;
    private int LiveType;
    private String ResTitle;
    private List<EmceeListBean> EmceeList;
    private List<PublishClassListBean> PublishClassList;

    public String getAcCreateId() {
        return AcCreateId;
    }

    public void setAcCreateId(String AcCreateId) {
        this.AcCreateId = AcCreateId;
    }

    public String getAcCreateNickName() {
        return AcCreateNickName;
    }

    public void setAcCreateNickName(String AcCreateNickName) {
        this.AcCreateNickName = AcCreateNickName;
    }

    public String getAcCreateRealName() {
        return AcCreateRealName;
    }

    public void setAcCreateRealName(String AcCreateRealName) {
        this.AcCreateRealName = AcCreateRealName;
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

    public String getCourseId() {
        return CourseId;
    }

    public void setCourseId(String CourseId) {
        this.CourseId = CourseId;
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

    public String getDemandId() {
        return DemandId;
    }

    public void setDemandId(String DemandId) {
        this.DemandId = DemandId;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getIntro() {
        return Intro;
    }

    public void setIntro(String Intro) {
        this.Intro = Intro;
    }

    public boolean isIsEbanshuLive() {
        return IsEbanshuLive;
    }

    public void setIsEbanshuLive(boolean IsEbanshuLive) {
        this.IsEbanshuLive = IsEbanshuLive;
    }

    public int getJoinCount() {
        return JoinCount;
    }

    public void setJoinCount(int JoinCount) {
        this.JoinCount = JoinCount;
    }

    public String getLiveId() {
        return LiveId;
    }

    public void setLiveId(String LiveId) {
        this.LiveId = LiveId;
    }

    public String getLivePrice() {
        return LivePrice;
    }

    public void setLivePrice(String LivePrice) {
        this.LivePrice = LivePrice;
    }

    public int getRoomId() {
        return RoomId;
    }

    public void setRoomId(int RoomId) {
        this.RoomId = RoomId;
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

    public String getShareUrl() {
        return ShareUrl;
    }

    public void setShareUrl(String ShareUrl) {
        this.ShareUrl = ShareUrl;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }

    public int getState() {
        return State;
    }

    public void setState(int State) {
        this.State = State;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public String getUpdateId() {
        return UpdateId;
    }

    public void setUpdateId(String UpdateId) {
        this.UpdateId = UpdateId;
    }

    public String getUpdateName() {
        return UpdateName;
    }

    public void setUpdateName(String UpdateName) {
        this.UpdateName = UpdateName;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String UpdateTime) {
        this.UpdateTime = UpdateTime;
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

    public List<EmceeListBean> getEmceeList() {
        return EmceeList;
    }

    public void setEmceeList(List<EmceeListBean> EmceeList) {
        this.EmceeList = EmceeList;
    }

    public List<PublishClassListBean> getPublishClassList() {
        return PublishClassList;
    }

    public void setPublishClassList(List<PublishClassListBean> PublishClassList) {
        this.PublishClassList = PublishClassList;
    }


    /**
     * LiveEntity 转 LiveVo
     * @return 原先直播对象
     */
    public LiveVo build(){
        return JSON.parseObject(JSON.toJSONString(this),LiveVo.class);
    }

    public static class EmceeListBean implements Serializable{
        private String ClassIds;
        private String CreateId;
        private String CreateName;
        private String CreateTime;
        private boolean Deleted;
        private int ExtId;
        private String HeadPicUrl;
        private int Id;
        private String MemberId;
        private String NickName;
        private String RealName;
        private String SchoolIds;
        private int Type;
        private String UpdateId;
        private String UpdateName;
        private String UpdateTime;

        public String getClassIds() {
            return ClassIds;
        }

        public void setClassIds(String ClassIds) {
            this.ClassIds = ClassIds;
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

        public int getExtId() {
            return ExtId;
        }

        public void setExtId(int ExtId) {
            this.ExtId = ExtId;
        }

        public String getHeadPicUrl() {
            return HeadPicUrl;
        }

        public void setHeadPicUrl(String HeadPicUrl) {
            this.HeadPicUrl = HeadPicUrl;
        }

        public int getId() {
            return Id;
        }

        public void setId(int Id) {
            this.Id = Id;
        }

        public String getMemberId() {
            return MemberId;
        }

        public void setMemberId(String MemberId) {
            this.MemberId = MemberId;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String NickName) {
            this.NickName = NickName;
        }

        public String getRealName() {
            return RealName;
        }

        public void setRealName(String RealName) {
            this.RealName = RealName;
        }

        public String getSchoolIds() {
            return SchoolIds;
        }

        public void setSchoolIds(String SchoolIds) {
            this.SchoolIds = SchoolIds;
        }

        public int getType() {
            return Type;
        }

        public void setType(int Type) {
            this.Type = Type;
        }

        public String getUpdateId() {
            return UpdateId;
        }

        public void setUpdateId(String UpdateId) {
            this.UpdateId = UpdateId;
        }

        public String getUpdateName() {
            return UpdateName;
        }

        public void setUpdateName(String UpdateName) {
            this.UpdateName = UpdateName;
        }

        public String getUpdateTime() {
            return UpdateTime;
        }

        public void setUpdateTime(String UpdateTime) {
            this.UpdateTime = UpdateTime;
        }


    }

    public static class PublishClassListBean implements Serializable {
        private String ClassId;
        private String ClassName;
        private String CreateId;
        private String CreateName;
        private String CreateTime;
        private boolean Deleted;
        private int ExtId;
        private int Id;
        private boolean IsBelong;
        private String SchoolId;
        private String SchoolName;
        private String UpdateId;
        private String UpdateName;
        private String UpdateTime;

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

        public int getExtId() {
            return ExtId;
        }

        public void setExtId(int ExtId) {
            this.ExtId = ExtId;
        }

        public int getId() {
            return Id;
        }

        public void setId(int Id) {
            this.Id = Id;
        }

        public boolean isIsBelong() {
            return IsBelong;
        }

        public void setIsBelong(boolean IsBelong) {
            this.IsBelong = IsBelong;
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

        public String getUpdateId() {
            return UpdateId;
        }

        public void setUpdateId(String UpdateId) {
            this.UpdateId = UpdateId;
        }

        public String getUpdateName() {
            return UpdateName;
        }

        public void setUpdateName(String UpdateName) {
            this.UpdateName = UpdateName;
        }

        public String getUpdateTime() {
            return UpdateTime;
        }

        public void setUpdateTime(String UpdateTime) {
            this.UpdateTime = UpdateTime;
        }
    }
}
