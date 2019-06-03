package com.lqwawa.intleducation.module.discovery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.lqresviewlib.office365.NoAdWebViewClient;

/**
 * @author: wangchao
 * @date: 2019/05/22
 * @desc:
 */
public class WebFragment extends MyBaseFragment {

    private WebView webView;
    private TextView contentTextView;

    private String url;
    private String bodyHtml;
    private String content;

    public static WebFragment newInstance(String url, String bodyHtml, String content) {

        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("bodyHtml", bodyHtml);
        args.putString("content", content);

        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        webView = (WebView) view.findViewById(R.id.web_view);
        contentTextView = (TextView) view.findViewById(R.id.tv_content);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            url = getArguments().getString("url");
            bodyHtml = getArguments().getString("bodyHtml");
            content = getArguments().getString("content");
        }

        contentTextView.setMovementMethod(new ScrollingMovementMethod());

        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultFontSize(40);
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


        contentTextView.setText(R.string.label_null_tutorial_introduces);
        if (url != null && webView != null) {
            webView.loadUrl(url.trim());
            webView.setWebViewClient(new NoAdWebViewClient(getContext(), webView));
        }

        if (bodyHtml != null && webView != null) {
            webView.loadData(bodyHtml, "text/html;charset=UTF-8", null);
            webView.setWebViewClient(new NoAdWebViewClient(getContext(), webView));
        }

        if (TextUtils.isEmpty(url) && TextUtils.isEmpty(bodyHtml)) {
            webView.setVisibility(View.GONE);
            contentTextView.setVisibility(View.VISIBLE);
            contentTextView.setText(!TextUtils.isEmpty(content) ? content :
                    getString(R.string.label_empty_content));
            contentTextView.setGravity(!TextUtils.isEmpty(content) ? Gravity.TOP : Gravity.CENTER);
        } else {
            webView.setVisibility(View.VISIBLE);
            contentTextView.setVisibility(View.GONE);
        }
    }
}
