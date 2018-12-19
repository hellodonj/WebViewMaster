package com.lqwawa.intleducation.common.db;

import org.xutils.DbManager;

/**
 * Created by XChen on 2016/11/18.
 * email:man0fchina@foxmail.com
 */

public class DbHelper {
    private static DbManager.DaoConfig daoConfig;

    public static DbManager.DaoConfig getDaoConfig() {
        return daoConfig;
    }

    public static void init() {
        daoConfig = new DbManager.DaoConfig()
                .setDbName("intl_education_db")//创建数据库的名称
                .setDbVersion(1)//数据库版本号
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    }
                });//数据库更新操作
    }
}
