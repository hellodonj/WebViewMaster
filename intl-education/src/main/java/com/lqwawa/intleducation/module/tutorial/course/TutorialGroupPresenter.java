package com.lqwawa.intleducation.module.tutorial.course;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅群的Presenter
 */
public class TutorialGroupPresenter extends BasePresenter<TutorialGroupContract.View>
    implements TutorialGroupContract.Presenter{

    public TutorialGroupPresenter(TutorialGroupContract.View view) {
        super(view);
    }

    @Override
    public void requestTutorDataByCourseId(@NonNull String courseId, @NonNull String memberId, int pageIndex) {
        CourseHelper.requestTutorDataByCourseId(courseId, memberId, 1, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<TutorialGroupEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialGroupContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<TutorialGroupEntity> tutorialGroupEntities) {
                final TutorialGroupContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        view.updateTutorView(tutorialGroupEntities);
                    }else{
                        view.updateMoreTutorView(tutorialGroupEntities);
                    }
                }
            }
        });
    }

    @Override
    public void requestAddTutorByStudentId(@NonNull String memberId, @NonNull String tutorMemberId, @NonNull String tutorName) {
        TutorialHelper.requestAddTutorByStudentId(memberId, tutorMemberId, tutorName, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialGroupContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final TutorialGroupContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateAddTutorByStudentIdView(aBoolean);
                }
            }
        });
    }
}
