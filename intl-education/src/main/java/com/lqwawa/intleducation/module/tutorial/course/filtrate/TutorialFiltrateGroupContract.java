package com.lqwawa.intleducation.module.tutorial.course.filtrate;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅群标签筛选页面的契约类
 */
public interface TutorialFiltrateGroupContract {

    interface Presenter extends BaseContract.Presenter{
        void requestTutorialConfigData(@NonNull String memberId);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateTutorialConfigView(List<LQCourseConfigEntity> entities);
    }

}
