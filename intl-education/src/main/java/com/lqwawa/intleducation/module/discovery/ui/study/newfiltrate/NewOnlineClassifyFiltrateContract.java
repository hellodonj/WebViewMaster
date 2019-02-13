package com.lqwawa.intleducation.module.discovery.ui.study.newfiltrate;

import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 小语种,国际课程,国家课程 在线课堂更多页面的契约类
 */
public interface NewOnlineClassifyFiltrateContract {

    interface Presenter extends BaseContract.Presenter{
        void requestNewOnlineClassifyConfigData(@Nullable NewOnlineClassifyFiltrateActivity.DataType dataType);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateNewOnlineClassifyConfigView(@Nullable List<NewOnlineConfigEntity> entities);
    }

}
