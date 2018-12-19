package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;

public class ContactsMessageDialog extends ContactsDialog {

    public ContactsMessageDialog(Context context, String title, String message,
            String leftButtonText, OnClickListener leftButtonClickListener,
            String rightButtonText, OnClickListener rightButtonClickListener) {
        this(context, R.style.Theme_ContactsDialog, title, message,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener);
    }

    public ContactsMessageDialog(Context context, String title,int layoutId,
            String leftButtonText, OnClickListener leftButtonClickListener,
            String rightButtonText, OnClickListener rightButtonClickListener) {
        super(context,  R.style.Theme_ContactsDialog, title,layoutId,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener);
        resizeDialog(0.8f);
    }


    public ContactsMessageDialog(Context context, int theme, String title, String message,
            String leftButtonText, OnClickListener leftButtonClickListener,
            String rightButtonText, OnClickListener rightButtonClickListener) {
        super(context, theme, title, R.layout.contacts_dialog_content_text,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener);

        TextView textView = (TextView) getContentView().findViewById(
                R.id.contacts_dialog_content_text);
        if (textView != null) {
            textView.setText(message);
        }
        resizeDialog(0.8f);
    }

    public void resizeDialog(float resize){
        if (resize <= 0){
            return;
        }
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        this.show();
        WindowManager windowManager = ((Activity)getDialogContext()).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int)(display.getWidth() * resize);
        window.setAttributes(lp);
    }

    /**
     * 设置message文本的gravity
     * @param gravity
     */
    public void setMessageGravity(int gravity){
        TextView textView = (TextView) getContentView().findViewById(
                R.id.contacts_dialog_content_text);
        if (textView != null) {
            textView.setGravity(gravity);
        }
    }
}
