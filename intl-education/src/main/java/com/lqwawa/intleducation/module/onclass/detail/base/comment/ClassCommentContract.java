package com.lqwawa.intleducation.module.onclass.detail.base.comment;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.OnlineCommentEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 学员评论的契约类
 * @date 2018/06/01 16:03
 * @history v1.0
 * **********************************
 */
public interface ClassCommentContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取在线课堂班级评论数据
        void requestOnlineClassCommentData(int id, int pageIndex);

        /**
         * 提交评论
         * @param type 0评论 1回复
         * @param courseId 班级Id
         * @param commentId 回复人的id
         * @param content 评论内容
         * @param starLevel 评星
         */
        void requestCommitComment(int type, int courseId, @NonNull String commentId, @NonNull String content, int starLevel);
    }

    interface View extends BaseContract.View<Presenter>{
        // 获取在线评论数据UI回调
        void updateOnlineClassCommentView(@NonNull OnlineCommentEntity onlineCommentEntity);
        void updateOnlineClassMoreCommentView(@NonNull OnlineCommentEntity onlineCommentEntity);
        // 提交评论，返回评论结果
        void commitCommentResult(boolean isSucceed);
    }

}
