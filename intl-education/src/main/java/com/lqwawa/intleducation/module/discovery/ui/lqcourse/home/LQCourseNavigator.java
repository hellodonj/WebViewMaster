package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function Holder回调接口定义
 * @date 2018/05/02 10:32
 * @history v1.0
 * **********************************
 */
public interface LQCourseNavigator {

    /**
     * 点击分类标题 进入相关Holder列表
     * @param entity 数据实体 有child
     */
    void onClickConfigTitleLayout(@NonNull LQCourseConfigEntity entity);

    /**
     * 点击相关分类,进入筛选列表
     * @param entity 数据实体
     */
    void onClickClassify(@NonNull LQCourseConfigEntity entity);

    /**
     * 点击热门标题,咋回事,感觉什么都不用传
     */
    void onClickCourseTitleLayout();

    /**
     * 点击某个课程,进入课程详情
     * @param courseVo 课程实体
     */
    void onClickCourse(@NonNull CourseVo courseVo);

    /**
     * 点击直播标题
     */
    void onClickLiveTitleLayout();

    /**
     * 点击直播实体
     * @param liveVo 直播Item
     */
    void onClickLive(@NonNull LiveVo liveVo);

    /**
     * 点击基础课程科目
     * @param entity 科目实体
     */
    void onClickBasicsSubject(@NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity);

    /**
     * 点击基础课程标题
     */
    void onClickBasicsLayout();

}
