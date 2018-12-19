package com.galaxyschool.app.wawaschool.fragment.contacts;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ContactItem implements Parcelable, Serializable {

    public static final int CONTACT_TYPE_GROUP = 0;
    public static final int CONTACT_TYPE_PERSON = 1;

    public String id;
    public String name;
    public String icon;
    public int type;
    public int chatType;
    public String schoolId;
    public String classId;
    public String hxId;
    public boolean isChatForbidden;
    public String schoolName;
    /**
     * 班级小组
     */
    public String GroupId;

    public ContactItem() {

    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
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

    public String getHxId() {
        return hxId;
    }

    public void setHxId(String hxId) {
        this.hxId = hxId;
    }

    public boolean isChatForbidden() {
        return isChatForbidden;
    }

    public void setIsChatForbidden(boolean isChatForbidden) {
        this.isChatForbidden = isChatForbidden;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeInt(this.type);
        dest.writeInt(this.chatType);
        dest.writeString(this.schoolId);
        dest.writeString(this.classId);
        dest.writeString(this.hxId);
        dest.writeByte(this.isChatForbidden ? (byte) 1 : (byte) 0);
        dest.writeString(this.schoolName);
        dest.writeString(this.GroupId);
    }

    protected ContactItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.icon = in.readString();
        this.type = in.readInt();
        this.chatType = in.readInt();
        this.schoolId = in.readString();
        this.classId = in.readString();
        this.hxId = in.readString();
        this.isChatForbidden = in.readByte() != 0;
        this.schoolName = in.readString();
        this.GroupId = in.readString();
    }

    public static final Creator<ContactItem> CREATOR = new Creator<ContactItem>() {
        @Override
        public ContactItem createFromParcel(Parcel source) {
            return new ContactItem(source);
        }

        @Override
        public ContactItem[] newArray(int size) {
            return new ContactItem[size];
        }
    };
}
