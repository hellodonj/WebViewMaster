package com.lqwawa.intleducation.module.discovery.ui.study.filtratelist.pager;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author medici
 * @desc 在线学习二级列表Pager页的契约类
 */
public class OnlineStudyFiltratePagerContract {

    interface Presenter extends BaseContract.Presenter{
        void requestOnlineStudyDataFromLabel(int pageIndex,
                                             @NonNull String keyWord,
                                             int sort, int firstId,
                                             int secondId, int thirdId,
                                             int fourthId);
        // 获取班级详情信息
        void requestLoadClassInfo(@NonNull String classId);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateOnlineStudyClassDataView(@NonNull List<OnlineClassEntity> entities);
        void updateOnlineStudyMoreClassDataView(@NonNull List<OnlineClassEntity> entities);
        // 用户已经参加该课程的回调
        void onClassCheckSucceed(@NonNull JoinClassEntity entity);
    }


}
