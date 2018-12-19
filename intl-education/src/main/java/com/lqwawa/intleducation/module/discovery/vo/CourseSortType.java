package com.lqwawa.intleducation.module.discovery.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by XChen on 2016/11/12.
 * email:man0fchina@foxmail.com
 */

public class CourseSortType implements Parcelable {
    private String Id;
    private String Name;
    private boolean isSelect;

    public CourseSortType() {

    }
    public CourseSortType(String id, String name, boolean select) {
        this.Id = id;
        this.Name = name;
        this.isSelect = select;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Id);
        dest.writeString(this.Name);
    }

    protected CourseSortType(Parcel in) {
        this.Id = in.readString();
        this.Name = in.readString();
    }

    public static final Creator<CourseSortType> CREATOR = new Creator<CourseSortType>() {
        public CourseSortType createFromParcel(Parcel source) {
            return new CourseSortType(source);
        }

        public CourseSortType[] newArray(int size) {
            return new CourseSortType[size];
        }
    };
}
