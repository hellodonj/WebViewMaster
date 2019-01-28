package com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.course;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.course.CourseStatisticsEntity;
import com.lqwawa.intleducation.factory.data.entity.course.LearningProgressEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * 班级学程课程统计的契约类
 */
public interface CourseStatisticsContract {

    interface Presenter extends BaseContract.Presenter{
        void requestCourseStatisticsData(@NonNull String classId, @NonNull String courseId);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateLearningStatisticsView(@NonNull List<CourseStatisticsEntity> entities);
    }

}
