package com.galaxyschool.app.wawaschool.views.categoryview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;

import java.util.List;

/**
 * Created by Administrator on 2016/7/25.
 */
public class CategoryExpandGridviewPopwindow extends PopupWindow implements View.OnClickListener {
    private Activity context;
    private LayoutInflater inflater;
    private List<Category> categorys;
    private View mRootView;
    private SureSelectListener sureListener;
    private  CategoryExpandGridview expandGridview;
    public interface SureSelectListener {
        void onSureSelect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure_btn:
                sureSelects();
                break;
            case R.id.spare_part:
                this.dismiss();
                break;
        }
    }

    private void sureSelects() {
        this.dismiss();
        if(expandGridview!=null){
            expandGridview.finishSelect();
        }
        sureListener.onSureSelect();
    }


    public CategoryExpandGridviewPopwindow(final Activity context,
                                           List<Category> categorys, SureSelectListener sureListener) {
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_expandgridview_popwindow, null);
        this.categorys = categorys;
        this.sureListener = sureListener;
        initView();
        setProperty();
    }

    public void showPopupMenu(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
        } else {
            this.dismiss();
        }
    }


    public void initView() {
        expandGridview = (CategoryExpandGridview) mRootView.findViewById(R.id
                .category_expandgridview);
        expandGridview.setCategorys(this.categorys);
        TextView sureBtn = (TextView) expandGridview.findViewById(R.id.sure_btn);
        LinearLayout sparepart = (LinearLayout) mRootView.findViewById(R.id.spare_part);
        sureBtn.setOnClickListener(this);
        sparepart.setOnClickListener(this);
    }


    private void setProperty() {
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
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
}
