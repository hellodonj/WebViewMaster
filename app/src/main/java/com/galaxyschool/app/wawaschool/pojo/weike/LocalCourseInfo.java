package com.galaxyschool.app.wawaschool.pojo.weike;

import android.content.pm.ActivityInfo;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;

import java.io.File;
import java.io.Serializable;

public class LocalCourseInfo implements Serializable{
    public String mPath;
    public String mParentPath;
    public String mPoints;
    public String mDescription;
    public long mLastModifiedTime;
    public int mCurrentPage;
    public int mPageCount;
    public long mDuration;
    public int mType;
    public boolean mIsFolder;
    public boolean mIsCheck;
    public int uploadCourseType;
    public int mOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    public long mMicroId;
    public String mMemberId;
    public boolean isSelect;
    public String mOriginVoicePath;
    public String mTitle;

    public LocalCourseInfo() {

    }

    public LocalCourseInfo(String path, long lastModifiedTime) {
        this.mPath = path;
        this.mLastModifiedTime = lastModifiedTime;
    }

    public LocalCourseInfo(
        String path, String parent, int currentPage,
        int pageCount, long lastModifiedTime, int type) {
        this(path, parent, currentPage, pageCount, lastModifiedTime);
        this.mType = type;
    }

    public LocalCourseInfo(
        String path, String parent, long duration, long lastModifiedTime, int type, String knowledge, String dscp) {
        this.mPath = path;
        this.mParentPath = parent;
        this.mDuration = duration;
        this.mLastModifiedTime = lastModifiedTime;
        this.mDuration = duration;
        this.mType = type;
        this.mPoints = knowledge;
        this.mDescription = dscp;
    }

    public LocalCourseInfo(
        String path, String parent, int currentPage,
        int pageCount, long lastModifiedTime) {
        this.mPath = path;
        this.mCurrentPage = currentPage;
        this.mParentPath = parent;
        this.mPageCount = pageCount;
        this.mLastModifiedTime = lastModifiedTime;
    }

    public LocalCourseDTO toLocalCourseDTO() {
        if(!TextUtils.isEmpty(mPath) && mPath.endsWith(File.separator)) {
            mPath = mPath.substring(0, mPath.length() - 1);
        }

        if(!TextUtils.isEmpty(mParentPath) && mParentPath.endsWith(File.separator)) {
            mParentPath = mParentPath.substring(0, mParentPath.length() - 1);
        }
        LocalCourseDTO dto =  new LocalCourseDTO(mPath, mParentPath, mPoints, mDescription,mLastModifiedTime, mCurrentPage,
            mPageCount, mDuration, mType);
        dto.setmOrientation(mOrientation);
        dto.setmMicroId(mMicroId);
        dto.setmMemberId(mMemberId);
        dto.setmTitle(mTitle);
        return dto;
    }
}
