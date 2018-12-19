package com.lqwawa.intleducation.module.discovery.ui.person.mygive;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * @author medici
 * @desc 我的授课契约类
 */
public class MyGiveInstructionPresenter extends BasePresenter<MyGiveInstructionContract.View>
    implements MyGiveInstructionContract.Presenter{

    public MyGiveInstructionPresenter(MyGiveInstructionContract.View view) {
        super(view);
    }
}
