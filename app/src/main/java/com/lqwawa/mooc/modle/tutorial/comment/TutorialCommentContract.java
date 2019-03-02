package com.lqwawa.mooc.modle.tutorial.comment;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorCommentEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author mrmedici
 * 帮辅主页评论契约类
 */
public interface TutorialCommentContract {

    interface Presenter extends BaseContract.Presenter{
        // 请求评论列表
        void requestTutorialCommentData(@NonNull String memberId,@NonNull String tutorMemberId,int pageIndex);
        // 教辅对评论的显示隐藏
        void requestSingleCommentChangeStatus(@NonNull String memberId,int commentId,int status);
        // 发送评论
        void requestSendTutorialComment(@NonNull String memberId,@NonNull String tutorMemberId,@NonNull String content);
        void requestAddCommentPraise(@NonNull String memberId, int commentId);

    }

    interface View extends BaseContract.View<Presenter>{
        void updateTutorialCommentView(@NonNull List<TutorCommentEntity> entities);
        void updateMoreTutorialCommentView(@NonNull List<TutorCommentEntity> entities);
        void updateSingleCommentChangeStatusView(boolean result,int oldStatus,int newStatus);
        void updateSendTutorialCommentView(@NonNull boolean result);
        void updateAddCommentPraiseView(boolean result);
    }

}
