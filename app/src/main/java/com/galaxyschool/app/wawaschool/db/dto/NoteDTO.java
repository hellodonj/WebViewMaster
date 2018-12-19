package com.galaxyschool.app.wawaschool.db.dto;

import com.galaxyschool.app.wawaschool.pojo.NoteInfo;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Author: wangchao
 * Time: 2015/10/26 16:12
 */
@DatabaseTable()
public class NoteDTO implements Serializable {

    // 主键 id 自增长
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField()
    long noteId;

    @DatabaseField()
    long dateTime;

    @DatabaseField()
    String title;

    @DatabaseField()
    String thumbnail;

    @DatabaseField()
    long createTime;

    @DatabaseField()
    boolean isUpdate;

    @DatabaseField()
    int noteType;

    public NoteDTO() {

    }

    public NoteDTO(long noteId, long dateTime, String title, String thumbnail, long createTime, boolean isUpdate) {
        this.noteId = noteId;
        this.dateTime = dateTime;
        this.title = title;
        this.thumbnail = thumbnail;
        this.createTime = createTime;
        this.isUpdate = isUpdate;
    }

    public NoteDTO(long noteId, long dateTime, String title, String thumbnail, long createTime, boolean isUpdate,
        int noteType) {
        this(noteId, dateTime, title,thumbnail,createTime, isUpdate);
        this.noteType = noteType;
    }

    public NoteDTO(long dateTime, String title, String thumbnail, long createTime) {
        this(0, dateTime, title, thumbnail, createTime, false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public NoteInfo toNoteInfo() {
        NoteInfo info = new NoteInfo();
        info.setNoteId(noteId);
        info.setTitle(title);
        info.setDateTime(dateTime);
        info.setThumbnail(thumbnail);
        info.setCreateTime(createTime);
        info.setIsUpdate(isUpdate);
        info.setNoteType(noteType);
        return info;
    }
}
