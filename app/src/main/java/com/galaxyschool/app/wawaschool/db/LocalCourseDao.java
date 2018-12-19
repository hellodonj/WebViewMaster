package com.galaxyschool.app.wawaschool.db;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.galaxyschool.app.wawaschool.common.Utils.removeFolderSeparator;

public class LocalCourseDao {
    private OrmLiteDBHelper mDatabaseHelper;

    public LocalCourseDao(Context context) {
        mDatabaseHelper = OrmLiteDBHelper.getInstance(context);
    }

    public LocalCourseDTO getLocalCourseDTO() throws SQLException {
        return (LocalCourseDTO) mDatabaseHelper.getLocalCourseDao();
    }

    public List<LocalCourseDTO> getLocalCourseByPath(String memberId, String path) throws SQLException {
        LocalCourseDTO dto = getLocalCourseDTOByPath(memberId, path);
        List<LocalCourseDTO> localCourseDTOs = null;
        if (dto != null) {
            if (localCourseDTOs == null) {
                localCourseDTOs = new ArrayList<LocalCourseDTO>();
            }
            localCourseDTOs.add(dto);
        }
        return localCourseDTOs;
    }

    public LocalCourseDTO getLocalCourseDTOByPath(String memberId, String path, String
            parentPath) throws SQLException {
        //如果path含有“/”,去掉
        if (!TextUtils.isEmpty(path)) {
            path = Utils.removeFolderSeparator(path);
        }

        Dao<LocalCourseDTO, String> dao = mDatabaseHelper.getDao(LocalCourseDTO.class);
        QueryBuilder queryBuilder = dao.queryBuilder();
        Where where = queryBuilder.where();
        where.eq("mMemberId", memberId);
        if (!TextUtils.isEmpty(path)) {
            where.and().eq("mPath", path);
        }
        if (!TextUtils.isEmpty(parentPath)) {
            where.and().eq("mParentPath", parentPath).and().isNull("mPath");
        }
        return (LocalCourseDTO) queryBuilder.queryForFirst();

    }
    public LocalCourseDTO getLocalCourseDTOByPath(String memberId, String path) throws
            SQLException {
        return getLocalCourseDTOByPath(memberId, path, null);
    }

    public List<LocalCourseDTO> getLocalFolders(String memberId, int type) throws SQLException {
        Dao<LocalCourseDTO, String> dao = mDatabaseHelper.getDao(LocalCourseDTO.class);
        QueryBuilder queryBuilder = dao.queryBuilder();
        Where where = queryBuilder.where();
        where.eq("mMemberId", memberId).and().eq("mType", type).and().isNull("mPath");
        return queryBuilder.query();
    }

    public List<LocalCourseDTO> getLocalCourses(String memberId, int type) throws SQLException {
        return getLocalCoursesByFolder(memberId, type, null);
    }

    public List<LocalCourseDTO> getLocalCoursesByFolder(String memberId, int type, String folder) throws SQLException {
        Dao<LocalCourseDTO, String> dao = mDatabaseHelper.getDao(LocalCourseDTO.class);
        QueryBuilder queryBuilder = dao.queryBuilder();
        Where where = queryBuilder.where();
        where.eq("mMemberId", memberId);
        where.and();
        where.eq("mType", type);
        if (!TextUtils.isEmpty(folder)) {
            where.and();
            where.eq("mParentPath", folder);
        }
        queryBuilder.orderBy("mLastModifiedTime", false);
        return queryBuilder.query();
    }

    public int updateLocalCourse(String memberId, String oldFilePath, String oldParent, String newParent) throws SQLException {

        Dao<LocalCourseDTO, String> dao = mDatabaseHelper.getDao(LocalCourseDTO.class);

        if (TextUtils.isEmpty(oldParent) && TextUtils.isEmpty(newParent)) {
            return -1;
        }
        if (oldParent.endsWith(File.separator)) {
            oldParent = oldParent.substring(0, oldParent.length() - 1);
        }
        if (newParent.endsWith(File.separator)) {
            newParent = newParent.substring(0, newParent.length() - 1);
        }
        if (oldFilePath != null && oldFilePath.endsWith(File.separator)) {
            oldFilePath = oldFilePath.substring(0, oldFilePath.length() - 1);
        }

        UpdateBuilder updateBuilder = dao.updateBuilder();
        if(!TextUtils.isEmpty(oldFilePath)) {
            updateBuilder.updateColumnValue("mPath", newParent + File.separator
                    + Utils.getFileNameFromPath(oldFilePath));
            Where where = updateBuilder.where();
            where.eq("mMemberId", memberId);
            where.and();
            where.eq("mPath", oldFilePath).and().eq("mParentPath", oldParent);
        } else {
            Where where = updateBuilder.where();
            where.eq("mMemberId", memberId);
            where.and();
            where.isNull("mPath").and().eq("mParentPath", oldParent);
        }
        updateBuilder.updateColumnValue("mParentPath", newParent);
        return updateBuilder.update();
    }

    public int updateLocalCourse(String memberId, String oldFilePath, LocalCourseInfo info) throws SQLException {
        return updateLocalCourse(memberId, oldFilePath, null, info);
    }

    public int updateLocalCourse(String memberId, String oldFilePath, String oldParentPath,
                                 LocalCourseInfo info) throws SQLException {
        if (TextUtils.isEmpty(oldFilePath) && TextUtils.isEmpty(oldParentPath)) {
            return 0;
        }

        boolean isFolder = TextUtils.isEmpty(oldParentPath) ? false : true;
        Dao<LocalCourseDTO, String> dao = mDatabaseHelper.getDao(LocalCourseDTO.class);
        if (oldFilePath != null && oldFilePath.endsWith(File.separator)) {
            oldFilePath = oldFilePath.substring(0, oldFilePath.length() - 1);
        }

        if (oldParentPath != null && oldParentPath.endsWith(File.separator)) {
            oldParentPath = oldParentPath.substring(0, oldParentPath.length() - 1);
        }

        UpdateBuilder updateBuilder = dao.updateBuilder();
        Where where = updateBuilder.where();
        where.eq("mMemberId", memberId);
        where.and();

        if (!isFolder) {
            where.eq("mPath", oldFilePath);
        } else {
            where.isNull("mPath").and().eq("mParentPath", oldParentPath);
        }

        if(info.mMicroId > 0) {
            updateBuilder.updateColumnValue("mMicroId", info.mMicroId);
        }

        if(!TextUtils.isEmpty(info.mPath)) {
            updateBuilder.updateColumnValue("mPath", info.mPath);
        }
        if(info.mPageCount > 0) {
            updateBuilder.updateColumnValue("mPageCount", info.mPageCount);
        }
        if(!TextUtils.isEmpty(info.mParentPath)) {
            updateBuilder.updateColumnValue("mParentPath", info.mParentPath);
        }
        if(info.mDuration > 0) {
            updateBuilder.updateColumnValue("mDuration", info.mDuration);
        }
        if(info.mLastModifiedTime > 0) {
            updateBuilder.updateColumnValue("mLastModifiedTime", info.mLastModifiedTime);
        }
        if(!TextUtils.isEmpty(info.mPoints)) {
            updateBuilder.updateColumnValue("mPoints", info.mPoints);
        }
        if(!TextUtils.isEmpty(info.mDescription)) {
            updateBuilder.updateColumnValue("mDescription", Utils.analysisTitleValid(info.mDescription));
        }

        if (!TextUtils.isEmpty(info.mTitle)) {
            updateBuilder.updateColumnValue("mTitle", Utils.analysisTitleValid(info.mTitle));
        }

//        if(info.mOrientation >= 0) {
//            updateBuilder.updateColumnValue("mOrientation", info.mOrientation);
//        }

        return updateBuilder.update();
    }

    public int updateLocalCourse(String memberId, String filepath, LocalCourseDTO dto) throws
            SQLException {
        return updateLocalCourse(memberId, filepath, null, dto.toLocalCourseInfo());
    }

    public int updateLocalCourse(String memberId, String filepath, String parentPath,
                                 LocalCourseDTO dto) throws SQLException {
        if (dto != null) {
            return updateLocalCourse(memberId, filepath, parentPath, dto.toLocalCourseInfo());
        }

        return 0;
    }

    public int updateLocalCourseByResId(String memberId, long resId, LocalCourseDTO data) throws
            SQLException {
        Dao<LocalCourseDTO, String> dao = mDatabaseHelper.getDao(LocalCourseDTO.class);
        UpdateBuilder updateBuilder = dao.updateBuilder();
        if(data.getmMicroId() >= 0) {
            updateBuilder.updateColumnValue("mMicroId", data.getmMicroId());
        }
        Where where = updateBuilder.where();
        where.eq("mMemberId", memberId);
        where.and();
        where.eq("mMicroId", resId);
        return updateBuilder.update();
    }




    public int deleteLocalCoursesByFolder(String memberId, String folder) throws SQLException {
        Dao<LocalCourseDTO, String> dao = mDatabaseHelper.getDao(LocalCourseDTO.class);
        DeleteBuilder deleteBuilder = dao.deleteBuilder();
//        queryBuilder.orderBy("", false);
        Where where = deleteBuilder.where();
        where.eq("mMemberId", memberId);
        where.and();
        where.eq("mPath", folder);
        return deleteBuilder.delete();
    }

    public int deleteLocalCoursesByParent(String memberId, String parent) throws SQLException {
        Dao<LocalCourseDTO, String> dao = mDatabaseHelper.getDao(LocalCourseDTO.class);
        DeleteBuilder deleteBuilder = dao.deleteBuilder();
//        queryBuilder.orderBy("", false);
        Where where = deleteBuilder.where();
        where.eq("mMemberId", memberId);
        where.and();
        where.eq("mParentPath", parent);
        return deleteBuilder.delete();
    }

    public void addOrUpdateLocalCourseDTO(LocalCourseDTO dto) {
        try {
            mDatabaseHelper.getLocalCourseDao().createOrUpdate(dto);
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("", "----------> addOrUpdatePushMessage error");
        }
    }
}
