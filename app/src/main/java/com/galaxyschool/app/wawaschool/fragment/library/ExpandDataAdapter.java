package com.galaxyschool.app.wawaschool.fragment.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.List;

public abstract class ExpandDataAdapter<T> extends BaseExpandableListAdapter {

    protected Context context;
    protected LayoutInflater inflater;
    protected int groupViewLayout;
    protected int childViewLayout;
    protected ExpandAdapterViewCreator creator;
    protected List<T> items;

    public ExpandDataAdapter(Context context, List<T> itemList,
                             int groupViewLayout, int childViewLayout) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.groupViewLayout = groupViewLayout;
        this.childViewLayout = childViewLayout;
        this.items = itemList;
    }

    public void setItemViewCreator(ExpandAdapterViewCreator creator) {
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

//    @Override
//    public Object getChild(int groupPosition, int childPosition) {
//
//    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 10000 + childPosition;
    }

//    @Override
//    public int getChildrenCount(int groupPosition) {
//        return 0;
//    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (this.creator == null) {
            this.creator = new DefaultExpandAdapterViewCreator(this.context,
                    this.groupViewLayout, this.childViewLayout);
        }
        return this.creator.getChildView(groupPosition, childPosition,
                isLastChild, convertView, parent);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.items != null ? this.items.get(groupPosition) : null;
    }

    @Override
    public int getGroupCount() {
        return this.items != null ? this.items.size() : 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (this.creator == null) {
            this.creator = new DefaultExpandAdapterViewCreator(this.context,
                    this.groupViewLayout, this.childViewLayout);
        }
        return this.creator.getGroupView(groupPosition, isExpanded, convertView, parent);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public static class DefaultExpandAdapterViewCreator implements ExpandAdapterViewCreator {

        private Context context;
        private int groupViewLayout;
        private int childViewLayout;

        public DefaultExpandAdapterViewCreator(Context context, int groupViewLayout,
                     int childViewLayout) {
            this.context = context;
            this.groupViewLayout = groupViewLayout;
            this.childViewLayout = childViewLayout;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                     View convertView, ViewGroup parent) {
            if (convertView == null) {
                if (this.groupViewLayout > 0) {
                    convertView = LayoutInflater.from(this.context).inflate(
                            this.groupViewLayout, null);
                }
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                     boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                if (this.childViewLayout > 0) {
                    convertView = LayoutInflater.from(this.context).inflate(
                            this.childViewLayout, null);
                }
            }
            return convertView;
        }

    }

    public interface ExpandAdapterViewCreator {

        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent);

        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent);

    }

}
