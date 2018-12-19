package com.lqwawa.intleducation.common.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.text.TextUtils;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.base.widgets.MyRatingBar;

/**
 * 自定义评论对话框
 *
 * @author sytao
 */
public class CommentDialog extends Dialog {
    // 低权限,只允许评论
    public static final int TYPE_COMMENT_LOW_PERMISSION = 0x1;
    // 高权限,允许评分
    public static final int TYPE_COMMENT_HIGH_PERMISSION = 0x2;
    // 当前权限类型
    private int currentCommentType = TYPE_COMMENT_LOW_PERMISSION;

    public static final String KEY_COMMENT_TYPE = "KEY_COMMENT_TYPE";
    private Context context;
    private View.OnClickListener positiveListener;
    // 评分容器
    private LinearLayout mGradeLayout;
    private TextView mTipText;
    private MyRatingBar ratingBarScort;//评分
    private ContainsEmojiEditText editTextCommentContent;//评论内容
    private CommitCallBack callback;
    private int curScort;
    private CommentData data;
    // 是否是家长身份
    private boolean isParent;

    public CommentDialog(Context context, int curScort, @IntRange(from = 1,to = 2) int commentType,boolean isParent, CommentData data,CommitCallBack callback) {
        this(context,curScort,commentType,data,callback);
        this.isParent = isParent;
    }

    public CommentDialog(Context context, int curScort, @IntRange(from = 1,to = 2) int commentType, CommentData data,CommitCallBack callback) {
        super(context);
        this.currentCommentType = commentType;
        this.context = context;
        this.curScort = curScort;
        this.data = data;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.callback = callback;
    }

    public CommentDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widgets_comment_dialog);
        zoom();
        initUI();
    }

    /**
     * 界面缩放
     */
    private void zoom() {
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        LayoutParams p = getWindow().getAttributes();
        float widthRatio = 1.0f;
        float heightRatio = 0.5f;
        p.width = (int) (d.getWidth() * widthRatio);
        //   p.height = (int) (d.getHeight() * heightRatio);
        p.height = LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(p);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private CommentData getParam() {
        CommentData module = new CommentData();
        module.setContent(editTextCommentContent.getText().toString());
        // @date   :2018/4/10 0010 下午 5:33
        // @func   :只允许评论不允许评分,startLevel 就是0
        module.setScort(currentCommentType == TYPE_COMMENT_LOW_PERMISSION ? 0 : (int)ratingBarScort.getRating());
        return module;
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        this.setCanceledOnTouchOutside(true);
        mGradeLayout = (LinearLayout) findViewById(R.id.grade_layout);
        mTipText = (TextView) findViewById(R.id.tip_text);
        ratingBarScort = ((MyRatingBar) findViewById(R.id.score_rating_bar));
        ratingBarScort.setRating(curScort);
        ratingBarScort.setMinStar(1);
        editTextCommentContent = (ContainsEmojiEditText) findViewById(R.id.comment_content_tv);
        TextView submit_btn = (TextView) findViewById(R.id.submit_bt);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isValidString(editTextCommentContent.getText().toString())) {
                    ToastUtil.showToast(context,
                            context.getResources().getString(
                                    R.string.enter_evaluation_content_please));
                    return;
                }
                if (null != positiveListener) {
                    positiveListener.onClick(v);
                }
                if (callback != null) {
                    callback.triggerSend(getParam());
                }
                CommentDialog.this.dismiss();
            }
        });

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                CommentDialog.this.data = null;
            }
        });

        this.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (null != positiveListener) {
                    positiveListener.onClick(null);
                }
                if (callback != null) {
                    callback.dismiss(getParam());
                }
            }
        });

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
            }
        });

        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // 设置评分容器显示与隐藏
        if(currentCommentType == TYPE_COMMENT_LOW_PERMISSION){
            // 低权限 说明没有添加到我的课程 或者是孩子的家长身份
            mGradeLayout.setVisibility(View.GONE);
            // 没有添加到我的课程,极有可能以前添加过
            if(curScort > 0 && !isParent){
                // 以前添加过,显示已评分
                mTipText.setText(R.string.label_has_score);
                mGradeLayout.setVisibility(View.VISIBLE);
            }
        }else{
            // 高权限 说明已经添加到我的课程
            mGradeLayout.setVisibility(View.VISIBLE);
        }

        if (this.curScort > 0){
            //  如果有评分,直接设置
            ratingBarScort.setRating(curScort);
            ratingBarScort.setEnabled(false);
            // 已经有评分,显示已评分
            mTipText.setText(R.string.label_has_score);
        }else{
            curScort = 5;
            ratingBarScort.setRating(curScort);
        }
        ratingBarScort.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1){
                    ratingBar.setRating(1);
                }
            }
        });

        // 设置数据
        if(null != data){
            ratingBarScort.setRating(data.getScort());
            editTextCommentContent.setText(data.getContent());
            editTextCommentContent.setSelection(data.getContent().length());
        }
    }

    private void sendComment(){

    }

    public void setPositiveListener(View.OnClickListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    /**
     * Dialog 回调接口
     */
    public interface CommitCallBack{
        // Dialog Dismiss回调时候调用
        void dismiss(CommentData module);
        // 点击发送时候调用
        void triggerSend(CommentData module);
    }

    /**
     * 评论数据
     */
    public class CommentData extends BaseVo{
        //评论内容
        private String content;
        //评分
        private int scort;//评分

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getScort() {
            return scort;
        }

        public void setScort(int scort) {
            this.scort = scort;
        }
    }
}