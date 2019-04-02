package com.lqwawa.lqresviewlib.office365;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.osastudio.apps.BaseActivity;

/**
 * Created by XChen on 2017/1/3.
 * email:man0fchina@foxmail.com
 */

public class WebActivity extends BaseActivity{
    private WebView webView;
    private String url;
    private String bodyHtml;
    private String text;
    private ImageView lr_back_iv;
    private TextView title_tv;
    private TextView mTvContent;
    public static void start(Activity activity, String url, String title){
        activity.startActivity(new Intent(activity, WebActivity.class)
        .putExtra("url", url).putExtra("title", title));
    }

    public static void start(Activity activity, String bodyHtml, String title,boolean isBodyHtml){
        activity.startActivity(new Intent(activity, WebActivity.class)
                .putExtra("bodyHtml", bodyHtml)
                .putExtra("title", title)
                .putExtra("isBodyHtml",isBodyHtml));
    }

    public static void start(boolean warning,Activity activity, String text, String title){
        activity.startActivity(new Intent(activity, WebActivity.class)
                .putExtra("warning", warning)
                .putExtra("title", title)
                .putExtra("text",text));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView)findViewById(R.id.web_view);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        mTvContent.setText(R.string.label_null_tutorial_introduces);

        WebSettings webSettings =   webView.getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        url = getIntent().getStringExtra("url");
        bodyHtml = getIntent().getStringExtra("bodyHtml");
        text = getIntent().getStringExtra("text");
        if(url != null && webView != null) {
            webView.loadUrl(url.trim());
            webView.setWebViewClient(new NoAdWebViewClient(this, webView));
        }

        if(bodyHtml != null && webView != null) {
            webView.loadData(bodyHtml,"text/html;charset=UTF-8",null);
            webView.setWebViewClient(new NoAdWebViewClient(this, webView));
        }

        if(url == null && bodyHtml == null){
            webView.setVisibility(View.GONE);
            mTvContent.setVisibility(View.VISIBLE);
        }else{
            webView.setVisibility(View.VISIBLE);
            mTvContent.setVisibility(View.GONE);
            mTvContent.setText(text);
        }

        lr_back_iv = (ImageView)findViewById(R.id.lr_back_iv);
        lr_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_tv = (TextView)findViewById(R.id.title_tv);
        title_tv.setText("" + getIntent().getStringExtra("title"));
    }


}
