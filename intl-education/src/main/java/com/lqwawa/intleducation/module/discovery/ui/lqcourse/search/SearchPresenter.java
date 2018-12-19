package com.lqwawa.intleducation.module.discovery.ui.lqcourse.search;

import android.content.Context;
import android.content.SharedPreferences;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.SPUtil;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 搜索页面的Presenter
 * @date 2018/05/05 13:58
 * @history v1.0
 * **********************************
 */
public class SearchPresenter extends BasePresenter<SearchContract.View>
    implements SearchContract.Presenter{

    private static final String KEY_SEARCH_STR = "SearchHistoryKey";

    public SearchPresenter(SearchContract.View view) {
        super(view);
    }

    @Override
    public List<String> getSearchHistories(int count) {
        List<String> mHistoriesKey = new ArrayList<>();
        String nameHead = UserHelper.isLogin() ? UserHelper.getUserId() : "";
        for (int i = 0; i < count; i++) {
            String key = nameHead.concat("-").concat(KEY_SEARCH_STR).concat(Integer.toString(i));
            String keyValue = SPUtil.getInstance().getString(key);
            if (!EmptyUtil.isEmpty(keyValue)) {
                mHistoriesKey.add(keyValue);
            }
        }
        return mHistoriesKey;
    }

    @Override
    public void updateSearchHistories(List<String> histories){
        String nameHead = UserHelper.isLogin() ? UserHelper.getUserId() : "";
        if (!EmptyUtil.isEmpty(histories)) {
            for (int i = 0; i < histories.size(); i++) {
                SPUtil.getInstance().put(nameHead.concat("-").concat(KEY_SEARCH_STR).concat(Integer.toString(i)),histories.get(i));
            }
        }
    }

    @Override
    public void clearAllSearchHistories(int count) {
        String nameHead = UserHelper.isLogin() ? UserHelper.getUserId() : "";
        for (int i = 0; i < count; i++) {
            SPUtil.getInstance().remove(nameHead.concat("-").concat(KEY_SEARCH_STR).concat(Integer.toString(i)));
        }
    }
}
