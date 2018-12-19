package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterFragment;
import com.lqwawa.apps.views.HorizontalProgressView;


public class FileFragment extends AdapterFragment implements OnClickListener{

    public static final String TAG = FileFragment.class.getSimpleName();

    public interface Constants {
        public final static String EXTRA_LAYOUT_ID = "layoutId";
        public final static String EXTRA_ID = "id";
        public final static String EXTRA_TITLE = "title";
        public final static String EXTRA_CONTENT_URL = "url";
        public final static String EXTRA_SOURCE = "source";
        public final static String EXTRA_SCHOOL_INFO = "school_info";
        //是否显示关闭布局
        public final static String EXTRA_SHOW_CLOSE_LAYOUT = "show_close_layout";
        public final static String EXTRA_ROOT_LAYOUT_ID = "root_layout_id";
    }
    private WebView webView;
    private String url;
    private int rootLayoutId;//根布局
    private HorizontalProgressView progressView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int layoutId = getArguments().getInt(Constants.EXTRA_LAYOUT_ID,
                R.layout.activity_file);
        return inflater.inflate(layoutId, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        url= getArguments().getString(Constants.EXTRA_CONTENT_URL);
        initViews();
        loadData(url);
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    private void initViews() {
        initHeaderView();
        initWebView();
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        textView.setText(Html.fromHtml(getArguments().getString(Constants.EXTRA_TITLE)));
        findViewById(R.id.contacts_header_left_btn).setOnClickListener(this);
        final TextView  textViewRightBtn= (TextView) findViewById(R.id.contacts_header_right_btn);
        textViewRightBtn.setVisibility(View.INVISIBLE);
        textViewRightBtn.setOnClickListener(this);

        //控制是否显示头布局和网页关闭按钮
        if (shouldShowCloseLayout()){
            View header = findViewById(R.id.contacts_header_layout);
            if (header != null){
                //隐藏头部
                header.setVisibility(View.GONE);
            }
            initCloseLayout();
        }
    }

    /**
     * 判断是否需要显示关闭布局
     * @return
     */
    private boolean shouldShowCloseLayout(){
        Bundle args = getArguments();
        if (args != null){
            int rootLayoutId = args.getInt(Constants.EXTRA_ROOT_LAYOUT_ID);
            return rootLayoutId != 0;
        }
        return false;
    }

    /**
     * 网页左上角覆盖“X”，关闭按钮。
     */
    private void initCloseLayout() {
        if (getArguments() != null) {
            rootLayoutId = getArguments().getInt(Constants.EXTRA_ROOT_LAYOUT_ID);
        }
        //去除状态栏后的布局
        View view = getActivity().getWindow().getDecorView().findViewById(rootLayoutId);
        ViewParent viewParent = view.getParent();
        if (viewParent != null && viewParent instanceof FrameLayout) {
            final FrameLayout frameLayout = (FrameLayout) viewParent;

            //内容布局
            FrameLayout contentLayout = new FrameLayout(getActivity());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            lp.width = (int) (40 * MyApplication.getDensity());
            lp.height = (int) (48 * MyApplication.getDensity());
            lp.leftMargin = (int) (40 * MyApplication.getDensity());
            contentLayout.setLayoutParams(lp);
            //设置背景
//            contentLayout.setBackground(
//                    getResources().getDrawable(R.drawable.selector_close_layout));
            contentLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //返回
                    getActivity().finish();
                }
            });

            //图片，加上一个关闭按钮
            ImageView closeImageView = new ImageView(getActivity());
            closeImageView.setImageResource(R.drawable.btn_web_close);
            closeImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            //图片布局
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.width = (int) (20 * MyApplication.getDensity());
            params.height = (int) (24 * MyApplication.getDensity());
            params.leftMargin = (int) (10 * MyApplication.getDensity());
            params.topMargin = (int) (12 * MyApplication.getDensity());

            //添加图片
            contentLayout.addView(closeImageView,params);

            //添加子布局
            frameLayout.addView(contentLayout);
        }
    }
    /**
     * WebView基本配置
     */
    private void initWebView() {
        progressView = new HorizontalProgressView(getActivity());
        progressView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, DensityUtils.dp2px(getActivity(), 4)));
        progressView.setColor(getResources().getColor(R.color.text_green));

        this.webView = (WebView) findViewById(R.id.web_view);
        this.webView.setVisibility(View.VISIBLE);
        this.webView.addView(progressView);

        WebSettings ws = this.webView.getSettings();
        ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        ws.setJavaScriptEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setDomStorageEnabled(true);

        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        ws.setAllowFileAccess(true);
        ws.setPluginState(WebSettings.PluginState.ON);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        if (mDensity == 240) {
            ws.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == 160) {
            ws.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else if(mDensity == 120) {
            ws.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        }else if(mDensity == DisplayMetrics.DENSITY_XHIGH){
            ws.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }else if (mDensity == DisplayMetrics.DENSITY_TV){
            ws.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }else{
            ws.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }

        // 允许其加载混合网络协议内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        this.webView.setLongClickable(false);
        this.webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
//                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //页面开始加载，进度条显示。
//                showLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //页面结束加载，进度条隐藏。
//                dismissLoadingDialog();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                // 接受证书
                handler.proceed();
            }
        });
        this.webView.setOnLongClickListener(new WebView.OnLongClickListener() {
            public boolean onLongClick(View v) {
                return true;
            }
        });

        this.webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100){
                    progressView.setVisibility(View.GONE);
                } else {
                    if (progressView.getVisibility() == View.GONE) {
                        progressView.setVisibility(View.VISIBLE);
                    }
                    progressView.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        this.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    private void loadData(String url) {
        if (!TextUtils.isEmpty(url)) {
            this.webView.loadUrl(url);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.contacts_header_left_btn){
            if (webView != null){
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }else {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            finish();
            return true;
        }
        return super.onKey(v, keyCode, event);
    }
    public WebView getWebView (){
        return webView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView!=null){
            webView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView!=null){
            webView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView!=null){
            webView.destroy();
            webView = null;
        }
    }
}
