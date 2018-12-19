package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.BroadcastNoteActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterFragment;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

/**校园电视台宣传页面
 */
public class CampusTVPromotionPageFragment extends AdapterFragment implements View.OnClickListener {
    public static final String TAG = CampusTVPromotionPageFragment.class.getSimpleName();

    public interface Constants {
        public final static String EXTRA_LAYOUT_ID = "layoutId";
        public final static String EXTRA_ID = "id";
        public final static String EXTRA_TITLE = "title";
        public final static String EXTRA_CONTENT_URL = "url";
        public final static String EXTRA_SOURCE = "source";
        public final static String EXTRA_SCHOOL_INFO = "school_info";
    }
    private WebView webView;
    private String url;
    private LinearLayout noResourceBgLayout;
    private TextView downloadBtn,phoneBtn;
    private String source;
    private SchoolInfo schoolInfo;
    private TextView textViewRightBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int layoutId = R.layout.layout_fragment_campus_tv_promotion_page;
        if (getArguments() != null) {
            layoutId = getArguments().getInt(Constants.EXTRA_LAYOUT_ID,
                    R.layout.layout_fragment_campus_tv_promotion_page);
        }
        return inflater.inflate(layoutId, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
        loadData(url);
    }

    private void getIntent() {
        if (getArguments() != null) {
            url = getArguments().getString(Constants.EXTRA_CONTENT_URL);
            source = getArguments().getString(Constants.EXTRA_SOURCE);
            schoolInfo = (SchoolInfo) getArguments().getSerializable(Constants.EXTRA_SCHOOL_INFO);
        }
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    private void initViews() {
        initHeaderView();
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (getArguments() != null) {
            textView.setText(Html.fromHtml(getArguments().getString(Constants.EXTRA_TITLE)));
        }
        findViewById(R.id.contacts_header_left_btn).setOnClickListener(this);
        textViewRightBtn= (TextView) findViewById(R.id.contacts_header_right_btn);
        textViewRightBtn.setVisibility(View.INVISIBLE);
        textViewRightBtn.setOnClickListener(this);

        initWebView();

        //宣传图
        this.noResourceBgLayout = (LinearLayout) findViewById(R.id.no_resource_bg_layout);
        if(TextUtils.isEmpty(source)){
            textViewRightBtn.setVisibility(View.INVISIBLE);
            this.noResourceBgLayout.setVisibility(View.GONE);
            this.webView.setVisibility(View.VISIBLE);
        }else if(source.equals("campus_live_show")) {
            textViewRightBtn.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(url)) {
                this.noResourceBgLayout.setVisibility(View.VISIBLE);
                this.webView.setVisibility(View.GONE);
                textViewRightBtn.setText(R.string.free_use);
            } else {
                this.noResourceBgLayout.setVisibility(View.GONE);
                this.webView.setVisibility(View.VISIBLE);
                textViewRightBtn.setText(R.string.share);
            }
        }

        downloadBtn = (TextView) findViewById(R.id.download_btn);
        phoneBtn = (TextView) findViewById(R.id.phone_btn);
        this.downloadBtn.setOnClickListener(this);
        this.phoneBtn.setOnClickListener(this);
    }

    /**
     * WebView基本配置
     */
    private void initWebView() {
        this.webView = (WebView) findViewById(R.id.web_view);
        this.webView.setVisibility(View.GONE);

        WebSettings ws = this.webView.getSettings();
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        ws.setJavaScriptEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setDomStorageEnabled(true);

        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
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

        this.webView.setLongClickable(false);
        this.webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        this.webView.setOnLongClickListener(new WebView.OnLongClickListener() {
            public boolean onLongClick(View v) {
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 0 && !TextUtils.isEmpty(source) && source.equals
                        ("campus_live_show")) {
                    textViewRightBtn.setVisibility(!webView.canGoBack() ?
                            View.VISIBLE : View.INVISIBLE);
                }
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
        });
    }

    private void loadData(String url) {
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.contacts_header_left_btn){
            finish();
        }else if(v.getId()==R.id.download_btn){
            TipMsgHelper.ShowLMsg(getActivity(),getString(R.string.campus_live_show_nosupport));
        } else if(v.getId()==R.id.phone_btn){
            ActivityUtils.gotoTelephone(getActivity());
        } else if(v.getId()==R.id.contacts_header_right_btn){
            if(!TextUtils.isEmpty(source)&&source.equals("campus_live_show")){
                if(TextUtils.isEmpty(url)){
                    Intent intent=new Intent(getActivity(),BroadcastNoteActivity.class);
                    intent.putExtra(BroadcastNoteActivity.IS_FROM_CAMPUS_ONLINE,true);
                    startActivity(intent);
                }else{
                    shareFreeAddress();
                }
            }
        }
    }

    protected void shareFreeAddress() {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(schoolInfo!=null?schoolInfo.getSchoolName()+
                getString(R.string.now_direct): getString(R.string.free_use_title));
        shareInfo.setContent(" ");
        String url=getString(R.string.free_use_address);
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if (schoolInfo!=null&&!TextUtils.isEmpty(schoolInfo.getSchoolLogo())) {
            umImage = new UMImage(getActivity(), AppSettings.getFileUrl(schoolInfo.getSchoolLogo()));
        } else {
            umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
        }
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        // resource.setId(schoolInfo.getSchoolId());
        resource.setTitle(schoolInfo!=null?schoolInfo.getSchoolName()+getString(R.string.now_direct)
                : getString(R.string.free_use_title));

        resource.setDescription(" ");
        resource.setShareUrl(url);
        if (schoolInfo!=null&&!TextUtils.isEmpty(schoolInfo.getSchoolLogo())) {
            resource.setThumbnailUrl(AppSettings.getFileUrl(schoolInfo.getSchoolLogo()));
        }
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        //resource.setFieldPatches(SharedResource.FIELD_PATCHES_SCHOOL_SHARE_URL);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
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