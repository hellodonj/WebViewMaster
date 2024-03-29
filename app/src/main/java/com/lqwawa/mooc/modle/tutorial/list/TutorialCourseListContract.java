package com.lqwawa.mooc.modle.tutorial.list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorCommentEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @author mrmedici
 * 帮辅主页帮辅课程契约类
 */
public interface TutorialCourseListContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取该帮辅老师的帮辅课程
        void requestTutorialCourseData(@NonNull String memberId, @Nullable String name,int type,int pageIndex);

    }

    interface View extends BaseContract.View<Presenter>{
        void updateTutorialCourseView(@Nullable List<CourseVo> courseVos);
        void updateMoreTutorialCourseView(@Nullable List<CourseVo> courseVos);

    }

}
