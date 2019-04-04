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
import com.osastudio.common.utils.LogUtils;

public class AssistantModelTipsDialog extends Dialog {
    private String TAG = AssistantModelTipsDialog.class.getSimpleName();
    private Context mContext;

    public AssistantModelTipsDialog(Context context) {
        super(context, R.style.Theme_ContactsDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_assistant_model_tips);
        TextView textView = (TextView) findViewById(R.id.tv_know);
        textView.setOnClickListener(v -> dismiss());
        CheckBox checkBox = (CheckBox) findViewById(R.id.cb_no_prompt);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    LogUtils.log(TAG, "isChecked = " + isChecked);
                    DemoApplication.getInstance().getPrefsManager().enableChangeAssistantModelTip
                            (isChecked);
                }
        );
        resizeDialog(AssistantModelTipsDialog.this, 0.8f);
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
