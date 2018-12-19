package com.galaxyschool.app.wawaschool.db.dto;

import android.app.Activity;

import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.slide.SlideUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@DatabaseTable()
public class LocalCourseDTO implements Serializable{
    @DatabaseField(id = true)
    protected String mPath;

    @DatabaseField()
    protected String mParentPath;

    @DatabaseField()
    protected String mPoints;

    @DatabaseField()
    protected String mDescription;

    @DatabaseField()
    protected long mLastModifiedTime;

    @DatabaseField()
    protected int mCurrentPage;

    @DatabaseField()
    protected int mPageCount;

    @DatabaseField()
    protected long mDuration;

    @DatabaseField()
    protected int mType;

    @DatabaseField()
    protected int mOrientation;

    @DatabaseField()
    protected long mMicroId;

    @DatabaseField()
    protected String mMemberId;

    @DatabaseField()
    protected String mTitle;

    public LocalCourseDTO() {

    }

    public LocalCourseDTO(
        String mPath, String mParentPath, String mPoints, String mDescription, long mLastModifiedTime, int
        mCurrentPage, int
            mPageCount,
        long mDuration,
        int mType) {
        this.mPath = mPath;
        this.mParentPath = mParentPath;
        this.mPoints = mPoints;
        this.mDescription = mDescription;
        this.mLastModifiedTime = mLastModifiedTime;
        this.mCurrentPage = mCurrentPage;
        this.mPageCount = mPageCount;
        this.mDuration = mDuration;
        this.mType = mType;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public String getmParentPath() {
        return mParentPath;
    }

    public void setmParentPath(String mParentPath) {
        this.mParentPath = mParentPath;
    }

    public String getmPoints() {
        return mPoints;
    }

    public void setmPoints(String mPoints) {
        this.mPoints = mPoints;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public long getmLastModifiedTime() {
        return mLastModifiedTime;
    }

    public void setmLastModifiedTime(long mLastModifiedTime) {
        this.mLastModifiedTime = mLastModifiedTime;
    }

    public int getmCurrentPage() {
        return mCurrentPage;
    }

    public void setmCurrentPage(int mCurrentPage) {
        this.mCurrentPage = mCurrentPage;
    }

    public int getmPageCount() {
        return mPageCount;
    }

    public void setmPageCount(int mPageCount) {
        this.mPageCount = mPageCount;
    }

    public long getmDuration() {
        return mDuration;
    }

    public void setmDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public int getmOrientation() {
        return mOrientation;
    }

    public void setmOrientation(int mOrientation) {
        this.mOrientation = mOrientation;
    }

    public long getmMicroId() {
        return mMicroId;
    }

    public void setmMicroId(long mMicroId) {
        this.mMicroId = mMicroId;
    }

    public String getmMemberId() {
        return mMemberId;
    }

    public void setmMemberId(String mMemberId) {
        this.mMemberId = mMemberId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public LocalCourseInfo toLocalCourseInfo() {
        LocalCourseInfo info = new LocalCourseInfo();
        info.mPath = mPath;
        info.mParentPath = mParentPath;
        info.mPoints = mPoints;
        info.mDescription = mDescription;
        info.mLastModifiedTime = mLastModifiedTime;
        info.mCurrentPage = mCurrentPage;
        info.mPageCount = mPageCount;
        info.mDuration = mDuration;
        info.mType = mType;
        info.mOrientation = mOrientation;
        info.mMicroId = mMicroId;
        info.mMemberId = mMemberId;
        info.mTitle = mTitle;
        return info;
    }

    public static void saveLocalCourse(Activity activity, String memberId, LocalCourseDTO localCourseDTO) {
        LocalCourseDao localCourseDao = new LocalCourseDao(activity);
        if (localCourseDTO != null) {
            localCourseDTO.setmMemberId(memberId);
        }
        localCourseDao.addOrUpdateLocalCourseDTO(localCourseDTO);
    }

    public static LocalCourseDTO getLocalCourse(Activity activity, String memberId, String
            path) {
        return getLocalCourse(activity, memberId, path, null);
    }

    public static LocalCourseDTO getLocalCourse(Activity activity, String memberId, String
            path, String parentPath) {
        LocalCourseDao localCourseDao = new LocalCourseDao(activity);
        LocalCourseDTO localCourseDTO = null;
        try {
            localCourseDTO = localCourseDao.getLocalCourseDTOByPath(memberId, path, parentPath);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return localCourseDTO;
    }

    public static int updateLocalCourse(Activity activity, String memberId, String
            path, LocalCourseDTO dto) {
        return updateLocalCourse(activity, memberId, path, null, dto);
    }

    public static int updateLocalCourse(Activity activity, String memberId, String
            filepath, String parentPath, LocalCourseDTO dto) {
        int ret = 0;

        LocalCourseDao localCourseDao = new LocalCourseDao(activity);
        try {
            ret = localCourseDao.updateLocalCourse(memberId, filepath, parentPath, dto);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static List<LocalCourseDTO> getAllLocalCourses(Activity activity, String memberId, int
            type){
        LocalCourseDao localCourseDao = new LocalCourseDao(activity);
        List<LocalCourseDTO> localCourseDTOs = null;
        try {
            localCourseDTOs = localCourseDao.getLocalCourses(memberId, type);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return localCourseDTOs;
    }

    public static int deleteLocalCourseByPath(Activity activity, String memberId, String path,
                                              boolean isDeleteFolder) {
        int ret = 0;

        LocalCourseDao localCourseDao = new LocalCourseDao(activity);
        try {
            ret = localCourseDao.deleteLocalCoursesByFolder(memberId, path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(ret > 0 && isDeleteFolder) {
            File saveFolder = new File(path);
            if (saveFolder.exists()) {
                SlideUtils.safeDeleteDirectory(saveFolder.getPath());
            }
        }
        return ret;
    }

    public static boolean updateLocalCourseByResId(Activity activity, String memberId, long
            oldResId, long newResdId) {
        int rtn = 0;
        LocalCourseDao localCourseDao = new LocalCourseDao(activity);
        LocalCourseDTO dto = new LocalCourseDTO();
        dto.setmMicroId(newResdId);
        try {
            rtn = localCourseDao.updateLocalCourseByResId(memberId, oldResId, dto);
        } catch (SQLException e) {

        }
        return rtn > 0;

    }
}
