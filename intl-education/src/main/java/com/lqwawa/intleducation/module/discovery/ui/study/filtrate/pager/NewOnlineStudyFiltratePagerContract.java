package com.lqwawa.intleducation.module.discovery.ui.study.filtrate.pager;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyType;
import com.lqwawa.intleducation.module.onclass.OnlineSortType;
import com.lqwawa.intleducation.module.onclass.pager.OnlineClassPagerContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc V5.11讲授课堂类型筛选Pager的契约类
 */
public interface NewOnlineStudyFiltratePagerContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取在线课堂班级列表
        void requestOnlineClassData(@NonNull String keyWord,
                                    int pageIndex, int pageSize,
                                    @OnlineStudyType.OnlineStudyRes int sort,
                                    int firstId, int secondId, int thirdId, int fourthId);
    }

    interface View extends BaseContract.View<Presenter>{
        // 在线课堂班级列表数据回调
        void updateOnlineClassView(@NonNull List<OnlineClassEntity> entities);
        // 在线课堂班级列表更多数据回调
        void updateMoreOnlineClassView(@NonNull List<OnlineClassEntity> entities);
    }

}
