package com.lqwawa.intleducation.module.tutorial.teacher.courses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @authr mrmedici
 * @desc 我帮辅的课程的Presenter
 */
public class TutorialCoursesPresenter extends BasePresenter<TutorialCoursesContract.View>
    implements TutorialCoursesContract.Presenter{

    public TutorialCoursesPresenter(TutorialCoursesContract.View view) {
        super(view);
    }

    @Override
    public void requestTutorialCoursesData(@NonNull String memberId, @Nullable String name, int pageIndex) {
        TutorialHelper.requestTutorialCourses(memberId, name, 1, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialCoursesContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final TutorialCoursesContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        view.updateTutorialCoursesView(courseVos);
                    }else{
                        view.updateMoreTutorialCoursesView(courseVos);
                    }
                }
            }
        });
    }
}
