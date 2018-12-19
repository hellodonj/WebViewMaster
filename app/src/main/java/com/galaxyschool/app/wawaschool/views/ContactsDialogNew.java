package com.galaxyschool.app.wawaschool.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;

/**
 * <br/>================================================
 * <br/> 作    者：Blizzard-liu
 * <br/> 版    本：1.0
 * <br/> 创建日期：2018/2/6 19:52
 * <br/> 描    述：
 * <br/> 修订历史：
 * <br/>================================================
 */

public class ContactsDialogNew extends Dialog implements View.OnClickListener {

    private Context context;
    private View contentView;
    private OnClickListener leftButtonClickListener, rightButtonClickListner,middleButtonClickListener;
    private boolean isAutoDismiss = true;

    public ContactsDialogNew(Context context, String title, int contentLayout,
                          String leftButtonText, OnClickListener leftButtonClickListener,
                          String rightButtonText, OnClickListener rightButtonClickListener) {
        this(context, R.style.Theme_ContactsDialog, title, contentLayout,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener);
    }

    public ContactsDialogNew(Context context, int theme, String title, int contentLayout,
                          String leftButtonText, OnClickListener leftButtonClickListener,
                          String rightButtonText, OnClickListener rightButtonClickListener) {

        this(context, theme, title, contentLayout,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener,"",null);

    }

    public ContactsDialogNew(Context context, int theme, String title, int contentLayout,
                          String leftButtonText, OnClickListener leftButtonClickListener,
                          String rightButtonText, OnClickListener rightButtonClickListener,
                          String middleButtonText, OnClickListener middleButtonClickListener) {
        super(context, theme);
        this.context = context;
        this.leftButtonClickListener = leftButtonClickListener;
        this.rightButtonClickListner = rightButtonClickListener;
        this.middleButtonClickListener = middleButtonClickListener;

        View rootView = LayoutInflater.from(context).inflate(R.layout.contacts_dialog_new, null);
        TextView textView = (TextView) rootView.findViewById(R.id.contacts_dialog_title);
        if (textView != null) {
            textView.setText(title);
        }

        View view = rootView.findViewById(R.id.contacts_dialog_title_layout);
        //        if (view != null && TextUtils.isEmpty(title)) {
        //            view.setVisibility(View.GONE);
        //        }

        ViewGroup layout = (ViewGroup) rootView.findViewById(R.id.contacts_dialog_content_layout);
        if (contentLayout > 0) {
            View content = LayoutInflater.from(context).inflate(contentLayout, null);
            layout.addView(content);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) content.getLayoutParams();
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            content.setLayoutParams(params);
        }

        //        View seperator = rootView.findViewById(R.id.contacts_dialog_button_seperator);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.contacts_dialog_left_button);
        if (imageView != null) {
            imageView.setOnClickListener(this);
            if (TextUtils.isEmpty(leftButtonText)) {
                imageView.setVisibility(View.GONE);
                //                seperator.setVisibility(View.GONE);
            }
        }
        TextView button = (TextView) rootView.findViewById(R.id.contacts_dialog_right_button);
        if (button != null) {
            button.setText(rightButtonText);
            button.setOnClickListener(this);
            if (TextUtils.isEmpty(rightButtonText)) {
                button.setVisibility(View.GONE);
                //                seperator.setVisibility(View.GONE);
            }
        }
        /**
         * 左边的btn按钮
         */
        button = (TextView) rootView.findViewById(R.id.contacts_dialog_middle_button);
        if (button != null) {
            button.setText(middleButtonText);
            button.setOnClickListener(this);
            if (TextUtils.isEmpty(middleButtonText)) {
                button.setVisibility(View.GONE);
                rootView.findViewById(R.id.contacts_dialog_middle_view).setVisibility(View.GONE);
            } else {
                button.setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.contacts_dialog_middle_view).setVisibility(View.VISIBLE);
            }
        }

        setContentView(rootView);
        this.contentView = rootView;
        setCanceledOnTouchOutside(false);
    }

    public void setIsAutoDismiss(boolean value) {
        isAutoDismiss = value;
    }

    public View getContentView() {
        return this.contentView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_dialog_left_button) {//左上角的叉
            dismiss();
            if (this.leftButtonClickListener != null) {
                this.leftButtonClickListener.onClick(ContactsDialogNew.this, v.getId());
            }
        } else if (v.getId() == R.id.contacts_dialog_right_button) {//右边的按钮
            if (isAutoDismiss) {
                dismiss();
            }
            if (this.rightButtonClickListner != null) {
                this.rightButtonClickListner.onClick(ContactsDialogNew.this, v.getId());
            }
        } else if (v.getId() == R.id.contacts_dialog_middle_button) {//左边的按钮
            if (isAutoDismiss) {
                dismiss();
            }
            if (this.middleButtonClickListener != null) {
                this.middleButtonClickListener.onClick(ContactsDialogNew.this, v.getId());
            }
        }
    }

}
