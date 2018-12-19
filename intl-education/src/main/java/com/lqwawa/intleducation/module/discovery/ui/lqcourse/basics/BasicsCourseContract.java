package com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 基础课程列表的契约类
 */
public class BasicsCourseContract {

    interface Presenter extends BaseContract.Presenter{
        // 点击基础课程科目
        void requestConfigWithBasicsEntity(@NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity);
    }

    interface View extends BaseContract.View<Presenter>{
        // 点击基础课程科目后跳转的回调
        void updateConfigView(@NonNull int parentId, @NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity, @NonNull List<LQCourseConfigEntity> entities);
    }

}
