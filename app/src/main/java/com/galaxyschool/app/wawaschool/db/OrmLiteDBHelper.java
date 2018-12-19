package com.galaxyschool.app.wawaschool.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.db.dto.AssignmentDTO;
import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;
import com.galaxyschool.app.wawaschool.db.dto.CourseDTO;
import com.galaxyschool.app.wawaschool.db.dto.DownloadCourseDTO;
import com.galaxyschool.app.wawaschool.db.dto.DraftDTO;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.db.dto.MediaDTO;
import com.galaxyschool.app.wawaschool.db.dto.MessageDTO;
import com.galaxyschool.app.wawaschool.db.dto.NewWatchWawaCourseResourceDTO;
import com.galaxyschool.app.wawaschool.db.dto.NewsDTO;
import com.galaxyschool.app.wawaschool.db.dto.NewsReadDTO;
import com.galaxyschool.app.wawaschool.db.dto.NoteDTO;
import com.galaxyschool.app.wawaschool.pojo.Book;
import com.galaxyschool.app.wawaschool.pojo.DownloadChwDto;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by shouyi on 6/8/15.
 */
public class OrmLiteDBHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = Utils.DB_FILE;
    private static final int DATABASE_VERSION = 11;

    private static OrmLiteDBHelper databaseHelper = null;

    public OrmLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static OrmLiteDBHelper getInstance(Context context) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, OrmLiteDBHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
        // TODO Auto-generated method stub
        try {
            TableUtils.createTableIfNotExists(connectionSource,
                    MessageDTO.class);
//			TableUtils.createTableIfNotExists(connectionSource,
//					AssignmentDTO.class);
//			TableUtils.createTableIfNotExists(connectionSource,
//					NewsReadDTO.class);
//			TableUtils.createTableIfNotExists(connectionSource, NewsDTO.class);
//			TableUtils
//					.createTableIfNotExists(connectionSource, CourseDTO.class);
            TableUtils.createTableIfNotExists(connectionSource, DraftDTO.class);
//			TableUtils.createTableIfNotExists(connectionSource,
//					DownloadChwDto.class);
            TableUtils.createTableIfNotExists(connectionSource,
                    NoteDTO.class);
//			TableUtils.createTableIfNotExists(connectionSource,
//					MediaDTO.class);
            TableUtils.createTableIfNotExists(connectionSource, MediaDTO.class);
            TableUtils.createTableIfNotExists(connectionSource, BookStoreBook.class);
            TableUtils.createTableIfNotExists(connectionSource, LocalCourseDTO.class);
            TableUtils.createTableIfNotExists(connectionSource, DownloadCourseDTO.class);
            //新版看课件
            TableUtils.createTableIfNotExists(connectionSource, NewWatchWawaCourseResourceDTO.class);
            TableUtils.createTableIfNotExists(connectionSource, Book.class);
        } catch (SQLException e) {
            Log.e(OrmLiteDBHelper.class.getName(), "create db error", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        dropTable(db, "messagedto");
        dropTable(db, "assignmentdtO");
        dropTable(db, "newsreaddtO");
        dropTable(db, "newsdtO");
        dropTable(db, "coursedtO");
        dropTable(db, "draftdtO");
        dropTable(db, "downloadchwdto");
        dropTable(db, "notedto");
        dropTable(db, "mediadto");
        dropTable(db, "book_store_book");
        dropTable(db, "localcoursedto");
        dropTable(db, "downloadcoursedto");
        dropTable(db, "new_watch_wawa_course_resource_dto");
        dropTable(db, "book");
//        try {
//            TableUtils.dropTable(connectionSource, MessageDTO.class, true);
//            TableUtils.dropTable(connectionSource, DraftDTO.class, true);
//            TableUtils.dropTable(connectionSource, NoteDTO.class, true);
//            TableUtils.dropTable(connectionSource, MediaDTO.class, true);
//            TableUtils.dropTable(connectionSource, BookStoreBook.class, true);
//            TableUtils.dropTable(connectionSource, LocalCourseDTO.class, true);
//            TableUtils.dropTable(connectionSource, DownloadCourseDTO.class, true);
//            TableUtils.dropTable(connectionSource, NewWatchWawaCourseResourceDTO.class, true);
//            TableUtils.dropTable(connectionSource, Book.class,true);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        onCreate(db);
    }

    private void dropTable(SQLiteDatabase db, String tableName) {
        try {
            String sql = "drop table if exists " + tableName;
            db.execSQL(sql);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
                          int arg3) {
        // TODO Auto-generated method stub
        try {
            if (arg2 == 1) {
                upgradeFor1To2();
                arg2 = 2;
            }
            if (arg2 == 2) {
                TableUtils.createTableIfNotExists(arg1, BookStoreBook.class);
                arg2 = 3;
            }
            if (arg2 == 3) {
                upgradeFor3To4();
                arg2 = 4;
            }
            if (arg2 == 4) {
                upgradeForm4o5();
                arg2 = 5;
            }
            if (arg2 == 5) {
                upgradeFor5To6();
                arg2 = 6;
            }
            if (arg2 == 6) {
                upgradeFor6To7();
                arg2 = 7;
            }
            if (arg2 == 7) {
                createNewWatchWawaCourseResourceDTOTable();
                arg2 = 8;
            }
            if (arg2 == 8) {
                dropUnusedTables();
                addColumnToLocalCourseDTO();
                arg2 = 9;
            }
            if (arg2 == 9) {
                addColumnToDownLoadCourseDTO();
                arg2 = 10;
            }
            if (arg2 == 10) {
                createBookDetailDTOTable();
                arg2 = 11;
            }
        } catch (SQLException e) {
            Log.e(OrmLiteDBHelper.class.getName(), "create db error", e);
            e.printStackTrace();
        }

    }


    private void createNewWatchWawaCourseResourceDTOTable() {
        try {
            TableUtils.createTableIfNotExists(connectionSource, NewWatchWawaCourseResourceDTO.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createBookDetailDTOTable() {
        try {
            TableUtils.createTableIfNotExists(connectionSource, Book.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void upgradeFor3To4() {
        try {
            Dao dao = getDao(BookStoreBook.class);
            if (dao.isTableExists()) {
                dao.executeRaw("ALTER TABLE 'book_store_book' ADD COLUMN CreateTime INTEGER DEFAULT 0;");
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.i("", "UpgradeFor3To4 ERROR");
        }

    }

    private void upgradeFor1To2() {
        try {
            Dao dao = getDao(DraftDTO.class);
            if (dao.isTableExists()) {
                dao.executeRaw("ALTER TABLE 'draftdto' ADD COLUMN resId INTEGER DEFAULT 0;");
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.i("", "UpgradeFor1To2 ERROR");
        }

    }

    private void upgradeForm4o5() {
        try {
            Dao dao = getDao(DraftDTO.class);
            if (dao.isTableExists()) {
                dao.executeRaw("ALTER TABLE 'draftdto' ADD COLUMN memberId TEXT DEFAULT NULL;");
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.i("", "UpgradeFor1To2 ERROR");
        }
    }

    private void upgradeFor5To6() {
        try {
            TableUtils.createTableIfNotExists(connectionSource,
                    LocalCourseDTO.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void upgradeFor6To7() {
        try {
            TableUtils.createTableIfNotExists(connectionSource,
                    DownloadCourseDTO.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropUnusedTables() {
        try {
            TableUtils.dropTable(connectionSource, AssignmentDTO.class, true);
            TableUtils.dropTable(connectionSource, NewsReadDTO.class, true);
            TableUtils.dropTable(connectionSource, NewsDTO.class, true);
            TableUtils.dropTable(connectionSource, CourseDTO.class, true);
            TableUtils.dropTable(connectionSource, DownloadChwDto.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addColumnToLocalCourseDTO() {
        try {
            Dao dao = getDao(LocalCourseDTO.class);
            if (dao.isTableExists()) {
                dao.executeRaw("ALTER TABLE 'LocalCourseDTO' ADD COLUMN mTitle TEXT DEFAULT NULL;");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addColumnToDownLoadCourseDTO() {
        try {
            Dao dao = getDao(DownloadCourseDTO.class);
            if (dao.isTableExists()) {
                dao.executeRaw("ALTER TABLE 'DownloadCourseDTO' ADD COLUMN authorId TEXT DEFAULT NULL;");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Dao getCommonDao(Class clazz) throws SQLException {
        if (clazz == null) {
            return null;
        }
        Dao dao = getDao(clazz);
        dao.setAutoCommit(
                getConnectionSource().getReadWriteConnection(dao.getTableName()), true);
        return dao;
    }

    public Dao getPushMessageDao() throws SQLException {
        return getCommonDao(MessageDTO.class);
    }

    public Dao getDraftDao() throws SQLException {
        return getCommonDao(DraftDTO.class);
    }

    public Dao getNoteDao() throws SQLException {
        return getCommonDao(NoteDTO.class);
    }

    public Dao getMediaDao() throws SQLException {
        return getCommonDao(MediaDTO.class);
    }

    public Dao getBookStoreBookDao() throws SQLException {
        return getCommonDao(BookStoreBook.class);
    }

    public Dao getLocalCourseDao() throws SQLException {
        return getCommonDao(LocalCourseDTO.class);
    }

    public Dao getDownloadCourseDao() throws SQLException {
        return getCommonDao(DownloadCourseDTO.class);
    }

    public Dao getNewWatchWawaCourseResourceDao() throws SQLException {
        return getCommonDao(NewWatchWawaCourseResourceDTO.class);
    }

    public Dao getBookDetailDao() throws SQLException {
        return getCommonDao(Book.class);
    }
}
