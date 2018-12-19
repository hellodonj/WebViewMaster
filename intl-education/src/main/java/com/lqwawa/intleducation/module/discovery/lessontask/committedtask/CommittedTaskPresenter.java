package com.lqwawa.intleducation.module.discovery.lessontask.committedtask;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.factory.presenter.BaseRecyclerPresenter;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课时小节详情页跳转的听说课,读写单详情的任务提交列表Presenter
 * @date 2018/04/13 09:37
 * @history v1.0
 * **********************************
 */
public class CommittedTaskPresenter extends BaseRecyclerPresenter<LqTaskCommitVo,CommittedTaskContract.View>
    implements CommittedTaskContract.Presenter{

    public CommittedTaskPresenter(CommittedTaskContract.View view) {
        super(view);
    }
}
