package com.lqwawa.intleducation.module.tutorial.student.courses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @authr mrmedici
 * @desc 学生的帮辅列表契约类
 */
public interface StudentTutorialContract {

    interface Presenter extends BaseContract.Presenter {
        // 请求我帮辅的课程列表
        void requestStudentTutorialData(@NonNull String memberId, @NonNull String tutorName, int pageIndex);

    }

    interface View extends BaseContract.View<Presenter>{
        void updateStudentTutorialView(List<TutorEntity> entities);
        void updateMoreStudentTutorialView(List<TutorEntity> entities);
    }

}
