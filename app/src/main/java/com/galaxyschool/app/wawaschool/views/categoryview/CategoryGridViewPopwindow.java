package com.galaxyschool.app.wawaschool.views.categoryview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.views.categoryview.CategoryGridview.SureSelectListener;

import java.util.List;

/**
 * Created by Administrator on 2016/7/23.
 */
public class CategoryGridViewPopwindow extends PopupWindow  implements View.OnClickListener {
    private Activity mContext;
    private LayoutInflater inflater;
    private Category category;
    private View mRootView;
    private SureSelectListener sureListener;
    private  CategoryGridview categoryGridview;
    public CategoryGridViewPopwindow(final Activity context,
                                     Category category, SureSelectListener sureListener
                                     ) {
        mContext = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_gridview_popwindow, null);
        this.category = category;
        this.sureListener=sureListener;
        initView();
        setProperty();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure_btn:
                sureSelects();
                break;
            case R.id.spare_part:
                dismiss();
                break;
        }
    }
    private void sureSelects() {
        dismiss();
        if(categoryGridview!=null){
            categoryGridview.finishSelect();
        }
        sureListener.onSureSelect();
    }

    public void initView() {
        categoryGridview = (CategoryGridview) mRootView.findViewById(R.id.category_gridview);
        categoryGridview.setCategory(this.category);
        categoryGridview.setSureListener(sureListener);
        TextView sureBtn = (TextView) categoryGridview.findViewById(R.id.sure_btn);
        LinearLayout sparepart = (LinearLayout) mRootView.findViewById(R.id.spare_part);
        sureBtn.setOnClickListener(this);
        sparepart.setOnClickListener(this);
    }



    private void setProperty() {
        int h = mContext.getWindowManager().getDefaultDisplay().getHeight();
        int w = mContext.getWindowManager().getDefaultDisplay().getWidth();
        this.setContentView(mRootView);
        this.setWidth(w);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
    }


    public void showPopupMenu(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

}
