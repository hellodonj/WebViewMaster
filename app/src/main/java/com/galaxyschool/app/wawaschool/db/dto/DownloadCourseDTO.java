package com.galaxyschool.app.wawaschool.db.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/14.
 */
@DatabaseTable()
public class DownloadCourseDTO implements Serializable {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private String resId;

    @DatabaseField
    private String userId;

    @DatabaseField
    private String title;

    @DatabaseField
    private String authorName;

    @DatabaseField
    private String introduction;

    @DatabaseField
    private String thumbnail;

    @DatabaseField
    private int screenType;

    @DatabaseField
    private String authorId;

    public DownloadCourseDTO() {

    }

    public DownloadCourseDTO(String id,
                             String resId,
                             String userId,
                             String title,
                             String authorName,
                             String introduction,
                             String thumbnail,
                             int screenType,
                             String authorId) {
        this.id = id;
        this.resId = resId;
        this.userId = userId;
        this.title = title;
        this.authorName = authorName;
        this.introduction = introduction;
        this.thumbnail = thumbnail;
        this.screenType = screenType;
        this.authorId = authorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}
