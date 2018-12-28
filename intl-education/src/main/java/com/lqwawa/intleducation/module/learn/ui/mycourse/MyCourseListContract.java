package com.lqwawa.intleducation.module.learn.ui.mycourse;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc V5.12版本新添加我的习课程标签筛选页面的契约类
 *
 */
public interface MyCourseListContract {

    interface Presenter extends BaseContract.Presenter{
        void requestStudentConfigData(@NonNull String memberId);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateStudentConfigView(@NonNull List<LQCourseConfigEntity> entities);
    }

}
