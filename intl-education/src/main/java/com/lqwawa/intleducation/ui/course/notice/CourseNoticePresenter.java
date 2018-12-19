package com.lqwawa.intleducation.ui.course.notice;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课程详情公告列表的Presenter
 * @date 2018/04/09 17:03
 * @history v1.0
 * **********************************
 */
public class CourseNoticePresenter extends BasePresenter<CourseNoticeContract.View>
        implements CourseNoticeContract.Presenter{

    public CourseNoticePresenter(CourseNoticeContract.View view) {
        super(view);
    }
}
