package com.galaxyschool.app.wawaschool.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.AccountActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.StudyInfoRecordUtil;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CampusOnline;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeSchoolListResult;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.lqwawa.apps.views.HorizontalProgressView;
import com.lqwawa.client.pojo.SourceFromType;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.osastudio.common.library.LqBase64Helper;
import com.osastudio.common.utils.TimerUtils;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by Administrator on 2017/1/18.
 */

public class CampusOnlineWebFragment extends ContactsListFragment implements View.OnClickListener {
    public static final String TAG = CampusOnlineWebFragment.class.getSimpleName();
    private ImageView mIcon;
    private boolean mIsMooc;
    private String mTitle;


    public interface Constants {
        public final static String EXTRA_LAYOUT_ID = "layoutId";
        public final static String EXTRA_ID = "id";
        public final static String EXTRA_TITLE = "title";
        public final static String EXTRA_CONTENT_URL = "url";
        public final static String EXTRA_SOURCE = "source";
        public final static String EXTRA_SCHOOL_INFO = "school_info";
    }

    private WebView webView;
    private TextView titleView;
    private TextView shareView;
    private String webUrl;
    private String source;
    private SchoolInfo schoolInfo;
    private CampusOnline campusOnline;
    private String loadUrl;
    private HorizontalProgressView progressView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_campus_online_layout, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }
        getIntent();
        initViews();

        if (isNeedTimerRecorder()){
            TimerUtils.getInstance().startTimer();
        }
    }

    private void getIntent() {
        webUrl = getArguments().getString(FileFragment.Constants.EXTRA_CONTENT_URL);
        mTitle = getArguments().getString(FileFragment.Constants.EXTRA_TITLE);
        mIsMooc = getArguments().getBoolean("isMooc");
        source = getArguments().getString(FileFragment.Constants.EXTRA_SOURCE);
        schoolInfo = (SchoolInfo) getArguments().getSerializable(Constants.EXTRA_SCHOOL_INFO);
        campusOnline = (CampusOnline) getArguments().getSerializable(CampusOnline.class
                .getSimpleName());
        initViews();
        loadData(webUrl);
    }

    private void initViews() {
        //webView加载的进度条
        progressView = new HorizontalProgressView(getActivity());
        progressView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, DensityUtils.dp2px(getActivity(), 4)));
        progressView.setColor(getResources().getColor(R.color.text_green));

        webView = (WebView) findViewById(R.id.campus_webview);
        if (webView != null){
            webView.addView(progressView);
        }

        shareView = (TextView) findViewById(R.id.header_right_btn);
        titleView = (TextView) findViewById(R.id.header_title);
        mIcon = (ImageView) findViewById(R.id.header_title_ico);
        shareView.setText(getString(R.string.share));
        shareView.setOnClickListener(this);
        mIcon.setOnClickListener(this);
        ImageView imageView = (ImageView) findViewById(R.id.header_title_left_btn);
        imageView.setOnClickListener(this);
        initWebView();
        if (campusOnline != null) {
            titleView.setText(campusOnline.getLname());
        }
    }

    private void loadData(String url) {
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        }
    }

    private void initWebView() {
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键
                        webView.goBack();   //后退

                        //webview.goForward();//前进
                        return true;    //已处理
                    }
                }
                return false;
            }
        });
        WebSettings ws = webView.getSettings();
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
        } else if (mDensity == 120) {
            ws.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        } else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
            ws.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == DisplayMetrics.DENSITY_TV) {
            ws.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else {
            ws.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }

//        this.webView.setBackgroundColor(0);
        this.webView.setLongClickable(false);
//      webView.setHorizontalScrollBarEnabled(false);
//      webView.setScrollBarStyle(webView.SCROLLBARS_OUTSIDE_OVERLAY);
//      webView.setHorizontalScrollbarOverlay(true);
        this.webView.setOnLongClickListener(new WebView.OnLongClickListener() {
            public boolean onLongClick(View v) {
                return true;
            }
        });
        this.webView.setWebViewClient(new WebViewClient());
        this.webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100){
                    progressView.setVisibility(View.GONE);
                } else {
                    if (progressView.getVisibility() == View.GONE){
                        progressView.setVisibility(View.VISIBLE);
                    }
                    progressView.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mIsMooc) {
                    mIcon.setVisibility(View.GONE);
                    shareView.setVisibility(View.VISIBLE);
                    titleView.setText(mTitle);
                } else {
                    if(webUrl.equals(url)) {
                        if (campusOnline != null) {
                            titleView.setText(campusOnline.getLname());
                        }

                        if (schoolInfo == null) {
                            mIcon.setVisibility(View.VISIBLE);
                        } else {
                            shareView.setVisibility(View.VISIBLE);
                        }

                    } else {
                        titleView.setText(view.getTitle());
                        shareView.setVisibility(View.GONE);
                        mIcon.setVisibility(View.GONE);
                    }
                }

            }
        });
        this.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.header_title_left_btn) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        } else if (v.getId() == R.id.header_title_ico) {
            showMoreMenu(findViewById(R.id.header_layout));
        } else if (v.getId() == R.id.header_right_btn) {
            shareFreeAddress();
        }
    }

    public WebView getWebView() {
        return webView;
    }

    public void showMoreMenu(View view) {
        List<PopupMenu.PopupMenuData> itemDatas = new ArrayList();
        if (schoolInfo == null) {
            itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.join_orizatination));
        }
        itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.share_to));
        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (schoolInfo == null) {
                            if (position == 0) {
                                //关注机构对用户身份的判断
                                if (isLogin()) {
                                    loadAttentionOriza();
                                } else {
                                    enterAccountActivity();
                                }
                            } else {
                                shareFreeAddress();
                            }
                        } else {
                            shareFreeAddress();
                        }
                    }
                };
        PopupMenu popupMenu = new PopupMenu(getActivity(), itemClickListener,
                itemDatas);
        popupMenu.showAsDropDown(view, view.getWidth(), -5);
    }

    //分享
    protected void shareFreeAddress() {
        UMImage umImage = null;
        ShareInfo shareInfo = new ShareInfo();
        SharedResource resource = new SharedResource();
        if (mIsMooc) {

            shareInfo.setTitle(mTitle);
            shareInfo.setContent(" ");
            umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
            resource.setTitle(mTitle);
            resource.setDescription(" ");
            resource.setThumbnailUrl("");
            int index = webUrl.indexOf("?");
            String headUrl = webUrl.substring(0,index);
            String bodyUrl = webUrl.substring(index+1,webUrl.length());
            bodyUrl = bodyUrl + "&auth=true";
            bodyUrl = LqBase64Helper.getEncoderString(bodyUrl);
            bodyUrl = bodyUrl.replaceAll("[\n\r]", "");
            String url = headUrl + "?enc=" + bodyUrl;
            resource.setShareUrl(url);
            shareInfo.setTargetUrl(url);
//            resource.setThumbnailUrl(AppSettings.getFileUrl(campusOnline.getLimg()));

        } else {
            shareInfo.setTitle(campusOnline.getLname());
            shareInfo.setContent(schoolInfo != null ? schoolInfo.getSchoolName() : " ");
            umImage = new UMImage(getActivity(), AppSettings.getFileUrl(campusOnline.getLimg()));
            resource.setTitle(campusOnline.getLname());
            resource.setDescription(schoolInfo != null ? schoolInfo.getSchoolName() : " ");
            resource.setThumbnailUrl(AppSettings.getFileUrl(campusOnline.getLimg()));
            resource.setShareUrl(webUrl);
            shareInfo.setTargetUrl(webUrl);
        }


//        if (schoolInfo!=null&&!TextUtils.isEmpty(schoolInfo.getSchoolLogo())) {
//            umImage = new UMImage(getActivity(), AppSettings.getFileUrl(schoolInfo.getSchoolLogo()));
//        } else {

//        }
        shareInfo.setuMediaObject(umImage);

        // resource.setId(schoolInfo.getSchoolId());

//        if (schoolInfo!=null&&!TextUtils.isEmpty(schoolInfo.getSchoolLogo())) {
//            resource.setThumbnailUrl(AppSettings.getFileUrl(schoolInfo.getSchoolLogo()));
//        }else {

//        }
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        //resource.setFieldPatches(SharedResource.FIELD_PATCHES_SCHOOL_SHARE_URL);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }

    private void loadAttentionOriza() {
        String url = getUrl();
        if (TextUtils.isEmpty(url)) {
            return;
        }
        loadUrl = url;
        pageHelper.setFetchingPageIndex(0);
        pageHelper.setPageSize(10);
        Map<String, Object> params = new HashMap();
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("LiveShowUrl", url);
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<SubscribeSchoolListResult>(
                        SubscribeSchoolListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        SubscribeSchoolListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateResult(result);
                    }

                    @Override
                    public void onError(NetroidError error) {
                        super.onError(error);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_CAMPUS_ONLINE_ORGANIZATION, params, listener);
    }

    private void updateResult(SubscribeSchoolListResult result) {
        List<SchoolInfo> schoolInfos = result.getModel().getSubscribeNoList();
        if (schoolInfos == null || schoolInfos.size() <= 0) {
            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.now_has_not_school_attention));
            return;
        }
        if (schoolInfos.size() == 1) {
            int state = schoolInfos.get(0).getState();
            if (state == 1 || state == 2) {
                TipMsgHelper.ShowLMsg(getActivity(), R.string.attention_already_oraz);
            } else {
                addSubscribe(schoolInfos.get(0).getSchoolId(), schoolInfos.get(0).getSchoolName());
            }
        } else {
            this.webView.onPause();
            CampusOnlineAttentionFragment fragment = new CampusOnlineAttentionFragment();
            Bundle args = new Bundle();
            args.putString(CampusOnlineAttentionFragment.LOAD_URL, loadUrl);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            fragment.setArguments(args);
            ft.show(fragment);
            ft.hide(this);
            ft.add(R.id.activity_body, fragment, CampusOnlineAttentionFragment.TAG);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void addSubscribe(final String subscribeUserId, final String schoolName) {
        if (TextUtils.isEmpty(subscribeUserId)) {
            return;
        }
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", subscribeUserId);
        DefaultDataListener<DataModelResult> listener =
                new DefaultDataListener<DataModelResult>(DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.attention_one_oriza, schoolName));
                        }
                    }
                };
        listener.setShowLoading(true);
        String serverUrl = ServerUrl.SUBSCRIBE_ADD_SCHOOL_URL;
        RequestHelper.sendPostRequest(getActivity(), serverUrl, params, listener);

    }

    private String getUrl() {
        String url = this.webUrl;
        url = url.substring(url.indexOf("?") + 1);
        String[] urlArray = url.split("&");
        for (int i = 0; i < urlArray.length; i++) {
            if (urlArray[i].contains("db=")) {
                url = urlArray[i].substring(urlArray[i].indexOf("=") + 1);
                return url;
            }
        }
        return null;
    }

    //如果用户没有登录之后才可以关注机构
    private void enterAccountActivity() {
        TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_login);
        //登录
        Intent intent = new Intent(getActivity(), AccountActivity.class);
        Bundle args = new Bundle();
        args.putBoolean(AccountActivity.EXTRA_HAS_LOGINED, false);
        args.putBoolean(AccountActivity.EXTRA_ENTER_HOME_AFTER_LOGIN, false);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }
    }

    @Override
    public void onDestroy() {
        if (this.webView != null) {
            webView.destroy();
            webView = null;
        }
        if (isNeedTimerRecorder()){
            TimerUtils.getInstance().pauseTimer();
            //把观看的数据同步给server
            StudyInfoRecordUtil.getInstance().
                    clearData().
                    setActivity(getActivity()).
                    setCampusOnline(campusOnline).
                    setCurrentModel(StudyInfoRecordUtil.RecordModel.ONLINE_MODEL).
                    setRecordTime(TimerUtils.getInstance().getCurrentTotalTime()).
                    setRecordType(2).
                    setSourceType(SourceFromType.OTHER).
                    setUserInfo(DemoApplication.getInstance().getUserInfo()).
                    send();
            TimerUtils.getInstance().stopTimer();
        }
        super.onDestroy();
    }


    public void setWebViewOnResume() {
        if (this.webView != null) {
            this.webView.onResume();
        }
    }

    /**
     * @return 判断返回记录观看时间的条件
     */
    private boolean isNeedTimerRecorder(){
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (userInfo != null && !mIsMooc){
            return true;
        }
        return false;
    }
}
