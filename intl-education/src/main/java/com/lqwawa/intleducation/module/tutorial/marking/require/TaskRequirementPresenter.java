package com.lqwawa.intleducation.module.tutorial.marking.require;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LearningTaskHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.learn.vo.LqTaskInfoVo;

/**
 * @author mrmedici
 * @desc 帮辅批阅页面，任务要求的Presenter
 */
public class TaskRequirementPresenter extends BasePresenter<TaskRequirementContract.View>
    implements TaskRequirementContract.Presenter{

    public TaskRequirementPresenter(TaskRequirementContract.View view) {
        super(view);
    }

    @Override
    public void requestTaskInfoByTaskId(@NonNull String taskId) {
        LearningTaskHelper.requestTaskInfoByTaskId(taskId, new DataSource.Callback<LqTaskInfoVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TaskRequirementContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(LqTaskInfoVo taskInfoVo) {
                final TaskRequirementContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateTaskInfoByTaskIdView(taskInfoVo);
                }
            }
        });
    }
}
