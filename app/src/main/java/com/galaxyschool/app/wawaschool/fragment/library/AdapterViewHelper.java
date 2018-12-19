package com.galaxyschool.app.wawaschool.fragment.library;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.List;

import static com.galaxyschool.app.wawaschool.fragment.library.DataAdapter.*;

public abstract class AdapterViewHelper<T> implements AdapterView.OnItemClickListener,
        AdapterViewCreator, DataLoader {

    protected Context context;
    protected AdapterView<BaseAdapter> adapterView;
    protected DataAdapter<T> dataAdapter;
    protected int itemViewLayout;
    protected AdapterViewCreator itemViewCreator;
    protected ViewHolder currViewHolder;
    protected int positionOffset;

    public AdapterViewHelper(Context context, AdapterView adapterView,
                             int itemViewLayout) {
        this.context = context;
        this.adapterView = adapterView;
        this.adapterView.setOnItemClickListener(this);
        this.itemViewLayout = itemViewLayout;
    }

    public Context getContext() {
        return this.context;
    }

    public int getPositionOffset() {
        return positionOffset;
    }

    public void setPositionOffset(int offset) {
        positionOffset = offset;
    }

    public AdapterView getAdapterView() {
        return this.adapterView;
    }

    public DataAdapter getDataAdapter() {
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
        if (this.dataAdapter == null) {
            this.dataAdapter = new DataAdapter(this.context, dataList,
                    this.itemViewLayout);
            this.dataAdapter.setItemViewCreator(AdapterViewHelper.this);
            this.adapterView.setAdapter(this.dataAdapter);
        } else {
            this.dataAdapter.setData(dataList);
        }
        if (notifyDataSetChanged) {
            this.dataAdapter.notifyDataSetChanged();
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
    public abstract void onItemClick(AdapterView<?> parent, View view,
            int position, long id);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (this.itemViewCreator == null) {
            this.itemViewCreator = new DefaultAdapterViewCreator(
                    this.context, this.itemViewLayout);
        }
        return this.itemViewCreator.getView(position, convertView, parent);
    }

}
