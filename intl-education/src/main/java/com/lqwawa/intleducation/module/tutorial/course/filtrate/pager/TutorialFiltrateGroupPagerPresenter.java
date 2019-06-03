package com.lqwawa.intleducation.module.tutorial.course.filtrate.pager;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅筛选片段页面的Presenter
 */
public class TutorialFiltrateGroupPagerPresenter extends BasePresenter<TutorialFiltrateGroupPagerContract.View>
        implements TutorialFiltrateGroupPagerContract.Presenter {

    public TutorialFiltrateGroupPagerPresenter(TutorialFiltrateGroupPagerContract.View view) {
        super(view);
    }

    @Override
    public void requestTutorDataByParams(@NonNull String level, int paramOneId, int paramTwoId,
                                         int paramThereId, String sort, int pageIndex,
                                         String classId) {
        TutorialHelper.requestTutorData(level, paramOneId, paramTwoId, paramThereId, sort,
                pageIndex, AppConfig.PAGE_SIZE,
                classId, new DataSource.Callback<List<TutorialGroupEntity>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final TutorialFiltrateGroupPagerContract.View view = getView();
                        if (EmptyUtil.isNotEmpty(view)) {
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(List<TutorialGroupEntity> tutorialGroupEntities) {
                        final TutorialFiltrateGroupPagerContract.View view = getView();
                        if (EmptyUtil.isNotEmpty(view)) {
                            if (pageIndex == 0) {
                                view.updateTutorView(tutorialGroupEntities);
                            } else {
                                view.updateMoreTutorView(tutorialGroupEntities);
                            }
                        }
                    }
                });
    }

    @Override
    public void requestAddTutor(@NonNull String memberId, @NonNull String tutorMemberId, @NonNull String tutorName, @NonNull String classId) {
        TutorialHelper.requestAddTutor(memberId, tutorMemberId, tutorName,
                classId, new DataSource.Callback<Boolean>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final TutorialFiltrateGroupPagerContract.View view = getView();
                        if (EmptyUtil.isNotEmpty(view)) {
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(Boolean aBoolean) {
                        final TutorialFiltrateGroupPagerContract.View view = getView();
                        if (EmptyUtil.isNotEmpty(view)) {
                            view.updateAddTutorView(aBoolean);
                        }
                    }
                });
    }
}
