package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 我的学程契约类
 * @date 2018/04/27 14:55
 * @history v1.0
 * **********************************
 */
public interface LQCourseContract {

    interface View extends BaseContract.View<Presenter>{
        /**
         * 获取数据回调更新轮播图
         * @param urlBanners 轮播图数据
         */
        void updateBannerViews(@NonNull List<String> urlBanners);

        /**
         * 获取分类数据,回调给View进行数据刷新
         * @param entities 数据源
         */
        void updateConfigViews(@NonNull List<LQCourseConfigEntity> entities);

        /**
         * 获取到直播数据,回调给View进行数据刷新
         * @param liveVos
         */
        void updateLiveView(@NonNull List<LiveVo> liveVos);

        /**
         * 回调热门频道数据,进行UI刷新
         * @param courseVos courseVos 热门数据
         */
        void updateHotCourseView(@NonNull List<CourseVo> courseVos);

        void updateNewBasicsConfigView(@NonNull List<LQBasicsOuterEntity> entities);
        // 点击基础课程科目后跳转的回调
        void updateConfigView(@NonNull int parentId, @NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity, @NonNull List<LQCourseConfigEntity> entities);
    }

    interface Presenter extends BaseContract.Presenter{
        // 获取新的基础课程数据
        void requestNewBasicConfig();

        // 点击基础课程科目
        void requestConfigWithBasicsEntity(@NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity);
    }

}
