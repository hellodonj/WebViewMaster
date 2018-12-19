package com.lqwawa.intleducation.base.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;

import com.lqwawa.intleducation.R;

/**
 * Created by XChen on 2016/11/24.
 * email:man0fchina@foxmail.com
 */


public class SuperListView extends LinearLayout {

    /**
     * Adapter that stores all row items
     */
    private Adapter adapter;
    /**
     * Observer for the data changes
     */
    private final DataSetObserver dataSetObserver = new AdapterDataSetObserver();
    /**
     * Special item click listener in order to allow to user to take an action
     */
    private OnItemClickListener itemClickListener;

    public SuperListView(Context context) {
        this(context, null);
    }

    public SuperListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("Adapter may not be null");
        }
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(this.dataSetObserver);
        }
        this.adapter = adapter;
        this.adapter.registerDataSetObserver(this.dataSetObserver);
        resetList();
        refreshList();
    }

    /**
     * It is called when the notifyDataSetChanged or first initialize
     */
    private void refreshList() {
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final View view = adapter.getView(i, null, this);
            LayoutParams param = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(param);
            final int position = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(SuperListView.this, view, position);
                    }
                }
            });
            addView(view);
        }
    }

    /**
     * Clears everything
     */
    private void resetList() {
        this.removeAllViews();
        invalidate();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(LinearLayout parent, View view, int position);
    }

    /**
     * observe data set changes, when the adapter notifyDataSetChanged method called, onChanged
     * method will be called and view will be refreshed.
     */
    class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            resetList();
            refreshList();
        }
    }
}