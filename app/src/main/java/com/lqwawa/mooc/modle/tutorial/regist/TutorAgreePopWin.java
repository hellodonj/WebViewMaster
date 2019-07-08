package com.lqwawa.mooc.modle.tutorial.regist;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;


/**
 * desc: 治疗明细的填写治疗记录的popup
 * author: dj
 * date: 2017/5/22 11:02
 */
public class TutorAgreePopWin extends PopupWindow {

    private Context mContext;

    private View view;

    private TextView btnConfirm;

    private WebView webView;

    private static final int RECORD = 10000;

    public TutorAgreePopWin(Activity context, String url) {
        mContext = context;

        this.view = LayoutInflater.from(context).inflate(R.layout.tutor_agreement_dialog, null);

        btnConfirm = (TextView) view.findViewById(R.id.tv_confirm);
        webView = (WebView) view.findViewById(R.id.web_view);

        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        if (url != null && webView != null) {
            webView.loadUrl(url.trim());
        }

        // 设置按钮监听
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onConfirmClick(btnConfirm, RECORD);
                dismiss();
            }
        });

        // 设置外部可点击
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xE0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.cancel_project_anim);

    }


    public interface OnConfirmClickListener {
        void onConfirmClick(TextView button, int tag);
    }

    private OnConfirmClickListener mListener;

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        mListener = listener;
    }
}
