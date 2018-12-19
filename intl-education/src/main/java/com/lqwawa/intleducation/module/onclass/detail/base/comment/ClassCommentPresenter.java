package com.lqwawa.intleducation.module.onclass.detail.base.comment;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.OnlineCommentEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 学员评论的Presenter
 * @date 2018/06/01 16:05
 * @history v1.0
 * **********************************
 */
public class ClassCommentPresenter extends BasePresenter<ClassCommentContract.View>
    implements ClassCommentContract.Presenter{

    public ClassCommentPresenter(ClassCommentContract.View view) {
        super(view);
    }

    @Override
    public void requestOnlineClassCommentData(int id,int pageIndex) {
        OnlineCourseHelper.requestOnlineCommentData(id, pageIndex,AppConfig.PAGE_SIZE,new DataSource.Callback<OnlineCommentEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassCommentContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(OnlineCommentEntity onlineCommentEntity) {
                final ClassCommentContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(onlineCommentEntity)){
                    if(pageIndex == 0){
                        view.updateOnlineClassCommentView(onlineCommentEntity);
                    }else{
                        view.updateOnlineClassMoreCommentView(onlineCommentEntity);
                    }
                }
            }
        });
    }

    @Override
    public void requestCommitComment(int type, int courseId, String commentId, @NonNull String content, int starLevel) {
        OnlineCourseHelper.requestCommitComment(type, courseId, commentId, content, starLevel, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassCommentContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    UIUtil.showToastSafe(UIUtil.getString(R.string.commit_comment) + UIUtil.getString(R.string.failed));
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final ClassCommentContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    if(aBoolean){
                        view.commitCommentResult(aBoolean);
                    }else{
                        UIUtil.showToastSafe(UIUtil.getString(R.string.commit_comment) + UIUtil.getString(R.string.failed));
                    }
                }
            }
        });
    }
}
