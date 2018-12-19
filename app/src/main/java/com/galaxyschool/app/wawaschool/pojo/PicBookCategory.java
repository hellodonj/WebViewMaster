package com.galaxyschool.app.wawaschool.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by wangchao on 5/14/16.
 */
public class PicBookCategory implements Parcelable{
    private int Type;
    private String TypeName;
    List<PicBookCategoryType> DetailList;

    public PicBookCategory() {
    }

    public PicBookCategory(int type, String typeName, List<PicBookCategoryType> detailList) {
        Type = type;
        TypeName = typeName;
        DetailList = detailList;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public List<PicBookCategoryType> getDetailList() {
        return DetailList;
    }

    public void setDetailList(List<PicBookCategoryType> detailList) {
        DetailList = detailList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.Type);
        dest.writeString(this.TypeName);
        dest.writeTypedList(DetailList);
    }

    protected PicBookCategory(Parcel in) {
        this.Type = in.readInt();
        this.TypeName = in.readString();
        this.DetailList = in.createTypedArrayList(PicBookCategoryType.CREATOR);
    }

    public static final Creator<PicBookCategory> CREATOR = new Creator<PicBookCategory>() {
        public PicBookCategory createFromParcel(Parcel source) {
            return new PicBookCategory(source);
        }

        public PicBookCategory[] newArray(int size) {
            return new PicBookCategory[size];
        }
    };
}
