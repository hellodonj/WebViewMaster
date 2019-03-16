package com.lqwawa.intleducation.module.tutorial.marking.require;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * @author mrmedici
 * @desc 帮辅批阅页面，任务要求的Presenter
 */
public class TaskRequirementPresenter extends BasePresenter<TaskRequirementContract.View>
    implements TaskRequirementContract.Presenter{

    public TaskRequirementPresenter(TaskRequirementContract.View view) {
        super(view);
    }
}
