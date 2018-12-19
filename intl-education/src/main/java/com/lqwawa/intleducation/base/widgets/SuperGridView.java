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


public class SuperGridView extends LinearLayout {

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

    private int columsNum;

    public SuperGridView(Context context) {
        this(context, null);
    }

    public SuperGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SuperGridView, 0, 0);
        columsNum = array.getResourceId(R.styleable.SuperGridView_num_columns, 1);
        array.recycle();
    }

    public void setNumColumns(int columsNum) {
        this.columsNum = columsNum;
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

            int groupPosition = i / columsNum;
            LinearLayout group = (LinearLayout) getChildAt(groupPosition);
            if (group == null) {
                group = new LinearLayout(getContext());
                group.setOrientation(HORIZONTAL);
                addView(group);
            }
            final View view = adapter.getView(i, null, this);
            group.setWeightSum(columsNum);
            LayoutParams param = new LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, 1.0F);
            view.setLayoutParams(param);
            final int position = i;
            final LinearLayout finalGroup = group;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(finalGroup, view, position);
                    }

                }
            });
            group.addView(view);
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