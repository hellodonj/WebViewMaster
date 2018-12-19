package com.lqwawa.intleducation.module.onclass.school;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.LQTeacherEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 机构信息片段的契约类
 * @date 2018/06/05 10:34
 * @history v1.0
 * **********************************
 */
public class SchoolInfoContract {

    interface Presenter extends BaseContract.Presenter{
        // 机构信息页面，获取机构课堂，在线课堂
        void requestOnlineSchoolInfoData(@NonNull String schoolId);
        // 获取班级详情信息
        void requestLoadClassInfo(@NonNull String classId);
        // 获取机构老师信息
        void requestOnlineSchoolTeacherData(@NonNull String schoolId);
    }


    interface View extends BaseContract.View<Presenter>{
        // 获取到机构信息相关课程的回调
        void updateOnlineSchoolCourseView(@NonNull List<CourseVo> courseVos);
        // 获取到机构信息相关班级的回调
        void updateOnlineSchoolClassView(@NonNull List<OnlineClassEntity> entities);
        // 用户已经参加该课程的回调
        void onClassCheckSucceed(@NonNull JoinClassEntity entity);
        // 获取到老师数据，进行UI回调
        void updateSchoolTeacherView(@NonNull List<LQTeacherEntity> entities);
    }

}
