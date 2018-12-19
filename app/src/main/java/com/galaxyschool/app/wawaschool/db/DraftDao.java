package com.galaxyschool.app.wawaschool.db;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.galaxyschool.app.wawaschool.db.dto.DraftDTO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

public class DraftDao {
	private OrmLiteDBHelper mDatabaseHelper;

	public DraftDao(Context context) {
		mDatabaseHelper = OrmLiteDBHelper.getInstance(context);
	}

	public DraftDTO getDraftDTO(String id) throws SQLException {
		return (DraftDTO) mDatabaseHelper.getDraftDao().queryForId(id);
	}
	
	public void addOrUpdateDraftDao(DraftDTO dto) {
		try {
			mDatabaseHelper.getDraftDao().createOrUpdate(dto);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("", "----------> addOrUpdatePushMessage error");
		}
	}

	public DraftDTO getDraftByChwPath(String chwPath, String memberId) throws SQLException{
		Dao<DraftDTO, String> dao = mDatabaseHelper.getDao(DraftDTO.class);
		QueryBuilder queryBuilder = dao.queryBuilder();
		Where where = queryBuilder.where();
		where.eq("chw", chwPath);
		where.and();
		where.eq("memberId", memberId);
		List<DraftDTO> draftDtoList = queryBuilder.query();
		if (draftDtoList != null && draftDtoList.size() > 0) {
			return draftDtoList.get(0);
		}
		return null;
	}

	public int updateDraftByChwPath(String chwPath, String memberId, DraftData data) throws SQLException {
		Dao<DraftDTO, String> dao = mDatabaseHelper.getDao(DraftDTO.class);
		UpdateBuilder updateBuilder = dao.updateBuilder();
		if(data.getResdId() > 0) {
			updateBuilder.updateColumnValue("resId", data.getResdId());
		}
		if(!TextUtils.isEmpty(data.getTitle())) {
			updateBuilder.updateColumnValue("title", data.getTitle());
		}
		Where where = updateBuilder.where();
		where.eq("chw", chwPath);
		where.and();
		where.eq("memberId", memberId);
		return updateBuilder.update();
	}

	public int updateDraftByResId(long resId, String memberId, DraftData data) throws SQLException {
		Dao<DraftDTO, String> dao = mDatabaseHelper.getDao(DraftDTO.class);
		UpdateBuilder updateBuilder = dao.updateBuilder();
		if(data.getResdId() >= 0) {
			updateBuilder.updateColumnValue("resId", data.getResdId());
		}
		Where where = updateBuilder.where();
		where.eq("resId", resId);
		where.and();
		where.eq("memberId", memberId);
		return updateBuilder.update();
	}


	public List<DraftDTO> getAllDraftsInType(int type, String memberId) throws SQLException {
		Dao<DraftDTO, String> dao = mDatabaseHelper.getDao(DraftDTO.class);
		QueryBuilder queryBuilder = dao.queryBuilder();
		Where where = queryBuilder.where();
		where.eq("draftType",type);
		where.and();
		where.eq("memberId", memberId);
		queryBuilder.orderBy("editTime", false);
		return queryBuilder.distinct().query();
	}


	public int deleteDraftById(int id, String memberId) throws SQLException {
		Dao<DraftDTO, String> dao = mDatabaseHelper.getDao(DraftDTO.class);
		DeleteBuilder deleteBuilder = dao.deleteBuilder();
		Where where = deleteBuilder.where();
		where.eq("id", id);
		where.and();
		where.eq("memberId", memberId);
		return deleteBuilder.delete();
	}

	public int deleteDraftByChwPath(String chwPath, String memberId) throws SQLException{
		Dao<DraftDTO, String> dao = mDatabaseHelper.getDao(DraftDTO.class);
		DeleteBuilder deleteBuilder = dao.deleteBuilder();
		Where where = deleteBuilder.where();
		where.eq("chw", chwPath);
		where.and();
		where.eq("memberId", memberId);
		return deleteBuilder.delete();
	}
	
}
