package com.lqwawa.intleducation.module.discovery.ui.classcourse.history;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.course.ClassCourseEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.organcourse.base.SchoolPermissionContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 班级历史学程的契约类
 */
public class HistoryClassCourseContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取班级学程标签
        void requestHistoryClassConfigData(@NonNull String hostId);

        void requestHistoryClassCourseData(@NonNull String classId,
                                    int role, @NonNull String name,
                                    @NonNull String level,
                                    int paramOneId, int paramTwoId,
                                    int pageIndex);
        // 班主任从历史班级中删除课程
        void requestDeleteCourseFromHistoryClass(@NonNull String token, @NonNull String classId, @NonNull String ids);
        // 老师添加历史课程
        void requestAddHistoryCourseFromClass(@NonNull String schoolId, @NonNull String classId, @NonNull List<ClassCourseEntity> entities);
        // 老师移除历史课程
        void requestRemoveHistoryCourseFromClass(@NonNull String schoolId, @NonNull String classId, @NonNull List<ClassCourseEntity> entities);
    }

    interface View extends BaseContract.View<Presenter>{

        void updateHistoryClassConfigView(@NonNull List<LQCourseConfigEntity> entities);
        void updateHistoryClassCourseView(List<ClassCourseEntity> entities);
        void updateMoreHistoryClassCourseView(List<ClassCourseEntity> entities);
        // 班主任从班级中删除课程的回调
        void updateDeleteCourseFromClassView(Boolean aBoolean);
        // 老师添加课程完成的回调
        void updateHistoryCourseFromClassView(Boolean aBoolean);

        // 是否触发更改历史学程的回调
        void triggerUpdate();
    }
}
