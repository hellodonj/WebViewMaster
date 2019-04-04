package com.lqwawa.intleducation.module.tutorial.target;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.tutorial.DateFlagEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.tutorial.marking.list.OrderByType;

import java.util.List;

/**
 * @author mrmedici
 * @desc 我的帮辅学生，我的帮辅老师页面的契约类
 */
public interface TutorialTargetTaskContract {

    interface Presenter extends BaseContract.Presenter{
        // 请求符合查询条件的作业列表
        void requestWorkDataWithIdentityId(@Nullable String memberId,
                                           @Nullable String tutorMemberId,
                                           @Nullable String assistStudent_Id,
                                           @Nullable String title,
                                           @Nullable String createTimeBegin,
                                           @Nullable String createTimeEnd,
                                           @Nullable String startTimeBegin,
                                           @Nullable String startTimeEnd,
                                           int state, @OrderByType.OrderByTypeRes int orderByType,
                                           int pageIndex);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateWorkDataWithIdentityIdView(List<TaskEntity> entities);
        void updateMoreWorkDataWithIdentityIdView(List<TaskEntity> entities);
    }

}
