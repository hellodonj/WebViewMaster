package com.galaxyschool.app.wawaschool.fragment.library;

import android.widget.ExpandableListView;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;

import java.util.HashMap;
import java.util.Map;

public class ExpandListFragment<T> extends AdapterFragment {

    protected Map<String, ExpandListViewHelper> listViewHelpers =
            new HashMap<String, ExpandListViewHelper>();

    protected ExpandableListView currListView;
    protected ExpandListViewHelper currListViewHelper;

    public void addListViewHelper(String tag, ExpandListViewHelper helper) {
        this.listViewHelpers.put(tag, helper);
    }

    public ExpandListViewHelper getListViewHelper(String tag) {
        return this.listViewHelpers.get(tag);
    }

    public ExpandableListView getCurrListView() {
        return this.currListView;
    }

    public void setCurrListViewHelper(ExpandableListView listView,
                                         ExpandListViewHelper listViewHelper) {
        this.currListView = listView;
        this.currListView.setOnGroupClickListener(listViewHelper);
        this.currListView.setOnChildClickListener(listViewHelper);
        this.currListViewHelper = listViewHelper;
    }

    public ExpandListViewHelper getCurrListViewHelper() {
        return this.currListViewHelper;
    }

}
