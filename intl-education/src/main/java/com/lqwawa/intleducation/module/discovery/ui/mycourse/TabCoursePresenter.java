package com.lqwawa.intleducation.module.discovery.ui.mycourse;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * @author medici
 * @desc 主页面我的课程页面，显示我的自主学习和我的孩子学习的Presenter
 */
public class TabCoursePresenter extends BasePresenter<TabCourseContract.View>
    implements TabCourseContract.Presenter{

    public TabCoursePresenter(TabCourseContract.View view) {
        super(view);
    }
}
