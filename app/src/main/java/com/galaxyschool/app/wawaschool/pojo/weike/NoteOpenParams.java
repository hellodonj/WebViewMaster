package com.galaxyschool.app.wawaschool.pojo.weike;

import android.os.Parcel;
import android.os.Parcelable;
import com.galaxyschool.app.wawaschool.pojo.NoteInfo;

/**
 * Created by wangchao on 1/5/16.
 */
public class NoteOpenParams implements Parcelable {

    public String path;
    public String createTime;
    public int openType;
    public int noteType;
    public NoteInfo noteInfo;
    public String schoolId;
    public String classId;
    public int shareType;
    public int sourceType;
    public boolean isPad;
    public boolean isTeacher;
    public int taskType;
    public boolean isOnlineSchool;

    public NoteOpenParams() {

    }

    public NoteOpenParams(String path, String createTime, int openType, int noteType, NoteInfo noteInfo, int sourceType, boolean isPad) {
        this.path = path;
        this.createTime = createTime;
        this.openType = openType;
        this.noteType = noteType;
        this.noteInfo = noteInfo;
        this.sourceType = sourceType;
        this.isPad = isPad;
    }

    public NoteOpenParams(String path, String createTime, int openType, int noteType, NoteInfo
            noteInfo, int sourceType, int taskType, boolean isPad) {
        this.path = path;
        this.createTime = createTime;
        this.openType = openType;
        this.noteType = noteType;
        this.noteInfo = noteInfo;
        this.sourceType = sourceType;
        this.taskType = taskType;
        this.isPad = isPad;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.createTime);
        dest.writeInt(this.openType);
        dest.writeInt(this.noteType);
        dest.writeParcelable(this.noteInfo, 0);
        dest.writeString(this.schoolId);
        dest.writeString(this.classId);
        dest.writeInt(this.shareType);
        dest.writeInt(this.sourceType);
        dest.writeByte(isPad ? (byte) 1 : (byte) 0);
        dest.writeByte(isTeacher ? (byte) 1 : (byte) 0);
        dest.writeInt(taskType);
        dest.writeByte(isOnlineSchool ? (byte) 1 : (byte) 0);
    }

    protected NoteOpenParams(Parcel in) {
        this.path = in.readString();
        this.createTime = in.readString();
        this.openType = in.readInt();
        this.noteType = in.readInt();
        this.noteInfo = in.readParcelable(NoteInfo.class.getClassLoader());
        this.schoolId = in.readString();
        this.classId = in.readString();
        this.shareType = in.readInt();
        this.sourceType = in.readInt();
        this.isPad = in.readByte() != 0;
        this.isTeacher = in.readByte() != 0;
        this.taskType = in.readInt();
        this.isOnlineSchool = in.readByte() != 0;
    }

    public static final Creator<NoteOpenParams> CREATOR = new Creator<NoteOpenParams>() {
        public NoteOpenParams createFromParcel(Parcel source) {
            return new NoteOpenParams(source);
        }

        public NoteOpenParams[] newArray(int size) {
            return new NoteOpenParams[size];
        }
    };
}
