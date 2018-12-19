package com.galaxyschool.app.wawaschool.db;

import android.content.Context;
import android.text.TextUtils;
import com.galaxyschool.app.wawaschool.db.dto.MediaDTO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

public class MediaDao {
	private OrmLiteDBHelper mDatabaseHelper;

	public MediaDao(Context context) {
		mDatabaseHelper = OrmLiteDBHelper.getInstance(context);
	}

	public void addOrUpdateMediaDTO(MediaDTO dto) {
		try {
			mDatabaseHelper.getMediaDao().createOrUpdate(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int updateMediaDTO(String path, int mediaType, MediaDTO dto) throws SQLException{
		Dao<MediaDTO, String> dao = mDatabaseHelper.getDao(MediaDTO.class);
		UpdateBuilder updateBuilder = dao.updateBuilder();
		if(!TextUtils.isEmpty(dto.getMediaId())) {
			updateBuilder.updateColumnValue("mediaId", dto.getMediaId());
		}
		if(!TextUtils.isEmpty(dto.getTitle())) {
			updateBuilder.updateColumnValue("title", dto.getTitle());
		}
		if(!TextUtils.isEmpty(dto.getThumbnail())) {
			updateBuilder.updateColumnValue("thumbnail", dto.getThumbnail());
		}
		if(dto.getCreateTime() > 0) {
			updateBuilder.updateColumnValue("createTime", dto.getCreateTime());
		}
		Where where = updateBuilder.where();
		where.eq("path", path);
		where.and();
		where.eq("mediaType", mediaType);
		return updateBuilder.update();
	}

	public List<MediaDTO> getMediaDTOs(int mediaType) throws SQLException {
		Dao<MediaDTO, String> dao = mDatabaseHelper.getDao(MediaDTO.class);
		QueryBuilder queryBuilder = dao.queryBuilder();
		queryBuilder.orderBy("createTime", false);
		Where where = queryBuilder.where();
		where.eq("mediaType", mediaType);
		return queryBuilder.query();
	}

	public MediaDTO getMediaDTOByPath(String path, int mediaType) throws SQLException{
		Dao<MediaDTO, String> dao = mDatabaseHelper.getDao(MediaDTO.class);
		QueryBuilder queryBuilder = dao.queryBuilder();
		Where where = queryBuilder.where();
		where.eq("path", path);
		where.and();
		where.eq("mediaType", mediaType);
		return (MediaDTO) queryBuilder.queryForFirst();
	}

	public MediaDTO getMediaDTOByMediaId(String mediaId, int mediaType) throws SQLException{
		Dao<MediaDTO, String> dao = mDatabaseHelper.getDao(MediaDTO.class);
		QueryBuilder queryBuilder = dao.queryBuilder();
		Where where = queryBuilder.where();
		where.eq("mediaId", mediaId);
		where.and();
		where.eq("mediaType", mediaType);
		return (MediaDTO)queryBuilder.queryForFirst();
	}

	public int deleteMediaDTOByPath(String path, int mediaType) throws SQLException{
		Dao<MediaDTO, String> dao = mDatabaseHelper.getDao(MediaDTO.class);
		DeleteBuilder deleteBuilder = dao.deleteBuilder();
		Where where = deleteBuilder.where();
		where.eq("path", path);
		where.and();
		where.eq("mediaType", mediaType);
		return deleteBuilder.delete();
	}

}
