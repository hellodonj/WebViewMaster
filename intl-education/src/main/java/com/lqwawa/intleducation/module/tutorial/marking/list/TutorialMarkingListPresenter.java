package com.lqwawa.intleducation.module.tutorial.marking.list;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * @author mrmedici
 * @desc 帮辅批阅列表页面的Presenter
 */
public class TutorialMarkingListPresenter extends BasePresenter<TutorialMarkingListContract.View>
    implements TutorialMarkingListContract.Presenter {

    public TutorialMarkingListPresenter(TutorialMarkingListContract.View view) {
        super(view);
    }

}
