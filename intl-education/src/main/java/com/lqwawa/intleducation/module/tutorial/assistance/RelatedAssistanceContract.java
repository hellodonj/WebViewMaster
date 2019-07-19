package com.lqwawa.intleducation.module.tutorial.assistance;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * 描述: 三习教案关联教辅的Contract
 * 作者|时间: djj on 2019/7/19 0019 上午 11:00
 */
public interface RelatedAssistanceContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取关联教辅列表
        void requestSxRelationCourse(@NonNull String courseId,int pageIndex);

    }

    interface View extends BaseContract.View<Presenter>{
        // 返回关联教辅列表 SxRelationCourseEntity
        void updateSxRelationCourseView(@NonNull List<CourseVo> courseEntity);
        void updateMoreSxRelationCourseView(@NonNull List<CourseVo> courseEntity);
    }

}
