package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.lqwawa.apps.views.ContainsEmojiEditText;

public class ExerciseTeacherCommentDialog extends Dialog {
    private String TAG = ExerciseTeacherCommentDialog.class.getSimpleName();
    private Context mContext;
    private ContainsEmojiEditText emojiEditText;
    private CallbackListener listener;

    public ExerciseTeacherCommentDialog(Context context, CallbackListener listener) {
        super(context, R.style.Theme_ContactsDialog);
        mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exercise_teacher_comment);
        initViews();
        setCancelable(false);
        resizeDialog(ExerciseTeacherCommentDialog.this, 0.8f);
    }

    private void initViews() {
        emojiEditText = (ContainsEmojiEditText) findViewById(R.id.et_teacher_comment);
        TextView leftTextV = (TextView) findViewById(R.id.tv_left);
        leftTextV.setOnClickListener(v -> dismiss());

        TextView rightTextV = (TextView) findViewById(R.id.tv_right);
        rightTextV.setOnClickListener(v -> {
            //获取当前填写的点评数据
            String inputContent = emojiEditText.getText().toString().trim();
            if (TextUtils.isEmpty(inputContent)) {
                //提示输入内容不能为空
                TipMsgHelper.ShowMsg(mContext, R.string.str_comments_should_not_be_empty);
                return;
            }
            if (listener != null) {
                listener.onBack(inputContent);
            }
            dismiss();
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
