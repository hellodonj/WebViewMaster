package com.lqwawa.intleducation.common.ui;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.StringUtils;

/**
 * Created by XChen on 2016/12/14.
 * email:man0fchina@foxmail.com
 */

public class CommonReplyView extends PopupWindow {
    private Activity parentActivity;
    private View conentView;
    private PopupWindowListener popupWindowListener;
    private ContainsEmojiEditText editTextContent;
    private TextView buttonSend;
    private String toName;
    private String imputContent;
    private boolean isDismissWithSend = false;

    public CommonReplyView(Activity activity, String toName, String imputContent, PopupWindowListener listener) {
        this.parentActivity = activity;
        this.popupWindowListener = listener;
        this.toName = toName;
        this.imputContent = imputContent;
        LayoutInflater inflater = activity.getLayoutInflater();
        conentView = inflater.inflate(R.layout.widgets_reply_popup_window, null);
        this.setContentView(conentView);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        initViews();
    }

    private void initViews() {
        setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("mengdd", "onTouch : ");
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        editTextContent = (ContainsEmojiEditText) conentView.findViewById(R.id.comment_et);
        if(toName != null) {
            editTextContent.setHint(parentActivity.getResources().getString(R.string.commit_reply)
                    + " " + toName);
        }else{
            editTextContent.setHint(parentActivity.getResources().getString(R.string.say_something));
        }
        if(StringUtils.isValidString(imputContent)){
            editTextContent.setText(imputContent);
            editTextContent.setSelection(imputContent.length());
        }
        editTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    buttonSend.setVisibility(View.VISIBLE);
                }else{
                    buttonSend.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buttonSend = (TextView) conentView.findViewById(R.id.send_btn);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindowListener != null){
                    popupWindowListener.onBtnSendClickListener(
                            editTextContent.getText().toString().trim());
                    isDismissWithSend = true;
                    dismiss();
                }
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(parentActivity, 1.0f);
                if(!isDismissWithSend){
                    if (popupWindowListener != null){
                        popupWindowListener.onDismiss(editTextContent.getText().toString().trim());
                    }
                }
            }
        });

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * 控制弹出popupView屏幕变暗与恢复
     */
    public void darkenBackground(Activity activity, Float bgcolor) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgcolor;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(lp);
    }

    public static void showView(Activity activity, String toName, PopupWindowListener listener) {
        CommonReplyView view = new CommonReplyView(activity, toName, "", listener);
        view.showAtLocation(activity.getWindow().getDecorView(),
                Gravity.BOTTOM, 0, 0);
        view.darkenBackground(activity, 0.6f);
    }
    public static void showView(Activity activity, String toName, String imputContent, PopupWindowListener listener) {
        CommonReplyView view = new CommonReplyView(activity, toName, imputContent, listener);
        view.showAtLocation(activity.getWindow().getDecorView(),
                Gravity.BOTTOM, 0, 0);
        view.darkenBackground(activity, 0.6f);
    }

    public interface PopupWindowListener {
        void onBtnSendClickListener(String content);
        void onDismiss(String content);
    }
}
