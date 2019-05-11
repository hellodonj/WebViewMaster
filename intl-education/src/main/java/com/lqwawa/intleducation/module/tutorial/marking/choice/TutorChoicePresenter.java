package com.lqwawa.intleducation.module.tutorial.marking.choice;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorChoiceEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorEntity;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅老师选择的Presenter
 */
public class TutorChoicePresenter extends BasePresenter<TutorChoiceContract.View>
        implements TutorChoiceContract.Presenter {

    public TutorChoicePresenter(TutorChoiceContract.View view) {
        super(view);
    }

    @Override
    public void requestAddAssistTask(@NonNull final TutorChoiceEntity entity, @NonNull String object) {
        TutorialHelper.requestAddAssistTask(object, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorChoiceContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final TutorChoiceContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.updateAddAssistTaskView(entity);
                }
            }
        });
    }

    @Override
    public void requestChoiceTutorData(@NonNull String memberId, @Nullable String courseId, @Nullable String chapterId, int pageIndex) {
        CourseHelper.requestTutorsByCourseId(memberId, courseId, chapterId, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<TutorChoiceEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorChoiceContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<TutorChoiceEntity> entities) {
                final TutorChoiceContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    if (pageIndex == 0) {
                        view.updateChoiceTutorView(entities);
                    } else {
                        view.updateMoreChoiceTutorView(entities);
                    }
                }
            }
        });


    }

    @Override
    public void requestChoiceTutorData(@NonNull String memberId, int pageIndex) {
        CourseHelper.requestMyTutorData(memberId, "", pageIndex, AppConfig.PAGE_SIZE,
                new DataSource.Callback<List<TutorEntity>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final TutorChoiceContract.View view = getView();
                        if (EmptyUtil.isNotEmpty(view)) {
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(List<TutorEntity> entities) {
                        final TutorChoiceContract.View view = getView();
                        if (EmptyUtil.isNotEmpty(view)) {
                            List<TutorChoiceEntity> tutorChoiceEntities = transferData(entities);
                            if (pageIndex == 0) {
                                view.updateChoiceTutorView(tutorChoiceEntities);
                            } else {
                                view.updateMoreChoiceTutorView(tutorChoiceEntities);
                            }
                        }
                    }
                });
    }

    private List<TutorChoiceEntity> transferData(List<TutorEntity> tutorEntities) {
        List<TutorChoiceEntity> tutorChoiceEntities = new ArrayList<>();
        if (tutorEntities != null && !tutorEntities.isEmpty()) {
            for (TutorEntity tutorEntity : tutorEntities) {
                TutorChoiceEntity tutorChoiceEntity = TutorChoiceEntity.build(tutorEntity);
                if (tutorChoiceEntity != null) {
                    tutorChoiceEntities.add(tutorChoiceEntity);
                }
            }
        }

        return tutorChoiceEntities;
    }
}
