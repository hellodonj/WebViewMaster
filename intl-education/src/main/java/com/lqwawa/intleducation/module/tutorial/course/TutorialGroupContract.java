package com.lqwawa.intleducation.module.tutorial.course;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅群的契约类
 */
public interface TutorialGroupContract {

    interface Presenter extends BaseContract.Presenter{
        void requestTutorDataByCourseId(@NonNull String courseId,
                                        @NonNull String memberId,
                                        int pageIndex);

        // 对帮辅老师进行关注
        void requestAddTutorByStudentId(@NonNull String memberId,@NonNull String tutorMemberId,@NonNull String tutorName);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateTutorView(List<TutorialGroupEntity> entities);
        void updateMoreTutorView(List<TutorialGroupEntity> entities);
        void updateAddTutorByStudentIdView(boolean result);
    }

}
