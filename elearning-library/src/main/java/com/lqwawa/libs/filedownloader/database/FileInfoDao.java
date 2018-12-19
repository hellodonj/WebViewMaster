package com.lqwawa.libs.filedownloader.database;

import android.text.TextUtils;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;
import com.lqwawa.libs.filedownloader.FileInfo;

import java.sql.SQLException;
import java.util.List;

public class FileInfoDao {

	private Dao<FileInfo, String> dao;


	public FileInfoDao(OrmLiteSqliteOpenHelper databaseHelper) throws SQLException {
        dao = databaseHelper.getDao(FileInfo.class);
	}

	public boolean fileExists(FileInfo fileInfo) {
		return fileExists(fileInfo.getId());
	}

    public boolean fileExists(String id) {
		try {
			return this.dao.idExists(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void addOrUpdateFile(FileInfo fileInfo) {
		try {
			this.dao.createOrUpdate(fileInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void updateFile(FileInfo fileInfo) {
		try {
			this.dao.update(fileInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<FileInfo> getUserFileList(String userId, String fileName) {
		try {
			Where where = this.dao.queryBuilder().orderBy("timestamp", false)
					.where().eq("userId", userId);
			if (!TextUtils.isEmpty(fileName)) {
				where.and().like("fileName", "%" + fileName + "%");
			}
            return where.query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public FileInfo getUserFile(String userId, String fileId) {
		try {
			return this.dao.queryBuilder().where().eq("userId", userId)
					.and().eq("fileId", fileId).queryForFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int deleteUserFile(String userId, String fileId) {
		try {
			DeleteBuilder deleteBuilder = this.dao.deleteBuilder();
			deleteBuilder.where().eq("userId", userId)
					.and().eq("fileId", fileId);
			return deleteBuilder.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

}
