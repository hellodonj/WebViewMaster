package com.lqwawa.intleducation.module.onclass.detail.base;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.osastudio.common.popmenu.EntryBean;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 班级详情基类契约类
 * @date 2018/06/01 11:42
 * @history v1.0
 * **********************************
 */
public interface BaseClassDetailContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取班级详情
        void requestClassDetail(int id, boolean refreshHeader);
        // 获取机构详情
        void requestSchoolInfo(@NonNull String schoolId);
        // 分享
        void share(@NonNull String title,
                   @NonNull String description,
                   @NonNull String thumbnailUrl,
                   @NonNull String url);
    }

    interface View<T extends Presenter> extends BaseContract.View<T>{
        // 获取班级详情,UI回调
        void updateClassDetailView(boolean refreshHeader, @NonNull ClassDetailEntity entity);
        // 获取机构详情,UI回调
        void updateSchoolInfoView(@NonNull SchoolInfoEntity entity);
    }

}
