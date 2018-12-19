package com.lqwawa.intleducation.module.onclass.teacherlist;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQTeacherEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 机构老师列表的契约类
 * @date 2018/06/05 18:03
 * @history v1.0
 * **********************************
 */
public class SchoolTeacherListContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取机构老师信息
        void requestOnlineSchoolTeacherData(@NonNull String schoolId, int pageIndex);
    }

    interface View extends BaseContract.View<Presenter>{
        // 获取到老师数据，进行UI回调
        void updateSchoolTeacherView(@NonNull List<LQTeacherEntity> entities);
        // 获取到更多老师数据,进行UI回调
        void updateSchoolMoreTeacherView(@NonNull List<LQTeacherEntity> entities);
    }

}
