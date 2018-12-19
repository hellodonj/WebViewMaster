package com.lqwawa.intleducation.common.ui;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.intleducation.R;

/**
 * Created by XChen on 2016/12/13.
 * email:man0fchina@foxmail.com
 */

public class CommonEditDialog extends Dialog{
    private Activity context;
    private CommitCallBack callback;
    private String title;
    private String content;
    private ContainsEmojiEditText editTextContent;
    private int maxLength;

    public CommonEditDialog(Activity context, String title, String content,int maxLength,
                            CommitCallBack callback) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.maxLength = maxLength;
        this.callback = callback;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widgets_edit_dialog);
        zoom();
        initUI();
    }

    /**
     * 界面缩放
     */
    private void zoom() {
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        float widthRatio = 1.0f;
        float heightRatio = 0.5f;
        p.width = (int) (d.getWidth() * widthRatio);
        //   p.height = (int) (d.getHeight() * heightRatio);
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(p);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void dismiss() {
        if (callback != null){
            callback.OnDismiss(editTextContent.getText().toString().trim());
        }
        super.dismiss();
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        this.setCanceledOnTouchOutside(true);
        editTextContent = ((ContainsEmojiEditText) findViewById(R.id.content_et));
        editTextContent.setText(content);
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};
        editTextContent.setFilters(filters);
        editTextContent.setSelection(editTextContent.getText().length());
        ((TextView) findViewById(R.id.title_tv)).setText(title);
        findViewById(R.id.edit_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public static interface CommitCallBack{
        public void OnDismiss(String content);
    }
}
