package com.icedcap.dubbing.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dsq on 2017/4/24.
 */

public class SrtEntity implements Parcelable {
    protected String content;
    protected int startTime;
    protected int endTime;
    protected String role;


    public SrtEntity() {
    }

    public SrtEntity(String role, int startTime, int endTime, String content) {
        this.role = role;
        this.startTime = startTime;
        this.endTime = endTime;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public SrtEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public int getStartTime() {
        return startTime;
    }

    public SrtEntity setStartTime(int startTime) {
        this.startTime = startTime;
        return this;
    }

    public int getEndTime() {
        return endTime;
    }

    public SrtEntity setEndTime(int endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getRole() {
        return role;
    }

    public SrtEntity setRole(String role) {
        this.role = role;
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeInt(this.startTime);
        dest.writeInt(this.endTime);
        dest.writeString(this.role);
    }

    protected SrtEntity(Parcel in) {
        this.content = in.readString();
        this.startTime = in.readInt();
        this.endTime = in.readInt();
        this.role = in.readString();
    }

    public static final Creator<SrtEntity> CREATOR = new Creator<SrtEntity>() {
        @Override
        public SrtEntity createFromParcel(Parcel source) {
            return new SrtEntity(source);
        }

        @Override
        public SrtEntity[] newArray(int size) {
            return new SrtEntity[size];
        }
    };
}
