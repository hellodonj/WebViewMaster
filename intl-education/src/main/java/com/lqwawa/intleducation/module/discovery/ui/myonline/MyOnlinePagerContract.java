package com.lqwawa.intleducation.module.discovery.ui.myonline;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author medici
 * @desc 我的在线学习的契约类
 */
public interface MyOnlinePagerContract {

    interface Presenter extends BaseContract.Presenter{
        void requestOnlineCourseData(@NonNull String curMemberId, @NonNull String keyWord, int pageIndex);
        // 获取班级详情信息
        void requestLoadClassInfo(@NonNull String classId);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateOnlineCourseView(@NonNull List<OnlineClassEntity> entities);
        void updateMoreOnlineCourseView(@NonNull List<OnlineClassEntity> entities);
        // 用户已经参加该课程的回调
        void onClassCheckSucceed(@NonNull JoinClassEntity entity);
    }

}
