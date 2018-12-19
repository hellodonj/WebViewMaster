package com.galaxyschool.app.wawaschool.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class SchoolClassDetail implements Parcelable{
    String schoolId;
    String classId;
    String schoolName;
    String className;
    boolean isSelect;
    boolean isNeedDelete;

    public SchoolClassDetail() {
    }

    public SchoolClassDetail(boolean isNeedDelete, String schoolId, String classId, String schoolName, String className, boolean isSelect) {
        this.isNeedDelete = isNeedDelete;
        this.schoolId = schoolId;
        this.classId = classId;
        this.schoolName = schoolName;
        this.className = className;
        this.isSelect = isSelect;
    }
    public SchoolClassDetail(Parcel src) {
        schoolId=src.readString();
        classId=src.readString();
        schoolName=src.readString();
        className=src.readString();
        isSelect = src.readInt() != 0;
        isNeedDelete = src.readInt() != 0;
    }
    public boolean isNeedDelete() {
        return isNeedDelete;
    }

    public void setIsNeedDelete(boolean needDelete) {
        isNeedDelete = needDelete;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(schoolId);
        dest.writeString(classId);
        dest.writeString(schoolName);
        dest.writeString(className);
        dest.writeInt(isSelect ? 1:0);
        dest.writeInt(isNeedDelete ? 1:0);
    }
    public static final Creator<SchoolClassDetail> CREATOR =
            new Creator<SchoolClassDetail>() {
                @Override
                public SchoolClassDetail createFromParcel(Parcel src) {
                    return new SchoolClassDetail(src);
                }

                @Override
                public SchoolClassDetail[] newArray(int size) {
                    return new SchoolClassDetail[size];
                }
            };
}
