package com.lqwawa.intleducation.module.tutorial.teacher.courses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.record.AuditType;

import java.util.List;

/**
 * @authr mrmedici
 * @desc 我帮辅的课程契约类
 */
public interface TutorialCoursesContract {

    interface Presenter extends BaseContract.Presenter {
        // 请求我帮辅的课程列表
        void requestTutorialCoursesData(@NonNull String memberId, @Nullable String name, int pageIndex);

    }

    interface View extends BaseContract.View<Presenter>{
        void updateTutorialCoursesView(List<CourseVo> courseVos);
        void updateMoreTutorialCoursesView(List<CourseVo> courseVos);
    }

}
