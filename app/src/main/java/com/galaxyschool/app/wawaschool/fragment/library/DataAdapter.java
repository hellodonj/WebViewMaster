package com.galaxyschool.app.wawaschool.fragment.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideBaseAdapter;

import java.util.List;

public class DataAdapter<T> extends SlideBaseAdapter {

    protected Context context;
    protected LayoutInflater inflater;
    protected int layout;
    protected AdapterViewCreator creator;
    protected List<T> items;

    public DataAdapter(Context context, List<T> itemList, int itemViewLayout) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.layout = itemViewLayout;
        this.items = itemList;
    }

    public void setItemViewCreator(AdapterViewCreator creator) {
        this.creator = creator;
    }

    public Context getContext() {
        return this.context;
    }

    public List<T> getData() {
        return this.items;
    }

    public boolean hasData() {
        return this.items != null && this.items.size() > 0;
    }

    public void setData(List<T> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items != null ? this.items.size() : 0;
    }

    @Override
    public T getItem(int position) {
        try {
            return this.items != null ? this.items.get(position) : null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (this.creator == null) {
            this.creator = new DefaultAdapterViewCreator(this.context,
                    this.layout);
        }
        return this.creator.getView(position, convertView, parent);
    }

    @Override
    public int getFrontViewId(int position) {
        return 0;
    }

    @Override
    public int getLeftBackViewId(int position) {
        return 0;
    }

    @Override
    public int getRightBackViewId(int position) {
        return 0;
    }

    public static class DefaultAdapterViewCreator implements AdapterViewCreator {

        private Context context;
        private int layout;

        public DefaultAdapterViewCreator(Context context, int itemViewLayout) {
            this.context = context;
            this.layout = itemViewLayout;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                if (this.layout > 0) {
                    convertView = LayoutInflater.from(this.context).inflate(
                            this.layout, null);
                }
            }
            return convertView;
        }

    }

    public interface AdapterViewCreator {

        public View getView(int position, View convertView, ViewGroup parent);

    }

}
