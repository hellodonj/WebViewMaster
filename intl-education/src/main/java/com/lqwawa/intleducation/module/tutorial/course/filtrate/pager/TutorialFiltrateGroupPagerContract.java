package com.lqwawa.intleducation.module.tutorial.course.filtrate.pager;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅筛选片段页面的契约类
 */
public interface TutorialFiltrateGroupPagerContract {


    interface Presenter extends BaseContract.Presenter{
        void requestTutorDataByParams(@NonNull String level,
                                      int paramOneId,
                                      int paramTwoId,
                                      int paramThereId,
                                      @HideSortType.SortRes String sort,
                                      int pageIndex);

        // 对帮辅老师进行关注
        void requestAddTutorByStudentId(@NonNull String memberId, @NonNull String tutorMemberId, @NonNull String tutorName);
    }


    interface View extends BaseContract.View<Presenter>{
        void updateTutorView(List<TutorialGroupEntity> entities);
        void updateMoreTutorView(List<TutorialGroupEntity> entities);
        void updateAddTutorByStudentIdView(boolean result);
    }
}
