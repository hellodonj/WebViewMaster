package com.galaxyschool.app.wawaschool.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Administrator on 2017/2/7.
 */

public class Reporter implements Parcelable{
  private int Id;
  private String SchoolId;
  private String SchoolName;
  private String ClassId;
  private String ClassName;
  private String MemberId;
  private String NickName;
  private String RealName;
  private String HeadPicUrl;
  private int PermType;
  private String CreateId;
  private String CreateName;
  private String CreateTime;
  private String UpdateId;
  private String UpdateName;
  private String UpdateTime;
  public Reporter() {
   }

  public Reporter(int id, String schoolId, String schoolName, String classId, String className, String memberId, String nickName, String realName, String headPicUrl, int permType, String createId, String createName, String createTime, String updateId, String updateName, String updateTime) {
        Id = id;
        SchoolId = schoolId;
        SchoolName = schoolName;
        ClassId = classId;
        ClassName = className;
        MemberId = memberId;
        NickName = nickName;
        RealName = realName;
        HeadPicUrl = headPicUrl;
        PermType = permType;
        CreateId = createId;
        CreateName = createName;
        CreateTime = createTime;
        UpdateId = updateId;
        UpdateName = updateName;
        UpdateTime = updateTime;
    }

    public static final Creator<Reporter> CREATOR = new Creator<Reporter>() {
        @Override
        public Reporter createFromParcel(Parcel in) {
            Reporter reporter=new Reporter();
            reporter.setId(in.readInt());
            reporter.setSchoolId(in.readString());
            reporter.setSchoolName(in.readString());
            reporter.setClassId(in.readString());
            reporter.setClassName(in.readString());
            reporter.setMemberId(in.readString());
            reporter.setNickName(in.readString());
            reporter.setRealName(in.readString());
            reporter.setHeadPicUrl(in.readString());
            reporter.setPermType(in.readInt());
            reporter.setCreateId(in.readString());
            reporter.setCreateName(in.readString());
            reporter.setCreateTime(in.readString());
            reporter.setUpdateId(in.readString());
            reporter.setUpdateName(in.readString());
            reporter.setUpdateTime(in.readString());
            return reporter;
        }

        @Override
        public Reporter[] newArray(int size) {
            return new Reporter[size];
        }
    };

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

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public int getPermType() {
        return PermType;
    }

    public void setPermType(int permType) {
        PermType = permType;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(SchoolId);
        dest.writeString(SchoolName);
        dest.writeString(ClassId);
        dest.writeString(ClassName);
        dest.writeString(MemberId);
        dest.writeString(NickName);
        dest.writeString(RealName);
        dest.writeString(HeadPicUrl);
        dest.writeInt(PermType);
        dest.writeString(CreateId);
        dest.writeString(CreateName);
        dest.writeString(CreateTime);
        dest.writeString(UpdateId);
        dest.writeString(UpdateName);
        dest.writeString(UpdateTime);
    }
}
