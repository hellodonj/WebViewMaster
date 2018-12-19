package com.galaxyschool.app.wawaschool.fragment;

import android.widget.ImageView;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsExpandPickerFragment extends ContactsExpandListFragment {

    Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();

    public void addItem(int position) {
        addItem(position, false);
    }

    public void addItem(int position, boolean selected) {
        this.map.put(position, selected);
    }

    public void removeItem(int position) {
        if (this.map.containsKey(position)) {
            this.map.remove(position);
        }
    }

    public void selectItem(int position, boolean selected) {
        this.map.put(position, selected);
    }

    public void selectAllItems(boolean selected) {
//        List list = getCurrListViewHelper().getData();
//        if (list == null || list.size() <= 0) {
//            return;
//        }
//
//        for (int i = 0; i < list.size(); i++) {
//            this.map.put(i, selected);
//        }

        for (Map.Entry<Integer, Boolean> entry : this.map.entrySet()) {
            entry.setValue(selected);
        }
    }

    public boolean isItemSelected(int position) {
        if (!this.map.containsKey(position)) {
            return false;
        }
        return this.map.get(position);
    }

    public boolean isAllItemsSelected() {
//        List list = getCurrListViewHelper().getData();
//        if (list == null || list.size() <= 0) {
//            return false;
//        }

        boolean allSelected = true;
//        for (int i = 0; i < list.size(); i++) {
//            if (!this.map.containsKey(i) || !this.map.get(i)) {
//                allSelected = false;
//                break;
//            }
//        }
        for (Map.Entry<Integer, Boolean> entry : this.map.entrySet()) {
            if (!entry.getValue()) {
                allSelected = false;
                break;
            }
        }
        return allSelected;
    }

    public List getSelectedGroupItems() {
        List list = getCurrListViewHelper().getData();
        if (list == null || list.size() <= 0) {
            return null;
        }

        ExpandDataAdapter dataAdapter = getCurrListViewHelper().getDataAdapter();
        List result = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (this.map.containsKey(i) && this.map.get(i).booleanValue()) {
                result.add(list.get(i));
            }
        }

        return result;
    }

    public List getSelectedItems() {
        List list = getCurrListViewHelper().getData();
        if (list == null || list.size() <= 0) {
            return null;
        }

        ExpandDataAdapter dataAdapter = getCurrListViewHelper().getDataAdapter();
        List result = new ArrayList();
//        for (int i = 0; i < list.size(); i++) {
//            if (this.map.containsKey(i) && this.map.get(i).booleanValue()) {
//                result.add(list.get(i));
//            }
//        }
        for (int i = 0; i < dataAdapter.getGroupCount(); i++) {
            for (int j = 0; j < dataAdapter.getChildrenCount(i); j++) {
                int id = (int) dataAdapter.getChildId(i, j);
                if (this.map.containsKey(id) && this.map.get(id).booleanValue()) {
                    result.add(dataAdapter.getChild(i, j));
                }
            }
        }

        return result;
    }

    public boolean hasSelectedGroupItems() {
        List list = getCurrListViewHelper().getData();
        if (list == null || list.size() <= 0) {
            return false;
        }

        ExpandDataAdapter dataAdapter = getCurrListViewHelper().getDataAdapter();
        List result = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (this.map.containsKey(i) && this.map.get(i).booleanValue()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasSelectedItems() {
        List list = getCurrListViewHelper().getData();
        if (list == null || list.size() <= 0) {
            return false;
        }

        ExpandDataAdapter dataAdapter = getCurrListViewHelper().getDataAdapter();
        List result = new ArrayList();
//        for (int i = 0; i < list.size(); i++) {
//            if (this.map.containsKey(i) && this.map.get(i).booleanValue()) {
//                return true;
//            }
//        }
        for (int i = 0; i < dataAdapter.getGroupCount(); i++) {
            for (int j = 0; j < dataAdapter.getChildrenCount(i); j++) {
                int id = (int) dataAdapter.getChildId(i, j);
                if (this.map.containsKey(id) && this.map.get(id).booleanValue()) {
                    return true;
                }
            }
        }

        return false;
    }

    protected class MyViewHolder extends ViewHolder {
        int position;
        ImageView selectorView;
    }

}
