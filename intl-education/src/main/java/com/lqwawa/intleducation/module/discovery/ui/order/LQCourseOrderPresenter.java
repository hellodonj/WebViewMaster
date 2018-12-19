package com.lqwawa.intleducation.module.discovery.ui.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.NotPurchasedChapterEntity;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * @desc Lq学程立即购买的Presenter
 * @author medici
 */
public class LQCourseOrderPresenter extends BasePresenter<LQCourseOrderContract.View>
    implements LQCourseOrderContract.Presenter{

    public LQCourseOrderPresenter(LQCourseOrderContract.View view) {
        super(view);
    }

    @Override
    public void requestNotPurchasedChapter(@NonNull String memberId, @Nullable String courseId, @Nullable String id) {
        CourseHelper.getPayCourseDetail(memberId,courseId, id, new DataSource.Callback<NotPurchasedChapterEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                LQCourseOrderContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(NotPurchasedChapterEntity entity) {
                LQCourseOrderContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && EmptyUtil.isNotEmpty(entity)){
                    view.updateNotPurchasedView(entity);
                }
            }
        });
    }
}
