package com.lqwawa.intleducation.base.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.xutils.common.Callback;

/**
 * Created by Administrator on 2015/10/9.
 */
public class MyBaseAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }
    protected ProgressDialog progressDialog;

    /**
     * 显示提示框
     */
    protected void showProgressDialog(String msg, Activity activity) {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
        }

        this.progressDialog.setMessage(msg);
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
    }
    /**
     * 关闭提示框
     */
    protected void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard(Activity parent) {
        if (parent.getWindow().getAttributes().softInputMode
                != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (parent.getCurrentFocus() != null) {
                InputMethodManager inputManager =
                        (InputMethodManager) parent.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(parent.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public interface OnContentChangedListener {
        void OnContentChanged();
    }
}
