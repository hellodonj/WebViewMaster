package com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.home.pager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc Pager页面参数
 */
public class PagerParams implements Serializable {

    private String level;
    // 排序规则
    private String sort;
    // 搜索关键词
    private String keyWord;

    public PagerParams(@NonNull String level,@Nullable String keyWord, @NonNull String sort) {
        this.level = level;
        this.keyWord = keyWord;
        this.sort = sort;
    }

    public String getLevel() {
        return level;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public String getSort() {
        return sort;
    }
}
