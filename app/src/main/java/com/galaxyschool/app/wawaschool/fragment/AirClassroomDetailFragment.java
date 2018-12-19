package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.ebanshu.android.api.EbsApiClient;
import com.galaxyschool.app.wawaschool.AirClassroomActivity;
import com.galaxyschool.app.wawaschool.MultiVideoPlayActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.StudyInfoRecordUtil;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.helper.CheckLanMp4UrlHelper;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;
import com.galaxyschool.app.wawaschool.pojo.PublishClass;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.OnlineIntroPopwindow;
import com.galaxyschool.app.wawaschool.views.PagerSlidingTabStrip;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.sdk.surfaceview.impl.BaseSurfaceView;
import com.lecloud.sdk.videoview.IMediaDataVideoView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.skin.videoview.live.UIActionLiveVideoView;
import com.lecloud.skin.videoview.vod.UIVodVideoView;
import com.libs.mediaplay.UniversalMediaController;
import com.libs.mediaplay.UniversalVideoView;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.LqResponseVo;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.module.discovery.adapter.LiveAdapter;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.ConfirmOrderActivity;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.vo.LiveDetailsVo;
import com.lqwawa.intleducation.module.learn.ui.LiveResListFragment;
import com.lqwawa.intleducation.module.learn.ui.SectionTaskDetailsActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.ui.WatchCourseListActivity;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.net.library.ResourceResult;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.osastudio.common.utils.LogUtils;
import com.umeng.socialize.media.UMImage;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.galaxyschool.app.wawaschool.fragment.AirClassroomDetailFragment.Contants.ISMOOC;

public class AirClassroomDetailFragment extends ContactsListFragment implements
        UIActionLiveVideoView.ContinuePlayOnline, UniversalVideoView.VideoViewCallback {
    public static String TAG = AirClassroomDetailFragment.class.getSimpleName();
    private IMediaDataVideoView videoView;
    private ISurfaceView surfaceView;
    private UniversalVideoView mp4VideoView;
    private UniversalMediaController mMediaController;
    private View mp4PlayRootView;
    private OnlineIntroPopwindow pop;
    private TeachResourceFragment teachResourceFragment;
    private InteractiveFragment interactiveFragment;
    private PublishObjectFragment publishObjectFragment;
    private AirClassStudyPracticeFragment beforeStudyFragment;
    private AirClassStudyPracticeFragment afterStudyFragment;
    //    private ClassroomIntroductionFragment introductionFragment;
    private TextView toExpectedView;
    private RelativeLayout startOnlineLayout;
    private RelativeLayout videoContainer;
    private ImageView onlineCover, mArrowRight, mLiveBack, mLiveShare;
    private LinearLayout bottomTeachLayout;
    private LinearLayout bottomInterLayout;
    private FrameLayout forecastLayout;
    private TextView mOnlineTitle, mOnlineCount, mIntroView;
    private LinearLayout mIntrolayout;

    private PagerSlidingTabStrip titleTip;//页签
    private ViewPager onlineRestab;
    private MyPagerAdapter mAdapter;
    private int currentIndex = 0;


    private int status = -1;
    //表示当前的播放状态
    private int currentPlayStatus = -1;
    //直播的资源对象
    private Emcee onlineRes;
    //是不是班主任
    private boolean isHeadMaster;
    //是不是老师
    private boolean isTeacher;
    //在预告的时候显示预告的图片
    private String forestCover;
    private SchoolInfo schoolInfo;
    //是否要显示教学材料的布局
    private boolean isShowPublishObject = false;
    //判断创建者是不是当前这个班级的
    private boolean isBelong = false;
    private String classId;
    //定义当前的角色
    private int role = -1;
    private Handler mHandler;


    //是不是点击开始直播之后直播状态的切换
    private boolean isAfterBegin = false;
    protected List<EmceeList> reporterClassBelong = new ArrayList<>();
    protected boolean isOnlineReporter;
    //小编身份不区分班级（只要比较memberId是否相同就可以了）
    protected boolean isReporterInAllClass;

    private ImageView imageViewStartPlayLive;

    private LinearLayout bottomBtnLayout;
    private boolean isFromMOOC = false;
    private LiveResListFragment beforeResListFragment;
    private LiveResListFragment afterResListFragment;
    private LiveDetailsVo liveDetailsVo;
    private boolean isLogin = false;
    private boolean isFromCourse = false;
    private long recordTime;
    private String userMemberId;
    //定时的器的参数
    private Timer mTimer;
    private TimerTask mTimeTask;
    //标识直播状态在当前界面的改变
    private boolean playModeChange = false;
    private boolean firstInit = true;
    private int roleType = -1;
    private boolean isHistoryClass;
    private boolean isBeforeFinish;
    private boolean isAfterFinish;
    private boolean isBeforeHasData;
    private boolean isAfterHasData;
    private boolean isLoadFinish;
    private boolean isDisplaySourceData;
    private SubscribeClassInfo currentClassInfo;
    private boolean isSchoolResVideo;

    @Override
    public void continuePlayOnline() throws UnsupportedEncodingException {
        popMessageDialog(getString(R.string.start_airclass_dialog_prompt), false, false, null);
    }

    @Override
    public void endAirclassOnline(Handler handler) {
        mHandler = handler;
        popMessageDialog(getString(R.string.confirm_end_online), false, true, null);
    }

    public interface Contants {
        String EMECCBEAN = "emeccBean";
        String ISMOOC = "isMooc";
        String SHOWBTN = "showBtn";
        String DISPLAY_SOURCE_DATA = "display_source_data";
    }

    //当前直播的状态  正在直播  点播(回顾) 预告
    public interface PlayMode {
        //正在直播
        int ONLINEPLAYING = 0;
        //点播即回顾
        int REPLAYVIDEO = 1;
        //预告
        int FORECAST = 2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_air_classroom_detail, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isLogin = UserHelper.isLogin();
        getIntentData();
        initViews();
        if (!isFromMOOC) {
            initLive();
        } else {
            registerBroadcastReceiver();
        }
        //直播
        if (currentPlayStatus == PlayMode.ONLINEPLAYING) {
            //更新为在线的状态
            updateOnlineNumStatus(true);
        }
        //增加浏览数+1
        addOnlineBrowseCount();
        //对空中课堂的观看数据进行记录
        if (isNeedTimerRecorder()) {
            recordTime = System.currentTimeMillis();
        }
        //校验当前的用户在不在当前的班级
        CheckUserIsInClass();
    }

    public void initLive() {
        //如果板书直播 隐藏分享的按钮
        if (onlineRes.isEbanshuLive() && mLiveShare != null) {
            mLiveShare.setVisibility(View.GONE);
        }
        if (isFromMOOC) {
            if (liveDetailsVo != null && liveDetailsVo.getLive().isIsDelete()) {
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                builder.setMessage(getActivity().getResources().getString(com.lqwawa.intleducation.R.string.live_is_invalid));
                builder.setPositiveButton(getActivity().getResources().getString(com.lqwawa.intleducation.R.string.i_know),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        getActivity().finish();
                    }
                });

                builder.create().show();
                return;
            }
        }
        if (UserHelper.isLogin() && !isMyLive() && liveDetailsVo != null && liveDetailsVo.isIsExpire()) {
            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
            builder.setMessage(getActivity().getResources().getString(com.lqwawa.intleducation.R.string.live_out_permissions));
            builder.setPositiveButton(getActivity().getResources().getString(com.lqwawa.intleducation.R.string.i_know),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    getActivity().finish();
                }
            });

            builder.create().show();
            return;
        }
        if (UserHelper.isLogin() && !isValidExtrasMemberId()
                && liveDetailsVo != null
                && liveDetailsVo.getLive().getEmceeIds().contains(UserHelper.getUserId())) {
            isOnlineReporter = true;
            teachResourceFragment.updateList(isOnlineReporter);
            if (titleTip.getCurrentPosition() == 2) {
                setCurrentBottomLayout(true, false);
            }
        }
        if (isFromMOOC && UserHelper.isLogin() && liveDetailsVo != null && isMyLive()//当前用户是主持人
                && StringUtils.isValidString(liveDetailsVo.getLive().getEmceeIds())) {
            if (liveDetailsVo.getLive().getEmceeIds().contains(UserHelper.getUserId())) {
                checkTeacherRole();
            }
        }
        //显示浏览数 以及 直播的类型
        refreshShowOnlineNum();
        choosePlayStatus(false);
        if (currentPlayStatus != PlayMode.FORECAST && !isFromMOOC) {
            //板书直播不走下面的流程
            if (onlineRes.isEbanshuLive()) {
                if (isCreator() && currentPlayStatus == PlayMode.ONLINEPLAYING) {
                    setListeners();
                    checkPlayModel();
                }
            } else {
                setListeners();
                checkPlayModel();
            }
        }
        if (isFromMOOC) {
            TextView textViewPrice = (TextView) findViewById(R.id.live_price_tv);
            textViewPrice.setText(liveDetailsVo.getLive().getPayType() == 0 ?
                    getString(R.string.free)
                    : "¥" + liveDetailsVo.getLive().getPrice());

            TextView textViewViewCourse = (TextView) findViewById(R.id.view_course_tv);
            View viewBottomSplit = findViewById(R.id.bottom_btn_split);
            if (liveDetailsVo.isHaveCourse()
                    && !isFromCourse && isMyLive()) {//不是从课程详情跳转的
                textViewViewCourse.setVisibility(View.VISIBLE);
                viewBottomSplit.setVisibility(View.VISIBLE);
            } else {
                textViewViewCourse.setVisibility(View.GONE);
                viewBottomSplit.setVisibility(View.GONE);
            }
            TextView textViewBuyOrAdd = (TextView) findViewById(R.id.buy_or_add_tv);
            if (!isValidExtrasMemberId()) {
                if ((liveDetailsVo.isIsJoin() && !liveDetailsVo.isIsExpire())
                        || (UserHelper.isLogin() &&
                        (liveDetailsVo.getLive().getEmceeIds().contains(UserHelper.getUserId())
                                || (StringUtils.isValidString(liveDetailsVo.getTeacherIds())
                                && liveDetailsVo.getTeacherIds().contains(UserHelper.getUserId()))
                                || (StringUtils.isValidString(liveDetailsVo.getTutorIds())
                                && liveDetailsVo.getTutorIds().contains(UserHelper.getUserId()))
                        ))) {
                    viewBottomSplit.setVisibility(View.GONE);
                    textViewBuyOrAdd.setVisibility(View.GONE);
                } else {
                    textViewBuyOrAdd.setVisibility(View.VISIBLE);
                    if ((liveDetailsVo.isIsBuy() && !liveDetailsVo.isIsExpire())
                            || liveDetailsVo.getLive().getPayType() == 0) {
                        textViewBuyOrAdd.setText(getString(R.string.add_to_my_live));
                    } else {
                        textViewBuyOrAdd.setText(getString(R.string.buy_immediately));
                    }
                }
            } else {
                viewBottomSplit.setVisibility(View.GONE);
                textViewBuyOrAdd.setVisibility(View.GONE);
            }
            beforeResListFragment.updateLiveInfo(liveDetailsVo);
            afterResListFragment.updateLiveInfo(liveDetailsVo);
            if (StringUtils.isValidString(liveDetailsVo.getLive().getEmceeIds())
                    && liveDetailsVo.getLive().getEmceeIds().contains(UserHelper.getUserId())) {
                role = 1;
            }
            imageViewStartPlayLive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (UserHelper.isLogin()) {
                        if ((liveDetailsVo != null && liveDetailsVo.isIsJoin()
                                && !liveDetailsVo.isIsExpire())
                                || (liveDetailsVo != null
                                && (liveDetailsVo.getLive().getEmceeIds().contains(UserHelper.getUserId())
                                || (StringUtils.isValidString(liveDetailsVo.getTeacherIds())
                                && liveDetailsVo.getTeacherIds().contains(UserHelper.getUserId()))
                                || (StringUtils.isValidString(liveDetailsVo.getTutorIds())
                                && liveDetailsVo.getTutorIds().contains(UserHelper.getUserId()))))
                                || (UserHelper.isLogin() && StringUtils.isValidString(getExtrasMemberId())
                                && !TextUtils.equals(getExtrasMemberId(), UserHelper.getUserId())
                                && liveDetailsVo.isIsJoin() && liveDetailsVo != null
                                && !liveDetailsVo.isIsExpire())) {
                            //区分板书直播
                            if ((!isCreator() || currentPlayStatus == PlayMode.REPLAYVIDEO) &&
                                    onlineRes.isEbanshuLive()) {
                                enterBlackBoardLiveDetail();
                                return;
                            }
                            choosePlayStatus(true);
                            setListeners();
                            checkPlayModel();
                        } else {
                            if (liveDetailsVo.getLive().getPayType() == 0
                                    || (liveDetailsVo.getLive().getPayType() == 1
                                    && liveDetailsVo.isIsBuyAndDelete()
                                    && !liveDetailsVo.isIsExpire())) {
                                ToastUtil.showToast(getActivity(), getString(R.string.limt_live_hint1));
                            } else if (liveDetailsVo.getLive().getPayType() == 1) {
                                if (liveDetailsVo.isIsBuy() && liveDetailsVo.isIsExpire()) {
                                    ToastUtil.showToast(getActivity(), getString(R.string.limt_live_hint2));
                                } else {
                                    ToastUtil.showToast(getActivity(), getString(R.string.limt_live_hint3));
                                }
                            }
                        }
                    } else {
                        LoginHelper.enterLogin(getActivity());
                    }
                }
            });
            if (liveDetailsVo.getLive().getState() > 0) {
                imageViewStartPlayLive.setVisibility(View.VISIBLE);
            } else {
                imageViewStartPlayLive.setVisibility(View.GONE);
            }
        }
    }

    private void checkTeacherRole() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("MemberId", UserHelper.getUserId());
        requestVo.addParams("SchoolId", liveDetailsVo.getLive().getSchoolId());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.WAWA_GetMemberRolesInSchool);
        params.setConnectTimeout(10000);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LqResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<LqResponseVo<String>>() {
                        });
                if (!result.isHasError()) {
                    String roles = result.getModel();
                    if (!UserHelper.isTeacher(roles)) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                        builder.setMessage(getActivity().getResources().getString(com.lqwawa.intleducation.R.string.limt_live_hint7));
                        builder.setPositiveButton(getActivity().getResources().getString(com.lqwawa.intleducation.R.string.i_know),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                getActivity().finish();
                            }
                        });

                        builder.create().show();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserHelper.isLogin() && !isLogin) {
            isLogin = true;
            userMemberId = getMemeberId();
            //更新为在线的状态
            updateOnlineNumStatus(true);
            sendBroadcastToUpdateLiveDetail();
            getLiveDetails();
        }
        if (isAfterBegin) {
            isAfterBegin = false;
            forecastLayout.setVisibility(View.GONE);
            currentPlayStatus = PlayMode.ONLINEPLAYING;
            if (isHasStartOnlinePermission()) {
                role = 1;
            }
            videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
            choosePlayStatus(isFromMOOC && videoView != null);
            setListeners();
            if (!isFromMOOC) {
                checkPlayModel();
            }
        }
        if (currentPlayStatus != PlayMode.FORECAST && videoView != null) {
            if (!videoView.isPlaying()) {
                videoView.onResume();
            }
        }
    }

    private void initViews() {
        initNormalView();
        initFragments();
        initForMOOC();
        initAirClassAddMyLive();
    }

    private void initAirClassAddMyLive() {
        if (showAddMyPermission()) {
            bottomBtnLayout = (LinearLayout) findViewById(R.id.layout_bottom_btn);
            bottomBtnLayout.setVisibility(View.VISIBLE);
            TextView textViewBuyOrAdd = (TextView) findViewById(R.id.buy_or_add_tv);
            textViewBuyOrAdd.setVisibility(View.VISIBLE);
            textViewBuyOrAdd.setText(getString(R.string.add_to_my_live));
            textViewBuyOrAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ButtonUtils.isFastDoubleClick()) {
                        addMyLiveForAirClass();
                    }
                }
            });
        }
    }

    private void initForMOOC() {
        if (!isFromMOOC) {
            return;
        }
        View btn = findViewById(R.id.lq_look_course);
        btn.setVisibility(View.GONE);

        int roleType = getMoocLiveRoleType(onlineRes);

        LinearLayout layoutInfoMore = (LinearLayout) findViewById(R.id.layout_info_more);
        bottomBtnLayout = (LinearLayout) findViewById(R.id.layout_bottom_btn);
        bottomBtnLayout.setVisibility(View.VISIBLE);
        beforeResListFragment = new LiveResListFragment();
        afterResListFragment = new LiveResListFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("liveId", "" + onlineRes.getId());
        bundle1.putInt("type", 0);
        bundle1.putBoolean("isHost", getArguments().getBoolean("isHost"));
        bundle1.putString("memberId", getArguments().getString("memberId"));
        bundle1.putInt("roleType", roleType);
        beforeResListFragment.setArguments(bundle1);
        Bundle bundle2 = new Bundle();
        bundle2.putString("liveId", "" + onlineRes.getId());
        bundle2.putInt("type", 1);
        bundle2.putBoolean("isHost", getArguments().getBoolean("isHost"));
        bundle2.putString("memberId", getArguments().getString("memberId"));
        bundle2.putInt("roleType", roleType);
        afterResListFragment.setArguments(bundle2);
        if (layoutInfoMore != null) {
            layoutInfoMore.setVisibility(View.VISIBLE);
            TextView textViewBuyOrAdd = (TextView) findViewById(R.id.buy_or_add_tv);
            textViewBuyOrAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!UserHelper.isLogin()) {
                        LoginHelper.enterLogin(getActivity());
                        return;
                    }
                    if (liveDetailsVo == null) {
                        return;
                    }
                    if ((liveDetailsVo.isIsBuy() && !liveDetailsVo.isIsExpire())
                            || liveDetailsVo.getLive().getPayType() == 0) {
                        if (!ButtonUtils.isFastDoubleClick()) {
                            addToMyLive(liveDetailsVo);
                        }
                    } else {
                        ConfirmOrderActivity.start(getActivity(), liveDetailsVo);
                    }
                }
            });
            TextView textViewViewCourse = (TextView) findViewById(R.id.view_course_tv);
            textViewViewCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (UserHelper.isLogin()) {
                        if (liveDetailsVo == null) {
                            return;
                        }
                        String courseId = liveDetailsVo.getLive().getCourseId();
                        if (!TextUtils.isEmpty(courseId)) {
                            if (courseId.contains(",")) {

                                WatchCourseListActivity.newInstance(getActivity(), courseId);

                            } else {

                                CourseDetailsActivity.start(getActivity(), courseId, true, UserHelper.getUserId());
                            }
                        }
                    } else {
                        LoginHelper.enterLogin(getActivity());
                    }
                }
            });
            getLiveDetails();
        }
    }

    private void addToMyLive(LiveDetailsVo liveDetails) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("liveId", liveDetails.getLive().getId());
        requestVo.addParams("type", liveDetails.isIsBuyAndDelete() ? 1 : 0);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.AddToMyLive + requestVo.getParams());

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(getActivity(),
                            (getResources().getString(R.string.join_live_success)));
                    sendBroadcastToUpdateLiveList();
                    sendBroadcastToUpdateLiveDetail();
                    getLiveDetails();
                } else {
                    ToastUtil.showToast(getActivity(),
                            (getResources().getString(R.string.add_live_failed))
                                    + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(getActivity(), getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getLiveDetails() {
        RequestVo requestVo = new RequestVo();
        if (isValidExtrasMemberId()) {
            requestVo.addParams("token", getExtrasMemberId());
        }
        requestVo.addParams("liveId", onlineRes.getId());

        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetLiveDetails + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<LiveDetailsVo> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<LiveDetailsVo>>() {
                        });
                if (result.getCode() == 0) {
                    liveDetailsVo = result.getData();
                    getArguments().putSerializable("liveDetails", liveDetailsVo);
                    if (liveDetailsVo != null) {
                        onlineRes.setAcCreateId(liveDetailsVo.getLive().getAcCreateId());
                        onlineRes.setAcCreateNickName(liveDetailsVo.getLive().getCreateName());
                        onlineRes.setAcCreateRealName(liveDetailsVo.getLive().getCreateName());
                        onlineRes.setCoverUrl(liveDetailsVo.getLive().getCoverUrl());
                        onlineRes.setCreateTime(liveDetailsVo.getLive().getStartTime());
                        onlineRes.setStartTime(liveDetailsVo.getLive().getStartTime());
                        onlineRes.setEndTime(liveDetailsVo.getLive().getEndTime());
                        onlineRes.setDemandId(liveDetailsVo.getLive().getLeVuid());
                        onlineRes.setId(Integer.valueOf(liveDetailsVo.getLive().getId()));
                        onlineRes.setIntro(liveDetailsVo.getLive().getIntro());
                        onlineRes.setSchoolId(liveDetailsVo.getLive().getSchoolId());
                        onlineRes.setSchoolName(liveDetailsVo.getLive().getSchoolName());
                        onlineRes.setLiveId(liveDetailsVo.getLive().getLeAcid());
                        onlineRes.setShareUrl(liveDetailsVo.getLive().getShareUrl());
                        onlineRes.setEmceeMemberIdStr(liveDetailsVo.getLive().getEmceeIds());
                        onlineRes.setState(liveDetailsVo.getLive().getState());
                        onlineRes.setTitle(liveDetailsVo.getLive().getTitle());
                        onlineRes.setEmceeNames(liveDetailsVo.getLive().getEmceeNames());
                        onlineRes.setCourseId(liveDetailsVo.getLive().getCourseId());

                        //浏览数
                        int browseCount = onlineRes.getBrowseCount();
                        if (browseCount < liveDetailsVo.getLive().getBrowseCount()) {
                            onlineRes.setBrowseCount(liveDetailsVo.getLive().getBrowseCount()
                                    + (firstInit ? 1 : 0));
                        }
                        if (firstInit) {
                            firstInit = false;
                        }
                        onlineRes.setJoinCount(liveDetailsVo.getLive().getJoinCount());

                        onlineRes.setCourseIds(liveDetailsVo.getLive().getCourseIds());
                        onlineRes.setPayType(liveDetailsVo.getLive().getPayType());
                        onlineRes.setPrice(liveDetailsVo.getLive().getPrice());
                        onlineRes.setRoomId(liveDetailsVo.getLive().getRoomId());
                        onlineRes.setIsEbanshuLive(liveDetailsVo.getLive().isIsEbanshuLive());
                        status = liveDetailsVo.getLive().getState();
                        changePlayModelStatus();
                    }
                    initLive();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private boolean isMyLive() {
        return UserHelper.isLogin() && (!isValidExtrasMemberId()
                || TextUtils.equals(getExtrasMemberId(), UserHelper.getUserId()));
    }

    private boolean isValidExtrasMemberId() {
        return StringUtils.isValidString(getExtrasMemberId());
    }

    private String getCurrentMemberId() {
        if (isMyLive()) {
            return UserHelper.getUserId();
        } else {
            return getExtrasMemberId();
        }
    }

    private String getExtrasMemberId() {
        return getArguments().getString("memberId");
    }

    private void getIntentData() {
        //备份用户的memberId 账号被挤掉memberId为空
        userMemberId = getMemeberId();

        Bundle bundle = getArguments();
        if (bundle != null) {
            isFromMOOC = bundle.getBoolean(Contants.ISMOOC, false);
            isFromCourse = bundle.getBoolean("isFromCourse", false);
            onlineRes = (Emcee) bundle.getSerializable(Contants.EMECCBEAN);
            isHeadMaster = bundle.getBoolean(AirClassroomFragment.Constants.EXTRA_IS_HEADMASTER, false);
            isTeacher = bundle.getBoolean(AirClassroomFragment.Constants.EXTRA_IS_TEACHER, false);
            schoolInfo = (SchoolInfo) bundle.getSerializable(AirClassroomActivity.EXTRA_IS_SCHOOLINFO);
            classId = bundle.getString(AirClassroomFragment.Constants.EXTRA_CONTACTS_CLASS_ID);
            roleType = bundle.getInt(AirClassroomActivity.EXTRA_ROLE_TYPE, -1);
            isHistoryClass = bundle.getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS, false);
            if (onlineRes != null) {
                isBelong = campareIsBelongCurrentClass(onlineRes);
                forestCover = onlineRes.getCoverUrl();
                isOnlineReporter = IsOnlineHost();
                if (!isOnlineReporter
                        && (roleType == RoleType.ROLE_TYPE_TEACHER || isTeacher || isHeadMaster)) {
                    isOnlineReporter = true;
                }
                isReporterInAllClass = isReporterInAllClass();
                if (!isReporterInAllClass
                        && (roleType == RoleType.ROLE_TYPE_TEACHER || isTeacher || isHeadMaster)) {
                    isReporterInAllClass = true;
                }
                if (onlineRes.getLiveType() == 1 && !TextUtils.isEmpty(onlineRes.getDemandId())) {
                    isSchoolResVideo = true;
                }
            }
            currentClassInfo = (SubscribeClassInfo) bundle.getSerializable(AirClassroomActivity.ExTRA_CLASS_INFO);
            if (currentClassInfo == null) {
                getCurrentClassInfo();
            }
            isDisplaySourceData = bundle.getBoolean(Contants.DISPLAY_SOURCE_DATA, false);
        }
    }

    private void initFragments() {
        //教学材料
        teachResourceFragment = new TeachResourceFragment();
        teachResourceFragment.setBottomLayoutId(bottomTeachLayout, bottomInterLayout);
        teachResourceFragment.setArguments(getArguments());
        //互动交流
//        interactiveFragment = new InteractiveFragment(onlineRes, bottomInterLayout);
        //发布对象
        publishObjectFragment = new PublishObjectFragment();
        publishObjectFragment.setOnlineRes(onlineRes);
        publishObjectFragment.setArguments(getArguments());
        //课堂简介
//        introductionFragment = new ClassroomIntroductionFragment(onlineRes);

        //课前预习和课后练习
        if (!isFromMOOC) {
            beforeStudyFragment = new AirClassStudyPracticeFragment();
            beforeStudyFragment.setCurrentStudyType(1);
            beforeStudyFragment.setArguments(getArguments());

            afterStudyFragment = new AirClassStudyPracticeFragment();
            afterStudyFragment.setCurrentStudyType(2);
            afterStudyFragment.setArguments(getArguments());
        }

        titleTip = (PagerSlidingTabStrip) findViewById(R.id.school_resource_tabs);
        onlineRestab = (ViewPager) findViewById(R.id.school_resource_pager);
        if (isOnlineReporter) {
            showTabCondition();
        } else {
            showLoadingDialog();
            titleTip.setVisibility(View.INVISIBLE);
            onlineRestab.setVisibility(View.INVISIBLE);
            showTabCondition();
        }
    }

    private void showTabCondition() {
        setCurrentBottomLayout(false, false);
        if (isReporterInAllClass || isHeadMaster) {
            isShowPublishObject = true;
            currentIndex = 0;
            //支持左右滑动的代码
            mAdapter = new MyPagerAdapter(getChildFragmentManager());
            onlineRestab.setOffscreenPageLimit(4);
            onlineRestab.setAdapter(mAdapter);
            titleTip.setViewPager(onlineRestab);
            onlineRestab.setCurrentItem(currentIndex);
        } else {
            //支持左右滑动的代码
            isShowPublishObject = false;
            currentIndex = 0;
            mAdapter = new MyPagerAdapter(getChildFragmentManager());
            onlineRestab.setAdapter(mAdapter);
            titleTip.setViewPager(onlineRestab);
            onlineRestab.setCurrentItem(currentIndex);
            onlineRestab.setOffscreenPageLimit(4);
        }
        if (isLoadFinish) {
            if (teachResourceFragment != null) {
                teachResourceFragment.loadOnlineMateria();
            }
            //校验完成
            if (!isBeforeHasData && !isAfterHasData) {
                if (isReporterInAllClass || isOnlineReporter) {
                    setCurrentBottomLayout(true, false);
                } else {
                    setCurrentBottomLayout(false, true);
                }
            }
        }

        titleTip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int interactivePosition = 2;
                if (isLoadFinish) {
                    if (isBeforeHasData && isAfterHasData) {
                        interactivePosition = 2;
                    } else if (isBeforeHasData || isAfterHasData) {
                        interactivePosition = 1;
                    } else {
                        interactivePosition = 0;
                    }
                }
                if (position == interactivePosition) {
                    //按条件显示互动交流的buttonLayout状态显示
                    if (isReporterInAllClass || isOnlineReporter) {
                        setCurrentBottomLayout(true, false);
                    } else {
                        setCurrentBottomLayout(false, true);
                    }
                } else {
                    UIUtils.hideSoftKeyboard(getActivity());
                    setCurrentBottomLayout(false, false);
                    if (showAddMyPermission()) {
                        bottomBtnLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initNormalView() {
        //根据条件显示来源
        LinearLayout displaySourceLayout = (LinearLayout) findViewById(R.id.ll_display_source);
        if (displaySourceLayout != null && isDisplaySourceData && currentClassInfo != null) {
            displaySourceLayout.setVisibility(View.VISIBLE);
            TextView displaySourceTextV = (TextView) findViewById(R.id.tv_display_source);
            displaySourceTextV.setText(currentClassInfo.getClassName());
            displaySourceTextV.setOnClickListener(v -> {
                //跳转
                ClassDetailActivity.show(getActivity(), currentClassInfo.getClassId());
            });
        }
        imageViewStartPlayLive = (ImageView) findViewById(R.id.start_play_live_iv);
        if (!isFromMOOC) {
            imageViewStartPlayLive.setOnClickListener(this);
        }
        //区别直播的状态
        status = onlineRes.getState();
        changePlayModelStatus();
        //显示直播的标题和浏览数
        refreshShowOnlineNum();
        mOnlineTitle = (TextView) findViewById(R.id.tv_online_title);
        if (mOnlineTitle != null) {
            mOnlineTitle.setText(onlineRes.getTitle());
        }
        //显示简介按钮的状态
        initIntroView();
        initTopButton();
        //设置播放view的容器
        if (currentPlayStatus != PlayMode.FORECAST) {
            videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
        } else {
            forecastLayout = (FrameLayout) findViewById(R.id.online_foreast_layout);
        }
        onlineCover = (ImageView) findViewById(R.id.online_cover);
        //把这两个layout传递给子fragment
        bottomTeachLayout = (LinearLayout) findViewById(R.id.bottom_layout_online);
        bottomInterLayout = (LinearLayout) findViewById(R.id.bottom_layout_interactive);

        //显示图片上的敬请期待
        toExpectedView = (TextView) findViewById(R.id.to_be_expected);
        startOnlineLayout = (RelativeLayout) findViewById(R.id.start_online_cover_btn);
        //开始直播的按钮
        TextView startOnlineTextV = (TextView) findViewById(R.id.tv_start_online_text);
        if (startOnlineTextV != null) {
            startOnlineTextV.setOnClickListener(this);
        }
        ImageView startOnlineImageV = (ImageView) findViewById(R.id.start_online_icon);
        if (startOnlineImageV != null) {
            startOnlineImageV.setOnClickListener(this);
        }
        if (getArguments().getBoolean(Contants.SHOWBTN)) {
            View btn = findViewById(R.id.lq_look_course);
            btn.setVisibility(View.VISIBLE);
            final String courseId = onlineRes.getCourseIds();

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(courseId)) {
                        if (courseId.contains(",")) {

                            WatchCourseListActivity.newInstance(getActivity(), courseId);

                        } else {

                            CourseDetailsActivity.start(getActivity(), courseId, true, UserHelper.getUserId());
                        }
                    }

                }
            });
        }
    }

    /**
     * 初始化简介的数据
     */
    private void initIntroView() {
        mIntrolayout = (LinearLayout) findViewById(R.id.layout_intro);
        if (mIntrolayout != null) {
            mIntrolayout.setOnClickListener(this);
        }
        mIntroView = (TextView) findViewById(R.id.tv_introduction);
        mArrowRight = (ImageView) findViewById(R.id.iv_arrow);
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

    public double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    protected boolean IsOnlineHost() {
        if (isFromMOOC) {
            if (UserHelper.isLogin() && liveDetailsVo != null
                    && StringUtils.isValidString(liveDetailsVo.getLive().getEmceeIds())) {
                return liveDetailsVo.getLive().getEmceeIds().contains(UserHelper.getUserId());
            } else {
                return false;
            }
        }
        if (!isTeacher) {
            return false;
        }
        if (TextUtils.equals(getMemeberId(), onlineRes.getAcCreateId())) {
            return true;
        }
        boolean flag = false;
        List<EmceeList> emceeList = onlineRes.getEmceeList();
        for (int i = 0; i < emceeList.size(); i++) {
            EmceeList emceeMember = emceeList.get(i);
            String classIds = emceeMember.getClassIds();
            String schoolIds = emceeMember.getSchoolIds();
            if (TextUtils.equals(getMemeberId(), emceeMember.getMemberId())
                    && !TextUtils.isEmpty(classIds)) {
                EmceeList tempData = null;
                if (classIds.contains(",")) {
                    String[] splitClassArray = classIds.split(",");
                    String[] splitSchoolArray = schoolIds.split(",");
                    for (int j = 0; j < splitClassArray.length; j++) {
                        tempData = new EmceeList();
                        tempData.setMemberId(emceeMember.getMemberId());
                        tempData.setSchoolIds(splitSchoolArray[j]);
                        tempData.setClassIds(splitClassArray[j]);
                        if (TextUtils.equals(classId, splitClassArray[j])) {
                            flag = true;
                        }
                        reporterClassBelong.add(tempData);
                    }
                } else {
                    tempData = new EmceeList();
                    tempData.setMemberId(emceeMember.getMemberId());
                    tempData.setSchoolIds(emceeMember.getSchoolIds());
                    tempData.setClassIds(emceeMember.getClassIds());
                    if (TextUtils.equals(classId, classIds)) {
                        flag = true;
                    }
                    reporterClassBelong.add(tempData);
                }
                break;
            }
        }
        return flag;
    }

    private boolean isReporterInAllClass() {
        if (getArguments().getBoolean(ISMOOC)) {
            return false;
        }
        if (!isTeacher) {
            return false;
        }

        if (TextUtils.equals(getMemeberId(), onlineRes.getAcCreateId())) {
            return true;
        }
        List<EmceeList> emceeList = onlineRes.getEmceeList();
        if (emceeList != null && emceeList.size() > 0) {
            for (EmceeList member : emceeList) {
                if (TextUtils.equals(member.getMemberId(), getMemeberId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_live_share:
                shareAirclassOnline();
                break;
            case R.id.start_online_icon:
            case R.id.tv_start_online_text:
                if (status == 0
                        || (isFromMOOC && liveDetailsVo != null
                        && liveDetailsVo.getLive().getState() == 0)) {
                    //开始直播
                    try {
                        startToOnlineAction();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.iv_live_back:
                UIUtils.hideSoftKeyboard(getActivity());
                finishCurrentActivity();
                break;
            case R.id.layout_intro:
                changeIntroStatus();
                break;
            case R.id.start_play_live_iv:
                //进入板书的详情页
                enterBlackBoardLiveDetail();
                break;
        }
    }

    /**
     * 点击界面切换简介的状态
     */
    private void changeIntroStatus() {
        int height = 0;
        if (onlineCover.getVisibility() == View.VISIBLE) {
            height = onlineCover.getMeasuredHeight();
        } else {
            height = videoContainer.getMeasuredHeight();
        }
        if (pop == null) {
            pop = new OnlineIntroPopwindow(getActivity(), onlineRes, height, isFromMOOC);
        }
        pop.showPopupMenu();
    }

    /**
     * 分享空中课堂的视频直播
     */
    private void shareAirclassOnline() {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(onlineRes.getTitle());
        shareInfo.setContent(schoolInfo != null ? schoolInfo.getSchoolName() : " ");
        String url = onlineRes.getShareUrl();
        shareInfo.setTargetUrl(url);
        //当缩略图为空的时候给于一个默认的缩略图
        UMImage umImage = null;
        String shareCoverUrl = onlineRes.getCoverUrl();
        if (TextUtils.isEmpty(shareCoverUrl)) {
            umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
        } else {
            umImage = new UMImage(getActivity(), AppSettings.getFileUrl(shareCoverUrl));
        }
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        resource.setTitle(onlineRes.getTitle());
        resource.setDescription(schoolInfo != null ? schoolInfo.getSchoolName() : " ");
        resource.setShareUrl(url);
        resource.setThumbnailUrl(AppSettings.getFileUrl(onlineRes.getCoverUrl()));
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }

    /**
     * 开启一个直播的动作
     */
    private void startToOnlineAction() throws UnsupportedEncodingException {
        String url = ServerUrl.BEGIN_AIRCLASSROOM_STATUS_ONLINE_BASE_URL;
        if (TextUtils.isEmpty(getMemeberId())) {
            return;
        }
        if ((!isFromMOOC && schoolInfo == null) || (isFromMOOC && liveDetailsVo == null)) {
            return;
        }
        String schoolId = isFromMOOC ? liveDetailsVo.getLive().getSchoolId()
                : schoolInfo.getSchoolId();
        if (TextUtils.isEmpty(schoolId)) {
            return;
        }
        String onlineTitle = onlineRes.getTitle();
        onlineTitle = URLEncoder.encode(onlineTitle, "UTF-8");
        StringBuilder builder = new StringBuilder();
        List<EmceeList> reporterList = onlineRes.getEmceeList();
        if (reporterList != null && reporterList.size() > 0) {
            for (int i = 0; i < reporterList.size(); i++) {
                String id = reporterList.get(i).getMemberId();
                if (i == 0) {
                    builder.append(id);
                } else {
                    builder.append(",").append(id);
                }
            }
        }
        //学程直播的主持人集合
        if (isFromMOOC) {
            String moocHostMemberIds = onlineRes.getEmceeMemberIdStr();
            if (!TextUtils.isEmpty(moocHostMemberIds)) {
                builder.append(moocHostMemberIds);
            }
        }
        //直播的封面
        String coverUrl = onlineRes.getCoverUrl();
        if (!TextUtils.isEmpty(coverUrl) && coverUrl.contains("coverUrl")) {
            JSONObject jsonObject = JSON.parseObject(coverUrl, Feature.AutoCloseSource);
            coverUrl = jsonObject.getString("coverUrl");
        }
        coverUrl = AppSettings.getFileUrl(coverUrl);
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("&").append("memberId=" + getMemeberId()).append("&").append
                ("memberList=" + builder.toString()).append("&").append("aid=" + onlineRes.getId())
                .append("&").append("title=" + onlineTitle).append("&").append
                ("pic=" + coverUrl).append("&").append("schoolId=" + schoolId);
        ThisStringRequest request = new ThisStringRequest(Request.Method.GET, urlBuilder.toString(), new
                Listener<String>() {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        if (!TextUtils.isEmpty(jsonString)) {
                            JSONObject jsonObject = JSON.parseObject(jsonString, Feature.AutoCloseSource);
                            final String activityId = jsonObject.getString("activityId");
                            if (TextUtils.isEmpty(activityId)) {
                                TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.server_connect_error));
                            } else {
                                //同步更新两栖蛙蛙直播的状态
                                upDateAirClassroomStatus(activityId, null);
                                //更新为直播中的状态
                                currentPlayStatus = PlayMode.ONLINEPLAYING;
                                String showTitle = null;
                                if (onlineRes.isEbanshuLive()) {
                                    showTitle = getString(R.string.str_start_online_live_blackboard);
                                } else {
                                    showTitle = getString(R.string.start_airclass_dialog_prompt);
                                }
                                if (isSchoolResVideo) {
                                    handleStartOnlineData(activityId);
                                    upDateOnlineState(true);
                                } else {
                                    popMessageDialog(showTitle, true, false, activityId);
                                }
//                                jumpToQingDaoAppOnline(activityId);
                            }
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

    private void finishCurrentActivity() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ActivityUtils.REQUEST_CODE_NEED_TO_REFRESH, true);
        intent.putExtras(bundle);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    /**
     * e-板书的sdk进入直播详情页
     */
    private void enterBlackBoardLiveActivity() {
        if (!isLogin()) {
            //没有登录跳转到登录的界面
            ActivityUtils.enterLogin(getActivity());
            return;
        }
        EbsApiClient ebsApiClient = new EbsApiClient("2", "123456");
        ebsApiClient.grantRoomPermission(getMemeberId(), onlineRes.getId() + "");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("app_id", "2");
        bundle.putString("app_key", "123456");
        bundle.putString("app_user_id", getMemeberId());
        bundle.putString("app_object_id", onlineRes.getId() + "");
        bundle.putString("ebs_room_id", onlineRes.getRoomId() + "");
        intent.putExtra("params", bundle);
//        intent.setClassName(getActivity().getPackageName(), "com.ebanshu.android.activity.RoomActivity");
        intent.setClassName(getActivity().getPackageName(), "com.galaxyschool.app.wawaschool.EbsClientActivity");
        startActivity(intent);
    }

    private void enterBlackBoardReplayActivity() {
        StringBuilder urlBuilder = new StringBuilder("https://api.ebanshu.net/v1/rooms/video/list?room_id=");
        urlBuilder.append(onlineRes.getRoomId());
        Map<String, String> params = new HashMap<>();
        params.put("ebs-app-id", "2");
        params.put("ebs-app-key", "123456");
        RequestHelper.getRequest(getActivity(), urlBuilder.toString(), new RequestHelper
                .RequestListener(getActivity(), ResourceResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    TipMsgHelper.ShowMsg(getActivity(), R.string.str_blackboard_live_overed);
                } else {
                    try {
                        org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
                        boolean isOk = jsonObject.getBoolean("ok");
                        if (isOk) {
                            org.json.JSONObject dataObj = jsonObject.getJSONObject("data");
                            if (dataObj != null) {
                                org.json.JSONArray videoList = dataObj.getJSONArray("object_list");
                                if (videoList != null && videoList.length() > 0) {
                                    ArrayList<String> videoPaths = new ArrayList<>();
                                    ArrayList<Integer> videoDurations = new ArrayList<>();
                                    for (int i = 0, len = videoList.length(); i < len; i++) {
                                        org.json.JSONObject videoObj = (org.json.JSONObject) videoList.get(i);
                                        if (videoObj != null) {
                                            org.json.JSONObject video = videoObj.getJSONObject("video");
                                            if (video != null) {
                                                videoPaths.add(video.getString("location_url"));
                                                double duration = video.getDouble("duration");
                                                int millisecond = (int) (duration * 1000);
                                                videoDurations.add(millisecond);
                                            }
                                        }
                                    }
                                    if (videoDurations.size() > 0 && videoPaths.size() > 0) {
                                        MultiVideoPlayActivity.start(getActivity(), onlineRes
                                                .getTitle(), videoPaths, videoDurations);
                                        return;
                                    }
                                }
                            }
                        }
                        TipMsgHelper.ShowMsg(getActivity(), R.string.str_blackboard_live_overed);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, params);
    }

    /**
     * 进入板书直播的详情页
     */
    private void enterBlackBoardLiveDetail() {
        if (currentPlayStatus == PlayMode.REPLAYVIDEO) {
            enterBlackBoardReplayActivity();
        } else {
            enterBlackBoardLiveActivity();
        }

//        Map<String, Object> param = new HashMap<>();
//        param.put("UserId", getMemeberId());
//        param.put("RoomId", onlineRes.getRoomId());
//        param.put("ExtId", onlineRes.getId());
//        DefaultDataListener listener = new DefaultDataListener<DataModelResult>(DataModelResult
//                .class) {
//            @Override
//            public void onSuccess(String jsonString) {
//                try {
//                    org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
//                    int errorCode = jsonObject.optInt("errorCode");
//                    if (errorCode == 0) {
//                        org.json.JSONObject blackBoardLive = jsonObject.getJSONObject("Model");
//                        if (blackBoardLive != null) {
//                            ActivityUtils.openNews(getActivity(), blackBoardLive.optString("EbanshuLiveUrl"),
//                                    onlineRes.getTitle());
//                        }
//                    } else {
//                        TipMsgHelper.ShowMsg(getActivity(), jsonObject.optString("errorMessage"));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        listener.setShowLoading(true);
//        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_BLACKBOARD_LIVE_BASE_URL, param,
//                listener);
    }

    /**
     * 增加浏览数
     */
    private void addOnlineBrowseCount() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Id", onlineRes.getId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        // 发送广播给我的直播列表，更新浏览数据
                        sendBroadcastToUpdateLiveList();
                        super.onSuccess(jsonString);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_AIRCLASSROOM_BROWSE_COUNT, params, listener);
    }

    private void upDateAirClassroomStatus(String activityId, String demanId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Id", onlineRes.getId());
        if (TextUtils.isEmpty(activityId)) {
            params.put("DemandId", demanId);
        } else {
            params.put("LiveId", activityId);
        }
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
                        } else {
                            String errorMessage = getResult().getErrorMessage();
                            if (TextUtils.isEmpty(errorMessage)) {
                                //更新为在线的状态
                                updateOnlineNumStatus(true);
                            } else {
                                TipMsgHelper.ShowLMsg(getActivity(), errorMessage);
                            }

                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_AIR_CLASSROOM_STATUS_BASE_URL, params, listener);
    }

    /**
     * 结束直播之后设置直播的状态为state = 2
     */
    private void upDateOnlineState(boolean isStartOnline) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Id", onlineRes.getId());
        params.put("State", isStartOnline ? 1 : 2);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                    }
                };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_AIRCLASS_STATE_BASE_URL, params, listener);
    }

    /**
     * 跳转到青岛lq直播app进行直播
     */
    private void jumpToQingDaoAppOnline(String activityId) {
        String memberId = onlineRes.getCreateId();
        String schoolId = onlineRes.getSchoolId();
        PackageManager packageManager = getActivity().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse
                ("lqlive://live:666/pushstream?activityId=" + activityId + "&memberId=" + memberId + "&schoolId=" + schoolId));
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isValid = !activities.isEmpty();
        if (isValid) {
            upDateAirClassroomStatus(activityId, null);
            getActivity().startActivity(intent);
            if (status == 0) {
                onlineRes.setLiveId(activityId);
                onlineRes.setState(1);
                status = 1;
                isAfterBegin = true;
            }
        } else {
            Uri uri = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.zw.lqlive");
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            getActivity().startActivity(it);
        }
    }

    /**
     * 结束直播
     */
    private void endUpOnlinePlay() {
        String url = ServerUrl.END_UP_AIRCLASSROOM_ONLINE_BASE_URL;
        StringBuilder tempUrl = new StringBuilder(url);
        tempUrl.append("&").append("memberId=" + getMemeberId()).append("&").append("aid=" +
                onlineRes.getId()).append("&").append("schoolId=" + onlineRes.getSchoolId());
        ThisStringRequest request = new ThisStringRequest(Request.Method.GET, tempUrl.toString(), new
                Listener<String>() {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        UIActionLiveVideoView actionLiveVideoView = (UIActionLiveVideoView) videoView;
                        if (actionLiveVideoView != null) {
                            actionLiveVideoView.processActionStatus(3);
                        }
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(0);
                        }
                        if (isFromMOOC) {
                            sendBroadcastToUpdateLiveList();
                        }
                        playModeChange = true;
                        //结束直播之后将直播的状态更新为2
                        upDateOnlineState(false);
                        //结束直播之后更新当前浏览数显示状态
                        currentPlayStatus = PlayMode.REPLAYVIDEO;
                        onlineRes.setState(2);
                        refreshShowOnlineNum();
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

    private void sendBroadcastToUpdateLiveList() {
        Intent broadIntent = new Intent();
        broadIntent.setAction("LIVE_STATUS_CHANGED");
        getActivity().sendBroadcast(broadIntent);
    }

    private void sendBroadcastToUpdateLiveDetail() {
        if (isFromMOOC && liveDetailsVo != null) {
            Intent broadIntent = new Intent();
            broadIntent.setAction("LIVE_DETAIL_CHANGED");
            broadIntent.putExtra("contentName", getContext().toString());
            broadIntent.putExtra("id", liveDetailsVo.getLive().getId());
            getActivity().sendBroadcast(broadIntent);
        }
    }

    /**
     * BroadcastReceiver
     ************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isFromMOOC) {
                return;
            }
            if (liveDetailsVo == null) {
                return;
            }
            String action = intent.getAction();
            if (action.equals("LIVE_DETAIL_CHANGED")
                    && !TextUtils.equals(intent.getStringExtra("contentName"), getContext().toString())
                    && TextUtils.equals(intent.getStringExtra("id"), liveDetailsVo.getLive().getId())) {
                getLiveDetails();
            }
        }
    };

    /**
     * 注册广播事件
     */
    protected void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        //要接收的类型
        myIntentFilter.addAction("LIVE_DETAIL_CHANGED");
        //注册广播
        getContext().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 根据状态来播放相应的资源
     */
    private void choosePlayStatus(boolean isForced) {
        if (currentPlayStatus == PlayMode.ONLINEPLAYING && (!isFromMOOC || isForced)) {
            //直播中
            if (videoContainer == null) {
                videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
            }
            //板书直播
            if (!isCreator() && onlineRes.isEbanshuLive() && !isFromMOOC) {
                configOnlineCoverHeight();
                imageViewStartPlayLive.setVisibility(View.VISIBLE);
                onlineCover.setVisibility(View.VISIBLE);
                videoContainer.setVisibility(View.GONE);
                showDefaultOnlineThumbnail();
            } else {
                //视频直播
                onlineCover.setVisibility(View.GONE);
                videoContainer.setVisibility(View.VISIBLE);
                imageViewStartPlayLive.setVisibility(View.GONE);
                if (isSchoolResVideo && !isHasStartOnlinePermission()) {
                    //相当于回顾的视频
                    videoView = new UIVodVideoView(getActivity(), false, true);
                } else {
                    //板书直播不显示重新连接的btn
                    videoView = new UIActionLiveVideoView(getActivity(), role, this, isSchoolResVideo || onlineRes.isEbanshuLive());
                }
                videoContainer.addView((View) videoView, computeContainerSize(getActivity(), 16, 9));
                teachResourceFragment.setVideoView(videoView);
            }
        } else if (currentPlayStatus == PlayMode.REPLAYVIDEO && (!isFromMOOC || isForced)) {
            //回顾
            if (videoContainer == null) {
                videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
            }
            //板书直播
            if (onlineRes.isEbanshuLive() && !isFromMOOC) {
                configOnlineCoverHeight();
                imageViewStartPlayLive.setVisibility(View.VISIBLE);
                onlineCover.setVisibility(View.VISIBLE);
                videoContainer.setVisibility(View.GONE);
                showDefaultOnlineThumbnail();
            } else {
                //视频直播
                imageViewStartPlayLive.setVisibility(View.GONE);
                onlineCover.setVisibility(View.GONE);
                videoContainer.setVisibility(View.VISIBLE);
                videoView = new UIVodVideoView(getActivity(), false, true);
                videoContainer.addView((View) videoView, computeContainerSize(getActivity(), 16, 9));
                teachResourceFragment.setVideoView(videoView);
            }
        } else if (currentPlayStatus == PlayMode.FORECAST) {
            //预告
            imageViewStartPlayLive.setVisibility(View.GONE);
            //设置头部图片的高度为16:8
            int width = getScreenWidth(getActivity());
            int height = width * 9 / 16;
            ViewGroup.LayoutParams params = onlineCover.getLayoutParams();
            params.width = width;
            params.height = height;
            onlineCover.setLayoutParams(params);
            boolean isReporter = false;
            if (onlineRes.isEbanshuLive()) {
                //板书直播
                if (isCreator()) {
                    isReporter = true;
                }
            } else {
                //视频直播
                if (isHasStartOnlinePermission()) {
                    isReporter = true;
                }
            }
            if (isReporter) {
                startOnlineLayout.setLayoutParams(params);
                toExpectedView.setVisibility(View.GONE);
                startOnlineLayout.setVisibility(View.VISIBLE);
            } else {
                toExpectedView.setLayoutParams(params);
                startOnlineLayout.setVisibility(View.GONE);
                toExpectedView.setVisibility(View.VISIBLE);
            }
            onlineCover.setVisibility(View.VISIBLE);
            showDefaultOnlineThumbnail();
        } else if ((isFromMOOC && !isForced) && (currentPlayStatus == PlayMode.ONLINEPLAYING
                || currentPlayStatus == PlayMode.REPLAYVIDEO)) {
            onlineCover.setVisibility(View.VISIBLE);
            imageViewStartPlayLive.setVisibility(View.VISIBLE);
            startOnlineLayout.setVisibility(View.GONE);
            toExpectedView.setVisibility(View.GONE);
            //设置头部图片的高度为16:8
            int width = getScreenWidth(getActivity());
            int height = width * 9 / 16;
            ViewGroup.LayoutParams params = onlineCover.getLayoutParams();
            params.width = width;
            params.height = height;
            onlineCover.setLayoutParams(params);
            showDefaultOnlineThumbnail();
        }
    }

    /**
     * 显示默认的缩略图
     */
    private void showDefaultOnlineThumbnail() {
        if (!TextUtils.isEmpty(forestCover) && forestCover.contains("coverUrl")) {
            JSONObject jsonObject = JSON.parseObject(forestCover, Feature.AutoCloseSource);
            forestCover = jsonObject.getString("coverUrl");
        }
        MyApplication.getThumbnailManager(getActivity()).displayThumbnailWithDefault(
                AppSettings.getFileUrl(forestCover), onlineCover, R.drawable.online_detail_default_thumbnail);
    }

    private void configOnlineCoverHeight() {
        //设置头部图片的高度为16:8
        int width = getScreenWidth(getActivity());
        int height = width * 9 / 16;
        ViewGroup.LayoutParams params = onlineCover.getLayoutParams();
        params.width = width;
        params.height = height;
        onlineCover.setLayoutParams(params);
    }

    /**
     * 判断是不是当前直播的创建者
     *
     * @return
     */
    private boolean isCreator() {
        if (onlineRes != null) {
            if (TextUtils.equals(getMemeberId(), onlineRes.getAcCreateId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 改变播放的状态
     */
    private void changePlayModelStatus() {
        if (status == 0) {
            currentPlayStatus = PlayMode.FORECAST;
        } else if (status == 1) {
            currentPlayStatus = PlayMode.ONLINEPLAYING;
            if (isHasStartOnlinePermission()) {
                role = 1;
            }
        } else if (status == 2) {
            currentPlayStatus = PlayMode.REPLAYVIDEO;
        }
    }

    /**
     * 是否有开始直播的权限
     */
    private boolean isHasStartOnlinePermission() {
        if (isFromMOOC) {
            return isOnlineReporter;
        } else {
            return isReporterInAllClass;
        }
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
        if (videoView != null) {
            videoView.setVideoViewListener(videoViewListener);
        }
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
        if (videoView == null) return;
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
        if (videoView == null) return;
        //直播
        if (currentPlayStatus == PlayMode.ONLINEPLAYING) {
            String mActionId = onlineRes.getLiveId();
            boolean mUseHls = false;
            String mCustomerId = "7f96c8e118ddb0a1746a2d3c30dffa1c";
            String p = "";
            String cuid = "b68e945493";
            String utoken = "";
            Bundle mBundle = new Bundle();
            mBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_ACTION_LIVE);
            mBundle.putString(PlayerParams.KEY_PLAY_ACTIONID, mActionId);
            mBundle.putBoolean(PlayerParams.KEY_PLAY_USEHLS, mUseHls);
            mBundle.putString(PlayerParams.KEY_PLAY_CUSTOMERID, mCustomerId);
            mBundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, p);
            mBundle.putString(PlayerParams.KEY_CUID, cuid);
            mBundle.putString(PlayerParams.KEY_UTOKEN, utoken);
            //在开始播放之前的准备工作
            setActionLiveParameter(mUseHls);
            if (videoContainer == null) {
                videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
            }
            videoView.setDataSource(mBundle);
        } else {
            //点播
            String uuid = "b68e945493";
//            String vuid = "62b41c008d";
            String vuid = onlineRes.getDemandId();
            String p = "";
            boolean isSaas = true;
            Bundle mBundle = new Bundle();
            mBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
            mBundle.putString(PlayerParams.KEY_PLAY_UUID, uuid);
            mBundle.putString(PlayerParams.KEY_PLAY_VUID, vuid);
            mBundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, p);
            mBundle.putBoolean("saas", isSaas);
            if (videoContainer == null) {
                videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
            }
            videoView.setDataSource(mBundle);
        }
    }

    /**
     * 来回切换tab 显示相应的布局
     */
    private void setCurrentBottomLayout(boolean isTeacher, boolean isStudent) {
        if (isHistoryClass) {
            isTeacher = false;
            isStudent = false;
        }
        if (isTeacher) {
            bottomTeachLayout.setVisibility(View.VISIBLE);
            bottomInterLayout.setVisibility(View.GONE);
            if (bottomBtnLayout != null) {
                bottomBtnLayout.setVisibility(View.GONE);
            }
        } else if (isStudent) {
            bottomInterLayout.setVisibility(View.VISIBLE);
            bottomTeachLayout.setVisibility(View.GONE);
            if (bottomBtnLayout != null) {
                bottomBtnLayout.setVisibility(View.GONE);
            }
        } else {
            bottomInterLayout.setVisibility(View.GONE);
            bottomTeachLayout.setVisibility(View.GONE);
            if (isFromMOOC) {
                bottomBtnLayout = (LinearLayout) findViewById(R.id.layout_bottom_btn);
                bottomBtnLayout.setVisibility(View.VISIBLE);
            }
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

    private boolean campareIsBelongCurrentClass(Emcee data) {
        List<PublishClass> publishClassList = data.getPublishClassList();
        if (publishClassList != null && publishClassList.size() > 0) {
            for (int i = 0; i < publishClassList.size(); i++) {
                PublishClass publishClass = publishClassList.get(i);
                if (publishClass.isBelong() && TextUtils.equals(classId, publishClass.getClassId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 配置当屏幕进行旋转的时候video进行相应的变化
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (currentPlayStatus != PlayMode.FORECAST) {
            if (videoView != null) {
                if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    findViewById(R.id.bottom_layout_airclass).setVisibility(View.VISIBLE);
                    mIntrolayout.setVisibility(View.VISIBLE);
                    mLiveShare.setVisibility(View.VISIBLE);
                    mLiveBack.setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.bottom_layout_airclass).setVisibility(View.GONE);
                    mIntrolayout.setVisibility(View.GONE);
                    mLiveShare.setVisibility(View.GONE);
                    mLiveBack.setVisibility(View.GONE);
                }
                videoView.onConfigurationChanged(newConfig);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (currentPlayStatus != PlayMode.FORECAST && videoView != null) {
            videoView.onPause();
        }

        if (mp4VideoView != null && mp4VideoView.isPlaying()) {
            mp4VideoView.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (currentPlayStatus != PlayMode.FORECAST && videoView != null) {
            videoView.stopAndRelease();
        }
    }

    @Override
    public void onDestroy() {
        if (isFromMOOC) {
            getContext().unregisterReceiver(mBroadcastReceiver);
        }
        if (currentPlayStatus != PlayMode.FORECAST) {
            if (videoView != null) {
                videoView.onDestroy();
            }
            if (mp4VideoView != null) {
                mp4VideoView.closePlayer();
            }
        }
        if (isNeedTimerRecorder()) {
            recordTime = (System.currentTimeMillis() - recordTime + 500) / 1000;
            //把观看的数据同步给server
            StudyInfoRecordUtil recordUtil = StudyInfoRecordUtil.getInstance().
                    clearData().
                    setActivity(getActivity()).
                    setOnlineRes(onlineRes).
                    setCurrentModel(StudyInfoRecordUtil.RecordModel.ONLINE_MODEL).
                    setRecordTime((int) recordTime).
                    setUserInfo(DemoApplication.getInstance().getUserInfo());
            if (isFromMOOC) {
                if (onlineRes.isFromMyLive()) {
                    recordUtil.setSourceType(SourceFromType.MY_ONLINE_LIVE);
                } else {
                    recordUtil.setSourceType(SourceFromType.ONLINE_LIVE);
                }
                recordUtil.setRecordType(3);
            } else {
                if (onlineRes.isFromMyLive()) {
                    recordUtil.setSourceType(SourceFromType.MY_ONLINE_LIVE);
                    recordUtil.setRecordType(3);
                } else {
                    recordUtil.setSourceType(SourceFromType.AIRCLASS_ONLINE);
                    recordUtil.setRecordType(1);
                }
            }
            recordUtil.send();
        }

        if (currentPlayStatus == PlayMode.ONLINEPLAYING || playModeChange) {
            //更新为离线的状态
            updateOnlineNumStatus(false);
        }
        //销毁定时器
        stopTimer();
        super.onDestroy();
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        private String[] titles = null;

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
            if (isAfterFinish && isBeforeFinish) {
                if (isShowPublishObject) {
                    if (isBeforeHasData && isAfterHasData) {
                        titles = new String[]{
                                getString(R.string.preview_before_class),
                                getString(R.string.after_before_class),
                                getString(R.string.interaction),
//                        getString(R.string.interaction),
                                getString(R.string.publish_object)};
                    } else if (isBeforeHasData) {
                        titles = new String[]{
                                getString(R.string.preview_before_class),
//                                getString(R.string.after_before_class),
                                getString(R.string.interaction),
//                        getString(R.string.interaction),
                                getString(R.string.publish_object)};
                    } else if (isAfterHasData) {
                        titles = new String[]{
//                                getString(R.string.preview_before_class),
                                getString(R.string.after_before_class),
                                getString(R.string.interaction),
//                        getString(R.string.interaction),
                                getString(R.string.publish_object)};
                    } else {
                        titles = new String[]{
//                                getString(R.string.preview_before_class),
//                                getString(R.string.after_before_class),
                                getString(R.string.interaction),
//                        getString(R.string.interaction),
                                getString(R.string.publish_object)};
                    }
                } else {
                    if (isBeforeHasData && isAfterHasData) {
                        titles = new String[]{
                                getString(R.string.preview_before_class),
                                getString(R.string.after_before_class),
                                getString(R.string.interaction),
//                        getString(R.string.interaction)
                        };
                    } else if (isBeforeHasData) {
                        titles = new String[]{
                                getString(R.string.preview_before_class),
//                                getString(R.string.after_before_class),
                                getString(R.string.interaction),
//                        getString(R.string.interaction)
                        };
                    } else if (isAfterHasData) {
                        titles = new String[]{
//                                getString(R.string.preview_before_class),
                                getString(R.string.after_before_class),
                                getString(R.string.interaction),
//                        getString(R.string.interaction)
                        };
                    } else {
                        titles = new String[]{
//                                getString(R.string.preview_before_class),
//                                getString(R.string.after_before_class),
                                getString(R.string.interaction),
//                        getString(R.string.interaction)
                        };
                    }
                }
            } else {
                if (isShowPublishObject) {
                    titles = new String[]{
                            getString(R.string.preview_before_class),
                            getString(R.string.after_before_class),
                            getString(R.string.interaction),
//                        getString(R.string.interaction),
                            getString(R.string.publish_object)};
                } else {
                    titles = new String[]{
                            getString(R.string.preview_before_class),
                            getString(R.string.after_before_class),
                            getString(R.string.interaction),
//                        getString(R.string.interaction)
                    };
                }
            }
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
        public long getItemId(int position) {
            return isLoadFinish ? getFragmentPosition(position) : position;
        }

        private long getFragmentPosition(int position) {
            if (position == 0) {
                if (isBeforeHasData) {
                    return 0;
                } else if (isAfterHasData) {
                    return 1;
                } else {
                    return 2;
                }
            } else if (position == 1) {
                if (isBeforeHasData && isAfterHasData) {
                    return 1;
                } else if (isBeforeHasData || isAfterHasData) {
                    return 2;
                } else {
                    return 3;
                }
            } else if (position == 2) {
                if (isBeforeHasData && isAfterHasData) {
                    return 2;
                } else if (isBeforeHasData || isAfterHasData) {
                    return 3;
                }
            }
            return position;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (isFromMOOC) {
                        return beforeResListFragment;
                    } else {
                        return beforeStudyFragment;
                    }
                case 1:
                    if (isFromMOOC) {
                        return afterResListFragment;
                    } else {
                        return afterStudyFragment;
                    }
                case 2:
                    return teachResourceFragment;
                default:
                    return publishObjectFragment;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConfirmOrderActivity.Rc_pay) {//从支付界面返回则刷新数据
            sendBroadcastToUpdateLiveDetail();
            getLiveDetails();
        } else if (requestCode == SectionTaskDetailsActivity.Rs_task_commit) {
            if (isFromMOOC) {
                if (beforeResListFragment != null && beforeResListFragment.isVisible()) {
                    beforeResListFragment.getData();
                }

                if (afterResListFragment != null && afterResListFragment.isVisible()) {
                    afterResListFragment.getData();
                }
            }
        }
    }

    /**
     * @return 判断返回记录观看时间的条件
     */
    private boolean isNeedTimerRecorder() {
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (userInfo != null) {
            return true;
        }
        return false;
    }

    /**
     * 显示加入我的直播btn条件判断
     *
     * @return
     */
    private boolean showAddMyPermission() {
//        if (isFromMOOC) return false;
//        if (roleType == RoleType.ROLE_TYPE_STUDENT && !onlineRes.isAddMyLived()) {
//            return true;
//        }
        return false;
    }

    /**
     * 空中课堂加入到我的直播
     */
    private void addMyLiveForAirClass() {
        Map<String, Object> param = new HashMap<>();
        param.put("ExtId", onlineRes.getId());
        param.put("ClassId", classId);
        param.put("MemberId", getMemeberId());
        DefaultDataListener listener = new DefaultDataListener<DataModelResult>(
                DataModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                DataModelResult result = getResult();
                if (result != null && result.isSuccess()) {
                    TipsHelper.showToast(getActivity(), getString(R.string.join_live_success));
                    onlineRes.setAddMyLived(true);
                    bottomBtnLayout.setVisibility(View.GONE);
                } else {
                    TipsHelper.showToast(getActivity(), getString(R.string.add_live_failed));
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.ADD_MY_LIVE_FROM_AIRCLASS_BASE_URL, param,
                listener);
    }

    /**
     * 更新空中课堂直播在线和离线的状态
     * 备注:只有在登录的情况下才统计
     */
    private void updateOnlineNumStatus(final boolean isOnline) {
        if (TextUtils.isEmpty(getMemeberId())) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("ExtId", onlineRes.getId());
        params.put("ClassId", classId);
        params.put("MemberId", userMemberId);
        //isOnline 为 true 表示上线  isOnline 为 false表示离线
        params.put("IsOnline", isOnline);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result != null && result.isSuccess()) {
                            //开启定时器的操作
                            if (isOnline) {
                                startTimer();
                            }
                        }
                    }
                };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_ONLINE_NUMBER_BASE_URL, params, listener);
    }


    /**
     * 开始计时器的操作
     */
    public void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimeTask != null) {
            mTimeTask.cancel();
        }
        mTimeTask = new TimerTask() {
            @Override
            public void run() {
                refreshRealTimeOnlineNum();
            }
        };
        if (mTimer != null && mTimeTask != null) {
            mTimer.schedule(mTimeTask, 1, 300000);
        }
    }

    /**
     * 停止定时器的运行
     */
    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimeTask != null) {
            mTimeTask.cancel();
            mTimeTask = null;
        }
    }

    /**
     * 实时刷新直播的在线人数
     */
    private void refreshRealTimeOnlineNum() {
        Map<String, Object> params = new HashMap<>();
        params.put("ExtId", onlineRes.getId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result != null && result.isSuccess()) {
                            //成功
                            try {
                                org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
                                org.json.JSONObject model = jsonObject.optJSONObject("Model");
                                int onlineNum = model.optInt("OnlineNum");
                                onlineRes.setOnlineNum(onlineNum);
                                refreshShowOnlineNum();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_ONLINE_ALWAYS_PEOPLE_NUM_BASE_URL, params, listener);
    }

    /**
     * 刷新显示在线人数的显示
     */
    public void refreshShowOnlineNum() {
        //空中课堂的在线人数
        mOnlineCount = (TextView) findViewById(R.id.tv_online_look_count);
        if (isFromMOOC) {
            mOnlineCount.setVisibility(View.GONE);
            //根据选择的状态来播放相应的资源
            TextView textViewViewCount = (TextView) findViewById(R.id.live_count_tv);
            String joinText = "";
            int joinCount = onlineRes.getJoinCount();
            //如果参加数大于一万显示 1.x万
            if (joinCount >= 10000) {
                double count = div(joinCount, 10000, 1);
                joinText = getString(R.string.join_count, String.valueOf
                        (count) + getResources().getString(R.string.ten_thousand));
            } else {
                joinText = getString(R.string.join_count, String.valueOf
                        (joinCount));
            }
            int browseCount = onlineRes.getBrowseCount();
            //如果浏览数大于一万显示 1.x万
            if (browseCount >= 10000) {
                double count = div(browseCount, 10000, 1);
                textViewViewCount.setText(joinText + " | " + getString(R.string.online_count, String.valueOf
                        (count) + getResources().getString(R.string.ten_thousand)) + " | "
                        + LiveAdapter.getLiveTypeText(getContext(), onlineRes.isEbanshuLive()));
            } else {
                textViewViewCount.setText(joinText + " | " + getString(R.string.online_count, String.valueOf
                        (browseCount)) + " | "
                        + LiveAdapter.getLiveTypeText(getContext(), onlineRes.isEbanshuLive()));
            }
        } else {
            mOnlineCount.setVisibility(View.VISIBLE);
            if (mOnlineCount != null) {
                if (onlineRes != null) {
                    int browseCount = onlineRes.getBrowseCount();
                    String stringText = null;
                    //如果浏览数大于一万显示 1.x万
                    if (browseCount >= 10000) {
                        double count = div(browseCount, 10000, 1);
                        stringText = getString(R.string.online_count, String.valueOf(count) + "万");
                    } else {
                        stringText = getString(R.string.online_count, String.valueOf(browseCount));
                    }
                    if (onlineRes.isEbanshuLive()) {
                        stringText = stringText + " | " + getString(R.string.live_type_blackboard);
                    } else {
                        stringText = stringText + " | " + getString(R.string.live_type_video);
                    }
                    //直播中显示多少人在上课
                    if (currentPlayStatus == PlayMode.ONLINEPLAYING) {
                        stringText = getString(R.string.str_how_many_people_online, onlineRes.getOnlineNum())
                                + " | " + stringText;
                    }
                    mOnlineCount.setText(stringText);
                }
            }
        }
    }

    /**
     * 弹出提示的dialog
     * isStartOnline 开始直播
     * isEndOnline 结束直播
     */
    private void popMessageDialog(String message,
                                  final boolean isStartOnline,
                                  final boolean isEndOnline,
                                  final String activityId) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(), null, message,
                isEndOnline ? getString(R.string.cancel) : null,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (isEndOnline) {
                            endUpOnlinePlay();
                        }
                    }
                });
        messageDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isStartOnline) {
                    handleStartOnlineData(activityId);
                }
            }
        });
        messageDialog.show();
    }

    private void handleStartOnlineData(String activityId) {
        if (status == 0) {
            onlineRes.setLiveId(activityId);
            onlineRes.setState(1);
            status = 1;
            if (isFromMOOC) {
                sendBroadcastToUpdateLiveList();
                if (liveDetailsVo != null) {
                    liveDetailsVo.getLive().setState(1);
                }
                currentPlayStatus = PlayMode.ONLINEPLAYING;
                videoContainer = (RelativeLayout)
                        findViewById(R.id.videoContainer);
                forecastLayout.setVisibility(View.GONE);
                choosePlayStatus(true);
                setListeners();
                checkPlayModel();
            } else {
                isAfterBegin = true;
            }
            onResume();
        }
    }


    /**
     * check 当前的用户在不在班级中(来自的我的直播数据)
     */
    private void CheckUserIsInClass() {
        if (isFromMOOC) return;
        if (TextUtils.isEmpty(getMemeberId()) || !onlineRes.isFromMyLive()) {
            return;
        }
        Bundle bundle = getArguments();
        boolean isMyCourseChildOnline = false;
        String studentMemberId = null;
        if (bundle != null) {
            isMyCourseChildOnline = bundle.getBoolean("isMyCourseChildOnline");
            studentMemberId = bundle.getString("memberId");
        }
        Map<String, Object> params = new HashMap<>();
        if (isMyCourseChildOnline && !TextUtils.isEmpty(studentMemberId)) {
            params.put("MemberId", studentMemberId);
        } else {
            params.put("MemberId", getMemeberId());
        }
        params.put("ClassId", classId);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (TextUtils.isEmpty(jsonString)) {
                            return;
                        }
                        try {
                            org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
                            boolean model = jsonObject.optBoolean("Model");
                            if (!model) {
                                //不在当前的班级
                                popButtonDialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.CHECK_MEMBER_ISIN_CLASS_BASE_URL, params, listener);
    }

    private void popButtonDialog() {
        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), "", getString(R.string.str_pop_dialog_prompt_text),
                "", null, getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }
        });
        dialog.setIsAutoDismiss(true);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 根据直播信息,判断当前用户的角色信息
     *
     * @param emcee 直播实体
     * @return {@link UserHelper.MoocRoleType#EDITOR},{@link UserHelper.MoocRoleType#PARENT},{@link UserHelper.MoocRoleType#STUDENT},{@link UserHelper.MoocRoleType#STUDENT}
     */
    private int getMoocLiveRoleType(Emcee emcee) {
        String curMemberId = UserHelper.getUserId();
        String memberId = getExtrasMemberId();
        // 如果上层页面传入的memberId 不是自己的memberId，那么说明是家长身份需要使用孩子的memberId
        if (!TextUtils.isEmpty(curMemberId) && !TextUtils.isEmpty(memberId) &&
                !curMemberId.equals(memberId)) {
            return UserHelper.MoocRoleType.PARENT;
        }

        if (!TextUtils.isEmpty(curMemberId) && emcee != null) {
            // 非空判断,验证通过
            if (!TextUtils.isEmpty(emcee.getAcCreateId()) && curMemberId.equals(emcee
                    .getAcCreateId())) {
                // 如果当前用户Id是直播课程的主编Id 返回主编角色
                return UserHelper.MoocRoleType.EDITOR;
            } else if (!TextUtils.isEmpty(emcee.getEmceeMemberIdStr()) && emcee
                    .getEmceeMemberIdStr().contains(curMemberId)) {
                // 小编集合中包含用户Id，则是小编角色
                return UserHelper.MoocRoleType.TEACHER;
            }
        }
        // 其它则是默认角色，学生
        return UserHelper.MoocRoleType.STUDENT;
    }

    public LinearLayout getBottomLayout(boolean isTeacherLayout) {
        if (isTeacherLayout) {
            return bottomTeachLayout;
        } else {
            return bottomInterLayout;
        }
    }

    private void getCurrentClassInfo() {
        currentClassInfo = new SubscribeClassInfo();
        if (onlineRes != null) {
            List<PublishClass> publishClasses = onlineRes.getPublishClassList();
            if (publishClasses != null && publishClasses.size() > 0) {
                for (int i = 0; i < publishClasses.size(); i++) {
                    PublishClass classData = publishClasses.get(i);
                    if (TextUtils.equals(classData.getClassId(), onlineRes.getClassId())) {
                        currentClassInfo.setClassId(classData.getClassId());
                        currentClassInfo.setClassName(classData.getClassName());
                        currentClassInfo.setSchoolId(classData.getSchoolId());
                        currentClassInfo.setSchoolName(classData.getSchoolName());
                        break;
                    }
                }
            }
        }
    }

    public void setLoadStudyTaskFinish(int type, boolean hasData) {
        dismissLoadingDialog();
        //课前预习 1 课后练习 2
        if (type == 1) {
            isBeforeFinish = true;
            isBeforeHasData = hasData;
        } else {
            isAfterFinish = true;
            isAfterHasData = hasData;
        }
        LogUtils.log(TAG, "loadFinish" + "--type=" + type);
        if (isBeforeFinish && isAfterFinish && !isLoadFinish) {
            isLoadFinish = true;
            LogUtils.log(TAG, "loadFinish");
            showTabCondition();
            titleTip.setVisibility(View.VISIBLE);
            onlineRestab.setVisibility(View.VISIBLE);
        }
    }

    private void checkPlayModel() {
        if (isSchoolResVideo) {
            //校本的视屏
            if (currentPlayStatus == PlayMode.ONLINEPLAYING && isHasStartOnlinePermission()) {
                //直播中 主编或者小编
                startPlayResources();
            } else {
                loadSchoolResVideoData();
            }
        } else {
            startPlayResources();
        }
    }

    private void loadSchoolResVideoData() {
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
        wawaCourseUtils.loadCourseDetail(onlineRes.getDemandId());
        wawaCourseUtils.setOnCourseDetailFinishListener(courseData -> {
            if (courseData != null) {
                String leValue = courseData.getLeValue();
                String resUrl = courseData.resourceurl;
                String uUid = null;
                String vUid = null;
                if (!TextUtils.isEmpty(leValue)) {
                    String[] values = leValue.split("&");
                    uUid = values[1].split("=")[1];
                    vUid = values[2].split("=")[1];
                }
                if (!TextUtils.isEmpty(vUid)) {
                    //用乐视的进行播放
                    onlineRes.setDemandId(vUid);
                    playSchoolResVideo();
                } else if (!TextUtils.isEmpty(resUrl)) {
                    String resTitle = onlineRes.getResTitle();
                    CheckLanMp4UrlHelper mp4UrlHelper = new CheckLanMp4UrlHelper(getActivity());
                    mp4UrlHelper.setCallBackListener(result -> {
                        if (result != null) {
                            playMp4FormatVideo((String) result);
                        } else {
                            playMp4FormatVideo(resUrl);
                        }
                    });
                    if (!TextUtils.isEmpty(resTitle)) {
                        mp4UrlHelper.setResTitle(resTitle)
                                .setMp4ResourceUrl(resUrl)
                                .checkLanUrl(true);
                    } else if (!TextUtils.isEmpty(resUrl)) {
                        mp4UrlHelper.setMp4ResourceUrl(resUrl).checkLanUrl(false);
                    }
                }
            }
        });
    }

    private void playSchoolResVideo() {
        String uuid = "b68e945493";
        String vuid = onlineRes.getDemandId();
        String p = "";
        boolean isSaas = true;
        Bundle mBundle = new Bundle();
        mBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
        mBundle.putString(PlayerParams.KEY_PLAY_UUID, uuid);
        mBundle.putString(PlayerParams.KEY_PLAY_VUID, vuid);
        mBundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, p);
        mBundle.putBoolean("saas", isSaas);
        if (videoContainer == null) {
            videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
        }
        videoView.setDataSource(mBundle);
    }

    private void playMp4FormatVideo(String resUrl) {
        if (videoContainer == null) {
            videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
        }
        videoContainer.removeAllViews();
        mp4PlayRootView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_video_play, null);
        mp4VideoView = (UniversalVideoView) mp4PlayRootView.findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) mp4PlayRootView.findViewById(R.id.media_controller);
        mMediaController.hideTitleLayout(true);
        mp4VideoView.setMediaController(mMediaController);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) videoContainer.getLayoutParams();
        layoutParams.width = getScreenWidth(getActivity());
        layoutParams.height = getScreenWidth(getActivity()) * 9 / 16;
        videoContainer.setLayoutParams(layoutParams);
        videoContainer.addView(mp4PlayRootView);
        mp4VideoView.setVideoViewCallback(this);
        mp4VideoView.setVideoPath(resUrl);
        mp4VideoView.requestFocus();
        mp4VideoView.start();
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {
        if (isFullscreen) {
            findViewById(R.id.bottom_layout_airclass).setVisibility(View.GONE);
            mIntrolayout.setVisibility(View.GONE);
            mLiveShare.setVisibility(View.GONE);
            mLiveBack.setVisibility(View.GONE);
            mMediaController.hideTitleLayout(false);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) videoContainer.getLayoutParams();
            layoutParams.width = getScreenWidth(getActivity());
            layoutParams.height = ScreenUtils.getScreenHeight(getActivity());
            videoContainer.setLayoutParams(layoutParams);
        } else {
            findViewById(R.id.bottom_layout_airclass).setVisibility(View.VISIBLE);
            mIntrolayout.setVisibility(View.VISIBLE);
            mLiveShare.setVisibility(View.VISIBLE);
            mLiveBack.setVisibility(View.VISIBLE);
            mMediaController.hideTitleLayout(true);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) videoContainer.getLayoutParams();
            layoutParams.width = getScreenWidth(getActivity());
            layoutParams.height = getScreenWidth(getActivity()) * 9 / 16;
            videoContainer.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {

    }
}
