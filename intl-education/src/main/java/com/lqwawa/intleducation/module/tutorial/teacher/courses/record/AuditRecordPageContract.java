package com.lqwawa.intleducation.module.tutorial.teacher.courses.record;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @authr mrmedici
 * @desc 帮辅审核记录的契约类
 */
public interface AuditRecordPageContract {

    interface Presenter extends BaseContract.Presenter {
        // 请求我帮辅的课程列表
        void requestTutorialCoursesData(@NonNull String memberId, @Nullable String name,@NonNull @AuditType.AuditTypeRes int type, int pageIndex);

    }

    interface View extends BaseContract.View<Presenter>{
        void updateTutorialCoursesView(List<CourseVo> courseVos);
        void updateMoreTutorialCoursesView(List<CourseVo> courseVos);
    }

}
