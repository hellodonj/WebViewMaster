package com.lqwawa.intleducation.module.onclass.detail.base.introduction;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课堂简介的契约类
 * @date 2018/06/01 16:03
 * @history v1.0
 * **********************************
 */
public interface ClassIntroductionContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取班级详情信息
        void requestLoadClassInfo(@NonNull String classId);
    }

    interface View extends BaseContract.View<Presenter>{
        // 班级详细信息的回调
        void onClassCheckSucceed(@NonNull JoinClassEntity entity);
    }

}
