package com.lqwawa.intleducation.module.discovery.ui.person.myhistory;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author medici
 * @desc 我的授课历史班契约类
 */
public interface HistoryClassContract {

    interface Presenter extends BaseContract.Presenter{
        void requestMyGiveOnlineCourse(@NonNull String memberId, @NonNull String keyWord, int pageIndex);
        // 获取班级详情信息
        void requestLoadClassInfo(@NonNull String classId);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateMyGiveOnlineCourseView(@NonNull List<OnlineClassEntity> entities);
        void updateMyGiveOnlineMoreCourseView(@NonNull List<OnlineClassEntity> entities);
        // 用户已经参加该课程的回调
        void onClassCheckSucceed(@NonNull JoinClassEntity entity);
    }

}
