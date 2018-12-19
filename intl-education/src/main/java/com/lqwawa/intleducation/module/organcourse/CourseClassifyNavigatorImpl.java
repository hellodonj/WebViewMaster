package com.lqwawa.intleducation.module.organcourse;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function Holder回调接口定义实现类
 * @date 2018/08/02 10:32
 * @history v1.0
 * **********************************
 */
public abstract class CourseClassifyNavigatorImpl implements CourseClassifyNavigator{

    /**
     * 点击分类标题 进入相关Holder列表
     * @param entity 数据实体 有child
     */
    public abstract void onClickConfigTitleLayout(@NonNull LQCourseConfigEntity entity);

    /**
     * 点击某个课程,进入课程详情
     * @param entity 分类信息
     * @param courseVo 课程实体
     */
    public void onClickCourse(@NonNull LQCourseConfigEntity entity,@NonNull CourseVo courseVo){

    }

}
