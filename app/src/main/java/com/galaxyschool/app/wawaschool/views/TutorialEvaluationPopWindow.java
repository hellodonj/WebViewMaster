package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;

/**
 * 描述: 帮辅评价的弹框
 * 作者|时间: djj on 2019/6/15 0015 下午 5:42
 */

public class TutorialEvaluationPopWindow extends PopupWindow {

    private Context mContext;
    private View mRootView;
    private RatingBar mRatingBar;
    private EditText mEtEvaluation;
    private TextView mTvSend;

    public TutorialEvaluationPopWindow(Activity context) {
        this.mContext = context;
        this.mRootView = LayoutInflater.from(context).inflate(R.layout.tutorial_evaluation_pop, null);

        mRatingBar = (RatingBar) mRootView.findViewById(R.id.evaluation_rating_bar);
        mEtEvaluation = (EditText) mRootView.findViewById(R.id.et_evaluation);
        mTvSend = (TextView) mRootView.findViewById(R.id.tv_btn_send);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mRatingBarListener.onRatingBarClick(mRatingBar, rating);
            }
        });

        mTvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSendClick(mTvSend, mEtEvaluation, mRatingBar.getRating());
                dismiss();
            }
        });

        // 设置外部可点击
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            this.setOutsideTouchable(true);
        }
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.mRootView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = mRootView.findViewById(R.id.popup_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.mRootView);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xFFFFFFF);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        //this.setAnimationStyle(R.style.cancel_project_anim);
    }

    public interface OnSendClickListener {
        void onSendClick(TextView button, EditText text, float rating);
    }

    private OnSendClickListener mListener;

    public void setOnSendClickListener(OnSendClickListener listener) {
        mListener = listener;
    }

    public interface OnRatingBarClickListener {
        void onRatingBarClick(RatingBar ratingBar, float v);
    }

    private OnRatingBarClickListener mRatingBarListener;

    public void setOnRatingBarClickListener(OnRatingBarClickListener listener) {
        mRatingBarListener = listener;
    }


}
