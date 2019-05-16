package com.lqwawa.intleducation.module.organcourse.filtrate.pager;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.organcourse.filtrate.OrganCourseFiltrateParams;

import java.util.List;

/**
 * @author wangchao
 * @desc 学程馆二级筛选子页面
 */
public interface OrganCourseFiltratePagerContract {
    interface Presenter extends BaseContract.Presenter {
        /**
         * 获取符合筛选条件的课程数据
         * @param more 是否是加载更多
         * @param organId 机构Id
         * @param pageIndex 分页数
         * @param pageSize 每页数据量
         */
        void requestCourseData(boolean more, int pageIndex,
                               int pageSize, OrganCourseFiltrateParams params);
        /**
         * 获取符合筛选条件的选择资源课程数据
         * @param more 是否是加载更多
         * @param organId 机构Id
         * @param pageIndex 分页数
         * @param pageSize 每页数据量
         * @param keyString 搜索关键词
         * @param level 级别
         */
        void requestCourseResourceData(boolean more, int pageIndex, int pageSize,
                                       OrganCourseFiltrateParams params);
    }

    interface View extends BaseContract.View<Presenter> {
        // 获取到符合筛选条件的课程
        void onCourseLoaded(List<CourseVo> courseVos);

        // 获取到更多数据
        void onMoreCourseLoaded(List<CourseVo> courseVos);

        // 获取到符合筛选条件的选择资源课程
        void onCourseResourceLoaded(List<CourseVo> courseVos);

        // 获取到更多选择资源数据
        void onMoreCourseResourceLoaded(List<CourseVo> courseVos);
    }
}
