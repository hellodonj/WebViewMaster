package com.lqwawa.intleducation.module.discovery.ui.classcourse;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.course.ClassCourseEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.organcourse.base.SchoolPermissionContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 班级学程的契约类
 */
public class ClassCourseContract {

    interface Presenter extends SchoolPermissionContract.Presenter{
        // 获取班级学程标签
        void requestClassConfigData(@NonNull String hostId);
        // 选择学习任务的请求
        void requestStudyTaskClassCourseData(@NonNull String classId,@NonNull String name,int pageIndex);

        void requestClassCourseData(@NonNull String classId,
                                    int role, @NonNull String name,
                                    @NonNull String level,
                                    int paramOneId,int paramTwoId,
                                    int pageIndex);
        // 班主任从班级中删除课程
        void requestDeleteCourseFromClass(@NonNull String token, @NonNull String classId, @NonNull String ids);
        // 老师添加课程
        void requestAddCourseFromClass(@NonNull String schoolId, @NonNull String classId, @NonNull String courseIds);
    }

    interface View extends SchoolPermissionContract.View<Presenter>{

        void updateClassConfigView(@NonNull List<LQCourseConfigEntity> entities);
        // 选择学习任务的信息回调
        void updateStudyTaskClassCourseView(List<ClassCourseEntity> entities);
        void updateMoreStudyTaskClassCourseView(List<ClassCourseEntity> entities);

        void updateClassCourseView(List<ClassCourseEntity> entities);
        void updateMoreClassCourseView(List<ClassCourseEntity> entities);
        // 班主任从班级中删除课程的回调
        void updateDeleteCourseFromClassView(Boolean aBoolean);
        // 老师添加课程完成的回调
        void updateAddCourseFromClassView(Boolean aBoolean);
    }
}
