package com.galaxyschool.app.wawaschool.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.galaxyschool.app.wawaschool.db.dto.MessageDTO;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PushMessage implements Parcelable, Serializable {

	public static final int MESSAGE_TYPE_CHAT = 0;
	public static final int MESSAGE_TYPE_NEWS = 1;
	public static final int MESSAGE_TYPE_NOTICE = 2;
	public static final int MESSAGE_TYPE_COMMENT = 3;
	public static final int MESSAGE_TYPE_HOMEWORK = 4;
	public static final int MESSAGE_TYPE_WAWA_COURSE = 5;

	public static final int MESSAGE_TYPE_CLASS_REQUEST_APPROVED = 6;
	public static final int MESSAGE_TYPE_CLASS_REQUEST = 7;
	public static final int MESSAGE_TYPE_FRIEND_REQUEST = 8;
	public static final int MESSAGE_TYPE_FRIEND_REQUEST_APPROVED = 9;

	public static final int MESSAGE_TYPE_COURSE = 11;
	public static final int MESSAGE_TYPE_LECTURE = 14;

	public static final int MESSAGE_TYPE_STUDY_TASK = 15;
	public static final int MESSAGE_TYPE_STUDY_TASK_REMIND = 16;

	private int type;
	private String id;
	private String title;
	private String subTitle;
	private String iconUrl;
	private String time;
	private String datetime;
	private int newCount;
	private int IsWawaTong;
	private int Category;

	public PushMessage() {

	}

	public PushMessage(Parcel src) {
		type = src.readInt();
		id = src.readString();
		title = src.readString();
		subTitle = src.readString();
		iconUrl = src.readString();
		time = src.readString();
		datetime = src.readString();
		newCount = src.readInt();
		IsWawaTong = src.readInt();
		Category = src.readInt();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public int getNewCount() {
		return newCount;
	}

	public void setNewCount(int newCount) {
		this.newCount = newCount;
	}

	public int getIsWawaTong() {
		return IsWawaTong;
	}

	public void setIsWawaTong(int isWawaTong) {
		IsWawaTong = isWawaTong;
	}

	public boolean isResourceMessage() {
		return IsWawaTong > 0;
	}

	public int getCategory() {
		return Category;
	}

	public void setCategory(int category) {
		Category = category;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dst, int flags) {
		dst.writeInt(type);
		dst.writeString(id);
		dst.writeString(time);
		dst.writeString(subTitle);
		dst.writeString(iconUrl);
		dst.writeString(time);
		dst.writeString(datetime);
		dst.writeInt(newCount);
		dst.writeInt(IsWawaTong);
		dst.writeInt(Category);
	}

	public static final Creator<PushMessage> CREATOR = new Creator<PushMessage>() {
		@Override
		public PushMessage createFromParcel(Parcel src) {
			return new PushMessage(src);
		}

		@Override
		public PushMessage[] newArray(int size) {
			return new PushMessage[size];
		}
	};

	public MessageDTO toMessageDTO() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(time);
			long times = date.getTime();
			MessageDTO dto = new MessageDTO(id, type, title, subTitle, times, newCount);
			dto.setIsWawaTong(IsWawaTong);
			dto.setCategory(Category);
			return dto;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}