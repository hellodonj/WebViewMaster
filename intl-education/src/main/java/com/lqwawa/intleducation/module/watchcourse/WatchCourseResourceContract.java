package com.lqwawa.intleducation.module.watchcourse;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 选择学程资源的契约类
 * @date 2018/06/26 11:13
 * @history v1.0
 * **********************************
 */
public interface WatchCourseResourceContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取课程详情,只或许学习计划
        void getCourseDetailWithCourseId(@NonNull String courseId);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateLoadedCourseDetailView(@NonNull CourseVo vo);
    }
}
