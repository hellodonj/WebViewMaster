package com.lqwawa.intleducation.module.discovery.ui.coursedetail.apply;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * @author mrmedici
 * @desc 课程帮辅申请的Presenter
 */
public class TutorialCourseApplyForPresenter extends BasePresenter<TutorialCourseApplyForContract.View>
        implements TutorialCourseApplyForContract.Presenter {


    public TutorialCourseApplyForPresenter(TutorialCourseApplyForContract.View view) {
        super(view);
    }

    @Override
    public void requestApplyForCourseTutor(@NonNull String memberId,
                                           @NonNull int courseId,
                                           int type, int isOrganTutorStatus,
                                           @NonNull String realName, @NonNull String markingPrice,
                                           @NonNull String provinceId, @NonNull String provinceName,
                                           @NonNull String cityId, @NonNull String cityName,
                                           @NonNull String countyId, @NonNull String countyName,
                                           boolean isLqOrganTutor) {
        CourseHelper.requestApplyForCourseTutor(memberId, courseId,
                type, isOrganTutorStatus,
                realName, markingPrice,
                provinceId, provinceName,
                cityId, cityName,
                countyId, countyName,
                isLqOrganTutor, new DataSource.Callback<Boolean>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final TutorialCourseApplyForContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(Boolean aBoolean) {
                        final TutorialCourseApplyForContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.updateApplyForCourseTutor(aBoolean);
                        }
                    }
                });
    }
}
