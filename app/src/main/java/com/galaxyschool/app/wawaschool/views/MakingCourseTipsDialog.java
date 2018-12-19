package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;

public class MakingCourseTipsDialog extends Dialog {

    private Context mContext;

    public MakingCourseTipsDialog(Context context) {
        super(context, R.style.Theme_ContactsDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_making_course_tips);

        TextView textView = (TextView) findViewById(R.id.confirm_btn);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        CheckBox checkBox = (CheckBox) findViewById(R.id.ignore_check_box);
        checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DemoApplication.getInstance().getPrefsManager().enableMakingCourseTips(!isChecked);
            }
        });
        checkBox.setChecked(!DemoApplication.getInstance().getPrefsManager()
                .isMakingCourseTipsEnabled());

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                resizeDialog(MakingCourseTipsDialog.this, 0.8f);
            }
        });
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
