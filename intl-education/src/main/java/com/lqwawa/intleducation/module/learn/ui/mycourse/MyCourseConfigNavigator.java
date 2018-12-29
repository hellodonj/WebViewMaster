package com.lqwawa.intleducation.module.learn.ui.mycourse;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

/**
 * @author mrmedici
 * @desc V5.12版本新添加我的习课程标签筛选页面的标签点击回调
 *
 */
public interface MyCourseConfigNavigator {

    // 点击习课程标签回调
    void onChoiceConfig(@NonNull LQCourseConfigEntity groupEntity,
                        @NonNull LQCourseConfigEntity childEntity,
                        @Nullable LQCourseConfigEntity configEntity);

}
