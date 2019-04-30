package com.lqwawa.intleducation.module.organcourse.filtrate;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.organcourse.base.SchoolPermissionContract;
import com.lqwawa.intleducation.module.organcourse.base.SchoolPermissionPresenter;

import java.util.List;

/**
 * @desc 学程馆二级筛选页面
 * @author medici
 */
public interface OrganCourseFiltrateContract {

    interface Presenter extends SchoolPermissionContract.Presenter{
        // 获取机构相关的所有标签
        void requestOrganCourseLabelData(@NonNull String organId, @NonNull int parentId,
                                         @NonNull String level, int libraryType);

        /**
         * 获取符合筛选条件的课程数据
         * @param more 是否是加载更多
         * @param organId 机构Id
         * @param pageIndex 分页数
         * @param pageSize 每页数据量
         * @param keyString 搜索关键词
         * @param level 级别
         * @param paramOneId 筛选条件1
         * @param paramTwoId 筛选条件2
         * @param paramThreeId 筛选条件3
         */
        void requestCourseData(boolean more, @NonNull String organId, int pageIndex, int pageSize
                , String keyString, @NonNull String level, int paramOneId, int paramTwoId,
                               int paramThreeId, int libraryType);

        /**
         * 获取符合筛选条件的选择资源课程数据
         * @param more 是否是加载更多
         * @param organId 机构Id
         * @param pageIndex 分页数
         * @param pageSize 每页数据量
         * @param keyString 搜索关键词
         * @param level 级别
         */
        void requestCourseResourceData(boolean more, @NonNull String organId, int pageIndex, int pageSize, String keyString, @NonNull String level);
    }

    interface View extends SchoolPermissionContract.View<Presenter>{
        // 获取标签成功的回调
        void updateOrganCourseLabelView(@NonNull List<LQCourseConfigEntity> entities);

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
