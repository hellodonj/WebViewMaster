package com.galaxyschool.app.wawaschool.db;

import android.content.Context;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;
import com.galaxyschool.app.wawaschool.pojo.Book;
import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KnIghT on 16-4-9.
 */
public class BookDao {

    private static BookDao dao;
    private  static Context context;
    private static Dao<Book, String> bookDao;
    private BookDao(){

    }

    public static BookDao getInstance(Context ctx)throws  Exception{
        context=ctx;
        if (dao == null){
            dao = new BookDao();
        }
        if(bookDao == null){
            bookDao = OrmLiteDBHelper.getInstance(context).getBookDetailDao();
        }
        return dao;
    }

    public void addBook(Book data,String memberId,String schoolId){
        try {
            if (bookDao == null) {
                return;
            }
            if (TextUtils.isEmpty(memberId)) {
                return;
            }
            data.setCreatedOn(System.currentTimeMillis());
            data.setMemberId(memberId);
            data.setSchoolId(schoolId);
            bookDao.createOrUpdate(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Book> getBookList(String memberId,String schoolId,int fromType){
        try {
            if (bookDao == null) {
                return null;
            }
            List<Book> list = null;
            if (TextUtils.isEmpty(memberId)) {
                list = new ArrayList<Book>();
            } else {
                list =  bookDao.queryBuilder().
                        orderBy("CreatedOn", false).
                        limit(3L).
                        where().eq("SchoolId",schoolId).
                        and().eq("MemberId", memberId).
                        and().eq("FromType",fromType).
                        query();
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
