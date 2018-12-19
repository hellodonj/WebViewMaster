package com.libs.yilib.pickimages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author 作者 shouyi
 * @version 创建时间：Dec 2, 2015 3:22:06 PM 类说明
 */
public class MediaInfo implements Parcelable {
	public String mPath;
	public long mModifyTime;
	public long mSize;
	public boolean mIsSelected;

	public MediaInfo(String path) {
		mPath = path;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(mPath);
		dest.writeLong(mModifyTime);
		dest.writeLong(mSize);
		dest.writeInt(mIsSelected ? 1 : 0);
	}

	public static final Creator<MediaInfo> CREATOR = new Creator<MediaInfo>() {
		@Override
		public MediaInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			MediaInfo imageInfo = new MediaInfo(source);
			return imageInfo;
		}

		@Override
		public MediaInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MediaInfo[size];
		}
	};

	public MediaInfo(Parcel parcel) {
		mPath = parcel.readString();
		mModifyTime = parcel.readLong();
		mSize = parcel.readLong();
		mIsSelected = parcel.readInt() == 1 ? true : false;
	}
}
