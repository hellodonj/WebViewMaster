package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;

public class ThirdLoginSuccessTipsDialog extends Dialog {
    private Context mContext;
    private UserInfo userInfo;

    public ThirdLoginSuccessTipsDialog(Context context, UserInfo userInfo) {
        super(context, R.style.Theme_ContactsDialog);
        mContext = context;
        this.userInfo = userInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_third_login_tips);
        initView();
        resizeDialog(ThirdLoginSuccessTipsDialog.this, 0.8f);
    }

    private void initView(){
        TextView textView = (TextView) findViewById(R.id.tv_ok);
        textView.setOnClickListener(v -> dismiss());
        TextView accountTipView = (TextView) findViewById(R.id.tv_tip_account);
        accountTipView.setText(mContext.getString(R.string.str_tip_account,userInfo.getNickName()));
        TextView passwordTipView = (TextView) findViewById(R.id.tv_tip_password);
        passwordTipView.setText(mContext.getString(R.string.str_tip_password,"111111"));
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
