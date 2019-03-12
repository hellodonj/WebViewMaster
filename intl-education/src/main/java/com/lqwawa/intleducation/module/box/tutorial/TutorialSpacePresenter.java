package com.lqwawa.intleducation.module.box.tutorial;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.box.TutorialSpaceBoxContract;

/**
 * @author medici
 * @desc 帮辅空间的Presenter
 */
public class TutorialSpacePresenter extends BasePresenter<TutorialSpaceContract.View>
    implements TutorialSpaceContract.Presenter{

    public TutorialSpacePresenter(TutorialSpaceContract.View view) {
        super(view);
    }
}
