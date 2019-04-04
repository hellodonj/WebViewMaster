package com.lqwawa.intleducation.module.tutorial.marking.require;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.LqTaskInfoVo;

/**
 * @author mrmedici
 * @desc 帮辅批阅页面，任务要求的契约类
 */
public interface TaskRequirementContract {

    interface Presenter extends BaseContract.Presenter{
        void requestTaskInfoByTaskId(@NonNull String taskId);
    }

    interface View extends BaseContract.View<Presenter>{

        void updateTaskInfoByTaskIdView(@NonNull LqTaskInfoVo taskInfoVo);

    }

}
