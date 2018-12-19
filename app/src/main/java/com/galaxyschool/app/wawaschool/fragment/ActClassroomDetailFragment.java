package com.galaxyschool.app.wawaschool.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.AirClassroomActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.PerformClassList;
import com.galaxyschool.app.wawaschool.pojo.PerformClassListResult;
import com.galaxyschool.app.wawaschool.pojo.PerformMember;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.views.OnlineIntroPopwindow;
import com.galaxyschool.app.wawaschool.views.PagerSlidingTabStrip;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.sdk.surfaceview.impl.BaseSurfaceView;
import com.lecloud.sdk.videoview.IMediaDataVideoView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.skin.videoview.vod.UIVodVideoView;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ActClassroomDetailFragment extends ContactsListFragment {
    public static String TAG = ActClassroomDetailFragment.class.getSimpleName();
    private IMediaDataVideoView videoView;
    private UIVodVideoView uiVodVideoView;
    private ISurfaceView surfaceView;
    //作品简介
//    private ActProductionIntroductionFragment productionIntroFragment;
    //演员表
    private ActActorListFragment actorListFragment;
    //讨论
    private ActTopicDiscussionFragment topicDiscussionFragment;
    private OnlineIntroPopwindow pop;
    private RelativeLayout videoContainer;
    private LinearLayout bottomLayout, mIntrolayout;
    private PagerSlidingTabStrip titleTip;
    private ViewPager onlineRestab;
    private MyPagerAdapter mAdapter;
    private TextView sourceFromTextV;
    private int currentIndex = 0;
    private SchoolInfo schoolInfo;
    private PerformClassList performClassList;

    private TextView lookCounts, praiseCounts, mOnlineTitle;
    private ImageView actQrCode, praiseBtn, mLiveBack, mLiveShare;

    private boolean isAlreadyPraise = false;


    private String[] titles = null;

    private int performId = -1;

    private boolean isFirstIn = true;
    private long currentPlayPosition = 0;
    private boolean isMp4;
    private boolean fromMyPerformance;
    private String myPerformanceUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_act_classroom_detail, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntentData();
        if (performClassList == null) {
            loadPerformData();
        } else {
            accordingConditionLoadData();
        }
    }

    private void accordingConditionLoadData() {
        initViews();
        choosePlayStatus();
        setListeners();
        startPlayResources();

        //更新播放视频的次数
        upDateActClassroomPlayCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoView != null && !videoView.isPlaying()) {
            if (!isMp4) {
                videoView.onResume();
            } else {
                if (!isFirstIn) {
                    videoView.seekTo(currentPlayPosition);
                    videoView.onResume();
                    if (uiVodVideoView != null) {
                        uiVodVideoView.onClickContinuePlay();
                    }
                }
                isFirstIn = false;
            }
        }
    }

    private void initViews() {
        initNormalView();
        initFragments();
        showViewData();
    }

    private void showViewData() {
        lookCounts = (TextView) findViewById(R.id.act_classroom_look_count);
        if (lookCounts != null) {
            lookCounts.setText(getString(R.string
                    .act_classroom_look_count, performClassList.getPlayCount() + ""));
        }
        praiseCounts = (TextView) findViewById(R.id.act_classroom_praise_count);
        if (praiseCounts != null) {
            praiseCounts.setText(performClassList.getPraiseCount() + "");
        }
        actQrCode = (ImageView) findViewById(R.id.act_qr_code_icon);

        if (performClassList != null) {
            if (performClassList.getType() == 0) {
                actQrCode.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.qr_code_icon));
            } else {
                actQrCode.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.performance_ar_grey));
            }
        }

        if (actQrCode != null) {
            actQrCode.setOnClickListener(this);
        }
        praiseBtn = (ImageView) findViewById(R.id.act_class_praise_btn);
        if (praiseBtn != null) {
            praiseBtn.setOnClickListener(this);
        }
    }

    private void getIntentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            schoolInfo = (SchoolInfo) bundle.getSerializable(AirClassroomActivity.EXTRA_IS_SCHOOLINFO);
            performClassList = (PerformClassList) bundle.getSerializable(ActivityUtils.EXTRA_ACT_CLASSROOM_DATA);
            if (performClassList == null) {
                performId = bundle.getInt(ActClassroomFragment.Constants.EXTRA_PERFORM_ID);
            }
            fromMyPerformance = bundle.getBoolean(ActClassroomFragment.Constants.EXTRA_FROM_MY_PERFORMANCE, false);
            if (fromMyPerformance) {
                myPerformanceUserId = bundle.getString(ActClassroomFragment.Constants.EXTRA_MY_PERFORMANCE_USERID);
            }
        }
    }

    private void initFragments() {
//        productionIntroFragment=new ActProductionIntroductionFragment(performClassList);
        actorListFragment = new ActActorListFragment(performClassList);
        topicDiscussionFragment = new ActTopicDiscussionFragment(performClassList, bottomLayout);

        currentIndex = 0;
        titleTip = (PagerSlidingTabStrip) findViewById(R.id.school_resource_tabs);
        onlineRestab = (ViewPager) findViewById(R.id.school_resource_pager);
        mAdapter = new MyPagerAdapter(getChildFragmentManager());
        onlineRestab.setAdapter(mAdapter);
        titleTip.setViewPager(onlineRestab);
        onlineRestab.setCurrentItem(currentIndex);
        onlineRestab.setOffscreenPageLimit(2);
        setCurrentBottomLayout(false);

        titleTip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    setCurrentBottomLayout(true);
                } else {
                    setCurrentBottomLayout(false);
                    //重置一下显示的数据
                    topicDiscussionFragment.resetEditText();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initNormalView() {
        initTopButton();
        //显示简介按钮的状态
        initIntroView();
        videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout_interactive);
    }

    private void initTopButton() {
        mLiveBack = (ImageView) findViewById(R.id.iv_live_back);
        if (mLiveBack != null) {
            mLiveBack.setOnClickListener(this);
        }
        mLiveShare = (ImageView) findViewById(R.id.iv_live_share);
        if (mLiveShare != null) {
            mLiveShare.setOnClickListener(this);
        }
    }

    /**
     * 初始化简介的数据
     */
    private void initIntroView() {
        mOnlineTitle = (TextView) findViewById(R.id.tv_online_title);
        if (mOnlineTitle != null) {
            mOnlineTitle.setText(performClassList.getTitle());
        }
        mIntrolayout = (LinearLayout) findViewById(R.id.layout_intro);
        if (mIntrolayout != null) {
            mIntrolayout.setOnClickListener(this);
        }
        //显示来源
        sourceFromTextV = (TextView) findViewById(R.id.tv_source_from);
        if (fromMyPerformance && performClassList != null) {
            List<PerformMember> performMembers = performClassList.getPerformMemberList();
            String sourName = getString(R.string.str_personal);
            if (performMembers != null && performMembers.size() > 0) {
                String userMemberId = myPerformanceUserId;
                if (TextUtils.isEmpty(userMemberId)) {
                    userMemberId = DemoApplication.getInstance().getMemberId();
                }
                String sourceFromClass = null;
                String sourceFromPersonal = null;
                for (int i = 0; i < performMembers.size(); i++) {
                    if (TextUtils.equals(userMemberId, performMembers.get(i).getMemberId())) {
                        if (TextUtils.isEmpty(performMembers.get(i).getClassName())) {
                            sourceFromPersonal = getString(R.string.str_personal);
                        } else {
                            sourceFromClass = performMembers.get(i).getClassName();
                        }
                    }
                }
                if (!TextUtils.isEmpty(sourceFromClass) && !TextUtils.isEmpty(sourceFromPersonal)){
                    String from = sourceFromClass + getString(R.string.str_and) + sourceFromPersonal;
                    sourName = getString(R.string.str_source_from,from);
                } else if (!TextUtils.isEmpty(sourceFromClass)){
                    sourName = getString(R.string.str_source_from, sourceFromClass);
                } else {
                    sourName = getString(R.string.str_source_from, sourceFromPersonal);
                }
            }
            sourceFromTextV.setText(sourName);
            sourceFromTextV.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新表演课堂点赞的次数
     */
    private void upDateActClassroomPraiseCount() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Id", performClassList.getId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        String errorMessage = getResult().getErrorMessage();
                        if (TextUtils.isEmpty(errorMessage)) {
                            praiseBtn.setImageResource(R.drawable.praise_green);
                            praiseCounts.setText((performClassList.getPraiseCount() + 1) + "");
                            isAlreadyPraise = true;
                            TipMsgHelper.ShowMsg(getActivity(), R.string.praise_success);
                        } else {
                            TipMsgHelper.ShowLMsg(getActivity(), errorMessage);
                        }
                    }
                };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.ACT_CLASSROOM_VIDEO_PRAISE_BASE_URL, params, listener);
    }

    /**
     * 更新表演课堂视频播放的次数
     */
    private void upDateActClassroomPlayCount() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Id", performClassList.getId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                    }
                };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.ACT_ALASSROOM_VIDEO_PLAY_BASE_URL, params, listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //表演课堂的分享
            case R.id.iv_live_share:
                shareActclassOnline();
                break;
            //返回 结束当前的界面
            case R.id.iv_live_back:
                UIUtils.hideSoftKeyboard(getActivity());
                getActivity().finish();
                break;
            //二维码
            case R.id.act_qr_code_icon:

                if (performClassList != null) {
                    if (performClassList.getType() == 0) {
                        showQRcodeDialog();
                    } else {
                        showARcodeDialog();
                    }
                }

                break;
            //点赞
            case R.id.act_class_praise_btn:
                if (isAlreadyPraise) {
                    TipMsgHelper.ShowLMsg(getActivity(), R.string.have_praised);
                } else {
                    upDateActClassroomPraiseCount();
                }
                break;
            case R.id.layout_intro:
                changeIntroStatus();
                break;
            default:
        }
    }

    private void showARcodeDialog() {

        String url = ServerUrl.ACT_CLASSROOM_VIDEO_AR_URL;
        StringBuilder tempUrl = new StringBuilder(url);
        tempUrl.append("?photo_id=" + performClassList.getPhotoId());

        ThisStringRequest request = new ThisStringRequest(Request.Method.GET, tempUrl.toString(), new
                Listener<String>() {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(jsonString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        JSONObject data = jsonObject.optJSONObject("data");

                        if (data != null) {
                            String picUrl = data.optString("pic");

                            Utils.showViewQrCodeDialog(getActivity(), picUrl, getString(R.string.act_classroom), performClassList.getTitle());

                        }

                    }

                    @Override
                    public void onError(NetroidError error) {
                        if (getActivity() == null) {
                            return;
                        }
                        TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());

    }

    private void showQRcodeDialog() {

        NewResourceInfo newResourceInfo = new NewResourceInfo();
        newResourceInfo.setShareAddress(performClassList.getShareUrl());
        newResourceInfo.setMicroId(performClassList.getResId());
        newResourceInfo.setTitle(performClassList.getTitle());
        Utils.showViewQrCodeDialog(getActivity(), newResourceInfo, getString(R.string.act_classroom));

    }

    /**
     * 点击界面切换简介的状态
     */
    private void changeIntroStatus() {
        int height = videoContainer.getMeasuredHeight();
        if (pop == null) {
            pop = new OnlineIntroPopwindow(getActivity(), performClassList, height);
        }
        pop.showPopupMenu();
    }

    /**
     * 通过id获取表演课堂单个item数据
     */
    private void loadPerformData() {
        Map<String, Object> params = new HashMap();
        //学校Id，必填
        params.put("Id", performId);

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_ACT_CLASSROOM_DETAIL_DATA_BASE_URL, params,
                new DefaultPullToRefreshDataListener<PerformClassListResult>(
                        PerformClassListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        performClassList = (PerformClassList) getResult().getModel().getData().get(0);
                        accordingConditionLoadData();
                    }
                });

    }

    /**
     * 分享表演课堂
     */
    private void shareActclassOnline() {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(performClassList.getTitle());
        shareInfo.setContent(schoolInfo != null ? schoolInfo.getSchoolName() : " ");
        String url = performClassList.getShareUrl();
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        umImage = new UMImage(getActivity(), AppSettings.getFileUrl(performClassList.getResThumbnail()));
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        resource.setTitle(performClassList.getTitle());
        resource.setDescription(schoolInfo != null ? schoolInfo.getSchoolName() : " ");
        resource.setShareUrl(url);
        resource.setThumbnailUrl(AppSettings.getFileUrl(performClassList.getResThumbnail()));
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }

    private void choosePlayStatus() {
        videoView = new UIVodVideoView(getActivity(), false, true);
        videoContainer.addView((View) videoView, computeContainerSize(getActivity(), 16, 9));
    }

    /**
     * 配置监听器
     */
    private void setListeners() {
        VideoViewListener videoViewListener = new VideoViewListener() {
            @Override
            public void onStateResult(int event, Bundle bundle) {
                handlePlayerEvent(event, bundle);// 处理播放器事件
            }

            @Override
            public String onGetVideoRateList(LinkedHashMap<String, String> linkedHashMap) {
                return null;
            }
        };
        videoView.setVideoViewListener(videoViewListener);

    }

    private void handlePlayerEvent(int state, Bundle bundle) {
        switch (state) {
            //准备开始播放
            case PlayerEvent.PLAY_PREPARED:
                if (videoView != null && MyApplication.getCdeInitSuccess()) {
                    videoView.onStart();
                }
                break;
            case PlayerEvent.ACTION_LIVE_PLAY_PROTOCOL:
                setActionLiveParameter(bundle.getBoolean(PlayerParams.KEY_PLAY_USEHLS));
                break;
            case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
                if (videoView != null && videoView instanceof UIVodVideoView) {
                    surfaceView = ((UIVodVideoView) videoView).getSurfaceView();
                    ((BaseSurfaceView) surfaceView).setDisplayMode(BaseSurfaceView.DISPLAY_MODE_SCALE_ZOOM);
                    int width = bundle.getInt(PlayerParams.KEY_WIDTH);
                    int height = bundle.getInt(PlayerParams.KEY_HEIGHT);
                    ((BaseSurfaceView) surfaceView).onVideoSizeChanged(width, height);
                }
                break;
            default:

                break;
        }
    }

    private void setActionLiveParameter(boolean hls) {
        if (hls) {
            videoView.setCacheWatermark(1000, 100);
            videoView.setMaxDelayTime(50000);
            videoView.setCachePreSize(1000);
            videoView.setCacheMaxSize(40000);
        } else {
            //rtmp
            videoView.setCacheWatermark(500, 100);
            videoView.setMaxDelayTime(1000);
            videoView.setCachePreSize(200);
            videoView.setCacheMaxSize(10000);
        }
    }

    /**
     * 开始播放视频资源
     */
    private void startPlayResources() {
        String vUid = performClassList.getVuid();
        if (TextUtils.isEmpty(vUid) || performClassList.getLeStatus() != 3) {
            isMp4 = true;
            String playPath = performClassList.getResUrl();
            videoView.setDataSource(playPath);
            videoView.onStart();
        } else {
            isMp4 = false;
            String uuid = "b68e945493";
            String vuid = vUid;
            String p = "";
            boolean isSaas = true;
            Bundle mBundle = new Bundle();
            mBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
            mBundle.putString(PlayerParams.KEY_PLAY_UUID, uuid);
            mBundle.putString(PlayerParams.KEY_PLAY_VUID, vuid);
            mBundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, p);
            mBundle.putBoolean("saas", isSaas);
            videoView.setDataSource(mBundle);
        }
        this.uiVodVideoView = (UIVodVideoView) videoView;
        if (uiVodVideoView != null) {
            //设置视频全屏播放的标题
            uiVodVideoView.setTitle(performClassList.getTitle());
        }
    }

    private void setCurrentBottomLayout(boolean isShow) {
        if (isShow) {
            bottomLayout.setVisibility(View.VISIBLE);
        } else {
            bottomLayout.setVisibility(View.GONE);
        }
    }

    private int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    private RelativeLayout.LayoutParams computeContainerSize(Context context, int mWidth, int mHeight) {
        int width = getScreenWidth(context);
        int height = width * mHeight / mWidth;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.width = width;
        params.height = height;
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        return params;
    }

    /**
     * 配置当屏幕进行旋转的时候video进行相应的变化
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (videoView != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                findViewById(R.id.bottom_layout_airclass).setVisibility(View.VISIBLE);
                mLiveShare.setVisibility(View.VISIBLE);
                mLiveBack.setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.bottom_layout_airclass).setVisibility(View.GONE);
                mLiveShare.setVisibility(View.GONE);
                mLiveBack.setVisibility(View.GONE);
            }
            videoView.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null) {
            if (isMp4) {
                currentPlayPosition = videoView.getCurrentPosition();
            }
            videoView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (videoView != null) {
            videoView.stopAndRelease();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.onDestroy();
        }
    }

    /**
     * 设置讨论数
     *
     * @param discussCount
     */
    public void setDiscussionCount(int discussCount) {
        LinearLayout layout = titleTip.getTabsContainer();
        if (layout != null) {
            TextView discussionTextView = (TextView) layout.getChildAt(titles.length - 1);
            if (discussionTextView != null) {
                //设置讨论数
                if (discussCount > 99) {
                    discussionTextView.setText(getString(R.string.discussion, "99+"));
                } else {
                    discussionTextView.setText(getString(R.string.discussion, discussCount + ""));
                }
            }
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
            titles = new String[]{
                    getString(R.string.actor_cast),
                    getString(R.string.discussion, "0")};
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return actorListFragment;
                case 1:
                    return topicDiscussionFragment;
                default:
                    return actorListFragment;
            }
        }
    }
}
