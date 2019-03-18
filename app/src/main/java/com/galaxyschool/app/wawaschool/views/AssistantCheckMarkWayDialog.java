package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;

public class AssistantCheckMarkWayDialog extends Dialog implements View.OnClickListener {
    private String TAG = AssistantCheckMarkWayDialog.class.getSimpleName();
    private Context mContext;
    private RadioButton readingRb;
    private RadioButton recordRb;
    private CallbackListener listener;

    public AssistantCheckMarkWayDialog(Context context, CallbackListener listener) {
        super(context, R.style.Theme_ContactsDialog);
        mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_assistant_check_mark);
        TextView confirmTextV = (TextView) findViewById(R.id.tv_confirm);
        confirmTextV.setOnClickListener(this);
        TextView cancelTextV = (TextView) findViewById(R.id.tv_cancel);
        cancelTextV.setOnClickListener(this);
        readingRb = (RadioButton) findViewById(R.id.rb_reading);
        readingRb.setOnClickListener(this);
        recordRb = (RadioButton) findViewById(R.id.rb_record);
        recordRb.setOnClickListener(this);
        resizeDialog(AssistantCheckMarkWayDialog.this, 0.8f);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_confirm) {
            this.dismiss();
            if (listener != null) {
                if (readingRb.isChecked()) {
                    listener.onBack(0);
                }
                if (recordRb.isChecked()) {
                    listener.onBack(1);
                }
            }
        } else if (viewId == R.id.tv_cancel) {
            this.dismiss();
        } else if (viewId == R.id.rb_reading) {
            readingRb.setChecked(true);
            recordRb.setChecked(false);
        } else if (viewId == R.id.rb_record) {
            readingRb.setChecked(false);
            recordRb.setChecked(true);
        }
    }

    private void resizeDialog(Dialog dialog, float ratio) {
        Window window = dialog.getWindow();
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        p.width = (int) (d.getWidth() * ratio);
        window.setAttributes(p);
    }

}
