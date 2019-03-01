package com.lqwawa.mooc.modle.tutorial;

import com.lqwawa.intleducation.factory.presenter.BaseContract;

/**
 * @author mrmedici
 * @desc 帮辅模式助教个人主页的契约类
 */
public interface TutorialHomePageContract {

    interface Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.View<Presenter>{

    }
}
