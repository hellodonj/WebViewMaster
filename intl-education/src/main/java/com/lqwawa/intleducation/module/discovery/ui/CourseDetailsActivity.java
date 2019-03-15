package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragmentActivity;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.ScrollViewEx;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.common.ui.CommentDialog;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.ui.PopupMenu;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.SPUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.constant.SharedConstant;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.apply.CourseApplyForNavigator;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.apply.TutorialCourseApplyForFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.PayCourseDialogFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.PayDialogNavigator;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.CourseRoute;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.intro.CourseIntroductionActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails.CourseDetailItemParams;
import com.lqwawa.intleducation.module.discovery.ui.navigator.CourseDetailsNavigator;
import com.lqwawa.intleducation.module.discovery.ui.observable.CourseVoObservable;
import com.lqwawa.intleducation.module.discovery.ui.order.LQCourseOrderActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.onclass.OnlineClassListFragment;
import com.lqwawa.intleducation.module.tutorial.course.TutorialGroupFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.oosic.apps.share.BaseShareUtils;
import com.oosic.apps.share.ShareInfo;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import static com.lqwawa.intleducation.base.utils.StringUtils.languageIsEnglish;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 课程详情
 */
public class CourseDetailsActivity extends MyBaseFragmentActivity
        implements View.OnClickListener,
        ScrollViewEx.ScrollViewListener,
        CourseDetailsNavigator,
        PayDialogNavigator {
    private static final String TAG = "CourseDetailsActivity";

    public static final String LQWAWA_PAY_RESULT_ACTION = "android.lqwawa.action.payresult";

    private static final String KEY_EXTRA_IS_SCHOOL_ENTER = "KEY_EXTRA_IS_SCHOOL_ENTER";
    // 是不是从在线课堂班级进入的
    private static final String KEY_EXTRA_IS_ONLINE_CLASS_ENTER = "KEY_EXTRA_IS_ONLINE_CLASS_ENTER";
    public static final int Rs_collect = 1028;
    private TopBar topBar;

    private ImageView imageViewCover;
    //课程名
    private TextView textViewCourseName;
    //课程进度
    private TextView textViewCourseProcess;
    //机构名
    private TextView textViewOrganName;
    // 简介
    private Button mBtnIntro;
    //机构信息主页入口
    private TextView mSchoolEnter;
    //老师名
    private TextView textViewTeacherName;
    //评分
    private RatingBar ratingBarGrade;
    // 分享
    private ImageView ivShare;
    //评分数
    private TextView textViewGrade;
    //参与人数
    private TextView textViewStrudyNumber;
    //课程价格
    private TextView textViewePriceTitle;
    //课程价格
    private TextView textViewCoursePrice;

    private RelativeLayout loadFailedLayout;
    private Button btnReload;

    private PullToRefreshView pullToRefreshView;

    private ScrollViewEx scrollView;

    private RadioGroup rg_tab;
    private RadioGroup rg_tab_f;

    private TextView mTvPrice;
    private TextView mTvOriginalPrice;
    private TextView textViewAddToCart;

    private TextView textViewPay;
    private TextView mBtnEnterPay;


    private LinearLayout mCommentLayout;
    // 评论内容
    private EditText mCommentContent;
    // 发送评论
    private TextView mBtnSend;

    private CourseVo courseVo;
    private String courseId;

    private int img_width;
    private int img_height;
    private ImageOptions imageOptions;
    private boolean collected = false;
    private int courseScore;
    private boolean canLoadMore = false;
    private boolean isComment = false;
    private int initTabIndex = 0;
    private CourseDetailsVo courseDetailsVo;
    private boolean isComeFromDetail = false;//是否是从学习的课程详情跳转过来的
    private boolean isMyCourse; // 是否从我的课程过来的
    private boolean isLqExcellent = false;//是否是精品学程
    // 是否可以编辑 true 代表学生身份 false 代表是家长身份
    private boolean mCanEdit;
    // 当前课程传入的memberId 可能是家长传孩子的memberId
    private String mCurMemberId;

    // CourseDetailsItemFragment introductionFragment;
    CourseDetailsItemFragment studyPlanFragment;
    CourseDetailsItemFragment courseCommentFragment;
    OnLoadStatusChangeListener onLoadStatusChangeListener;
    // private ClassroomFragment mClassroomFragment;
    private OnlineClassListFragment mOnlineClassFragment;
    private TutorialGroupFragment mTutorialGroupFragment;

    // 是否是机构主页进来的
    private boolean isSchoolEnter;
    // 是否是在线课堂班级过来的
    private boolean isOnlineClassEnter;
    // 课程详情参数
    private CourseDetailParams mCourseDetailParams;
    private SchoolInfoEntity mSchoolEntity;
    // 是否是学程馆进来的，并且获取到授权
    private boolean isAuthorized;

    // 在线课堂Tab
    private RadioButton mRbLive,mRbLiveF;

    /**
     * 实体机构,学程馆入口
     * @param isAuthorized 是否已经授权
     * @param params 机构学程传的参数
     * @param isSchoolEnter 是否是从空中学校过来
     * @param activity
     * @param courseId
     * @param canEdit
     * @param memberId
     */
    public static void start(final boolean isAuthorized,
                             final @NonNull CourseDetailParams params,
                             final boolean isSchoolEnter,
                             final Activity activity,
                             final String courseId,
                             final boolean canEdit,
                             final String memberId) {
        final CourseRoute route = new CourseRoute();
        route.navigation(activity, courseId, memberId, params, new CourseRoute.NavigationListener() {
            @Override
            public void route(boolean needToLearn) {
                super.route(needToLearn);
                if(needToLearn){
                    params.buildOrganJoinState(needToLearn);
                    MyCourseDetailsActivity.start(isAuthorized,params,isSchoolEnter,activity, courseId,  false, true, memberId);
                }else{
                    activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                            .putExtra("id", courseId).putExtra("canEdit", canEdit)
                            .putExtra("memberId" ,memberId)
                            .putExtra("isAuthorized",isAuthorized)
                            .putExtra(ACTIVITY_BUNDLE_OBJECT,params)
                            .putExtra(KEY_EXTRA_IS_SCHOOL_ENTER,isSchoolEnter));
                }
            }
        });
        /*final CourseDetails courseDetails = new CourseDetails();
        courseDetails.checkCourseDetails(activity, courseId, memberId, false, "",
                new CourseDetails.OnCheckCourseStatusListener() {
                    @Override
                    public void onCheckSuccess(CourseDetailsVo courseDetailsVo, boolean needToLearn) {
                        if(needToLearn || params.isOrganCounselor()){
                            // 是否购买过
                            params.buildOrganJoinState(needToLearn);
                            MyCourseDetailsActivity.start(isAuthorized,params,isSchoolEnter,activity, courseId,  false, true, memberId);
                        }else{
                            *//*if(!isAuthorized){
                                // 没有购买,并且是未授权的
                                UIUtil.showToastSafe(R.string.label_please_request_authorization);
                                return;
                            }*//*
                            activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                                    .putExtra("id", courseId).putExtra("canEdit", canEdit)
                                    .putExtra("memberId" ,memberId)
                                    .putExtra("isAuthorized",isAuthorized)
                                    .putExtra(ACTIVITY_BUNDLE_OBJECT,params)
                                    .putExtra(KEY_EXTRA_IS_SCHOOL_ENTER,isSchoolEnter));
                        }
                    }

                    @Override
                    public void onCheckFailed(int code, String message) {

                    }
                });*/
    }

    /**
     * 跳转到课程详情页 支持从学程中的课程列表 首页 跳转
     * 如果当前用户已经购买参加了课程自动跳转到我的直播详情
     * @param isMyCourse 是否从我的课程入口进来
     * @param activity
     * @param courseId
     * @param canEdit
     * @param memberId
     */
    public static void start(final boolean isMyCourse,
                             final Activity activity,
                             final String courseId,
                             final boolean canEdit,
                             final String memberId) {
        final CourseRoute route = new CourseRoute();
        route.navigation(activity, courseId, memberId, null, new CourseRoute.NavigationListener() {
            @Override
            public void route(boolean needToLearn) {
                super.route(needToLearn);
                if(needToLearn){
                    // 去详情页面
                    MyCourseDetailsActivity.start(activity, courseId,  false, canEdit, memberId);
                }else{
                    // 去未加入页面
                    activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                            .putExtra("id", courseId).putExtra("canEdit", canEdit)
                            .putExtra("memberId" ,memberId)
                            .putExtra("isMyCourse",isMyCourse));
                }
            }
        });
    }

    /**
     * 跳转到课程详情页 支持从学程中的课程列表 首页 跳转
     * 如果当前用户已经购买参加了课程自动跳转到我的直播详情
     * @param activity
     * @param courseId
     * @param canEdit
     * @param memberId
     */
    public static void start(final Activity activity,
                             final String courseId,
                             final boolean canEdit,
                             final String memberId) {
        /*final CourseDetails courseDetails = new CourseDetails();
        courseDetails.checkCourseDetails(activity, courseId, memberId, false, "",
                new CourseDetails.OnCheckCourseStatusListener() {
                    @Override
                    public void onCheckSuccess(CourseDetailsVo courseDetailsVo, boolean needToLearn) {
                        if(needToLearn){
                            MyCourseDetailsActivity.start(activity, courseId,  false, true, memberId);
                        }else{
                            activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                                    .putExtra("id", courseId).putExtra("canEdit", canEdit)
                                    .putExtra("memberId" ,memberId));
                        }
                    }

                    @Override
                    public void onCheckFailed(int code, String message) {

                    }
                });*/

            start(false,activity,courseId,canEdit,memberId);
    }

    /**
     * 班级学程和机构学程的入口
     * @param activity
     * @param courseId
     * @param canEdit
     * @param memberId
     */
    public static void start(final Activity activity,
                             final String courseId,
                             final boolean canEdit,
                             final String memberId,
                             final @NonNull CourseDetailParams params) {
        final CourseRoute route = new CourseRoute();
        route.navigation(activity, courseId, memberId, params, new CourseRoute.NavigationListener() {
            @Override
            public void route(boolean needToLearn) {
                super.route(needToLearn);
                if(needToLearn){
                    // 去详情页面
                    MyCourseDetailsActivity.start(activity, courseId,
                            false, true, memberId,params);
                }else{
                    // 去未加入页面
                    activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                            .putExtra("id", courseId)
                            .putExtra("canEdit", canEdit)
                            .putExtra("memberId" ,memberId)
                            .putExtra(ACTIVITY_BUNDLE_OBJECT,params));
                }
            }
        });

        /*final CourseDetails courseDetails = new CourseDetails();
        courseDetails.checkCourseDetails(activity, courseId, memberId, false, "",
                new CourseDetails.OnCheckCourseStatusListener() {
                    @Override
                    public void onCheckSuccess(CourseDetailsVo courseDetailsVo, boolean needToLearn) {
                        if(needToLearn || params.isClassTeacher()
                                || params.isClassParent()
                                || params.isOrganCounselor()){
                            // 或者班级学程的老师
                            MyCourseDetailsActivity.start(activity, courseId,
                                    false, true, memberId,params);
                        }else{
                            activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                                    .putExtra("id", courseId)
                                    .putExtra("canEdit", canEdit)
                                    .putExtra("memberId" ,memberId)
                                    .putExtra(ACTIVITY_BUNDLE_OBJECT,params));
                        }
                    }

                    @Override
                    public void onCheckFailed(int code, String message) {

                    }
                });*/
    }

    /**
     * 跳转到课程详情页 支持从学程中的课程列表 首页 跳转
     * 如果当前用户已经购买参加了课程自动跳转到我的直播详情
     * @param activity
     * @param courseId
     * @param canEdit
     * @param memberId
     * @param schoolEnter 机构主页跳转学程的入口
     * @param isOnlineClassEnter 是否是在线课堂班级过来的
     */
    public static void start(final Activity activity,
                             boolean schoolEnter,
                             boolean isOnlineClassEnter,
                             final String courseId,
                             final boolean canEdit,
                             final String memberId) {

        final CourseRoute route = new CourseRoute();
        route.navigation(activity, courseId, memberId, null, new CourseRoute.NavigationListener() {
            @Override
            public void route(boolean needToLearn) {
                super.route(needToLearn);
                if(needToLearn){
                    // 去详情页面
                    MyCourseDetailsActivity.start(activity, courseId,  false, true, memberId,schoolEnter,isOnlineClassEnter);
                }else{
                    // 去未加入页面
                    activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                            .putExtra("id", courseId).putExtra("canEdit", canEdit)
                            .putExtra("memberId" ,memberId).putExtra(KEY_EXTRA_IS_SCHOOL_ENTER,schoolEnter)
                            .putExtra(KEY_EXTRA_IS_ONLINE_CLASS_ENTER,isOnlineClassEnter));
                }
            }
        });

        /*final CourseDetails courseDetails = new CourseDetails();
        courseDetails.checkCourseDetails(activity, courseId, memberId, false, "",
                new CourseDetails.OnCheckCourseStatusListener() {
                    @Override
                    public void onCheckSuccess(CourseDetailsVo courseDetailsVo, boolean needToLearn) {
                        if(needToLearn){
                            MyCourseDetailsActivity.start(activity, courseId,  false, true, memberId,schoolEnter,isOnlineClassEnter);
                        }else{
                            activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                                    .putExtra("id", courseId).putExtra("canEdit", canEdit)
                                    .putExtra("memberId" ,memberId).putExtra(KEY_EXTRA_IS_SCHOOL_ENTER,schoolEnter)
                                    .putExtra(KEY_EXTRA_IS_ONLINE_CLASS_ENTER,isOnlineClassEnter));
                        }
                    }

                    @Override
                    public void onCheckFailed(int code, String message) {

                    }
                });*/
    }

    /**
     * 跳转到课程详情页 支持从学程中的课程列表 首页 跳转
     * 如果当前用户已经购买参加了课程自动跳转到我的直播详情
     * @param activity
     * @param courseId
     * @param canEdit
     * @param memberId
     * @param schoolEnter 机构主页跳转学程的入口
     * @param isOnlineClassEnter 是否是在线课堂班级过来的
     * @param isOnlineTeacher 是否是在线课堂的老师
     */
    public static void start(final Activity activity,
                             boolean schoolEnter,
                             boolean isOnlineClassEnter,
                             final String courseId,
                             final boolean canEdit,
                             final String memberId,
                             boolean isOnlineTeacher) {

        final CourseRoute route = new CourseRoute(isOnlineTeacher);
        route.navigation(activity, courseId, memberId, null, new CourseRoute.NavigationListener() {
            @Override
            public void route(boolean needToLearn) {
                super.route(needToLearn);
                if(needToLearn){
                    // 去详情页面
                    final boolean isOnlineCounselor = route.isOnlineCounselor();
                    MyCourseDetailsActivity.start(activity, courseId,  false, true, memberId,schoolEnter,isOnlineClassEnter,isOnlineCounselor);
                }else{
                    // 去未加入页面
                    activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                            .putExtra("id", courseId).putExtra("canEdit", canEdit)
                            .putExtra("memberId" ,memberId).putExtra(KEY_EXTRA_IS_SCHOOL_ENTER,schoolEnter)
                            .putExtra(KEY_EXTRA_IS_ONLINE_CLASS_ENTER,isOnlineClassEnter));
                }
            }
        });

        /*final CourseDetails courseDetails = new CourseDetails(isOnlineTeacher);
        courseDetails.checkCourseDetails(activity, courseId, memberId, false, "",
                new CourseDetails.OnCheckCourseStatusListener() {
                    @Override
                    public void onCheckSuccess(CourseDetailsVo courseDetailsVo, boolean needToLearn) {
                        if(needToLearn){
                            final boolean handleOnlineTeacher = courseDetails.getHandleOnlineTeacher();
                            MyCourseDetailsActivity.start(activity, courseId,  false, true, memberId,schoolEnter,isOnlineClassEnter,handleOnlineTeacher);
                        }else{
                            activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                                    .putExtra("id", courseId).putExtra("canEdit", canEdit)
                                    .putExtra("memberId" ,memberId).putExtra(KEY_EXTRA_IS_SCHOOL_ENTER,schoolEnter)
                                    .putExtra(KEY_EXTRA_IS_ONLINE_CLASS_ENTER,isOnlineClassEnter));
                        }
                    }

                    @Override
                    public void onCheckFailed(int code, String message) {

                    }
                });*/
    }

    /**
     * 在线课堂入口
     * @param activity 上下文对象
     * @param courseId 课程Id
     * @param canEdit 不是家长身份
     * @param memberId 自己的Id
     * @param isOnlineTeacher 是否是在线课堂的老师
     */
    public static void start(final Activity activity,
                             final String courseId,
                             final boolean canEdit,
                             final String memberId,
                             final boolean isOnlineTeacher){

        final CourseRoute route = new CourseRoute(isOnlineTeacher);
        route.navigation(activity, courseId, memberId, null, new CourseRoute.NavigationListener() {
            @Override
            public void route(boolean needToLearn) {
                super.route(needToLearn);
                if(needToLearn){
                    // 去详情页面
                    final boolean isOnlineCounselor = route.isOnlineCounselor();
                    MyCourseDetailsActivity.start(activity, courseId,  false, true, memberId,isOnlineCounselor);
                }else{
                    // 去未加入页面
                    activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                            .putExtra("id", courseId).putExtra("canEdit", canEdit)
                            .putExtra("memberId" ,memberId));
                }
            }
        });

        /*final CourseDetails courseDetails = new CourseDetails(isOnlineTeacher);
        courseDetails.checkCourseDetails(activity, courseId, memberId, false, "",
                new CourseDetails.OnCheckCourseStatusListener() {
                    @Override
                    public void onCheckSuccess(CourseDetailsVo courseDetailsVo, boolean needToLearn) {
                        if(needToLearn){
                            final boolean handleOnlineTeacher = courseDetails.getHandleOnlineTeacher();
                            MyCourseDetailsActivity.start(activity, courseId,  false, true, memberId,handleOnlineTeacher);
                        }else{
                            activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                                    .putExtra("id", courseId).putExtra("canEdit", canEdit)
                                    .putExtra("memberId" ,memberId));
                        }
                    }

                    @Override
                    public void onCheckFailed(int code, String message) {

                    }
                });*/
    }

    /**
     * 跳转到课程详情页 仅适用于从精品学程的课程列表跳转
     * 如果当前用户已经购买参加了课程自动跳转到我的直播详情
     * @param activity
     * @param courseId
     * @param canEdit
     * @param memberId
     * @param schoolId
     * @param isLqExcellent
     */
    public static void start(final Activity activity,
                             final String courseId,
                             final boolean canEdit,
                             final String memberId,
                             final String schoolId,
                             final boolean isLqExcellent) {

        final CourseRoute route = new CourseRoute();
        route.navigation(activity, courseId, memberId, null, new CourseRoute.NavigationListener() {
            @Override
            public void route(boolean needToLearn) {
                super.route(needToLearn);
                if(needToLearn){
                    // 去详情页面
                    MyCourseDetailsActivity.start(activity, courseId,  false, true, memberId);
                }else{
                    // 去未加入页面
                    activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                            .putExtra("id", courseId).putExtra("canEdit", canEdit)
                            .putExtra("memberId" ,memberId));
                }
            }
        });

        /*CourseDetails courseDetails = new CourseDetails();
        courseDetails.checkCourseDetails(activity, courseId, memberId, isLqExcellent, schoolId,
                new CourseDetails.OnCheckCourseStatusListener() {
                    @Override
                    public void onCheckSuccess(CourseDetailsVo courseDetailsVo, boolean needToLearn) {
                        if(needToLearn){
                            MyCourseDetailsActivity.start(activity, courseId,  false, true, memberId);
                        }else{
                            activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                                    .putExtra("id", courseId).putExtra("canEdit", canEdit)
                                    .putExtra("memberId" ,memberId));
                        }
                    }
                    @Override
                    public void onCheckFailed(int code, String message) {

                    }
                });*/
    }

    /**
     * 跳转到课程详情页 仅适用于从我收藏的的课程列表跳转
     * @param activity
     * @param courseId
     * @param canEdit
     * @param memberId
     */
    public static void startForResult(Activity activity, String courseId, boolean canEdit, String memberId) {
        activity.startActivityForResult(new Intent(activity, CourseDetailsActivity.class)
                .putExtra("id", courseId).putExtra("canEdit", canEdit)
                .putExtra("memberId" ,memberId), Rs_collect);
    }

    /**
     * 跳转到课程详情页 仅适用于从我的通知列表中跳转
     * @param activity
     * @param courseId
     * @param canEdit
     * @param isComment
     * @param memberId
     */
    public static void start(Activity activity, String courseId
            , boolean canEdit, boolean isComment, String memberId) {
        activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                .putExtra("id", courseId).putExtra("canEdit", canEdit)
                .putExtra("isComment", isComment)
                .putExtra("memberId" ,memberId));
    }

    /**
     * 跳转到课程详情页 仅适用于从我的通知列表中跳转
     * @param activity
     * @param courseId
     * @param canEdit
     * @param tabIndex
     * @param memberId
     */
    public static void start(Activity activity, String courseId, boolean canEdit,
                             int tabIndex, String memberId) {
        activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                .putExtra("id", courseId).putExtra("canEdit", canEdit).putExtra("tabIndex", tabIndex)
                .putExtra("memberId" ,memberId));
    }

    /**
     * 跳转到课程详情页 仅使用从直播的查看课程时有多个课程的课程列表跳转
     * @param activity
     * @param courseId
     * @param canEdit
     * @param tabIndex
     * @param vo
     * @param memberId
     */
    public static void start(final Activity activity,
                             final String courseId,
                             final boolean canEdit,
                             final int tabIndex,
                             final CourseVo vo,
                             final String memberId) {
        final CourseRoute route = new CourseRoute();
        route.navigation(activity, courseId, memberId, null, new CourseRoute.NavigationListener() {
            @Override
            public void route(boolean needToLearn) {
                super.route(needToLearn);
                if(needToLearn){
                    // 去详情页面
                    MyCourseDetailsActivity.start(activity, courseId,  false, true, memberId,vo);
                }else{
                    // 去未加入页面
                    activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                            .putExtra("id", courseId).putExtra("canEdit", canEdit)
                            .putExtra("tabIndex", tabIndex).putExtra("CourseVo", vo)
                            .putExtra("memberId" ,memberId));
                }
            }
        });

        /*CourseDetails courseDetails = new CourseDetails();
        courseDetails.checkCourseDetails(activity, courseId, memberId, false, "",
                new CourseDetails.OnCheckCourseStatusListener() {
                    @Override
                    public void onCheckSuccess(CourseDetailsVo courseDetailsVo, boolean needToLearn) {
                        if(needToLearn){
                            MyCourseDetailsActivity.start(activity, courseId,  false, true, memberId);
                        }else{
                            activity.startActivity(new Intent(activity, CourseDetailsActivity.class)
                                    .putExtra("id", courseId).putExtra("canEdit", canEdit)
                                    .putExtra("tabIndex", tabIndex).putExtra("CourseVo", vo)
                                    .putExtra("memberId" ,memberId));
                        }
                    }

                    @Override
                    public void onCheckFailed(int code, String message) {

                    }
                });*/
    }

    /**
     * 跳转到课程详情页 仅使用与从我的课程详情页跳转到课程详情
     * @param activity
     * @param courseId
     * @param canEdit
     * @param tabIndex
     * @param isComeFromDetail
     * @param memberId
     */
    public static void start(Activity activity, String courseId, boolean canEdit
            , int tabIndex, boolean isComeFromDetail, String memberId) {
        activity.startActivityForResult(new Intent(activity, CourseDetailsActivity.class)
                .putExtra("id", courseId).putExtra("canEdit", canEdit)
                .putExtra("tabIndex", tabIndex)
                .putExtra("isComeFromDetail", isComeFromDetail)
                .putExtra("memberId" ,memberId), Rs_collect);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        registerBroadcast();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        isSchoolEnter = getIntent().getBooleanExtra(KEY_EXTRA_IS_SCHOOL_ENTER,false);
        isOnlineClassEnter = getIntent().getBooleanExtra(KEY_EXTRA_IS_ONLINE_CLASS_ENTER,false);
        if(getIntent().hasExtra(ACTIVITY_BUNDLE_OBJECT)){
            mCourseDetailParams = (CourseDetailParams) getIntent().getSerializableExtra(ACTIVITY_BUNDLE_OBJECT);
        }else{
            mCourseDetailParams = new CourseDetailParams();
        }
        mCanEdit = getIntent().getBooleanExtra("canEdit",false);
        mCurMemberId = getIntent().getStringExtra("memberId");
        mCommentLayout = (LinearLayout) findViewById(R.id.comment_layout);
        mCommentContent = (EditText) findViewById(R.id.et_comment_content);
        mBtnSend = (TextView) findViewById(R.id.btn_send);

        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.showBottomSplitView(false);
        topBar.setTitleColor(R.color.textLight);
        imageViewCover = (ImageView) findViewById(R.id.cover_iv);
        textViewCourseName = (TextView) findViewById(R.id.course_name_tv);
        textViewCourseProcess = (TextView) findViewById(R.id.course_process);
        textViewOrganName = (TextView) findViewById(R.id.organ_name_tv);
        mBtnIntro = (Button) findViewById(R.id.btn_introduction);
        mSchoolEnter = (TextView) findViewById(R.id.tv_school_enter);
        textViewTeacherName = (TextView) findViewById(R.id.teacher_name_tv);
        ratingBarGrade = (RatingBar) findViewById(R.id.grade_rating_bar);
        ivShare = (ImageView) findViewById(R.id.iv_share);
        textViewGrade = (TextView) findViewById(R.id.grade_tv);
        textViewStrudyNumber = (TextView) findViewById(R.id.study_number_tv);
        textViewePriceTitle = (TextView) findViewById(R.id.price_title_tv);
        textViewCoursePrice = (TextView) findViewById(R.id.course_price);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        scrollView = (ScrollViewEx) findViewById(R.id.scrollview);
        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
        rg_tab_f = (RadioGroup) findViewById(R.id.rg_tab_f);

        mRbLive = (RadioButton) findViewById(R.id.rb_live);
        mRbLiveF = (RadioButton) findViewById(R.id.rb_live_f);

        if(isOnlineClassEnter){
            mRbLive.setVisibility(View.GONE);
            mRbLiveF.setVisibility(View.GONE);
        }

        textViewAddToCart = (TextView) findViewById(R.id.add_to_cart_tv);
        mTvPrice = (TextView) findViewById(R.id.tv_price);
        mTvOriginalPrice = (TextView) findViewById(R.id.tv_original_price);
        textViewPay = (TextView) findViewById(R.id.pay_tv);
        mBtnEnterPay = (TextView) findViewById(R.id.btn_enter_pay);
        courseId = getIntent().getStringExtra("id");
        isComment = getIntent().getBooleanExtra("isComment", false);
        initTabIndex = getIntent().getIntExtra("tabIndex", 0);
        isComeFromDetail = getIntent().getBooleanExtra("isComeFromDetail", false);
        isMyCourse = getIntent().getBooleanExtra("isMyCourse",false);
        isLqExcellent = getIntent().getBooleanExtra("isLqExcellent", false);
        isAuthorized = getIntent().getBooleanExtra("isAuthorized",false);
        initViews();
        initData(false);
    }

    private void initViews() {
        // canEdit true 代表就是孩子身份,可以看课件，做读写单相关的动作
        // canEdit false 代表就是家长身份，有些学习相关的功能，不能使用
        if (!MainApplication.appIsLQMOOC() && !getIntent().getBooleanExtra("canEdit", false)){
            findViewById(R.id.bottom_lay).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_enter_pay).setVisibility(View.GONE);
        }
        //初始化下拉刷新
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                updateData();
            }
        });
        pullToRefreshView.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                /*if(mClassroomFragment.isVisible()){
                    mClassroomFragment.getMore();
                }*/

                if(mOnlineClassFragment.isVisible()){
                    // @date   :2018/6/8 0008 上午 12:13
                    // @func   :V5.7修改直播为在线课堂列表
                    mOnlineClassFragment.getMore();
                }

                if(mTutorialGroupFragment.isVisible()){
                    mTutorialGroupFragment.getMore();
                }

                if (courseCommentFragment.isVisible()) {
                    courseCommentFragment.getMore();
                }
            }
        });
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        topBar.setBack(true);
        topBar.setTranslationBackground(true);
        topBar.setLeftFunctionImage1(R.drawable.ic_back_translate_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(false){
            // 未加入页面 目前没有溢出菜单
            topBar.setRightFunctionImage1(R.drawable.ic_all_classify_small, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 溢出菜单
                    List<PopupMenu.PopupMenuData> items = new ArrayList();
                    if(EmptyUtil.isEmpty(courseVo)) return;
                    if(courseVo.isInClass()){
                        PopupMenu.PopupMenuData data = data = new PopupMenu.PopupMenuData(0, R.string.label_old_in_class,
                                R.string.label_old_in_class);
                        items.add(data);
                    }else{
                        PopupMenu.PopupMenuData data = data = new PopupMenu.PopupMenuData(0, R.string.label_course_in_class,
                                R.string.label_course_in_class);
                        items.add(data);
                    }

                    AdapterView.OnItemClickListener itemClickListener =
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    if (view.getTag() == null) {
                                        return;
                                    }
                                    PopupMenu.PopupMenuData data = (PopupMenu.PopupMenuData) view.getTag();
                                    if (data.getId() == R.string.label_course_in_class) {
                                        // 去指定班级的页面
                                        // 去指定到班级
                                        Intent intent = new Intent();
                                        intent.setClassName(activity.getPackageName(),"com.lqwawa.mooc.select.SchoolClassSelectActivity");
                                        Bundle bundle = new Bundle();
                                        bundle.putString("courseId",courseVo.getCourseId());
                                        intent.putExtras(bundle);
                                        activity.startActivity(intent);
                                    }else{

                                    }
                                }
                            };
                    PopupMenu popupMenu = new PopupMenu(CourseDetailsActivity.this, itemClickListener, items);
                    popupMenu.showAsDropDown(v, v.getWidth(), 0);
                }
            });
        }



        /*if (MainApplication.appIsLQMOOC() || getIntent().getBooleanExtra("canEdit", false)) {
            topBar.setRightFunctionImage1(R.drawable.ic_collect_off, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collect(!collected);
                }
            });
        }*/
        ivShare.setOnClickListener(this);
        textViewPay.setOnClickListener(this);
        mBtnEnterPay.setOnClickListener(this);
        textViewAddToCart.setOnClickListener(this);
        btnReload.setOnClickListener(this);
        if(isSchoolEnter || true){
            mSchoolEnter.setVisibility(View.GONE);
            textViewOrganName.setEnabled(false);
        }

        textViewOrganName.setOnClickListener(this);
        mBtnIntro.setOnClickListener(this);
        mSchoolEnter.setOnClickListener(this);
        int p_width = getWindowManager().getDefaultDisplay().getWidth();
        int p_height = getWindowManager().getDefaultDisplay().getHeight();
        img_width = p_width / 3 - DisplayUtil.dip2px(activity, 20);
        img_height = img_width * 297 / 210;
        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setLoadingDrawableId(R.drawable.default_cover_h)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.default_cover_h)//加载失败后默认显示图片
                .build();
        //findViewById(R.id.fragment_container).setMinimumHeight(p_height);
        LinearLayout.LayoutParams coverLayParams = (LinearLayout.LayoutParams) imageViewCover.getLayoutParams();
        coverLayParams.width = img_width;
        coverLayParams.height = img_height;
        imageViewCover.setLayoutParams(coverLayParams);

        if(android.os.Build.VERSION.SDK_INT  >= 23){
            scrollView.setOnScrollChangeListener(new ScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    onScrollChanged((ScrollView)view, i, i1, i2, i3);
                }
            });
        }else{
            scrollView.setScrollViewListener(this);
        }

        // @date   :2018/4/10 0010 下午 4:26
        // @func   :点击文本框,显示评论对话框
        mCommentContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(null !=courseCommentFragment) courseCommentFragment.comment(data);
                    return true;
                }
                return false;
            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null !=courseCommentFragment) courseCommentFragment.commitComment(data);
            }
        });
    }

    private void initTabAndFragment() {
        //下拉刷新异步回调
        onLoadStatusChangeListener = new OnLoadStatusChangeListener() {
            @Override
            public void onLoadSuccess() {
                loadFailedLayout.setVisibility(View.GONE);
                pullToRefreshView.onHeaderRefreshComplete();
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onLoadFlailed() {
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onHeaderRefreshComplete();
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onLoadFinish(boolean canLoadMore) {
                pullToRefreshView.onHeaderRefreshComplete();
                pullToRefreshView.onFooterRefreshComplete();
                if (courseCommentFragment.isVisible()) {
                    pullToRefreshView.setLoadMoreEnable(canLoadMore);
                }else if(mTutorialGroupFragment.isVisible()){
                    pullToRefreshView.setLoadMoreEnable(canLoadMore);
                }

                CourseDetailsActivity.this.canLoadMore = canLoadMore;
            }

            @Override
            public void onCommitComment() {
                initData(true);
            }
        };
        // introductionFragment = new CourseDetailsItemFragment();
        studyPlanFragment = new CourseDetailsItemFragment();
        courseCommentFragment = new CourseDetailsItemFragment();
        //直播
        // mClassroomFragment = new ClassroomFragment();
        // @date   :2018/6/8 0008 上午 12:14
        // @func   :V5.7 将直播修改为在线课堂列表
        mOnlineClassFragment = OnlineClassListFragment.newInstance(courseId);
        mTutorialGroupFragment = TutorialGroupFragment.newInstance(courseId,mCurMemberId);
        // introductionFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        studyPlanFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        courseCommentFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        // mClassroomFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        mOnlineClassFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        mTutorialGroupFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);

        String id = courseId;
        if (courseVo != null) {
            id = courseVo.getId();
        }

        // 生成参数
        CourseDetailItemParams params1 = new CourseDetailItemParams(false,mCurMemberId,!mCanEdit,id);
        params1.setDataType(CourseDetailItemParams.COURSE_DETAIL_ITEM_INTRODUCTION);
        // 设置课程详情参数
        params1.setCourseParams(mCourseDetailParams);

        // type == 1 课程简介
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable(CourseDetailsItemFragment.FRAGMENT_BUNDLE_OBJECT,params1);

        // introductionFragment.setArguments(bundle1);
        Bundle bundle2 = new Bundle();
        // bundle2.putBoolean(CourseDetailsItemFragment.KEY_EXTRA_ONLINE_TEACHER,isOnlineTeacher);
        bundle2.putSerializable(CourseVo.class.getSimpleName(), courseVo);
        if(getIntent().getExtras().containsKey("CourseVo")){
            CourseVo vo = (CourseVo) getIntent().getSerializableExtra("CourseVo");
            bundle2.putSerializable(CourseVo.class.getSimpleName(), vo);
        }
        // 课程大纲传参
        CourseDetailItemParams params2 = (CourseDetailItemParams) params1.clone();
        params2.setDataType(CourseDetailItemParams.COURSE_DETAIL_ITEM_STUDY_PLAN);
        bundle2.putSerializable(CourseDetailsItemFragment.FRAGMENT_BUNDLE_OBJECT,params2);

        studyPlanFragment.setArguments(bundle2);
        // @date   :2018/6/8 0008 上午 12:20
        // @func   :V5.7将直播换成在线课堂
        /*Bundle bundle6 = new Bundle();
        bundle6.putString("id", id);
        mClassroomFragment.setArguments(bundle6);*/
        Bundle bundle3 = new Bundle();
        // 课程评论
        CourseDetailItemParams params3 = (CourseDetailItemParams) params1.clone();
        params3.setDataType(CourseDetailItemParams.COURSE_DETAIL_ITEM_COURSE_COMMENT);
        params3.setComment(true);
        bundle3.putSerializable(CourseDetailsItemFragment.FRAGMENT_BUNDLE_OBJECT,params3);
        courseCommentFragment.setArguments(bundle3);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // fragmentTransaction.add(R.id.fragment_container, introductionFragment);
        fragmentTransaction.add(R.id.fragment_container, studyPlanFragment);
        // fragmentTransaction.add(R.id.fragment_container, mClassroomFragment);
        // @date   :2018/6/8 0008 上午 12:21
        // @func   :V5.7将直播换成在线课堂

        // @func   :V5.9默认先显示课程大纲
        fragmentTransaction.add(R.id.fragment_container, mOnlineClassFragment);
        fragmentTransaction.add(R.id.fragment_container,mTutorialGroupFragment);
        fragmentTransaction.add(R.id.fragment_container, courseCommentFragment);
        fragmentTransaction.hide(courseCommentFragment);
        fragmentTransaction.show(studyPlanFragment);
        // fragmentTransaction.hide(mClassroomFragment);
        fragmentTransaction.hide(mOnlineClassFragment);
        fragmentTransaction.hide(mTutorialGroupFragment);
        // fragmentTransaction.hide(introductionFragment);
        fragmentTransaction.commit();

        RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rg_tab.getVisibility() == View.VISIBLE && group.getId() == R.id.rg_tab) {
                    FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    // fragmentTransaction.hide(introductionFragment);
                    fragmentTransaction.hide(studyPlanFragment);
                    fragmentTransaction.hide(courseCommentFragment);
                    // @date   :2018/6/8 0008 上午 12:23
                    // @func   :将直播换成在线课堂
                    fragmentTransaction.hide(mOnlineClassFragment);
                    fragmentTransaction.hide(mTutorialGroupFragment);
                    // fragmentTransaction.hide(mClassroomFragment);
                    pullToRefreshView.setLoadMoreEnable(false);
                    /*if (checkedId == R.id.rb_course_introduction) {
                        // fragmentTransaction.show(introductionFragment);
                        rg_tab_f.check(R.id.rb_course_introduction_f);
                    } else */if (checkedId == R.id.rb_study_plan) {
                        fragmentTransaction.show(studyPlanFragment);
                        rg_tab_f.check(R.id.rb_study_plan_f);
                    } else if (checkedId == R.id.rb_live) {
                        // fragmentTransaction.show(mClassroomFragment);
                        // @date   :2018/6/8 0008 上午 12:23
                        // @func   :将直播换成在线课堂
                        fragmentTransaction.show(mOnlineClassFragment);
                        rg_tab_f.check(R.id.rb_live_f);
                    }else if (checkedId == R.id.rb_course_comment) {
                        fragmentTransaction.show(courseCommentFragment);
                        rg_tab_f.check(R.id.rb_course_comment_f);
                        pullToRefreshView.setLoadMoreEnable(canLoadMore);
                    }else if(checkedId == R.id.rb_tutorial_group){
                        fragmentTransaction.show(mTutorialGroupFragment);
                        rg_tab_f.check(R.id.rb_tutorial_group_f);
                        pullToRefreshView.setLoadMoreEnable(canLoadMore);
                    }
                    fragmentTransaction.commitAllowingStateLoss();
                } else if (rg_tab_f.getVisibility() == View.VISIBLE && group.getId() == R.id.rg_tab_f) {
                    FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    // fragmentTransaction.hide(introductionFragment);
                    fragmentTransaction.hide(studyPlanFragment);
                    fragmentTransaction.hide(courseCommentFragment);
                    // @date   :2018/6/8 0008 上午 12:23
                    // @func   :V5.7将直播换成在线课堂
                    // fragmentTransaction.hide(mClassroomFragment);
                    fragmentTransaction.hide(mOnlineClassFragment);
                    fragmentTransaction.hide(mTutorialGroupFragment);
                    pullToRefreshView.setLoadMoreEnable(false);
                    /*if (checkedId == R.id.rb_course_introduction_f) {
                        fragmentTransaction.show(introductionFragment);
                        rg_tab.check(R.id.rb_course_introduction);
                    } else */if (checkedId == R.id.rb_study_plan_f) {
                        fragmentTransaction.show(studyPlanFragment);
                        rg_tab.check(R.id.rb_study_plan);
                    } else if (checkedId == R.id.rb_live_f) {
                        // @date   :2018/6/8 0008 上午 12:24
                        // @func   :V5.7 将直播换成在线课堂
                        // fragmentTransaction.show(mClassroomFragment);
                        fragmentTransaction.show(mOnlineClassFragment);
                        rg_tab.check(R.id.rb_live);
                    }else if (checkedId == R.id.rb_course_comment_f) {
                        fragmentTransaction.show(courseCommentFragment);
                        rg_tab.check(R.id.rb_course_comment);
                        pullToRefreshView.setLoadMoreEnable(canLoadMore);
                    }else if(checkedId == R.id.rb_tutorial_group_f){
                        fragmentTransaction.show(mTutorialGroupFragment);
                        rg_tab.check(R.id.rb_tutorial_group);
                        pullToRefreshView.setLoadMoreEnable(canLoadMore);
                    }
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        };
        rg_tab.setOnCheckedChangeListener(listener);
        rg_tab_f.setOnCheckedChangeListener(listener);
    }

    //scrollview 滚动条位置变动时调整顶部工具条的状态及tab
    private int oldy = 0;
    @Override
    public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oy) {
        if(y > 1000){
            return;
        }
        if (y < 0) {
            y = 0;
        }
        if (y > 0xff && oldy <= 0xff) {
            topBar.setBackgroundColor(Color.parseColor("#1A1A1A"));
            topBar.setTranslationBackground(false);
            // 不显示底部阴影
            topBar.showBottomSplitView(false);
        } else if (y <= 0xff) {
            String ap = Integer.toHexString(y);
            if (ap.length() == 1) {
                ap = "0" + ap;
            }
            String colorString = "#" + ap + "1A1A1A";
            LogUtil.d(TAG, colorString);
            topBar.setBackgroundColor(Color.parseColor(colorString));
            topBar.showBottomSplitView(false);
            // 获取到左边返回,显示透明背景
            ImageView leftFunctionImage1 = (ImageView) topBar.findViewById(R.id.left_function1_image);
            if(!EmptyUtil.isEmpty(leftFunctionImage1)){
                leftFunctionImage1.setBackground(activity.getResources().getDrawable(
                        R.drawable.com_circle_black_trans_bg_selecter));
            }
        }
        if(courseVo != null) {
            if (oldy == 0 && y > 0) {
                topBar.setTitle(courseVo.getName());
            }
        }
        if(y == 0){
            topBar.setTitle("");
        }

        int tabTop = rg_tab.getTop() - rg_tab.getHeight() - DisplayUtil.dip2px(activity, 8);
        if (y > tabTop && oldy <= tabTop) {
            rg_tab_f.setVisibility(View.VISIBLE);
            rg_tab.setVisibility(View.INVISIBLE);
        } else if (y <= tabTop && oldy > tabTop) {
            rg_tab_f.setVisibility(View.GONE);
            rg_tab.setVisibility(View.VISIBLE);
        }
        oldy = y;
    }

    private void updateData() {
        /*if (introductionFragment.isVisible()) {
            introductionFragment.updateData();
        }*/
        if (studyPlanFragment.isVisible()) {
            studyPlanFragment.updateData();
        }

        if (courseCommentFragment.isVisible()) {
            courseCommentFragment.updateData();
        }

        // @date   :2018/6/8 0008 上午 12:25
        // @func   :V5.7 将直播换成在线课堂
        /*if(mClassroomFragment.isVisible()){
            mClassroomFragment.updateData();
        }*/
        if (mOnlineClassFragment.isVisible()) {
            mOnlineClassFragment.onHeaderRefresh();
        }

        if(mTutorialGroupFragment.isVisible()){
            mTutorialGroupFragment.onHeaderRefresh();
        }
    }

    /**
     * 加载课程信息
     * @param refresh 是否是刷新数据
     */
    private void initData(final boolean refresh) {
        String token = UserHelper.getUserId();
        if(!mCanEdit && isMyCourse){
            token = mCurMemberId;
        }

        int dataType = CourseDetailItemParams.COURSE_DETAIL_ITEM_INTRODUCTION;
        String schoolIds = null;
        if(isLqExcellent){
            //来自LQ精品学程
            schoolIds = getIntent().getStringExtra("schoolId");
        }else if(UserHelper.isLogin() && mCanEdit) {
            schoolIds =  UserHelper.getUserInfo().getSchoolIds();
        }

        LQCourseHelper.requestCourseDetailByCourseId(token, courseId,schoolIds, dataType, 0, AppConfig.PAGE_SIZE, new DataSource.Callback<CourseDetailsVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(CourseDetailsVo courseDetailsVo) {
                CourseDetailsActivity.this.courseDetailsVo = courseDetailsVo;
                if(courseDetailsVo.isSucceed()){
                    getIntent().putExtra("isBuy", courseDetailsVo.isIsBuy());
                    getIntent().putExtra("isExpire", courseDetailsVo.isIsExpire());
                    getIntent().putExtra("isJoin", courseDetailsVo.isIsJoin());
                    collected = courseDetailsVo.isIsCollect();
                    courseScore = courseDetailsVo.getCourseScore();
                    List<CourseVo> voList = courseDetailsVo.getCourse();
                    if (voList != null && voList.size() > 0) {
                        courseVo = voList.get(0);
                        getIntent().putExtra("payType", courseVo.getPayType());
                        // 刷新数据不initTab数据
                        if(!refresh){
                            initTabAndFragment();
                        }
                        // TODO 没办法,这里只能启动延时任务了。因为历史原因,无法将课程传到CourseDetailsFragment,后续会改进
                        UIUtil.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                courseObservable.triggerObservers(courseVo);
                            }
                        },1000);
                        updateView();


                        // 获取用户的机构关注状态
                        /*String userId = UserHelper.getUserId();
                        String schoolId = courseVo.getOrganId();
                        SchoolHelper.requestSchoolInfo(userId, schoolId, new DataSource.Callback<SchoolInfoEntity>() {
                            @Override
                            public void onDataNotAvailable(int strRes) {

                            }

                            @Override
                            public void onDataLoaded(SchoolInfoEntity entity) {
                                CourseDetailsActivity.this.mSchoolEntity = entity;
                                // mSchoolEnter.setTextColor(entity.getState() != 0 ? UIUtil.getColor(R.color.colorGary) : UIUtil.getColor(R.color.colorAccent));
                                // mSchoolEnter.setBackgroundResource(entity.getState() != 0 ? R.drawable.bg_rectangle_gary_radius_10 : R.drawable.bg_rectangle_accent_radius_10);
                                mSchoolEnter.setTextColor(UIUtil.getColor(R.color.colorAccent));
                                mSchoolEnter.setBackgroundResource(R.drawable.bg_rectangle_accent_radius_10);
                                mSchoolEnter.setText(getString(R.string.label_enter_school));
                                *//*if(entity.getState() != 0){
                                    // 关注状态
                                    mSchoolEnter.setText(getString(R.string.label_yet_attention));
                                }else{
                                    mSchoolEnter.setText(getString(R.string.label_add_attention));
                                }*//*
                            }
                        });*/
                    }
                }

            }
        });
    }

    private void updateView() {
        if (courseVo != null && courseDetailsVo != null) {

            boolean tutorialMode = SPUtil.getInstance().getBoolean(SharedConstant.KEY_APPLICATION_MODE);

            if (isMyCourse && /*mCourseDetailParams.isClassCourseEnter() && */courseDetailsVo.isIsExpire()) {
                // 班级学程入口进入
                // 课程权限已到期
                CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                builder.setMessage(activity.getResources().getString(R.string.course_out_permissions));
                builder.setPositiveButton(activity.getResources().getString(R.string.i_know),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        // activity.finish();
                    }
                });

                builder.create().show();
                // 班级学程入口，过期了，只是提醒
                // return;
            }


            /**
             * Edit by ChenXin at 2017/10/17
             * For 资源浏览时带入机构id
             */
            getIntent().putExtra("schoolId", courseVo.getOrganId());

            float score = courseVo.getCommentNum() == 0 ? 0 :
                    1.0f * courseVo.getTotalScore() / courseVo.getCommentNum();
            ratingBarGrade.setRating(score);
            x.image().bind(imageViewCover,
                    courseVo.getThumbnailUrl().trim(),
                    imageOptions);
            textViewCourseName.setText(courseVo.getName());
            textViewCourseProcess.setText(getString(R.string.week_all));
            SpannableStringBuilder builder = new SpannableStringBuilder("" + courseVo.getWeekCount());
            ForegroundColorSpan greenSpan = new ForegroundColorSpan(
                    activity.getResources().getColor(R.color.com_text_green));
            builder.setSpan(greenSpan, 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewCourseProcess.append(builder);
            textViewCourseProcess.append(getChapterNumString(courseVo.getFirstTitle()));
            SpannableStringBuilder processStr = new SpannableStringBuilder("");
            processStr.append("(");
            String courseStatus = "";
            switch (courseVo.getProgressStatus()){
                default:
                case 0:
                    courseStatus = getResources().getString(R.string.course_status_0);
                    break;
                case 1:
                    courseStatus = getResources().getString(R.string.course_status_1);
                    break;
                case 2:
                    courseStatus = getResources().getString(R.string.course_status_2);
                    break;
            }
            processStr.append(courseStatus);
            if(courseVo.getProgressStatus() == 1){
                processStr.append(" ");
                processStr.append(String.format(getResources().getString(R.string.update_to_the),
                        "" + courseVo.getProgress()));
            }
            processStr.append(")");
            greenSpan = new ForegroundColorSpan(
                    activity.getResources().getColor(R.color.com_text_green));
            processStr.setSpan(greenSpan, 0, processStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewCourseProcess.append(processStr);
            textViewOrganName.setText(courseVo.getOrganName());
            textViewTeacherName.setText(courseVo.getTeachersName());
            textViewGrade.setText("(" + courseVo.getCommentNum() + ")");
            textViewStrudyNumber.setText(String.format(getText(R.string.some_study).toString(),courseVo.getStudentNum()));
            if (courseVo.getPrice() > 0) {
                textViewePriceTitle.setVisibility(View.VISIBLE);
                textViewCoursePrice.setText("¥" + courseVo.getPrice());
            } else {
                textViewePriceTitle.setVisibility(View.GONE);
                textViewCoursePrice.setText(activity.getResources().getString(R.string.free));
            }



            /*收藏按钮隐藏
            if (MainApplication.appIsLQMOOC() || getIntent().getBooleanExtra("canEdit", false)) {
                topBar.setRightFunctionImage1(
                        collected ? R.drawable.ic_collection_on :
                                R.drawable.ic_collect_off);
            }*/
            if(courseVo.getPrice() == 0){
                // 免费
                mTvOriginalPrice.setVisibility(View.GONE);
                StringUtil.fillSafeTextView(mTvPrice,UIUtil.getString(R.string.label_class_gratis));
            }else{
                // 收费
                StringUtil.fillSafeTextView(mTvPrice,Common.Constance.MOOC_MONEY_MARK + courseVo.getPrice());
                if(courseVo.isDiscount()){
                    // 有打折价
                    mTvOriginalPrice.setVisibility(View.VISIBLE);
                    // 中划线
                    mTvOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    StringUtil.fillSafeTextView(mTvOriginalPrice,Common.Constance.MOOC_MONEY_MARK + courseVo.getOriginalPrice());
                }else{
                    // 无打折价
                    mTvOriginalPrice.setVisibility(View.GONE);
                }
            }

            if (courseVo.getPrice() == 0 || isLqExcellent) {//免费
                if (courseDetailsVo.isIsJoin()) {
                    if(!tutorialMode){
                        textViewPay.setText(getResources().getString(R.string.to_learn));
                    }else{
                        textViewPay.setText(getResources().getString(R.string.label_apply_to_be_tutorial));
                    }
                    mBtnEnterPay.setText(UIUtil.getString(R.string.to_learn));
                    textViewPay.setCompoundDrawables(null, null, null, null);
                } else {
                    if(!tutorialMode){
                        textViewPay.setText(getResources().getString(R.string.to_join));
                    }else{
                        textViewPay.setText(getResources().getString(R.string.label_apply_to_be_tutorial));
                    }
                    mBtnEnterPay.setText(UIUtil.getString(R.string.to_join));
                    textViewPay.setCompoundDrawables(null, null, null, null);
                }
            } else {
                if (courseDetailsVo.isIsBuy() && !courseDetailsVo.isIsExpire()) {
                    if(courseDetailsVo.isIsJoin()){
                        if(!tutorialMode){
                            textViewPay.setText(getResources().getString(R.string.to_learn));
                        }else{
                            textViewPay.setText(getResources().getString(R.string.label_apply_to_be_tutorial));
                        }
                        mBtnEnterPay.setText(UIUtil.getString(R.string.to_learn));
                    }else{
                        if(!tutorialMode){
                            textViewPay.setText(getResources().getString(R.string.to_join));
                        }else{
                            textViewPay.setText(getResources().getString(R.string.label_apply_to_be_tutorial));
                        }
                        mBtnEnterPay.setText(UIUtil.getString(R.string.to_join));
                    }
                    textViewPay.setCompoundDrawables(null, null, null, null);
                } else {
                    if(!tutorialMode){
                        textViewPay.setText(getResources().getString(R.string.buy_immediately));
                        textViewPay.setCompoundDrawables(null,
                                getResources().getDrawable(R.drawable.ic_pay), null, null);
                    }else{
                        textViewPay.setText(getResources().getString(R.string.label_apply_to_be_tutorial));
                        textViewPay.setCompoundDrawables(null, null, null, null);
                    }
                    mBtnEnterPay.setText(UIUtil.getString(R.string.buy_immediately));
                }
            }

            if(mCourseDetailParams.isOrganCourseEnter()){
                // 机构学程的入口进来的
                // 并且是收费的
                if(courseVo.getPrice() != 0 && mCourseDetailParams.isAuthorized()){
                    // 并且已经授权的
                    // 设置文本
                    // 并且设置Tag
                    if(!tutorialMode) {
                        mBtnEnterPay.setTag(1);
                        textViewPay.setTag(1);
                        textViewPay.setText(getString(R.string.label_join_course));
                        mBtnEnterPay.setText(getString(R.string.label_join_course));
                    }
                }
            }

            textViewPay.setVisibility(View.VISIBLE);
            // mBtnEnterPay.setVisibility(View.VISIBLE);
            if (initTabIndex == 1) {
                rg_tab.check(R.id.rb_study_plan);
                initTabIndex = 0;
            }
            if (isComment || initTabIndex == 2) {
                rg_tab.check(R.id.rb_course_comment);
                initTabIndex = 0;
            }

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.reload_bt) {
            loadFailedLayout.setVisibility(View.GONE);
            pullToRefreshView.showRefresh();
            initData(false);
        }else if(id == R.id.tv_school_enter || id == R.id.organ_name_tv){
            // 点击关注
            // 进入机构主页
            if(EmptyUtil.isNotEmpty(courseVo)){
                /*Intent intent=new Intent();
                intent.putExtra("isOpenSchoolSpace",true);
                intent.putExtra("schoolId",courseVo.getOrganId());
                intent.setClassName(getPackageName(),
                        "com.galaxyschool.app.wawaschool.OpenCourseHelpActivity");
                startActivity(intent);*/
                if (!UserHelper.isLogin()) {
                    LoginHelper.enterLogin(activity);
                    return;
                }


                if(EmptyUtil.isEmpty(mSchoolEntity)){
                    // 已经进入机构
                    return;
                }

                if(mSchoolEntity.hasJoinedSchool() || mSchoolEntity.hasSubscribed()){
                    // 已关注
                    sendSchoolSpaceRefreshBroadcast();
                }else{
                    // 如果没有关注 +关注
                    SchoolHelper.requestSubscribeSchool(courseVo.getOrganId(), new DataSource.Callback<Object>() {
                        @Override
                        public void onDataNotAvailable(int strRes) {
                            UIUtil.showToastSafe(strRes);
                        }

                        @Override
                        public void onDataLoaded(Object object) {
                            // 关注成功,发送广播,刷新UI
                            sendSchoolSpaceRefreshBroadcast();
                        }
                    });
                }

            }
        } else if (id == R.id.pay_tv || id == R.id.btn_enter_pay) {
            if(ButtonUtils.isFastDoubleClick()) {
                return;
            }
            if (!UserHelper.isLogin()) {
                LoginHelper.enterLogin(activity);
            } else {
                boolean tutorialMode = SPUtil.getInstance().getBoolean(SharedConstant.KEY_APPLICATION_MODE);
                if(tutorialMode){
                    // TODO 申请成为课程的帮辅老师
                    TutorialCourseApplyForFragment.show(
                            getSupportFragmentManager(),
                            mCurMemberId, courseId,
                            courseDetailsVo.getIsOrganTutorStatus(),new CourseApplyForNavigator() {
                        @Override
                        public void onCourseTutorEnter(boolean isCourseTutor) {
                            if(isCourseTutor){
                                toJoinCourseDetailsActivity();
                            }
                        }
                    });
                    return;
                }

                if(id == R.id.btn_enter_pay || id == R.id.pay_tv){
                    if(EmptyUtil.isNotEmpty(view.getTag()) &&
                            view.getTag() instanceof Integer){

                        int tag = (int) view.getTag();
                        if(tag == 1){
                            if(EmptyUtil.isNotEmpty(mCourseDetailParams)){
                                String schoolId = mCourseDetailParams.getSchoolId();
                                String classId = mCourseDetailParams.getClassId();
                                CourseHelper.requestJoinInCourse(mCurMemberId, courseId, schoolId, classId, new DataSource.SucceedCallback<Boolean>() {

                                    @Override
                                    public void onDataLoaded(Boolean aBoolean) {
                                        if(aBoolean){
                                            EventBus.getDefault().post(new MessageEvent(EventConstant.TRIGGER_UPDATE_COURSE));
                                            mCourseDetailParams.buildOrganJoinState(true);
                                            MyCourseDetailsActivity.start(isAuthorized,mCourseDetailParams,isSchoolEnter,activity, courseId,  false, true, mCurMemberId);
                                            finish();
                                        }
                                    }
                                });
                            }
                            return;
                        }
                    }
                }


                if (courseVo != null) {
                    if (courseVo.getPrice() == 0 && courseDetailsVo != null || isLqExcellent) {//免费
                        if (courseDetailsVo.isIsJoin()) {
                            if (isComeFromDetail) {
                                finish();
                            } else {
                                MyCourseDetailsActivity.start(activity, courseId, true, true,
                                        activity.getIntent().getStringExtra("memberId"),isSchoolEnter,isOnlineClassEnter,mCourseDetailParams);
                            }
                        } else {//免费的没有参与 立即参与
                            if(isSelfCourse(courseVo)){
                                //当前用户是课程的创建者或者讲师
                                ToastUtil.showToast(activity, activity.getResources()
                                        .getString(R.string.join_self_course_tip));
                            }else {
                                join(false);
                            }
                        }
                    } else {
                        if (courseDetailsVo.isIsBuy() && !courseDetailsVo.isIsExpire()) {
                            if (isComeFromDetail) {
                                finish();
                            } else {
                                if (courseDetailsVo.isIsJoin()) {
                                    // MyCourseDetailsActivity.start(activity, courseId, true, true, getIntent().getStringExtra("memberId"));
                                    MyCourseDetailsActivity.start(activity, courseId,  false, true, getIntent().getStringExtra("memberId"),isSchoolEnter,isOnlineClassEnter,mCourseDetailParams);
                                } else {
                                    join(false);
                                    //MyCourseDetailsActivity.start(activity, courseId, true, getIntent().getStringExtra("memberId"));
                                }
                            }
                        } else {
                            if(isSelfCourse(courseVo)){
                                //当前用户是课程的创建者或者讲师
                                ToastUtil.showToast(activity, activity.getResources()
                                        .getString(R.string.buy_self_course_tip));
                            }else {
                                // 购买课程
                                int intCourseId = Integer.parseInt(courseId);
                                if(!mCanEdit){
                                    // 家长身份
                                    PayCourseDialogFragment.show(getSupportFragmentManager(),courseVo,null,true,mCurMemberId,intCourseId,PayCourseDialogFragment.TYPE_COURSE,this);
                                }else{
                                    // 普通身份
                                    PayCourseDialogFragment.show(getSupportFragmentManager(),courseVo,null,intCourseId,PayCourseDialogFragment.TYPE_COURSE,this);
                                }

                                // ConfirmOrderActivity.start(activity, courseVo);
                                // 给自己买
                                // String memberId = UserHelper.getUserId();
                                // LQCourseOrderActivity.show(activity,courseVo,courseVo.getOrganId(),memberId);
                                // LQCourseOrderActivity.show(activity,"19337");
                            }
                        }
                    }
                }
            }
        } else if (id == R.id.add_to_cart_tv) {
            if (!UserHelper.isLogin()) {
                LoginHelper.enterLogin(activity);
            }
        } else if( id == R.id.btn_introduction){
            // 简介
            if(EmptyUtil.isEmpty(courseVo)) return;
            CourseDetailItemParams params = new CourseDetailItemParams(false,mCurMemberId,!mCanEdit,courseId);
            params.setDataType(CourseDetailItemParams.COURSE_DETAIL_ITEM_INTRODUCTION);
            CourseIntroductionActivity.show(this,params);
        }else if(id == R.id.iv_share){
            // 分享
            if(EmptyUtil.isEmpty(courseVo)) return;
            StringBuilder titleBuilder = new StringBuilder();
            StringBuilder descriptionBuilder = new StringBuilder();
            String title = courseVo.getName();
            String description = courseVo.getOrganName();
            titleBuilder.append(UIUtil.getString(R.string.label_share_course_title));
            titleBuilder.append(title);
            // V5.13取消老师信息的分享
            /*String teachers = courseVo.getTeachersName();
            if(EmptyUtil.isNotEmpty(teachers) && teachers.length() > 7){
                teachers = teachers.substring(0,7) + "...";
            }
            descriptionBuilder.append(teachers + "\n");*/
            if(courseVo.getPrice() == 0){
                descriptionBuilder.append(UIUtil.getString(R.string.label_class_gratis) + "\n");
            }else{
                descriptionBuilder.append(Common.Constance.MOOC_MONEY_MARK + " " + courseVo.getPrice() + "\n");
            }
            float score = courseVo.getCommentNum() == 0 ? 0 :
                    1.0f * courseVo.getTotalScore() / courseVo.getCommentNum();
            for(int index = 0; index < Math.ceil(score);index++){
                descriptionBuilder.append("\u2B50");
            }

            final String thumbnailUrl = courseVo.getThumbnailUrl();
            final String url = AppConfig.ServerUrl.CourseDetailShareUrl.replace("{id}", courseVo.getId());
            share(titleBuilder.toString(),descriptionBuilder.toString(),thumbnailUrl,url);
        }
    }

    @Override
    public void onChoiceConfirm(@NonNull String curMemberId) {
        // 弹框购买选中
        // UIUtil.showToastSafe(curMemberId);
        // 判断这个Id是否是老师
        if(UserHelper.checkCourseAuthorWithUserId(curMemberId,courseVo)){
            // 老师身份
            UIUtil.showToastSafe(R.string.label_course_buy_warning);
            return;
        }
        LQCourseOrderActivity.show(activity,courseVo,courseVo.getOrganId(),curMemberId);
    }

    /**
     * 课程分享
     * @param title 标题
     * @param description 描述
     * @param thumbnailUrl 缩略图
     * @param url 分享地址
     */
    public void share(String title,String description,String thumbnailUrl,String url) {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(title);
        shareInfo.setContent(description);
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;

        if (!TextUtils.isEmpty(thumbnailUrl)) {
            umImage = new UMImage(activity, thumbnailUrl);
        } else {
            umImage = new UMImage(activity, R.drawable.default_cover);
        }

        shareInfo.setuMediaObject(umImage);
        BaseShareUtils utils = new BaseShareUtils(activity);
        utils.share(activity.getWindow().getDecorView(),shareInfo);

    }

    /**
     * 发送一个去空中学校并且刷新的广播
     */
    private void sendSchoolSpaceRefreshBroadcast(){
        //关注/取消关注成功后，向校园空间发广播
        Intent broadIntent = new Intent();
        broadIntent.setAction("action_change_lqCourse_tab");
        broadIntent.putExtra("schoolId",courseVo.getOrganId());
        activity.sendBroadcast(broadIntent);
    }

    private boolean isSelfCourse(CourseVo courseVo){
        return UserHelper.isLogin()
                && (TextUtils.equals(courseVo.getCreateId(), UserHelper.getUserId())
                || (StringUtils.isValidString(courseVo.getTeachersId())
                && courseVo.getTeachersId().contains(UserHelper.getUserId())));
    }

    private void join(final boolean isBackground) {
        if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
            LoginHelper.enterLogin(activity);
            return;
        }
        if(courseVo.getPayType() == 1
                && courseDetailsVo.isIsBuy()
                && !courseDetailsVo.isIsJoin()){//LQ学程里的收费课程 购买后又退出课程了
            doRejoin();
            return;
        }
        RequestVo requestVo = new RequestVo();
        int type = 0;
        if(getIntent().getBooleanExtra("isLqExcellent", false)
                && courseVo.getPayType() == 1
                && !courseDetailsVo.isIsBuy()){//来自LQ精品学程
            type = 3;
            requestVo.addParams("shcoolId", getIntent().getStringExtra("schoolId"));
        }
        requestVo.addParams("type", type);
        requestVo.addParams("courseId", courseId);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.joinInCourse + requestVo.getParams());

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.joinInCourse));
                    if (!isBackground) {
                        ToastUtil.showToast(activity, (getResources().getString(R.string.join_success)));
                    }

                    toJoinCourseDetailsActivity();
                } else {
                    if (!isBackground) {
                        ToastUtil.showToast(activity, (getResources().getString(R.string.join_failed))
                                + result.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "收藏失败:" + throwable.getMessage());
                if (!isBackground) {
                    ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
                }
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 去已加入课程详情页面
     */
    private void toJoinCourseDetailsActivity(){
        // @date   :2018/4/12 0012 上午 10:52
        // @func   :没有必要再更新数据
        // initData();
        // courseCommentFragment.updateData();
        // @date   :2018/4/11 0011 下午 2:43
        // @func   :直接去 我的课程详情页
        // 先把支付去学习隐藏
        MyCourseDetailsActivity.start(activity, courseId, true, true,
                activity.getIntent().getStringExtra("memberId"),isSchoolEnter,isOnlineClassEnter,mCourseDetailParams);
        // 当前页面 finish掉
        finish();
    }

    private void doRejoin(){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.reJoinInCourse + requestVo.getParams());

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    // @date   :2018/4/12 0012 上午 10:55
                    // @func   :v5.5取消去学习页面的显示
                    sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.joinInCourse));
                    ToastUtil.showToast(activity, (getResources().getString(R.string.join_success)));
                    // initData();
                    // courseCommentFragment.updateData();
                    toJoinCourseDetailsActivity();
                }else{
                    ToastUtil.showToast(activity, (getResources().getString(R.string.join_failed))
                            + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void collect(final boolean isCollect) {
        if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
            LoginHelper.enterLogin(activity);
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("id", courseId);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.collectCourse + requestVo.getParams());
        if (!isCollect) {
            params =
                    new RequestParams(AppConfig.ServerUrl.deleteCollectCourse + requestVo.getParams());
        }

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity, (isCollect ? ""
                            : getResources().getString(R.string.cancel))
                            + getResources().getString(R.string.collect)
                            + getResources().getString(R.string.success)
                            + "!");
                    setResult(Activity.RESULT_OK);
                    collected = isCollect;
                    /*if (MainApplication.appIsLQMOOC()
                            || getIntent().getBooleanExtra("canEdit", false)) {
                        topBar.setRightFunctionImage1(
                                collected ? R.drawable.ic_collection_on :
                                        R.drawable.ic_collect_off);
                    }*/
                } else {
                    ToastUtil.showToast(activity, (isCollect ? ""
                            : getResources().getString(R.string.cancel))
                            + getResources().getString(R.string.collect)
                            + getResources().getString(R.string.failed)
                            + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "收藏失败:" + throwable.getMessage());
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginHelper.RS_LOGIN) {
            if(UserHelper.isLogin()) {
                // 重新进入该页面
                CourseDetailsActivity.start(this,courseVo.getId(), true, UserHelper.getUserId());
                finish();
            }
        } else if (requestCode == CourseDetailsActivity.Rs_collect && resultCode == Activity.RESULT_OK) {
            initData(true);
        } else if (requestCode == ConfirmOrderActivity.Rc_pay){
            //从支付界面返回则刷新数据
            initData(true);
            studyPlanFragment.updateData();
        }
    }

    public String getChapterNumString(String chapterName){
        if (languageIsEnglish()){
            if (chapterName.equals("单元")){
                return  "Unit";
            }else if(chapterName.equals("章")){
                return  "Chapter";
            }else if(chapterName.equals("周")){
                return  "Week";
            }
        }
        return chapterName ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void commitComment() {
        // 已加入课程需要关注的信息
    }

    @Override
    public void courseCommentVisible() {
        findViewById(R.id.bottom_lay).setVisibility(View.GONE);
        // 评论区域显示
        mCommentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void otherFragmentVisible() {
        // V5.9底部不显示购买
        findViewById(R.id.bottom_lay).setVisibility(View.VISIBLE);
        // 评论区域隐藏
        mCommentLayout.setVisibility(View.GONE);
        if (!MainApplication.appIsLQMOOC() && !getIntent().getBooleanExtra("canEdit", false)){
            findViewById(R.id.bottom_lay).setVisibility(View.VISIBLE);
        }
    }

    // @date   :2018/4/10 0010 下午 5:48
    // @func   :保存评论数据
    private CommentDialog.CommentData data;

    @Override
    public void setContent(CommentDialog.CommentData data) {
        this.mCommentContent.setText(data.getContent());
        this.data = data;
    }

    @Override
    public void clearContent() {
        this.mCommentContent.getText().clear();
        this.data = null;
    }

    // 创建课程信息的观察者对象
    private CourseVoObservable courseObservable = new CourseVoObservable();

    /**
     * 返回课程信息的观察者对象
     * @return
     */
    @Override
    public Observable getCourseObservable(){
        return courseObservable;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event){
        if(EventWrapper.isMatch(event, EventConstant.APPOINT_COURSE_IN_CLASS_EVENT)){
            // 刷新UI
            courseVo.setInClass(true);
        }
    }

    /**
     * 注册广播
     */
    private void registerBroadcast(){
        LocalBroadcastManager mManager = LocalBroadcastManager.getInstance(UIUtil.getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LQWAWA_PAY_RESULT_ACTION);
        mManager.registerReceiver(mReceiver,intentFilter);
    }

    /**
     * 取消注册广播
     */
    private void unRegisterBroadcast() {
        LocalBroadcastManager mManager = LocalBroadcastManager.getInstance(UIUtil.getContext());
        mManager.unregisterReceiver(mReceiver);
    }


    /**
     * 购买的课程,收到购买成功的广播,进入到课程详情页
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(LQWAWA_PAY_RESULT_ACTION.equals(action)){
                // 收到收费课程购买成功的回调
                toJoinCourseDetailsActivity();
            }
        }
    };
}
