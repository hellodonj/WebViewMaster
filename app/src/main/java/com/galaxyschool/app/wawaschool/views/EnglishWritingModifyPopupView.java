package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;

/**
 * 英文写作修改pop窗口
 */
public class EnglishWritingModifyPopupView extends PopupWindow implements View.OnClickListener,
        View.OnTouchListener {


    private View mRootView;
    private EnglishWritingEditText mEditText;
    private TextView mCancelBtn, mCommitBtn;
    private LayoutInflater mLayoutInflater;
    private Activity mContext;
    private OnSentenceModifiedListener mOnSentenceModifiedListener;

    public interface OnSentenceModifiedListener{
        void onSentenceModified(String content);
    }

    public EnglishWritingModifyPopupView(Activity context,OnSentenceModifiedListener listener) {
        super(context);
        mContext = context;
        mOnSentenceModifiedListener = listener;
        mLayoutInflater = (LayoutInflater) mContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initViews();
    }


    private void initViews() {
        mRootView = mLayoutInflater.inflate(R.layout.layout_popwindow_comment_by_sentence, null);
        mEditText = (EnglishWritingEditText) mRootView.findViewById(R.id.et_modify);
        mCancelBtn = (TextView) mRootView.findViewById(R.id.cancel_btn);
        mCommitBtn = (TextView) mRootView.findViewById(R.id.commit_btn);
        mRootView.setOnTouchListener(this);
        mCancelBtn.setOnClickListener(this);
        mCommitBtn.setOnClickListener(this);

        setContentView(mRootView);
        setFocusable(true);
        setAnimationStyle(R.style.AnimBottom);
        setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        int color = mContext.getResources().getColor(R.color.popup_root_view_bg);
        ColorDrawable colorDrawable = new ColorDrawable(color);
        setBackgroundDrawable(colorDrawable);

    }

    /**
     * 展示窗口
     * @param view
     */
    public void showPopupWindowAtLocation(View view){
        if (view != null){
            //坐标是相对整个屏幕而言，Y坐标为View左上角到屏幕顶部的距离。
            int[] locationArray = new int[2];
            //获取View在屏幕坐标系的坐标
            view.getLocationOnScreen(locationArray);
            int locationX = locationArray[0];
            int locationY = locationArray[1];
            int screenWidth = ScreenUtils.getScreenWidth(mContext);
            int screenHeight = ScreenUtils.getScreenHeight(mContext);
            //手机上的窗口宽度是屏幕宽度
            int width = screenWidth;
            int height = screenHeight - locationY;
            setWidth(width);
            setHeight(height);
            //showAtLocation方法较为安全,该方法里面的parent对象可以是布局里面的任意View或者ViewGroup，没啥讲究。
            // Gravity.NO_GRAVITY是代表以屏幕左上角为原点。
            showAtLocation(view, Gravity.NO_GRAVITY,locationX, locationY);
        }
    }

    /**
     * 设置输入框内容
     * @param content
     */
    public void setContent(String content){
        if (!TextUtils.isEmpty(content)){
            if (mEditText != null){
                mEditText.setText(content);
            }
        }
    }

    private boolean isAllowCommit(){
        if (mEditText != null && mEditText.getText().toString().trim().length() > 0){
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_btn:
                //提交
                if (isAllowCommit()){
                    dismiss();
                    if (mOnSentenceModifiedListener != null){
                        String content = mEditText.getText().toString().trim();
                        mOnSentenceModifiedListener.onSentenceModified(content);
                    }
                }else {
                    TipsHelper.showToast(mContext,R.string.pls_input_article_content);
                }
                break;
            case R.id.cancel_btn:
                //取消
                dismiss();
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = mRootView.findViewById(R.id.layout_bottom).getTop();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (y > height) {
                dismiss();
            }
        }
        return true;
    }
}
