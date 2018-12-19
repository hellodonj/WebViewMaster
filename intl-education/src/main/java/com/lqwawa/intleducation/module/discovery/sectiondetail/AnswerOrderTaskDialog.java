package com.lqwawa.intleducation.module.discovery.sectiondetail;

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

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.SPUtil;
import com.lqwawa.intleducation.factory.constant.SharedConstant;
import com.osastudio.common.utils.LogUtils;

public class AnswerOrderTaskDialog extends Dialog {
    private String TAG = AnswerOrderTaskDialog.class.getSimpleName();
    private Context mContext;
    private CallbackListener listener;

    public AnswerOrderTaskDialog(Context context, CallbackListener listener) {
        super(context, R.style.Theme_Public_Dialog);
        mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_answer_order_layout);
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
                    SPUtil.getInstance().put(SharedConstant.KEY_AUTO_MARK_WARNING,isChecked);
                }
        );
        resizeDialog(AnswerOrderTaskDialog.this, 0.8f);
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

    public interface CallbackListener {
        void onBack(Object result);
    }

}
