package com.lqwawa.intleducation.module.tutorial.marking.list.pager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.tutorial.DateFlagEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.tutorial.marking.list.OrderByType;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅批阅列表页面的契约类
 */
public interface TutorialMarkingPagerContract {

    interface Presenter extends BaseContract.Presenter{
        // 请求日期标记
        void requestDateFlagForAssist(int position,
                                      @NonNull String memberId,
                                      @NonNull String role,
                                      @NonNull String startTimeBegin,
                                      @NonNull String startTimeEnd,
                                      int state);
        // 请求符合查询条件的作业列表
        void requestWorkDataWithIdentityId(@Nullable String memberId,
                                           @Nullable String tutorMemberId,
                                           @Nullable String assistStudent_Id,
                                           @Nullable String title,
                                           @Nullable String createTimeBegin,
                                           @Nullable String createTimeEnd,
                                           @Nullable String startTimeBegin,
                                           @Nullable String startTimeEnd,
                                           int state,@OrderByType.OrderByTypeRes int orderByType,
                                           int pageIndex);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateDataFlagForAssist(@NonNull String startTimeBegin,int position,@NonNull List<DateFlagEntity> entities);
        void updateWorkDataWithIdentityIdView(List<TaskEntity> entities);
        void updateMoreWorkDataWithIdentityIdView(List<TaskEntity> entities);
    }

}
