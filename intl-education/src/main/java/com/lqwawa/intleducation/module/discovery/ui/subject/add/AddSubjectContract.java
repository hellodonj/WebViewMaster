package com.lqwawa.intleducation.module.discovery.ui.subject.add;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * 添加科目的契约类
 */
public interface AddSubjectContract {

    interface Presenter extends BaseContract.Presenter{
        void requestAssignConfigData(@NonNull String memberId);
        String getSelectedIds(@NonNull List<LQCourseConfigEntity> entities);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateAssignConfigView(@NonNull List<LQCourseConfigEntity> entities);
    }

}
