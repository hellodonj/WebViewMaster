package com.lqwawa.intleducation.module.tutorial.classtutor;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

import java.util.List;

/**
 * @desc 班级帮辅的Presenter
 */
public class ClassTutorPresenter extends BasePresenter<ClassTutorContract.View>
        implements ClassTutorContract.Presenter {

    public ClassTutorPresenter(ClassTutorContract.View view) {
        super(view);
    }


    @Override
    public void requestClassTutors(@NonNull String classId) {
        TutorialHelper.requestClassTutors(classId, new DataSource.Callback<List<TutorialGroupEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassTutorContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<TutorialGroupEntity> tutorialGroupEntities) {
                final ClassTutorContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.updateTutorView(tutorialGroupEntities);
                }
            }
        });
    }

    @Override
    public void requestDeleteClassTutor(@NonNull String tutorMemberId, @NonNull String classId) {
        TutorialHelper.requestDeleteClassTutor(tutorMemberId, classId,
                new DataSource.Callback<Boolean>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final ClassTutorContract.View view = getView();
                        if (EmptyUtil.isNotEmpty(view)) {
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(Boolean aBoolean) {
                        final ClassTutorContract.View view = getView();
                        if (EmptyUtil.isNotEmpty(view)) {
                            view.updateDeleteClassTutorView(aBoolean);
                        }
                    }
                });
    }

    @Override
    public void requestAddTutorByStudentId(@NonNull String memberId, @NonNull String tutorMemberId, @NonNull String tutorName) {
        TutorialHelper.requestAddTutorByStudentId(memberId, tutorMemberId, tutorName, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassTutorContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final ClassTutorContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.updateAddTutorByStudentIdView(aBoolean);
                }
            }
        });
    }
}
