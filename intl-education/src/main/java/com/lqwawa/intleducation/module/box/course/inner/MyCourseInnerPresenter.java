package com.lqwawa.intleducation.module.box.course.inner;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * @author mrmedici
 * @desc 个人课程的Presenter
 */
public class MyCourseInnerPresenter extends BasePresenter<MyCourseInnerContract.View>
        implements MyCourseInnerContract.Presenter{

    public MyCourseInnerPresenter(MyCourseInnerContract.View view) {
        super(view);
    }
}
