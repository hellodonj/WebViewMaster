package com.lqwawa.intleducation.module.discovery.ui.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.course.NotPurchasedChapterEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

/**
 * @desc LQ学程立即购买的契约类
 * @author medici
 */
public class LQCourseOrderContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取章未购买信息与课程信息
        void requestNotPurchasedChapter(@NonNull String memberId,
                                        @Nullable String courseId,
                                        @Nullable String id);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateNotPurchasedView(@NonNull NotPurchasedChapterEntity entity);
    }

}
