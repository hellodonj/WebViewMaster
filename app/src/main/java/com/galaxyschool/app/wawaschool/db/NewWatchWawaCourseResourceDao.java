package com.galaxyschool.app.wawaschool.db;

import android.content.Context;

import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.db.dto.NewWatchWawaCourseResourceDTO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

/**
 * 新版看课件资源
 */
public class NewWatchWawaCourseResourceDao {

    private static NewWatchWawaCourseResourceDao dao;
    private static Context context;
    private static Dao<NewWatchWawaCourseResourceDTO, String> resourceDao;

    public static NewWatchWawaCourseResourceDao getInstance(Context ctx) throws Exception{
        context = ctx;
        if (dao == null){
            dao = new NewWatchWawaCourseResourceDao();
        }
        if(resourceDao == null){
            resourceDao = OrmLiteDBHelper.getInstance(context).getNewWatchWawaCourseResourceDao();
        }
        return dao;
    }

    /**
     * 添加资源
     * @param data
     */
    public void addResource(NewWatchWawaCourseResourceDTO data) {
        try {
            if (resourceDao == null) {
                return;
            }
            if (data != null) {
                resourceDao.createOrUpdate(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除资源
     */
    public int deleteResource(String taskId, String studentId){
        try{
            if (resourceDao == null) {
                return 0;
            }
            DeleteBuilder deleteBuilder = resourceDao.deleteBuilder();
            Where where = deleteBuilder.where();
            where.eq("taskId",taskId);
            where.and();
            where.eq("studentId",studentId);
            return deleteBuilder.delete();
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 更新资源
     * @param taskId
     * @param data
     * @return
     */
    public int updateResource(String taskId, String studentId,NewWatchWawaCourseResourceDTO data){
        try{
            if (resourceDao == null) {
                return 0;
            }
            if (data != null){
                UpdateBuilder updateBuilder = resourceDao.updateBuilder();
                Where where = updateBuilder.where();
                where.eq("taskId",taskId);
                where.and();
                where.eq("studentId",studentId);
                updateBuilder.updateColumnValue("ids", Utils.analysisTitleValid(data.getIds()));
                updateBuilder.updateColumnValue("readAll",data.isReadAll());
                return updateBuilder.update();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询资源列表
     * @param taskId
     * @return
     */
    public NewWatchWawaCourseResourceDTO queryResource(String taskId, String studentId){
        try {
            if (resourceDao == null) {
                return null;
            }
            QueryBuilder queryBuilder = resourceDao.queryBuilder();
            Where where = queryBuilder.where();
            where.eq("taskId",taskId);
            //连接语句不能少，否则报错。
            where.and();
            where.eq("studentId",studentId);
            return (NewWatchWawaCourseResourceDTO) queryBuilder.queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
