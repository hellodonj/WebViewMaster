package com.lqwawa.intleducation.module.discovery.ui.study.filtrate.pager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc Pager页面参数
 */
public class PagerParams implements Serializable {

    // 排序规则
    private String sort;
    // 搜索关键词
    private String keyWord;

    public PagerParams(@Nullable String keyWord,@NonNull String sort) {
        this.keyWord = keyWord;
        this.sort = sort;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public String getSort() {
        return sort;
    }
}
