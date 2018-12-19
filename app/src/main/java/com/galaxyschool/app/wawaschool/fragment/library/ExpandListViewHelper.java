package com.galaxyschool.app.wawaschool.fragment.library;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import java.util.List;

public abstract class ExpandListViewHelper<T> implements
        ExpandableListView.OnGroupClickListener,
        ExpandableListView.OnChildClickListener, DataLoader {

    protected Context context;
    protected ExpandableListView listView;
    protected ExpandDataAdapter<T> dataAdapter;
    protected ViewHolder currViewHolder;

    public ExpandListViewHelper(Context context, ExpandableListView listView,
                ExpandDataAdapter dataAdapter) {
        this.context = context;
        this.listView = listView;
        this.listView.setOnGroupClickListener(this);
        this.listView.setOnChildClickListener(this);
        this.listView.setAdapter(dataAdapter);
        this.dataAdapter = dataAdapter;
    }

    public AdapterView getListView() {
        return this.listView;
    }

    public ExpandDataAdapter getDataAdapter() {
        return this.dataAdapter;
    }

    public void setCurrViewHolder(ViewHolder holder) {
        this.currViewHolder = holder;
    }

    public ViewHolder getCurrViewHolder() {
        return this.currViewHolder;
    }

    public void setData(List<T> dataList) {
        setData(dataList, true);
    }

    public void setData(List<T> dataList, boolean notifyDataSetChanged) {
        if (this.dataAdapter != null) {
            this.dataAdapter.setData(dataList);
            if (notifyDataSetChanged) {
                this.dataAdapter.notifyDataSetChanged();
            }
        }
    }

    public List<T> getData() {
        return this.dataAdapter != null ? this.dataAdapter.getData() : null;
    }

    public boolean hasData() {
        return this.dataAdapter != null ? this.dataAdapter.hasData() : false;
    }

    public void update() {
        if (this.dataAdapter != null) {
            this.dataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public abstract void loadData();

    @Override
    public void clearData() {
        if (this.dataAdapter != null) {
            if (this.dataAdapter.getData() != null) {
                this.dataAdapter.getData().clear();
                this.dataAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public abstract boolean onChildClick(ExpandableListView parent, View v,
                int groupPosition, int childPosition, long id);

    @Override
    public abstract boolean onGroupClick(ExpandableListView parent, View v,
                int groupPosition, long id);

}
