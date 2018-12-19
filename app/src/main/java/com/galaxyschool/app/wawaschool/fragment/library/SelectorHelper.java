package com.galaxyschool.app.wawaschool.fragment.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016.06.19.
 * 单选/多选帮助类
 */
public class SelectorHelper<T> {
    private Map<Integer, Boolean> map = new HashMap<>();
    private List<T> list;

    public SelectorHelper(List<T> list) {
        this.list = list;
    }

    /**
     * 选中/非选中
     *
     * @param position
     * @param selected
     */
    public void selectItem(int position, boolean selected) {
        map.put(position, selected);
    }

    /**
     * 全选/非全选
     */
    public void selectAllItems(boolean selected) {
        if (list == null || list.size() <= 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            map.put(i, selected);
        }
    }

    /**
     * 条目是否被选中
     *
     * @return
     */
    public boolean isItemSelected(int position) {

        //如果压根就没找到该item，肯定是没选中的。
        if (!map.containsKey(position)) {
            return false;
        }
        //在找到item的情况下，获取选中状态。
        return map.get(position);
    }

    /**
     * 判断是否全选
     *
     * @return
     */
    public boolean isAllItemsSelected() {
        //在遍历之前，都需要判定list是否为空，这是一个好习惯。
        if (list == null || list.size() <= 0) {
            return false;
        }
        //标识是否全部选中
        boolean isAllItemsSelected = true;
        for (int i = 0; i < list.size(); i++) {
            if (!map.containsKey(i) || !map.get(i)) {
                isAllItemsSelected = false;
                //这里的意思就是如果只要找到一个是假的，就不再找了。
                break;
            }
        }
        return isAllItemsSelected;
    }

    /**
     * 获得选中的条目个数
     *
     * @return
     */
    public int getSelectedItemsCount() {

        if (list == null || list.size() <= 0) {
            return 0;
        }
        //选中的条目个数
        int selectedItemsCount = 0;
        for (int i = 0; i < list.size(); i++) {
            if (map.containsKey(i) && map.get(i)) {
                //找到一个就加1
                selectedItemsCount++;
            }
        }
        return selectedItemsCount;
    }

    /**
     * 判定是否有条目被选中
     *
     * @return
     */
    public boolean hasSelectedItems() {

        if (list == null || list.size() <= 0) {
            return false;
        }
        //是否有条目被选中
        boolean hasSelectedItems = false;
        for (int i = 0; i < list.size(); i++) {
            if (map.containsKey(i) && map.get(i)) {
                //找到一个就行了
                hasSelectedItems = true;
                break;
            }
        }
        return hasSelectedItems;
    }

    /**
     * 获得选中的items
     */

    public List<T> getSelectedItems() {
        if (list == null || list.size() <= 0) {
            return null;
        }
        List<T> resultList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (map.containsKey(i) && map.get(i)) {
                resultList.add(list.get(i));
            }
        }
        return resultList;
    }
}
