package com.galaxyschool.app.wawaschool.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import com.galaxyschool.app.wawaschool.db.dto.NoteDTO;

/**
 * Author: wangchao
 * Time: 2015/10/26 16:08
 */
public class NoteInfo implements Parcelable{
    long noteId;
    long dateTime;
    String title;
    String thumbnail;
    long createTime;
    boolean isUpdate;
    int noteType;
    String resourceUrl;

    public NoteInfo() {

    }

    public NoteInfo(long noteId, long dateTime, String title, String thumbnail, long createTime, boolean isUpdate) {
        this.noteId = noteId;
        this.dateTime = dateTime;
        this.title = title;
        this.thumbnail = thumbnail;
        this.createTime = createTime;
        this.isUpdate = isUpdate;
    }

    public NoteInfo(
        long noteId, long dateTime, String title, String thumbnail, long createTime, boolean isUpdate, int noteType) {
        this(noteId, dateTime, title, thumbnail, createTime, isUpdate);
        this.noteType = noteType;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public int getNoteType() {
        return noteType;
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public NoteDTO toNoteDTO(long noteId, long dateTime, String title, String thumbnail, long createTime, boolean
        isUpdate, int noteType) {
        return new NoteDTO(noteId, dateTime, title, thumbnail, createTime, isUpdate, noteType);
    }

    public NoteDTO toNoteDTO(NoteInfo noteInfo) {
        return new NoteDTO(noteInfo.noteId, noteInfo.dateTime, noteInfo.title, noteInfo.thumbnail, noteInfo
            .createTime, noteInfo
            .isUpdate, noteInfo.getNoteType());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.noteId);
        dest.writeLong(this.dateTime);
        dest.writeString(this.title);
        dest.writeString(this.thumbnail);
        dest.writeLong(this.createTime);
        dest.writeByte(isUpdate ? (byte) 1 : (byte) 0);
        dest.writeInt(this.noteType);
        dest.writeString(this.resourceUrl);
    }

    protected NoteInfo(Parcel in) {
        this.noteId = in.readLong();
        this.dateTime = in.readLong();
        this.title = in.readString();
        this.thumbnail = in.readString();
        this.createTime = in.readLong();
        this.isUpdate = in.readByte() != 0;
        this.noteType = in.readInt();
        this.resourceUrl = in.readString();
    }

    public static final Creator<NoteInfo> CREATOR = new Creator<NoteInfo>() {
        public NoteInfo createFromParcel(Parcel source) {
            return new NoteInfo(source);
        }

        public NoteInfo[] newArray(int size) {
            return new NoteInfo[size];
        }
    };
}
