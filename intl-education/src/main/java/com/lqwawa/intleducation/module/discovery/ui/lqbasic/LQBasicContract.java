package com.lqwawa.intleducation.module.discovery.ui.lqbasic;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @author mrmedici
 * @desc 国家课程的契约类
 */
public interface LQBasicContract {

    interface Presenter extends BaseContract.Presenter{

        // 获取国家课程的标签
        void requestBasicCourseConfigData();
        // 获取国家课程的名称
        void requestConfigData();
        // 获取国家课程标签
        void requestNewBasicConfig();
        // 获取国家课程的热门数据和在线课堂
        void requestLQRmCourseData();
        // 点击基础课程科目
        void requestConfigWithBasicsEntity(@NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity);
    }


    interface View extends BaseContract.View<Presenter>{
        // 国家课程标签的回调
        void updateBasicCourseConfigView(@NonNull List<LQCourseConfigEntity> entities);
        /**
         * 获取分类数据,回调给View进行数据刷新
         * @param entities 数据源
         */
        void updateConfigViews(@NonNull List<LQCourseConfigEntity> entities);
        // 获取国家课程标签的回调
        void updateNewBasicsConfigView(@NonNull List<LQBasicsOuterEntity> entities);
        // 获取国家课程的热门数据的回调
        void updateLQRmCourseData(List<CourseVo> entities);
        // 获取国家课程的在线课堂的回调
        void updateLQRmOnlineCourseData(List<OnlineClassEntity> entities);
        // 点击基础课程科目后跳转的回调
        void updateConfigView(@NonNull int parentId, @NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity, @NonNull List<LQCourseConfigEntity> entities);
    }

}
