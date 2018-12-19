package com.lqwawa.intleducation.module.organcourse.online.pager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @desc 在线机构学程馆分页Fragment的契约类
 * @author medici
 */
public interface CourseShopPagerContract {

    interface Presenter extends BaseContract.Presenter{
        /**
         * 获取符合筛选条件的课程数据
         * @param organId 机构Id
         * @param pageIndex 分页数
         * @param pageSize 每页数据量
         * @param sort 加载课程类型
         * @param payType 加载课程收费类型
         * @param keyString 搜索关键词
         */
        void requestCourseData(@Nullable String organId, int pageIndex, int pageSize, @NonNull String sort, int payType, String keyString);

        /**
         * 获取符合筛选条件的课程数据 加载更多
         * @param organId 机构Id
         * @param pageIndex 分页数
         * @param pageSize 每页数据量
         * @param sort 加载课程类型
         * @param payType 加载课程收费类型
         * @param keyString 搜索关键词
         */
        void requestMoreCourseData(@Nullable String organId, int pageIndex, int pageSize, @NonNull String sort, int payType, String keyString);
    }

    interface View extends BaseContract.View<Presenter>{
        // 获取到符合条件的课程
        void onCourseLoaded(List<CourseVo> courseVos);

        // 获取到更多数据
        void onMoreCourseLoaded(List<CourseVo> courseVos);

    }

}
