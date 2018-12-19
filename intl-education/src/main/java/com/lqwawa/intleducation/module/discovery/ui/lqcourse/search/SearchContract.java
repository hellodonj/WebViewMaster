package com.lqwawa.intleducation.module.discovery.ui.lqcourse.search;

import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 搜索页面的契约类
 * @date 2018/05/05 13:55
 * @history v1.0
 * **********************************
 */
public interface SearchContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取历史搜索记录
        List<String> getSearchHistories(int count);

        // 保存新增的搜索记录
        void updateSearchHistories(List<String> histories);

        // 清除所有的搜索记录
        void clearAllSearchHistories(int count);
    }

    interface View extends BaseContract.View<Presenter>{

    }

}
