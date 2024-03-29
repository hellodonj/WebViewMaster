package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 筛选页面的契约类
 * @date 2018/05/03 16:54
 * @history v1.0
 * **********************************
 */
public class CourseFiltrateContract {

    interface Presenter extends BaseContract.Presenter{

        /**
         * 获取分类数据
         * @param level 级别
         * @param parentId 父类Id
         */
        void requestConfigData(@NonNull int level, @NonNull int parentId);

        /**
         * 获取符合筛选条件的课程数据
         * @param pageIndex 分页数
         * @param pageSize 每页数据量
         * @param level 分类级别
         * @param keyString 搜索关键词
         * @param paramOneId 筛选条件1
         * @param paramTwoId 筛选条件2
         * @param paramThreeId 筛选条件3
         */
        void requestCourseData(int pageIndex, int pageSize, @NonNull String level, String keyString, int paramOneId, int paramTwoId, int paramThreeId);

        /**
         * 获取符合筛选条件的课程数据 加载更多
         * @param pageIndex 分页数
         * @param pageSize 每页数据量
         * @param level 分类级别
         * @param keyString 搜索关键词
         * @param paramOneId 筛选条件1
         * @param paramTwoId 筛选条件2
         * @param paramThreeId 筛选条件3
         */
        void requestMoreCourseData(int pageIndex, int pageSize, @NonNull String level, String keyString, int paramOneId, int paramTwoId, int paramThreeId);
        // 分享
        void share(String title, String description, String thumbnailUrl, String url);
    }

    interface View extends BaseContract.View<Presenter>{
        // 获取到三级数据
        void onConfigLoaded(List<LQCourseConfigEntity> entities);

        // 获取到符合筛选条件的课程
        void onCourseLoaded(List<CourseVo> courseVos);

        // 获取到更多数据
        void onMoreCourseLoaded(List<CourseVo> courseVos);
    }

}
