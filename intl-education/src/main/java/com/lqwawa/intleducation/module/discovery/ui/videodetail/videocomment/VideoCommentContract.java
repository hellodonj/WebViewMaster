package com.lqwawa.intleducation.module.discovery.ui.videodetail.videocomment;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;

import java.util.List;

/**
 * @author: wangchao
 * @date: 2019/05/07
 * @desc: 视频馆视频列表契约类
 */
public interface VideoCommentContract {
    interface Presenter extends BaseContract.Presenter {
        // 获取视频评论列表
        void requestComments(long courseId, int pageIndex, int pageSize);

        // 发送视频评论
        void addComment(String memberId, long courseId, String content);
    }

    interface View extends BaseContract.View<VideoCommentContract.Presenter> {
        // 获取视频评论列表回调
        void updateCommentsView(List<CommentVo> comments);
        // 发送视频评论回调
        void addComment(boolean isSuccess);
    }
}
