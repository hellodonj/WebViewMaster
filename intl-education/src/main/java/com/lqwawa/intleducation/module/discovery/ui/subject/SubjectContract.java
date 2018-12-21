package com.lqwawa.intleducation.module.discovery.ui.subject;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * 科目设置的契约类
 */
public interface SubjectContract {

    interface Presenter extends BaseContract.Presenter{
        void requestTeacherConfigData(@NonNull String memberId);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateTeacherConfigView(@NonNull List<LQCourseConfigEntity> entities);
    }

}
