package com.lqwawa.intleducation.module.tutorial.classtutor;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @desc 班级帮辅的契约类
 */
public interface ClassTutorContract {

    interface Presenter extends BaseContract.Presenter {

        void requestClassTutors(@NonNull String classId);

        void requestDeleteClassTutor(@NonNull String classId, @NonNull String tutorMemberId);

        // 对帮辅老师进行关注
        void requestAddTutorByStudentId(@NonNull String memberId, @NonNull String tutorMemberId, @NonNull String tutorName);
    }

    interface View extends BaseContract.View<Presenter> {
        
        void updateTutorView(List<TutorialGroupEntity> entities);

        void updateDeleteClassTutorView(boolean result);

        void updateAddTutorByStudentIdView(boolean result);
    }

}
