package com.lqwawa.libs.filedownloader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.lqwawa.libs.filedownloader.FileInfo;

import java.io.File;
import java.sql.SQLException;

public class DownloadDatabaseHelper extends OrmLiteSqliteOpenHelper {

	public static final String DATABASE_NAME = "file_downloader.db";
	public static final int DATABASE_VERSION = 1;

	private Context context;
	private FileInfoDao fileInfoDao;


	public DownloadDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	public DownloadDatabaseHelper(Context context, File databaseDir) {
		super(context, new File(databaseDir, DATABASE_NAME).getAbsolutePath(), null,
				DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTableIfNotExists(getConnectionSource(), FileInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(getConnectionSource(), FileInfo.class, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
			int newVersion) {

	}

	@Override
	public void close() {
		super.close();
		fileInfoDao = null;
	}

	public FileInfoDao getFileInfoDao() {
		if (fileInfoDao == null) {
			try {
				fileInfoDao = new FileInfoDao(this);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return fileInfoDao;
	}

}
