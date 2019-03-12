package com.lqwawa.intleducation.module.tutorial.marking.list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.tutorial.DateFlagEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅批阅列表页面的契约类
 */
public interface TutorialMarkingListContract {

    interface Presenter extends BaseContract.Presenter{

    }

    interface View extends BaseContract.View<Presenter>{

    }

}
