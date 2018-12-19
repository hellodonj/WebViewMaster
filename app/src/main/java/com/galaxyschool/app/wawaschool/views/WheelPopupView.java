package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.views.wheelview.ArrayWheelAdapter;
import com.galaxyschool.app.wawaschool.views.wheelview.WheelView;


public class WheelPopupView extends PopupWindow implements View.OnClickListener, View.OnTouchListener, WheelView.OnWheelChangedListener {

    public interface OnSelectChangeListener {
        void onSelectChange(int index, String relationType);
    }

    private View mRootView;
    private TextView mCancelBtn, mConfirmBtn;
    private WheelView mWheelView;
    private LayoutInflater mLayoutInflater;
    private Activity mContext;
    private RelationArrayAdapter relationArrayAdapter;
    private String[] mRelationTypes;
    private int relationIndex;
    private int curIndex = 1;
    private OnSelectChangeListener listener;

    public WheelPopupView(Activity context, int index, OnSelectChangeListener listener,
                          String [] strings) {
        super(context);
        mContext = context;
        relationIndex = index;
        this.listener = listener;
        mLayoutInflater = (LayoutInflater) mContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRelationTypes = strings;
        initViews();
    }


    private void initViews() {
        mRootView = mLayoutInflater.inflate(R.layout.relation_popup_view, null);
        mCancelBtn = (TextView) mRootView.findViewById(R.id.relation_cancel_btn);
        mConfirmBtn = (TextView) mRootView.findViewById(R.id.relation_confirm_btn);
        mWheelView = (WheelView) mRootView.findViewById(R.id.relation_wheelview);
        mRootView.setOnTouchListener(this);
        mCancelBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);

        if(relationIndex < 0) {
            curIndex = 1;
        } else {
            curIndex = relationIndex;
        }
        relationArrayAdapter = new RelationArrayAdapter(mContext, mRelationTypes, curIndex);
        mWheelView.setViewAdapter(relationArrayAdapter);
        mWheelView.setCurrentItem(curIndex);
        mWheelView.addChangingListener(this);
//        mWheelView.setCyclic(true);

        setContentView(mRootView);
        setFocusable(true);
        setAnimationStyle(R.style.AnimBottom);
        setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        int color = mContext.getResources().getColor(R.color.popup_root_view_bg);
        ColorDrawable colorDrawable = new ColorDrawable(color);
        setBackgroundDrawable(colorDrawable);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relation_confirm_btn:
                if(listener != null) {
                    listener.onSelectChange(curIndex, mRelationTypes[curIndex]);
                }
                dismiss();
                break;
            case R.id.relation_cancel_btn:
//                if(listener != null) {
//                    if(relationIndex >= 0) {
//                        listener.onSelectChange(relationIndex, mRelationTypes[curIndex]);
//                    } else {
//                        listener.onSelectChange(relationIndex, mContext.getString(R.string.relation));
//                    }
//                }
                dismiss();
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = mRootView.findViewById(R.id.relation_pop_layout).getTop();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (y < height) {
                dismiss();
            }
        }
        return true;
    }

    private class RelationArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public RelationArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(16);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                // view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if(oldValue != newValue) {
            curIndex = newValue;
        }
    }
}
