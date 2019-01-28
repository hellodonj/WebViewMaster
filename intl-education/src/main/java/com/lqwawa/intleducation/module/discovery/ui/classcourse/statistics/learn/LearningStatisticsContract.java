package com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.learn;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.course.LearningProgressEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * 班级学程学习统计的契约类
 */
public interface LearningStatisticsContract {

    interface Presenter extends BaseContract.Presenter{
        void requestLearningStatisticsData(@NonNull String classId, @NonNull String courseId, int type);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateLearningStatisticsView(@NonNull List<LearningProgressEntity> entities);
    }

}
