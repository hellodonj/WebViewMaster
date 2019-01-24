package com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.home;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @author mrmedici
 * @desc 国家课程二级列表筛选的契约类
 */
public interface LQBasicFiltrateContract {

    interface Presenter extends BaseContract.Presenter{
        void requestBasicConfigData(int parentId, int dataType);

        /**
         * 获取符合筛选条件的课程数据 加载更多
         * @param more true 加载更多
         * @param pageIndex 分页数
         * @param pageSize 每页数据量
         * @param level 分类级别
         * @param keyString 搜索关键词
         * @param paramOneId 筛选条件1
         * @param paramTwoId 筛选条件2
         * @param paramThreeId 筛选条件3
         */
        void requestCourseData(boolean more, int pageIndex, int pageSize, @NonNull String level, String keyString, int paramOneId, int paramTwoId, int paramThreeId);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateBasicConfigView(@NonNull List<LQCourseConfigEntity> entities);

        // 回调课程
        void updateCourseView(@NonNull List<CourseVo> courseVos);
        // 回调分页课程
        void updateMoreCourseView(@NonNull List<CourseVo> courseVos);
    }

}
