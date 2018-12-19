package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by renbao on 16-3-23.
 */
public class MyWebView extends WebView {
    public MyWebView(Context context) {
        super(context);
        setAttributes(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(context);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle);
        setAttributes(context);
    }
    public void setAttributes(Context context){
        WebSettings webSettings = this.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);//�ؼ��
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true); // ����֧��javascript�ű�
        webSettings.setAllowFileAccess(true); // ��������ļ�
        webSettings.setBuiltInZoomControls(true); // ������ʾ���Ű�ť
        webSettings.setSupportZoom(true); // ֧������
        webSettings.setLoadWithOverviewMode(true);
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        if (mDensity == 240) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == 160) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else if(mDensity == 120) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        }else if(mDensity == DisplayMetrics.DENSITY_XHIGH){
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }else if (mDensity == DisplayMetrics.DENSITY_TV){
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }else{
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }
        /**
         * ��WebView��ʾͼƬ����ʹ��������� ������ҳ�������ͣ� 1��LayoutAlgorithm.NARROW_COLUMNS ��
         * ��Ӧ���ݴ�С 2��LayoutAlgorithm.SINGLE_COLUMN:��Ӧ��Ļ�����ݽ��Զ�����
         */
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //����ֵ��true��ʱ�����ȥWebView�򿪣�Ϊfalse����ϵͳ���������������
                view.loadUrl(url);
                return true;
            }
        });
    }

}
