package com.lqwawa.intleducation.module.discovery.ui.study.filtrate;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc V5.11讲授课堂类型筛选的契约类
 */
public interface NewOnlineStudyFiltrateContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取在线学习标签
        void requestOnlineStudyLabelData();
    }

    interface View extends BaseContract.View<Presenter>{
        // 在线学校标签数据的UI回调
        void updateOnlineStudyLabelView(@NonNull List<NewOnlineConfigEntity> entities);
    }

}
