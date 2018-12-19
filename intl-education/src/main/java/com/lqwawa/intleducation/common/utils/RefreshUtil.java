package com.lqwawa.intleducation.common.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/8/10 18:39
 * 描    述：下拉刷新保存选中的状态,支持单选/多选
 * 修订历史：
 * ================================================
 */

public class RefreshUtil {

    private ArrayList<String> IdList;


    private RefreshUtil() {

    }

    private static class HolderClass {
        private static final RefreshUtil instance = new RefreshUtil();
    }

    public static RefreshUtil getInstance() {

        return HolderClass.instance;
    }

    /**
     * 保存选中的资源
     *
     * @param id 传入资源id
     */
    public boolean contains(String id) {
        if (IdList == null) {
            return false;
        }
        for (int i = 0; i < IdList.size(); i++) {
            if (TextUtils.equals(id, IdList.get(i))) {
                return true;
            }
        }
        return false;
    }


    /**
     * 保存选中的资源
     *
     * @param id 传入资源id
     */
    public void addId(String id) {

        if (IdList == null) {
            IdList = new ArrayList<>();
        }
        if (!IdList.contains(id)) {

            IdList.add(id);
        }
    }

    /**
     * 删除选中的资源
     *
     * @param id 传入资源id
     */
    public void removeId(String id) {
        if (IdList == null) {
            IdList = new ArrayList<>();
        }
        if (IdList.contains(id)) {

            IdList.remove(id);
        }
    }

    /**
     * 清空数据
     */
    public void clear() {
        if (IdList != null) {
            IdList.clear();
            IdList = null;
        }
    }

    /**
     * 刷新后重置选中状态
     *
     * @param list
     */
    public <T>  void refresh( @NonNull List<T> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        if (IdList == null || IdList.size() <= 0) {
            return;
        }
        for (T t : list) {
            for (String s : IdList) {
                if (t instanceof SectionResListVo) {
                    if (TextUtils.equals(((SectionResListVo) t).getId(), s)) {
                        ((SectionResListVo) t).setChecked(true);
                    }
                }
            }

        }
    }

}

