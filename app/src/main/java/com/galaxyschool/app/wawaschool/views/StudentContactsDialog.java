package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;

public class StudentContactsDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private View contentView;
    private OnClickListener leftButtonClickListener, rightButtonClickListner;
    private boolean isAutoDismiss = true;

    public StudentContactsDialog(Context context, String title, int contentLayout,
                                 String leftButtonText, OnClickListener
                                         leftButtonClickListener, String rightButtonText,
                                 OnClickListener rightButtonClickListener) {
        this(context, R.style.Theme_ContactsDialog, title, contentLayout,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener);
    }

    public StudentContactsDialog(Context context, int theme, String title, int contentLayout,
                                 String leftButtonText,
                                 OnClickListener leftButtonClickListener,
                                 String rightButtonText,
                                 OnClickListener rightButtonClickListener) {
        super(context, theme);
        this.context = context;
        this.leftButtonClickListener = leftButtonClickListener;
        this.rightButtonClickListner = rightButtonClickListener;

        View rootView = LayoutInflater.from(context).inflate(R.layout.student_contacts_dialog, null);
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
        Button button = (Button) rootView.findViewById(R.id.contacts_dialog_right_button);
        if (button != null) {
            button.setText(rightButtonText);
            button.setOnClickListener(this);
            if (TextUtils.isEmpty(rightButtonText)) {
                button.setVisibility(View.GONE);
//                seperator.setVisibility(View.GONE);
            }
        }

        setContentView(rootView);
        this.contentView = rootView;
        setCanceledOnTouchOutside(false);
        resizeDialog(0.8f);
    }

    public void resizeDialog(float resize){
        if (resize <= 0){
            return;
        }
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        this.show();
        WindowManager windowManager = ((Activity)this.context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int)(display.getWidth() * resize);
        window.setAttributes(lp);
    }

    public void setIsAutoDismiss(boolean value) {
        isAutoDismiss = value;
    }

    public View getContentView() {
        return this.contentView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_dialog_left_button) {
            dismiss();
            if (this.leftButtonClickListener != null) {
                this.leftButtonClickListener.onClick(StudentContactsDialog.this, v.getId());
            }
        } else if (v.getId() == R.id.contacts_dialog_right_button) {
            if (isAutoDismiss) {
                dismiss();
            }
            if (this.rightButtonClickListner != null) {
                this.rightButtonClickListner.onClick(StudentContactsDialog.this, v.getId());
            }
        }
    }

}
