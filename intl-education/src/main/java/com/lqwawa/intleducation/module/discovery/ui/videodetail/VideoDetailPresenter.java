package com.lqwawa.intleducation.module.discovery.ui.videodetail;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.VideoResourceEntity;
import com.lqwawa.intleducation.factory.helper.VideoDetailHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.videodetail.videocomment.VideoCommentContract;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;

import java.util.List;

/**
 * @author: wangchao
 * @date: 2019/05/07
 * @desc 视频馆视频详情Presenter
 */
public class VideoDetailPresenter extends BasePresenter<VideoDetailContract.View>
        implements VideoDetailContract.Presenter {

    public VideoDetailPresenter(VideoDetailContract.View view) {
        super(view);
    }

    @Override
    public void requestResources(long chapterId) {
        VideoDetailHelper.getSRListByChapterId(chapterId, new DataSource.Callback<List<VideoResourceEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                VideoDetailContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<VideoResourceEntity> entities) {
                VideoDetailContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.updateResourcesView(entities);
                }
            }
        });
    }

    @Override
    public void requestComments(long courseId, int pageIndex, int pageSize) {
        VideoDetailHelper.getVideoCommentList(courseId, pageIndex, pageSize, new DataSource.Callback<List<CommentVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                VideoDetailContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CommentVo> commentVos) {
                VideoDetailContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.updateCommentsView(commentVos);
                }
            }
        });
    }

    @Override
    public void addComment(String memberId, long courseId, String content) {
        VideoDetailHelper.addVideoComment(memberId, courseId, content, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                VideoDetailContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                VideoDetailContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.addComment(aBoolean);
                }
            }
        });
    }
}
