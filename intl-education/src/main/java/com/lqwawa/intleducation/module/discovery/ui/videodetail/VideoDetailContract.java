package com.lqwawa.intleducation.module.discovery.ui.videodetail;

import com.lqwawa.intleducation.factory.data.entity.course.VideoResourceEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.ui.videodetail.videocomment.VideoCommentContract;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;

import java.util.List;

/**
 * @author: wangchao
 * @date: 2019/05/07
 * @desc: 视频馆视频详情契约类
 */
public interface VideoDetailContract {

    interface Presenter extends BaseContract.Presenter {
        // 获取配套资源列表
        void requestResources(long chapterId);

        // 获取视频评论列表
        void requestComments(long courseId, int pageIndex, int pageSize);

        // 发送视频评论
        void addComment(String memberId, long courseId, String content);
    }

    interface View extends BaseContract.View<VideoDetailContract.Presenter> {
        // 获取配套资源列表回调
        void updateResourcesView(List<VideoResourceEntity> entities);
        // 获取视频评论列表回调
        void updateCommentsView(List<CommentVo> comments);
        // 发送视频评论回调
        void addComment(boolean isSuccess);
    }
}
