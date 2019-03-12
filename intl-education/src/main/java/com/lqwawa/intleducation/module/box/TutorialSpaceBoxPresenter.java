package com.lqwawa.intleducation.module.box;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * @author medici
 * @desc 主帮辅盒子页面，显示我的课程以及帮辅空间的Presenter
 */
public class TutorialSpaceBoxPresenter extends BasePresenter<TutorialSpaceBoxContract.View>
    implements TutorialSpaceBoxContract.Presenter{

    public TutorialSpaceBoxPresenter(TutorialSpaceBoxContract.View view) {
        super(view);
    }
}
