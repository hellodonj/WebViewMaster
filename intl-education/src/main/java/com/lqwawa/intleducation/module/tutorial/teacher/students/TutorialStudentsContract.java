package com.lqwawa.intleducation.module.tutorial.teacher.students;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.tutorial.AssistStudentEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @authr mrmedici
 * @desc 学生的帮辅列表契约类
 */
public interface TutorialStudentsContract {

    interface Presenter extends BaseContract.Presenter {
        // 请求我帮辅的学生列表
        void requestTutorialStudentData(@NonNull String memberId, @NonNull String name, int pageIndex);

    }

    interface View extends BaseContract.View<Presenter>{
        void updateTutorialStudentView(List<AssistStudentEntity> entities);
        void updateMoreTutorialStudentView(List<AssistStudentEntity> entities);
    }

}
