package com.lqwawa.intleducation.module.discovery.lessontask.missionrequire;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 任务要求的Presenter
 * @date 2018/04/13 10:22
 * @history v1.0
 * **********************************
 */
public class MissionRequirePresenter extends BasePresenter<MissionRequireContract.View>
    implements MissionRequireContract.Presenter{

    public MissionRequirePresenter(MissionRequireContract.View view) {
        super(view);
    }
}
