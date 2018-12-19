package com.galaxyschool.app.wawaschool.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangchao on 5/14/16.
 */
public class PicBookCategoryType implements Parcelable{
    private String Id;
    private String Name;
    private boolean isSelect;

    public PicBookCategoryType() {
    }

    public PicBookCategoryType(String id, String name) {
        Id = id;
        Name = name;
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

    protected PicBookCategoryType(Parcel in) {
        this.Id = in.readString();
        this.Name = in.readString();
    }

    public static final Creator<PicBookCategoryType> CREATOR = new Creator<PicBookCategoryType>() {
        public PicBookCategoryType createFromParcel(Parcel source) {
            return new PicBookCategoryType(source);
        }

        public PicBookCategoryType[] newArray(int size) {
            return new PicBookCategoryType[size];
        }
    };
}
