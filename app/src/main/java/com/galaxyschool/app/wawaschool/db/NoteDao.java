package com.galaxyschool.app.wawaschool.db;

import android.content.Context;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.db.dto.NoteDTO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

public class NoteDao {
	private OrmLiteDBHelper mDatabaseHelper;

	public NoteDao(Context context) {
		mDatabaseHelper = OrmLiteDBHelper.getInstance(context);
	}

	public void addOrUpdateNoteDTO(NoteDTO dto) {
		try {
			mDatabaseHelper.getNoteDao().createOrUpdate(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int updateNoteDTO(long dateTime, int noteType, NoteDTO noteDTO) throws SQLException{
		Dao<NoteDTO, String> dao = mDatabaseHelper.getDao(NoteDTO.class);
		UpdateBuilder updateBuilder = dao.updateBuilder();
		if(noteDTO.getNoteId() > 0) {
			updateBuilder.updateColumnValue("noteId", noteDTO.getNoteId());
		}
		if(!TextUtils.isEmpty(noteDTO.getTitle())) {
			updateBuilder.updateColumnValue("title", Utils.transformSpecialCharacters(noteDTO.getTitle()));
		}
		if(!TextUtils.isEmpty(noteDTO.getThumbnail())) {
			updateBuilder.updateColumnValue("thumbnail", noteDTO.getThumbnail());
		}
		if(noteDTO.getCreateTime() > 0) {
			updateBuilder.updateColumnValue("createTime", noteDTO.getCreateTime());
		}
		updateBuilder.updateColumnValue("isUpdate", noteDTO.isUpdate());
		Where where = updateBuilder.where();
		where.eq("dateTime", dateTime);
		where.and();
		where.eq("noteType", noteType);
		return updateBuilder.update();
	}

	public List<NoteDTO> getNoteDTOs(long startTime, long endTime) throws SQLException {
		Dao<NoteDTO, String> dao = mDatabaseHelper.getDao(NoteDTO.class);
		QueryBuilder queryBuilder = dao.queryBuilder();
//		Where where = queryBuilder.where();
//		where.between("dateTime", startTime, endTime);
		queryBuilder.orderBy("createTime", false);
		return queryBuilder.query();
	}

	public List<NoteDTO> getNoteDTOs(int noteType) throws SQLException {
		Dao<NoteDTO, String> dao = mDatabaseHelper.getDao(NoteDTO.class);
		QueryBuilder queryBuilder = dao.queryBuilder();
		queryBuilder.orderBy("createTime", false);
		Where where = queryBuilder.where();
		where.eq("noteType", noteType);
		return queryBuilder.query();
	}

	public NoteDTO getNoteDTOByDateTime(long dateTime, int noteType) throws SQLException{
		Dao<NoteDTO, String> dao = mDatabaseHelper.getDao(NoteDTO.class);
		QueryBuilder queryBuilder = dao.queryBuilder();
		Where where = queryBuilder.where();
		where.eq("dateTime", dateTime);
		where.and();
		where.eq("noteType", noteType);
		NoteDTO noteDTO = (NoteDTO) queryBuilder.queryForFirst();
		if (noteDTO != null){
			noteDTO.setTitle(Utils.reductionTitleValid(noteDTO.getTitle()));
		}
		return noteDTO;
	}

	public NoteDTO getNoteDTOByNoteId(long noteId, int noteType) throws SQLException{
		Dao<NoteDTO, String> dao = mDatabaseHelper.getDao(NoteDTO.class);
		QueryBuilder queryBuilder = dao.queryBuilder();
		Where where = queryBuilder.where();
		where.eq("noteId", noteId);
		where.and();
		where.eq("noteType", noteType);
		NoteDTO noteDTO = (NoteDTO) queryBuilder.queryForFirst();
		if (noteDTO != null){
			noteDTO.setTitle(Utils.reductionTitleValid(noteDTO.getTitle()));
		}
		return noteDTO;
	}

	public int deleteNoteDTOByDateTime(long dateTime, int noteType) throws SQLException{
		Dao<NoteDTO, String> dao = mDatabaseHelper.getDao(NoteDTO.class);
		DeleteBuilder deleteBuilder = dao.deleteBuilder();
		Where where = deleteBuilder.where();
		where.eq("dateTime", dateTime);
		where.and();
		where.eq("noteType", noteType);
		return deleteBuilder.delete();
	}

}
