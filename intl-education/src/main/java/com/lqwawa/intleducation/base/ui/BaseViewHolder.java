package com.lqwawa.intleducation.base.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * @author: ly
 * @date : 2018/3/28
 * @desc :
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews = new SparseArray<>();
    private View mConvertView;
    private int mLayoutId;
    protected Context mContext;

    public BaseViewHolder(Context context, int layoutId, View itemView) {
        super(itemView);
        this.mContext = context;
        this.mLayoutId = layoutId;
        mConvertView = itemView;
        mConvertView.setTag(this);
    }

    @SuppressWarnings("unchecked")
    public final <R extends View> R getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (R) view;
    }

    public int getLayoutId() {
        return mLayoutId;
    }

    public View getItemView() {
        return mConvertView;
    }

    public Context getContext() {
        return mContext;
    }
}
