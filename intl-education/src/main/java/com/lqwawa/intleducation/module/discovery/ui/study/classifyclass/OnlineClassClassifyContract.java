package com.lqwawa.intleducation.module.discovery.ui.study.classifyclass;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.ui.study.filtratelist.pager.OnlineStudyFiltratePagerContract;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线学习在线班级分类列表的契约类
 * @date 2018/05/31 14:58
 * @history v1.0
 * **********************************
 */
public interface OnlineClassClassifyContract {

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
