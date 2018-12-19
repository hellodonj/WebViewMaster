package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.osastudio.common.utils.LogUtils;

public class DoTaskOrderTipsDialog extends Dialog {
    private String TAG = DoTaskOrderTipsDialog.class.getSimpleName();
    private Context mContext;
    private CallbackListener listener;

    public DoTaskOrderTipsDialog(Context context, CallbackListener listener) {
        super(context, R.style.Theme_ContactsDialog);
        mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_do_task_order_tips);
        TextView textView = (TextView) findViewById(R.id.tv_begin_to_answer);
        textView.setOnClickListener(v -> {
                    dismiss();
                    if (listener != null) {
                        listener.onBack(true);
                    }
                }
        );
        CheckBox checkBox = (CheckBox) findViewById(R.id.check_do_task_tip);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    LogUtils.log(TAG, "isChecked = " + isChecked);
                    DemoApplication.getInstance().getPrefsManager().enableDoTaskOrderTips
                            (isChecked);
                }
        );
        resizeDialog(DoTaskOrderTipsDialog.this, 0.8f);
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
