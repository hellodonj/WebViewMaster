package com.lqwawa.mooc.modle.tutorial.comment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorCommentEntity;
import com.lqwawa.mooc.modle.tutorial.TutorialParams;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅主页评论列表页面
 */
public class TutorialCommentFragment extends PresenterFragment<TutorialCommentContract.Presenter>
    implements TutorialCommentContract.View{

    public static Fragment newInstance(@NonNull TutorialParams params){
        Fragment fragment = new TutorialCommentFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected TutorialCommentContract.Presenter initPresenter() {
        return new TutorialCommentPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return com.galaxyschool.app.wawaschool.R.layout.fragment_tutorial_comment;
    }

    @Override
    public void updateTutorialCommentView(@NonNull List<TutorCommentEntity> entities) {

    }

    @Override
    public void updateMoreTutorialCommentView(@NonNull List<TutorCommentEntity> entities) {

    }

    @Override
    public void updateSingleCommentChangeStatusView(boolean result, int oldStatus, int newStatus) {

    }

    @Override
    public void updateSendTutorialCommentView(@NonNull boolean result) {

    }

    @Override
    public void updateAddCommentPraiseView(boolean result) {

    }
}
