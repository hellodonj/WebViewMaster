package com.lqwawa.intleducation.module.discovery.ui.videodetail.videocomment;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.VideoDetailHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;

import java.util.List;

/**
 * @author: wangchao
 * @date: 2019/05/07
 * @desc 视频馆视频评论列表Presenter
 */
public class VideoCommentPresenter extends BasePresenter<VideoCommentContract.View>
        implements VideoCommentContract.Presenter {

    public VideoCommentPresenter(VideoCommentContract.View view) {
        super(view);
    }


    @Override
    public void requestComments(long courseId, int pageIndex, int pageSize) {
        VideoDetailHelper.getVideoCommentList(courseId, pageIndex, pageSize, new DataSource.Callback<List<CommentVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                VideoCommentContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CommentVo> commentVos) {
                VideoCommentContract.View view = getView();
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
                VideoCommentContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                VideoCommentContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.addComment(aBoolean);
                }
            }
        });
    }
}
