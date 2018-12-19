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
    private ImageView lr_back_iv;
    private TextView title_tv;

    public static void start(Activity activity, String url, String title){
        activity.startActivity(new Intent(activity, WebActivity.class)
        .putExtra("url", url).putExtra("title", title));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView)findViewById(R.id.web_view);
        WebSettings webSettings =   webView.getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        url = getIntent().getStringExtra("url");
        if(url != null && webView != null) {
            webView.loadUrl(url.trim());
            webView.setWebViewClient(new NoAdWebViewClient(this, webView));
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
