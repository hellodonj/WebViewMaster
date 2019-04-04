package com.galaxyschool.app.wawaschool.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/10/16 17:13
 * 描    述：通用的数据bean
 * 修订历史：
 * ================================================
 */

public class CheckMarkInfo implements Serializable {

    /**
     * Model : [{"Id":5,"TaskId":50,"CommitTaskId":2,"ResId":"23698-18","ResUrl":"http://resop.lqwawa.com/course/xiaolaba/2016/10/20/a8d4d261-d9e7-4967-83ed-ba15418401a0/head.jpg","IsTeacher":true,"CreateId":"bf6da6a1-45a5-482f-9348-5c539a00ac59","CreateName":"s5000","HeadPicUrl":"http://filetestop.lqwawa.com/UploadFiles/20160818041230/bf6da6a1-45a5-482f-9348-5c539a00ac59/7d316c72-65a1-4b50-8297-8da518d8d0ed.jpg","CreateTime":"2017-10-14 17:00:07"},{"Id":4,"TaskId":50,"CommitTaskId":2,"ResId":"23698-18","ResUrl":"http://resop.lqwawa.com/course/xiaolaba/2016/10/20/a8d4d261-d9e7-4967-83ed-ba15418401a0/head.jpg","IsTeacher":false,"CreateId":"bf6da6a1-45a5-482f-9348-5c539a00ac59","CreateName":"s5000","HeadPicUrl":"http://filetestop.lqwawa.com/UploadFiles/20160818041230/bf6da6a1-45a5-482f-9348-5c539a00ac59/7d316c72-65a1-4b50-8297-8da518d8d0ed.jpg","CreateTime":"2017-10-14 16:59:57"},{"Id":2,"TaskId":50,"CommitTaskId":2,"ResId":"23698-18","ResUrl":"http://resop.lqwawa.com/course/xiaolaba/2016/10/20/a8d4d261-d9e7-4967-83ed-ba15418401a0/head.jpg","IsTeacher":false,"CreateId":"bf6da6a1-45a5-482f-9348-5c539a00ac59","CreateName":"s5000","HeadPicUrl":"http://filetestop.lqwawa.com/UploadFiles/20160818041230/bf6da6a1-45a5-482f-9348-5c539a00ac59/7d316c72-65a1-4b50-8297-8da518d8d0ed.jpg","CreateTime":"2017-10-14 16:37:22"},{"Id":1,"TaskId":50,"CommitTaskId":2,"ResId":"23698-18","ResUrl":"http://resop.lqwawa.com/course/xiaolaba/2016/10/20/a8d4d261-d9e7-4967-83ed-ba15418401a0/head.jpg","IsTeacher":false,"CreateId":"bf6da6a1-45a5-482f-9348-5c539a00ac59","CreateName":"s5000","HeadPicUrl":"http://filetestop.lqwawa.com/UploadFiles/20160818041230/bf6da6a1-45a5-482f-9348-5c539a00ac59/7d316c72-65a1-4b50-8297-8da518d8d0ed.jpg","CreateTime":"2017-10-14 16:36:58"}]
     * ErrorCode : 0
     * ErrorMessage : null
     */

    private int ErrorCode;
    private String ErrorMessage;
    private List<ModelBean> Model;
    private List<ModelBean> data;
    private boolean HasError;
    /**
     * 天赋密码分享url
     */
    private String shareUrl;

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public List<ModelBean> getData() {
        return data;
    }

    public void setData(List<ModelBean> data) {
        this.data = data;
    }

    public boolean isHasError() {
        return HasError;
    }

    public void setIsHasError(boolean hasError) {
        HasError = hasError;
    }

    public int getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(int ErrorCode) {
        this.ErrorCode = ErrorCode;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String ErrorMessage) {
        this.ErrorMessage = ErrorMessage;
    }

    public List<ModelBean> getModel() {
        return Model;
    }

    public void setModel(List<ModelBean> Model) {
        this.Model = Model;
    }

    public static class ModelBean implements Parcelable {
        /**
         * Id : 5
         * TaskId : 50
         * CommitTaskId : 2
         * ResId : 23698-18
         * ResUrl : http://resop.lqwawa.com/course/xiaolaba/2016/10/20/a8d4d261-d9e7-4967-83ed-ba15418401a0/head.jpg
         * IsTeacher : true
         * CreateId : bf6da6a1-45a5-482f-9348-5c539a00ac59
         * CreateName : s5000
         * HeadPicUrl : http://filetestop.lqwawa.com/UploadFiles/20160818041230/bf6da6a1-45a5-482f-9348-5c539a00ac59/7d316c72-65a1-4b50-8297-8da518d8d0ed.jpg
         * CreateTime : 2017-10-14 17:00:07
         */

        private int Id;
        private int TaskId;
        private int CommitTaskId;
        private String ResId;
        private String ResUrl;
        private boolean IsTeacher;
        private String CreateId;
        private String CreateName;
        private String HeadPicUrl;
        private String CreateTime;
        private int ReviewFlag;

        /**
         * 班级分组  http://121.42.155.135:8080/
         */
        private int GroupId;
        private String SchoolId;
        private String SchoolName;
        private String ClassName;
        private String LogoUrl;
        private String ClassId;
        private String GroupName;
        private String UpdateId;
        private String UpdateTime;
        private String ClassHeadPicUrl;
        private boolean Deleted;
        private List<ModelBean> StudyGroupList;

        private String ChildId;
        private String RealName;
        private String NickName;
        /**
         *  thumbArray :天赋密码图片集合
         */
        private List<String> thumbArray;
        /**
         *  type :天赋密码数据类型
         */
        private int type;
        /**
         * 天赋密码获取解读对比resType
         */
        private String resType;

        private boolean showDeleted;
        private boolean IsOnlineSchool;
        private String MemberId;
        private int SubmitRole;
        private String SubmitTime;

        public String getMemberId() {
            return MemberId;
        }

        public void setMemberId(String memberId) {
            MemberId = memberId;
        }

        public int getSubmitRole() {
            return SubmitRole;
        }

        public void setSubmitRole(int submitRole) {
            SubmitRole = submitRole;
        }

        public String getSubmitTime() {
            return SubmitTime;
        }

        public void setSubmitTime(String submitTime) {
            SubmitTime = submitTime;
        }

        public int getReviewFlag() {
            return ReviewFlag;
        }

        public void setReviewFlag(int reviewFlag) {
            ReviewFlag = reviewFlag;
        }

        public boolean isOnlineSchool() {
            return IsOnlineSchool;
        }

        public void setIsOnlineSchool(boolean onlineSchool) {
            IsOnlineSchool = onlineSchool;
        }

        public boolean isShowDeleted() {
            return showDeleted;
        }

        public void setShowDeleted(boolean showDeleted) {
            this.showDeleted = showDeleted;
        }

        public String getResType() {
            return resType;
        }

        public void setResType(String resType) {
            this.resType = resType;
        }

        public List<String> getThumbArray() {
            return thumbArray;
        }

        public void setThumbArray(List<String> thumbArray) {
            this.thumbArray = thumbArray;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String nickName) {
            NickName = nickName;
        }

        public String getChildId() {
            return ChildId;
        }

        public void setChildId(String childId) {
            ChildId = childId;
        }

        public String getRealName() {
            return RealName;
        }

        public void setRealName(String realName) {
            RealName = realName;
        }

        public String getClassHeadPicUrl() {
            return ClassHeadPicUrl;
        }

        public void setClassHeadPicUrl(String classHeadPicUrl) {
            ClassHeadPicUrl = classHeadPicUrl;
        }

        public String getClassName() {
            return ClassName;
        }

        public void setClassName(String className) {
            ClassName = className;
        }

        public String getSchoolName() {
            return SchoolName;
        }

        public void setSchoolName(String schoolName) {
            SchoolName = schoolName;
        }

        public String getLogoUrl() {
            return LogoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            LogoUrl = logoUrl;
        }

        public List<ModelBean> getStudyGroupList() {
            return StudyGroupList;
        }

        public void setStudyGroupList(List<ModelBean> studyGroupList) {
            StudyGroupList = studyGroupList;
        }

        public boolean isTeacher() {
            return IsTeacher;
        }

        public void setTeacher(boolean teacher) {
            IsTeacher = teacher;
        }

        public int getGroupId() {
            return GroupId;
        }

        public void setGroupId(int groupId) {
            GroupId = groupId;
        }

        public String getSchoolId() {
            return SchoolId;
        }

        public void setSchoolId(String schoolId) {
            SchoolId = schoolId;
        }

        public String getClassId() {
            return ClassId;
        }

        public void setClassId(String classId) {
            ClassId = classId;
        }

        public String getGroupName() {
            return GroupName;
        }

        public void setGroupName(String groupName) {
            GroupName = groupName;
        }

        public String getUpdateId() {
            return UpdateId;
        }

        public void setUpdateId(String updateId) {
            UpdateId = updateId;
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

        public int getId() {
            return Id;
        }

        public void setId(int Id) {
            this.Id = Id;
        }

        public int getTaskId() {
            return TaskId;
        }

        public void setTaskId(int TaskId) {
            this.TaskId = TaskId;
        }

        public int getCommitTaskId() {
            return CommitTaskId;
        }

        public void setCommitTaskId(int CommitTaskId) {
            this.CommitTaskId = CommitTaskId;
        }

        public String getResId() {
            return ResId;
        }

        public void setResId(String ResId) {
            this.ResId = ResId;
        }

        public String getResUrl() {
            return ResUrl;
        }

        public void setResUrl(String ResUrl) {
            this.ResUrl = ResUrl;
        }

        public boolean isIsTeacher() {
            return IsTeacher;
        }

        public void setIsTeacher(boolean IsTeacher) {
            this.IsTeacher = IsTeacher;
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

        public String getHeadPicUrl() {
            return HeadPicUrl;
        }

        public void setHeadPicUrl(String HeadPicUrl) {
            this.HeadPicUrl = HeadPicUrl;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public ModelBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.Id);
            dest.writeInt(this.TaskId);
            dest.writeInt(this.CommitTaskId);
            dest.writeString(this.ResId);
            dest.writeString(this.ResUrl);
            dest.writeByte(this.IsTeacher ? (byte) 1 : (byte) 0);
            dest.writeString(this.CreateId);
            dest.writeString(this.CreateName);
            dest.writeString(this.HeadPicUrl);
            dest.writeString(this.CreateTime);
            dest.writeInt(this.GroupId);
            dest.writeString(this.SchoolId);
            dest.writeString(this.SchoolName);
            dest.writeString(this.ClassName);
            dest.writeString(this.LogoUrl);
            dest.writeString(this.ClassId);
            dest.writeString(this.GroupName);
            dest.writeString(this.UpdateId);
            dest.writeString(this.UpdateTime);
            dest.writeString(this.ClassHeadPicUrl);
            dest.writeByte(this.Deleted ? (byte) 1 : (byte) 0);
            dest.writeTypedList(this.StudyGroupList);
            dest.writeString(this.ChildId);
            dest.writeString(this.RealName);
            dest.writeString(this.NickName);
            dest.writeStringList(this.thumbArray);
            dest.writeInt(this.type);
            dest.writeString(this.resType);
            dest.writeByte(this.showDeleted ? (byte) 1 : (byte) 0);
            dest.writeByte(this.IsOnlineSchool ? (byte) 1 : (byte) 0);
            dest.writeInt(this.ReviewFlag);
            dest.writeString(this.MemberId);
            dest.writeInt(SubmitRole);
            dest.writeString(SubmitTime);
        }

        protected ModelBean(Parcel in) {
            this.Id = in.readInt();
            this.TaskId = in.readInt();
            this.CommitTaskId = in.readInt();
            this.ResId = in.readString();
            this.ResUrl = in.readString();
            this.IsTeacher = in.readByte() != 0;
            this.CreateId = in.readString();
            this.CreateName = in.readString();
            this.HeadPicUrl = in.readString();
            this.CreateTime = in.readString();
            this.GroupId = in.readInt();
            this.SchoolId = in.readString();
            this.SchoolName = in.readString();
            this.ClassName = in.readString();
            this.LogoUrl = in.readString();
            this.ClassId = in.readString();
            this.GroupName = in.readString();
            this.UpdateId = in.readString();
            this.UpdateTime = in.readString();
            this.ClassHeadPicUrl = in.readString();
            this.Deleted = in.readByte() != 0;
            this.StudyGroupList = in.createTypedArrayList(ModelBean.CREATOR);
            this.ChildId = in.readString();
            this.RealName = in.readString();
            this.NickName = in.readString();
            this.thumbArray = in.createStringArrayList();
            this.type = in.readInt();
            this.resType = in.readString();
            this.showDeleted = in.readByte() != 0;
            this.IsOnlineSchool = in.readByte() != 0;
            this.ReviewFlag = in.readInt();
            this.MemberId = in.readString();
            this.SubmitRole = in.readInt();
            this.SubmitTime = in.readString();
        }

        public static final Creator<ModelBean> CREATOR = new Creator<ModelBean>() {
            @Override
            public ModelBean createFromParcel(Parcel source) {
                return new ModelBean(source);
            }

            @Override
            public ModelBean[] newArray(int size) {
                return new ModelBean[size];
            }
        };
    }
}
