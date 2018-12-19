package com.galaxyschool.app.wawaschool.db;

import android.content.Context;
import android.text.TextUtils;
import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;
import com.j256.ormlite.dao.Dao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KnIghT on 16-4-9.
 */
public class BookStoreBookDao {

    private static BookStoreBookDao dao;//���������Լ�
    private  static Context context;
    private static Dao<BookStoreBook, String> bookDao;//sqlLite �Դ��dao
    private  BookStoreBookDao(){ }

    /**
     *
     * @param ctx
     * @return ʵ��������Լ���ͬʱ��ʼ��sqlite��Dao������ݿ�ı�İ�����
     * @throws Exception
     */
    public static BookStoreBookDao getInstance(Context ctx)throws  Exception{
        context=ctx;
        if (dao==null){
            dao=new BookStoreBookDao();
        }
        if(bookDao==null){
            bookDao = OrmLiteDBHelper.getInstance(context).getBookStoreBookDao();
        }
        return dao;
    }


    /**
     * ��¼ĳ�˵��������ĳ���飨���ã�����Ȼ������ѧУ����Ϣ
     * ���ӻ��޸���ݿ���Ҫ�����޸ļ���ʱ��(��ǰ���߼�����������ݿ⣬���ӵ��������������ݿ� ����1)
     * @param data
     * @param memberId
     */
    public void addBook(BookStoreBook data,String memberId,String schoolId){
        try {
            if (bookDao == null) {
                return;
            }
            if (TextUtils.isEmpty(memberId)) {
                return;
            }
            BookStoreBook book = bookDao.queryBuilder().
                    where().eq("Id", data.getId()).
                    and().
                    eq("MemberId", memberId).
                    queryForFirst();
            if (book == null) {
                data.setCount(1);
            } else {
                data.setCount(data.getCount()+1);
            }
            data.setCreatedOn(System.currentTimeMillis());
            data.setMemberId(memberId);
            data.setSchoolId(schoolId);
            bookDao.createOrUpdate(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新book的CourseType
     * @param data
     * @param memberId
     * @param schoolId
     */
    public void updateBook(BookStoreBook data,String memberId,String schoolId,int courseType) {
        try {
            if (bookDao == null) {
                return;
            }
            if (TextUtils.isEmpty(memberId)) {
                return;
            }
            BookStoreBook book = bookDao.queryBuilder().where().eq("Id", data.getId()).and().eq("MemberId", memberId).queryForFirst();
            if (book == null) {
                data.setCount(1);
            } else {
                data.setCount(data.getCount()+1);
            }
            data.setCreatedOn(System.currentTimeMillis());
            data.setMemberId(memberId);
            data.setSchoolId(schoolId);
            data.setCourseType(courseType);
            bookDao.createOrUpdate(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param memberId
     * @param schoolId
     * @return ����ĳ�˹�ע��ĳУ���鱾
     */
    public List<BookStoreBook> getBookList(String memberId,String schoolId){
        try {
            if (bookDao == null) {
                return null;
            }
            List<BookStoreBook> list=null;
            if (TextUtils.isEmpty(memberId)) {
                list = new ArrayList<BookStoreBook>();
            } else {
                list = bookDao.queryBuilder().
                        orderBy("CreatedOn", false).
                        limit(3L).
                        where().eq("SchoolId",schoolId).
                        and().eq("MemberId", memberId).
                        query();
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
