package com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.learn;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.LearningProgressEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.learn.LearningStatisticsContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 学习统计的Presenter
 */
public class LearningStatisticsPresenter extends BasePresenter<LearningStatisticsContract.View> implements LearningStatisticsContract.Presenter{

    public LearningStatisticsPresenter(LearningStatisticsContract.View view) {
        super(view);
    }

    @Override
    public void requestLearningStatisticsData(@NonNull String classId, @NonNull String courseId, int type) {
        OnlineCourseHelper.requestLearningStatisticsData(classId, courseId, type, new DataSource.Callback<List<LearningProgressEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final LearningStatisticsContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LearningProgressEntity> entities) {
                final LearningStatisticsContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateLearningStatisticsView(entities);
                }
            }
        });
    }
}
