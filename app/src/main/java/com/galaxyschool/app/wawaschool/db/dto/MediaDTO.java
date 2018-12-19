package com.galaxyschool.app.wawaschool.db.dto;

import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by wangchao on 12/26/15.
 */
@DatabaseTable()
public class MediaDTO implements Serializable{
    @DatabaseField(generatedId = true)
    int id;

    @DatabaseField()
    String mediaId;

    @DatabaseField()
    String title;

    @DatabaseField()
    String path;

    @DatabaseField()
    String thumbnail;

    @DatabaseField()
    long duration;

    @DatabaseField()
    long createTime;

    @DatabaseField()
    int mediaType;

    public MediaDTO() {
    }

    public MediaDTO(String title, String path, String thumbnail, long duration, long createTime, int mediaType) {
        this.title = title;
        this.path = path;
        this.thumbnail = thumbnail;
        this.duration = duration;
        this.createTime = createTime;
        this.mediaType = mediaType;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public MediaInfo toMediaInfo() {
        return new MediaInfo(title, path, thumbnail, duration, createTime, mediaType);
    }
}
