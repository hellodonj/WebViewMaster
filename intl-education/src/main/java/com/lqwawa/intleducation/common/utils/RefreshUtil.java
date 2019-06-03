package com.lqwawa.intleducation.common.utils;

import java.util.ArrayList;

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

    private ArrayList<Object> idList;


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
    public boolean contains(Object id) {
        if (idList != null && idList.size() > 0) {
            return idList.contains(id);
        }

        return false;
    }


    /**
     * 保存选中的资源
     *
     * @param id 传入资源id
     */
    public void addId(Object id) {
        if (idList == null) {
            idList = new ArrayList<>();
        }
        if (!idList.contains(id)) {
            idList.add(id);
        }
    }

    /**
     * 删除选中的资源
     *
     * @param id 传入资源id
     */
    public void removeId(Object id) {
        if (idList == null) {
            idList = new ArrayList<>();
        }
        if (idList.contains(id)) {
            idList.remove(id);
        }
    }

    /**
     * 清空数据
     */
    public void clear() {
        if (idList != null) {
            idList.clear();
            idList = null;
        }
    }
}

