package com.galaxyschool.app.wawaschool.db;

import android.content.Context;

import com.galaxyschool.app.wawaschool.db.dto.DownloadCourseDTO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2016/7/14.
 */
public class DownloadCourseDao {

    private OrmLiteDBHelper mDatabaseHelper;

    public DownloadCourseDao(Context context) {
        mDatabaseHelper = OrmLiteDBHelper.getInstance(context);
    }

    public void addOrUpdateDownloadCourseDTO(DownloadCourseDTO dto) {
        try {
            mDatabaseHelper.getDownloadCourseDao().createOrUpdate(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DownloadCourseDTO> getDownloadCourseByPath(String memberId) throws SQLException {
        Dao<DownloadCourseDTO, String> dao = mDatabaseHelper.getDao(DownloadCourseDTO.class);
        QueryBuilder queryBuilder = dao.queryBuilder();
        Where where = queryBuilder.where();
        where.eq("userId", memberId);
        return queryBuilder.query();
    }

    public int deleteDownCoursesByResId(String memberId, String resId) throws SQLException {
        Dao<DownloadCourseDTO, String> dao = mDatabaseHelper.getDao(DownloadCourseDTO.class);
        DeleteBuilder deleteBuilder = dao.deleteBuilder();
        Where where = deleteBuilder.where();
        where.eq("userId", memberId);
        where.and();
        where.eq("resId", resId);
        return deleteBuilder.delete();
    }
}
