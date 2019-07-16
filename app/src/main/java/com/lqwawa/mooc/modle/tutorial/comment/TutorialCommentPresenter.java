package com.lqwawa.mooc.modle.tutorial.comment;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorCommentEntity;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅主页评论列表的契约类
 */
public class TutorialCommentPresenter extends BasePresenter<TutorialCommentContract.View> implements TutorialCommentContract.Presenter {

    public TutorialCommentPresenter(TutorialCommentContract.View view) {
        super(view);
    }

    @Override
    public void requestTutorialCommentData(@NonNull String memberId, @NonNull String tutorMemberId, int pageIndex) {
        TutorialHelper.requestTutorCommentData(memberId, tutorMemberId, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<TutorCommentEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialCommentContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<TutorCommentEntity> entities) {
                final TutorialCommentContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.updateTutorialCommentView(entities);
                }
            }
        });
    }

    @Override
    public void requestSingleCommentChangeStatus(@NonNull String memberId, int commentId, int status) {
        TutorialHelper.requestTutorSingleCommentState(memberId, commentId, status, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialCommentContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final TutorialCommentContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.updateSingleCommentChangeStatusView(aBoolean, status, 1 - status);
                }
            }
        });
    }

    @Override
    public void requestSendTutorialComment(@NonNull String memberId, @NonNull String tutorMemberId, @NonNull String content, @NonNull int starLevel) {
        TutorialHelper.requestAddTutorialComment(memberId, tutorMemberId, content, starLevel, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialCommentContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final TutorialCommentContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.updateSendTutorialCommentView(aBoolean);
                }
            }
        });
    }

    @Override
    public void requestAddCommentPraise(@NonNull String memberId, int commentId) {
        TutorialHelper.requestAddPraiseByCommentId(memberId, commentId, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialCommentContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final TutorialCommentContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.updateAddCommentPraiseView(aBoolean);
                }
            }
        });
    }
}
