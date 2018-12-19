package com.lqwawa.intleducation.module.discovery.lessontask.committedtask;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 任务提交列表的Fragment
 * @date 2018/04/13 09:35
 * @history v1.0
 * **********************************
 */
public class CommittedTaskContract {


    interface Presenter extends BaseContract.Presenter{

    }

    interface View extends BaseContract.RecyclerView<Presenter,LqTaskCommitVo>{

    }

}
