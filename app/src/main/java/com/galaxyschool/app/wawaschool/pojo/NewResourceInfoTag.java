package com.galaxyschool.app.wawaschool.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

/**
 *
 * Created by Administrator on 2016.06.17.
 */
public class NewResourceInfoTag extends NewResourceInfo{

    List<NewResourceInfo> SplitInfoList;

    public NewResourceInfoTag() {

    }

    public NewResourceInfoTag(List<NewResourceInfo> splitInfoList) {
        SplitInfoList = splitInfoList;
    }

    public List<NewResourceInfo> getSplitInfoList() {
        return SplitInfoList;
    }

    public void setSplitInfoList(List<NewResourceInfo> splitInfoList) {
        SplitInfoList = splitInfoList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(SplitInfoList);
    }

    protected NewResourceInfoTag(Parcel in) {
        super(in);
        this.SplitInfoList = in.createTypedArrayList(NewResourceInfo.CREATOR);
    }

    public static final Creator<NewResourceInfoTag> CREATOR = new Creator<NewResourceInfoTag>() {
        public NewResourceInfoTag createFromParcel(Parcel source) {
            return new NewResourceInfoTag(source);
        }

        public NewResourceInfoTag[] newArray(int size) {
            return new NewResourceInfoTag[size];
        }
    };
}
