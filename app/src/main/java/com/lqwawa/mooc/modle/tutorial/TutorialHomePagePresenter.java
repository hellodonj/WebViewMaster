package com.lqwawa.mooc.modle.tutorial;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource.Callback;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.helper.UserHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.factory.data.entity.TutorStarLevelEntity;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅模式助教个人主页的Presenter
 */
public class TutorialHomePagePresenter extends BasePresenter<TutorialHomePageContract.View>
    implements TutorialHomePageContract.Presenter{

    public TutorialHomePagePresenter(TutorialHomePageContract.View view) {
        super(view);
    }

    @Override
    public void requestUserInfoWithUserId(@NonNull String userId) {
        UserHelper.requestUserInfoWithUserId(userId, new Callback<UserEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialHomePageContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(UserEntity entity) {
                final TutorialHomePageContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) &&
                        EmptyUtil.isNotEmpty(entity)){
                    view.updateUserInfoView(entity);
                }
            }
        });
    }

    @Override
    public void requestTutorSubjectList(@NonNull String tutorMemberId) {
        UserHelper.requestTutorSubjectList(tutorMemberId, new Callback<List<String>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialHomePageContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<String> subjects) {
                final TutorialHomePageContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) &&
                        EmptyUtil.isNotEmpty(subjects)){
                    view.updateTutorSubjectView(subjects);
                }
            }
        });
    }

    @Override
    public void requestQueryAddedTutorState(@NonNull String memberId,
                                            @NonNull String tutorMemberId, String classId) {
        TutorialHelper.requestQueryAddedTutorByTutorId(memberId, tutorMemberId,
                classId, new Callback<Boolean>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final TutorialHomePageContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(Boolean aBoolean) {
                        final TutorialHomePageContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.updateQueryAddedTutorStateView(aBoolean);
                        }
                    }
                });
    }


    @Override
    public void requestAddTutor(@NonNull String memberId, @NonNull String tutorMemberId, @NonNull String tutorName, @NonNull String classId) {
        TutorialHelper.requestAddTutor(memberId, tutorMemberId, tutorName,
                classId, new Callback<Boolean>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final TutorialHomePageContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(Boolean aBoolean) {
                        final TutorialHomePageContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.updateAddTutorView(aBoolean);
                        }
                    }
                });
    }

    @Override
    public void requestTutorStarLevel(@NonNull String tutorMemberId) {
        UserHelper.requestTutorStarLevel(tutorMemberId, new Callback<TutorStarLevelEntity>() {
            @Override
            public void onDataLoaded(TutorStarLevelEntity starLevelEntities) {
                final TutorialHomePageContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) &&
                        EmptyUtil.isNotEmpty(starLevelEntities)){
                    view.updateTutorStarLevel(starLevelEntities);
                }
            }

            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialHomePageContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }
        });
    }
}
