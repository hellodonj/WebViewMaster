package com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.course;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.CourseStatisticsEntity;
import com.lqwawa.intleducation.factory.data.entity.course.LearningProgressEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.learn.LearningStatisticsContract;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;

import java.util.List;

/**
 * @author mrmedici
 * @desc 课程统计的Presenter
 */
public class CourseStatisticsPresenter extends BasePresenter<CourseStatisticsContract.View> implements CourseStatisticsContract.Presenter{

    public CourseStatisticsPresenter(CourseStatisticsContract.View view) {
        super(view);
    }

    @Override
    public void requestCourseStatisticsData(@NonNull String classId, @NonNull String courseId) {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        OnlineCourseHelper.requestCourseStatisticsData(languageRes,classId, courseId, new DataSource.Callback<List<CourseStatisticsEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final CourseStatisticsContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CourseStatisticsEntity> entities) {
                final CourseStatisticsContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateLearningStatisticsView(entities);
                }
            }
        });
    }
}
