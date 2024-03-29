package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.LogUtil;
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
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.CourseRateEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseBindClassEntity;
import com.lqwawa.intleducation.factory.data.entity.course.CourseResourceEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.intro.CourseIntroductionActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails.CourseDetailItemParams;
import com.lqwawa.intleducation.module.discovery.ui.navigator.CourseDetailsNavigator;
import com.lqwawa.intleducation.module.discovery.ui.observable.CourseVoObservable;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.NoticeVo;
import com.lqwawa.intleducation.module.login.ui.LoginActivity;
import com.lqwawa.intleducation.module.onclass.OnlineClassListFragment;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.tutorial.assistance.RelatedAssistanceFragment;
import com.lqwawa.intleducation.module.tutorial.course.TutorialGroupFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.ui.course.notice.CourseNoticeListActivity;
import com.oosic.apps.share.BaseShareUtils;
import com.oosic.apps.share.ShareInfo;
import com.osastudio.common.utils.XImageLoader;
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
 * 我的课程详情
 */
public class MyCourseDetailsActivity extends MyBaseFragmentActivity
        implements View.OnClickListener,
        ScrollViewEx.ScrollViewListener,
        CourseDetailsNavigator {
    public static final String KEY_IS_FROM_MY_COURSE = "isFromMyCourse";

    private static final String KEY_EXTRA_IS_SCHOOL_ENTER = "KEY_EXTRA_IS_SCHOOL_ENTER";
    // 是不是从在线课堂班级进入的
    private static final String KEY_EXTRA_IS_ONLINE_CLASS_ENTER = "KEY_EXTRA_IS_ONLINE_CLASS_ENTER";
    //是不是从我的订单 去学习进入
    private static final String KEY_ORDER_LIST_ENTER = "KEY_ORDER_LIST_ENTER";
    private static final String TAG = "CourseDetailsActivity";
    private TopBar topBar;
    private ImageView imageViewCover;
    private LinearLayout layCourseInfoRoot;
    //课程名
    private TextView textViewCourseName;
    //课程进度
    private TextView textViewCourseProcess;
    //机构名
    private TextView textViewOrganName;
    // 简介
    private Button mBtnIntro;
    //机构信息入口
    private TextView mSchoolEnter;
    //老师名
    private TextView textViewTeacherName;

    private RelativeLayout loadFailedLayout;
    private Button btnReload;
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

    private PullToRefreshView pullToRefreshView;

    private ScrollViewEx scrollView;

    private TextView textViewLiveTimetable;//课程表按钮

    private LinearLayout mCommentLayout;
    // 评论内容
    private EditText mCommentContent;
    // 发送评论
    private TextView mBtnSend;

    private RadioGroup rg_tab;
    private RadioGroup rg_tab_f;
    private String id;
    // 课程Id
    private String mCourseId;
    private int initTabIndex = 0;
    private boolean collected = false;
    private int courseScore;
    private CourseVo courseVo;
    private CourseDetailsVo courseDetailsVo;

    private boolean isOnlineCounselor;
    // 传入的memberId if memberId = UserHelper.getUserId() 孩子身份
    //是否是精品学程
    private boolean isLqExcellent = false;
    private String mCurMemberId;
    // true 学生身份进入
    private boolean mCanEdit;

    private int img_width;
    private int img_height;
    private ImageOptions imageOptions;
    // 课程评价可以下拉刷新
    private boolean[] canLoadMore = new boolean[]{false, false, false, false, false, false, false};
    // 是否加载更多的状态
    private boolean mCanLoadMore;
    private boolean isComeFromDetail = false;//是否是从首页的课程详情跳转过来的
    /**
     * V5.5 我的课程详情页 将公告换成课程介绍
     */
    // CourseDetailsItemFragment introductionFragment;
    CourseDetailsItemFragment studyPlanFragment;
    // @date   :2018/4/10 0010 下午 4:36
    // @func   :评分标准改成为课程评价,评分标准迁移到课程介绍里
//     ScoringCriteriaFragment scoringCriteriaListFragment;
    CourseDetailsItemFragment courseCommentFragment;
    ExamListFragment homeworkListFragment;
    ExamListFragment examListFragment;
    OnLoadStatusChangeListener onLoadStatusChangeListener;
    // @date   :2018/6/8 0008 上午 1:01
    // @func   :V5.7 直播修改为在线课堂
    // private ClassroomFragment mClassroomFragment;
    private OnlineClassListFragment mOnlineClassFragment;
    //关联教辅
    private RelatedAssistanceFragment mAssistanceFragment;
    // 帮辅群
    private TutorialGroupFragment mTutorialGroupFragment;

    // 学习进度容器
    private LinearLayout mProgressLayout;
    // 学习进度 节显示
    private ProgressBar mLearnRate;
    // 学习进度 百分比显示
    private TextView mLearnPercent;

    // 公告容器
    private ViewGroup mNoticeContainer;
    // 公告文本
    private TextView mNoticeContent;

    private String memberId;
    private boolean canEdit;

    private boolean isSchoolEnter;
    private boolean isOnlineClassEnter;
    // 课程详情参数
    private CourseDetailParams mCourseDetailParams;
    private boolean isOnlineTeacher;
    private SchoolInfoEntity mSchoolEntity;
    private boolean isFromScan;

    // 在线课堂Tab
    private RadioButton mRbLive, mRbLiveF;
    //三习教案tab
    private RadioButton mRbStudy, mRbStudyF;
    //教案评价
    private RadioButton mRbComment,mRbCommentF;
    public static final int RESOURCE_PLAY_COMPLETED_REQUEST_CODE = 168;

    //播放列表
    private Button mBtnPlayList;
    private LinearLayout mLLPlayList;
    private List<CourseResourceEntity> playListVo;

    public static void start(Activity activity, String id, boolean canEdit, String memberId, String schoolId, CourseDetailParams params) {
        activity.startActivity(new Intent(activity, MyCourseDetailsActivity.class)
                .putExtra("id", id)
                .putExtra("canEdit", canEdit)
                .putExtra("memberId", memberId)
                .putExtra("SchoolId", schoolId)
                .putExtra(KEY_IS_FROM_MY_COURSE, true)
                .putExtra(ACTIVITY_BUNDLE_OBJECT, params));
    }

    /**
     * @param activity
     * @param id
     * @param isComeFromDetail
     * @param canEdit
     * @param memberId
     * @param isSchoolEnter      是否是从空中学校过来
     * @param isOnlineClassEnter 是否是在线课堂班级过来的
     * @param isOnlineTeacher    是否是在线课堂的老师
     * @param isAuthorized       是否授权
     * @param params             课程详情参数
     * @param vo                 课程信息
     */
    public static void start(Activity activity, String id, boolean isComeFromDetail,
                             boolean canEdit, String memberId, boolean isSchoolEnter, boolean isOnlineClassEnter,
                             boolean isOnlineTeacher, boolean isAuthorized, CourseDetailParams params, CourseVo vo) {
        Intent intent = new Intent(activity, MyCourseDetailsActivity.class)
                .putExtra("id", id)
                .putExtra("isComeFromDetail", isComeFromDetail)
                .putExtra("canEdit", canEdit)
                .putExtra("memberId", memberId)
                .putExtra(KEY_EXTRA_IS_SCHOOL_ENTER, isSchoolEnter)
                .putExtra(KEY_EXTRA_IS_ONLINE_CLASS_ENTER, isOnlineClassEnter)
                .putExtra("isOnlineTeacher", isOnlineTeacher)
                .putExtra("isAuthorized", isAuthorized);
        if (params != null) {
            intent.putExtra(ACTIVITY_BUNDLE_OBJECT, params);
        }
        if (vo != null) {
            intent.putExtra("CourseVo", vo);
        }
        activity.startActivityForResult(intent, CourseDetailsActivity.Rs_collect);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course_details);
        registerBroadcastReceiver();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        isSchoolEnter = getIntent().getBooleanExtra(KEY_EXTRA_IS_SCHOOL_ENTER, false);
        isOnlineClassEnter = getIntent().getBooleanExtra(KEY_EXTRA_IS_ONLINE_CLASS_ENTER, false);
        if (getIntent().hasExtra(ACTIVITY_BUNDLE_OBJECT)) {
            mCourseDetailParams = (CourseDetailParams) getIntent().getSerializableExtra(ACTIVITY_BUNDLE_OBJECT);
        } else {
            mCourseDetailParams = new CourseDetailParams();
        }
        // 是否是从在线课堂进来的老师
        isOnlineTeacher = getIntent().getBooleanExtra("isOnlineTeacher", false);

        mCommentLayout = (LinearLayout) findViewById(R.id.comment_layout);
        mCommentContent = (EditText) findViewById(R.id.et_comment_content);
        mBtnSend = (TextView) findViewById(R.id.btn_send);

        canEdit = getIntent().getBooleanExtra("canEdit", false);
        mCanEdit = getIntent().getBooleanExtra("canEdit", false);
        memberId = getIntent().getStringExtra("memberId");
        mCurMemberId = getIntent().getStringExtra("memberId");

        topBar = (TopBar) findViewById(R.id.top_bar);
        imageViewCover = (ImageView) findViewById(R.id.cover_iv);
        textViewCourseProcess = (TextView) findViewById(R.id.course_process);
        layCourseInfoRoot = (LinearLayout) findViewById(R.id.course_info_root_lay);
        textViewCourseName = (TextView) findViewById(R.id.course_name_tv);
        textViewOrganName = (TextView) findViewById(R.id.organ_name_tv);
        mBtnIntro = (Button) findViewById(R.id.btn_introduction);
        mSchoolEnter = (TextView) findViewById(R.id.tv_school_enter);
        textViewTeacherName = (TextView) findViewById(R.id.teacher_name_tv);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        ratingBarGrade = (RatingBar) findViewById(R.id.grade_rating_bar);
        ivShare = (ImageView) findViewById(R.id.iv_share);
        textViewGrade = (TextView) findViewById(R.id.grade_tv);
        textViewStrudyNumber = (TextView) findViewById(R.id.study_number_tv);
        textViewePriceTitle = (TextView) findViewById(R.id.price_title_tv);
        textViewCoursePrice = (TextView) findViewById(R.id.course_price);
        btnReload = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        scrollView = (ScrollViewEx) findViewById(R.id.scrollview);
        textViewLiveTimetable = (TextView) findViewById(R.id.live_timetable_tv);

        // @date   :2018/4/9 0009 上午 10:13
        // @func   :v5.5取消进度列表显示进度条
        // gridViewLearnProcess = (NoScrollGridView) findViewById(R.id.learn_process_lay);
        // gridViewLearnProcessMore = (NoScrollGridView) findViewById(R.id.learn_process_more_lay);
        // imageViewHideMore = (ImageView) findViewById(R.id.hide_arrow);
        mProgressLayout = (LinearLayout) findViewById(R.id.progress_layout);
        mLearnPercent = (TextView) findViewById(R.id.txt_progress_percent);
        mLearnRate = (ProgressBar) findViewById(R.id.pb_course_progress);
        mNoticeContainer = (ViewGroup) findViewById(R.id.notice_container);
        mNoticeContent = (TextView) findViewById(R.id.txt_notice);
        // @date   :2018/4/8 0008 下午 6:25
        // @func   :版本5.5移除了该功能
        // gridViewLearnProcess = (NoScrollGridView) findViewById(R.id.learn_process_lay);
        // gridViewLearnProcessMore = (NoScrollGridView) findViewById(R.id.learn_process_more_lay);
        // 版本5.5移除了该功能
//        imageViewHideMore = (ImageView) findViewById(R.id.hide_arrow);
        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
        rg_tab_f = (RadioGroup) findViewById(R.id.rg_tab_f);
        mRbStudy = (RadioButton) findViewById(R.id.rb_task);
        mRbStudyF = (RadioButton) findViewById(R.id.rb_task_f);
        mRbComment= (RadioButton) findViewById(R.id.rb_scoring_criteria);
        mRbCommentF= (RadioButton) findViewById(R.id.rb_scoring_criteria_f);

        mRbLive = (RadioButton) findViewById(R.id.rb_live);
        mRbLiveF = (RadioButton) findViewById(R.id.rb_live_f);
        mBtnPlayList = (Button) findViewById(R.id.btn_play_list);
        mLLPlayList = (LinearLayout) findViewById(R.id.ll_play_list);

        if (isOnlineClassEnter) {
            mRbLive.setVisibility(View.GONE);
            mRbLiveF.setVisibility(View.GONE);
        }

        if (mCourseDetailParams != null && mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_TEACHING_PLAN) {
            mRbStudy.setText(getResources().getText(R.string.label_lesson_plan));
            mRbStudyF.setText(getResources().getText(R.string.label_lesson_plan));
            mRbLive.setText(getResources().getText(R.string.label_teaching_assistant));
            mRbLiveF.setText(getResources().getText(R.string.label_teaching_assistant));
            mRbComment.setText(getResources().getText(R.string.label_lesson_comment));
            mRbCommentF.setText(getResources().getText(R.string.label_lesson_comment));
        }else {
            mRbStudy.setText(getResources().getText(R.string.label_tab_course_chapter));
            mRbStudyF.setText(getResources().getText(R.string.label_tab_course_chapter));
            mRbLive.setText(getResources().getText(R.string.label_teach_class));
            mRbLiveF.setText(getResources().getText(R.string.label_teach_class));
            mRbComment.setText(getResources().getText(R.string.label_course_evaluation));
            mRbCommentF.setText(getResources().getText(R.string.label_course_evaluation));
        }

        id = getIntent().getStringExtra("id");
        mCourseId = getIntent().getStringExtra("id");
        isComeFromDetail = getIntent().getBooleanExtra("isComeFromDetail", false);
        isLqExcellent = getIntent().getBooleanExtra("isLqExcellent", false);
        initTabIndex = getIntent().getIntExtra("tabIndex", 0);
        isFromScan = mCourseDetailParams.isFromScan();
        if (id == null) {
            ToastUtil.showToast(activity, getResources().getString(R.string.data_is_empty));
            finish();
        }
        initViews();
    }

    private void initViews() {
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
                /*if (introductionFragment.isVisible()) {
                    introductionFragment.getMore();
                } else */
                if (studyPlanFragment.isVisible()) {
                    studyPlanFragment.getMore();
                } else if (courseCommentFragment.isVisible()) {
                    // initData();
                    courseCommentFragment.getMore();
                } else if (homeworkListFragment.isVisible()) {
                    homeworkListFragment.getMore();
                } else if (examListFragment.isVisible()) {
                    examListFragment.getMore();
                }
//                else if (mOnlineClassFragment.isVisible()) {
//                    mOnlineClassFragment.getMore();
//                }
                else if (mTutorialGroupFragment.isVisible()) {
                    mTutorialGroupFragment.getMore();
                } else if (mCourseDetailParams != null && mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_TEACHING_PLAN){
                    if (mAssistanceFragment.isVisible()){
                        mAssistanceFragment.requestSxRelationCourse(true);
                    }
                }else {
                    if (mOnlineClassFragment.isVisible()) {
                        mOnlineClassFragment.getMore();
                    }
                }
            }
        });
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        //初始化顶部工具条
        topBar.setBack(true);
        topBar.setTitleColor(R.color.textLight);
        topBar.setTranslationBackground(true);
        topBar.showBottomSplitView(false);
        topBar.setLeftFunctionImage1(R.drawable.ic_back_translate_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnReload.setOnClickListener(this);
        textViewLiveTimetable.setOnClickListener(this);
        if (isSchoolEnter || true) {
            mSchoolEnter.setVisibility(View.GONE);
            textViewOrganName.setEnabled(false);

        }

        ivShare.setOnClickListener(this);
        textViewOrganName.setOnClickListener(this);
        mBtnIntro.setOnClickListener(this);
        mSchoolEnter.setOnClickListener(this);
        mBtnPlayList.setOnClickListener(this);
        if (activity.getIntent().getBooleanExtra("canEdit", false)) {
            layCourseInfoRoot.setOnClickListener(this);
        }
        if (findViewById(R.id.more_tv) != null) {
            findViewById(R.id.more_tv).setOnClickListener(this);
        }

        int p_width = getWindowManager().getDefaultDisplay().getWidth();
        img_width = p_width / 3 - DisplayUtil.dip2px(activity, 20);
        img_height = img_width * 297 / 210;
        imageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.img_def, false, false, null);
        imageViewCover.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));

        // @date   :2018/4/11 0011 上午 11:31
        // @func   :点击文本框,显示评论对话框
        mCommentContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (null != courseCommentFragment) courseCommentFragment.comment(data);
                    return true;
                }
                return false;
            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != courseCommentFragment) courseCommentFragment.commitComment(data);
            }
        });

        // 视频馆/图书馆课程隐藏空中课堂和帮辅群
        boolean isHide =
                mCourseDetailParams != null && (mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_VIDEO_LIBRARY
                        || mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_LIBRARY
                        || mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_BRAIN_LIBRARY);
        findViewById(R.id.rb_live).setVisibility(isHide || isOnlineClassEnter ? View.GONE : View.VISIBLE);
        findViewById(R.id.rb_live_f).setVisibility(isHide || isOnlineClassEnter ? View.GONE : View.VISIBLE);
        findViewById(R.id.rb_tutorial_group).setVisibility(isHide ? View.GONE : View.VISIBLE);
        findViewById(R.id.rb_tutorial_group_f).setVisibility(isHide ? View.GONE :
                View.VISIBLE);

        initData();
        scrollView.setScrollViewListener(this);
        mNoticeContainer.setOnClickListener(this);
    }

    /**
     * Menu配置
     */
    private void initMenu() {
        boolean isAddInClass = false;
        boolean isAddPlayList = mCourseDetailParams != null && mCourseDetailParams.isMyCourse();
        if (mCanEdit && !mCourseDetailParams.isClassParent() &&
                !mCourseDetailParams.isClassTeacher() &&
                // 需要判断是否是机构的授权老师
                !mCourseDetailParams.isOrganCounselor() &&
                !UserHelper.checkCourseAuthor(courseVo, isOnlineCounselor) &&
                !isOnlineTeacher) {
            isAddInClass = true;
        }
        final boolean finalIsAddInClass = isAddInClass;
        final boolean finalIsAddPlayList = isAddPlayList;
        View.OnClickListener clickListener = null;
        if (isAddInClass || isAddPlayList) {
            clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initMenu(v, finalIsAddInClass, finalIsAddPlayList);
                }
            };
        }
        topBar.setRightFunctionImage1(R.drawable.ic_all_classify_small, clickListener);
    }

    private void initMenu(View view, boolean isAddInClass, boolean isAddPlayList) {
        // 溢出菜单
        List<PopupMenu.PopupMenuData> items = new ArrayList();
        if (EmptyUtil.isEmpty(courseVo)) return;
        if (courseVo.isInClass() && false) {
            // 没有这个inClass字段,用接口拉取
            PopupMenu.PopupMenuData data = new PopupMenu.PopupMenuData(0, R.string.label_old_in_class,
                    R.string.label_old_in_class);
            PopupMenu.PopupMenuData data_play = new PopupMenu.PopupMenuData(0, R.string.label_play_list,
                    R.string.label_play_list);
            items.add(data);
            items.add(data_play);
        } else {
            if (isAddInClass) {
                PopupMenu.PopupMenuData data = new PopupMenu.PopupMenuData(0, R.string.label_course_in_class,
                        R.string.label_course_in_class);
                items.add(data);
            }
            if (isAddPlayList) {
                PopupMenu.PopupMenuData data_play = new PopupMenu.PopupMenuData(0, R.string.label_play_list,
                        R.string.label_play_list);
                items.add(data_play);
            }
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
                            // 先用接口拉判断是否已经绑定班级
                            String token = mCurMemberId;
                            CourseHelper.isBindClass(token, courseVo.getId(), new DataSource.Callback<LQCourseBindClassEntity>() {
                                @Override
                                public void onDataNotAvailable(int strRes) {
                                    UIUtil.showToastSafe(strRes);
                                }

                                @Override
                                public void onDataLoaded(LQCourseBindClassEntity lqCourseBindClassEntity) {
                                    if (!lqCourseBindClassEntity.isBindClass()) {
                                        // 去指定班级的页面
                                        // 去指定到班级
                                        Intent intent = new Intent();
                                        intent.setClassName(activity.getPackageName(), "com.lqwawa.mooc.select.SchoolClassSelectActivity");
                                        Bundle bundle = new Bundle();
                                        bundle.putString("courseId", courseVo.getId());
                                        intent.putExtras(bundle);
                                        activity.startActivity(intent);
                                    } else {
                                        // 吐司弹提示
                                        UIUtil.showToastSafe(R.string.label_old_in_class);
                                    }
                                }
                            });
                        } else if (data.getId() == R.string.label_play_list) {
                            String ids = mCourseId;
                            if (courseVo != null) {
                                ids = courseVo.getId();
                            }
                            // 生成参数
                            CourseDetailItemParams params1 = new CourseDetailItemParams(false, mCurMemberId, !mCanEdit, ids);
                            params1.setDataType(CourseDetailItemParams.COURSE_DETAIL_ITEM_STUDY_PLAN);
                            // 设置课程详情参数
                            params1.setCourseParams(mCourseDetailParams);
                            Intent intent = new Intent();
                            intent.setClassName(activity.getPackageName(), "com.lqwawa.mooc.select.PlayListViewActivity");
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(CourseDetailsItemFragment.FRAGMENT_BUNDLE_OBJECT, params1);
                            intent.putExtras(bundle);
                            activity.startActivity(intent);
                        }
                    }
                };
        PopupMenu popupMenu = new PopupMenu(MyCourseDetailsActivity.this, itemClickListener, items);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
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
                //loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onHeaderRefreshComplete();
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onLoadFinish(boolean canLoadMore) {
                pullToRefreshView.onHeaderRefreshComplete();
                pullToRefreshView.onFooterRefreshComplete();
                if (courseCommentFragment.isVisible()) {
                    pullToRefreshView.setLoadMoreEnable(canLoadMore);
                } else if (mTutorialGroupFragment.isVisible()) {
                    pullToRefreshView.setLoadMoreEnable(canLoadMore);
                }

                MyCourseDetailsActivity.this.mCanLoadMore = canLoadMore;
                /*pullToRefreshView.setLoadMoreEnable(canLoadMore);
                MyCourseDetailsActivity.this
                        .canLoadMore[getRadioBtnIndex(rg_tab.getCheckedRadioButtonId())]
                        = canLoadMore;*/
            }

            @Override
            public void onCommitComment() {

            }
        };
        studyPlanFragment = new CourseDetailsItemFragment();
        studyPlanFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        courseCommentFragment = new CourseDetailsItemFragment();
        courseCommentFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        homeworkListFragment = new ExamListFragment();
        homeworkListFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        examListFragment = new ExamListFragment();
        examListFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        if (mCourseDetailParams != null && mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_TEACHING_PLAN) {
            mAssistanceFragment = RelatedAssistanceFragment.newInstance(id);
            mAssistanceFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        }else {
            mOnlineClassFragment = OnlineClassListFragment.newInstance(id);
            mOnlineClassFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        }

        // 帮辅群
        mTutorialGroupFragment = TutorialGroupFragment.newInstance(id, mCurMemberId);
        mTutorialGroupFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);

        Bundle bundle1 = new Bundle();

        CourseDetailItemParams params1 = new CourseDetailItemParams(true, mCurMemberId, !mCanEdit, mCourseId);
        params1.setDataType(CourseDetailItemParams.COURSE_DETAIL_ITEM_INTRODUCTION);
        params1.setCourseParams(mCourseDetailParams);
        bundle1.putSerializable(CourseDetailsItemFragment.FRAGMENT_BUNDLE_OBJECT, params1);

        Bundle bundle2 = new Bundle();
        bundle2.putBoolean(CourseDetailsItemFragment.KEY_EXTRA_ONLINE_TEACHER, isOnlineTeacher);
        bundle2.putSerializable(CourseVo.class.getSimpleName(), courseVo);
        if (getIntent().getExtras().containsKey("CourseVo")) {
            CourseVo vo = (CourseVo) getIntent().getSerializableExtra("CourseVo");
            bundle2.putSerializable(CourseVo.class.getSimpleName(), vo);
        }
        bundle2.putBoolean("isFromScan", isFromScan);

        // 传入课程Item详情参数
        CourseDetailItemParams params2 = (CourseDetailItemParams) params1.clone();
        params2.setDataType(CourseDetailItemParams.COURSE_DETAIL_ITEM_STUDY_PLAN);
        bundle2.putSerializable(CourseDetailsItemFragment.FRAGMENT_BUNDLE_OBJECT, params2);
//        bundle2.putStringArrayList("resIds", (ArrayList<String>) resIds);

        studyPlanFragment.setArguments(bundle2);
        // TODO 我的课程详情课程评价 从课程详情迁移过来的,传参类型尚未明清
        Bundle bundle3 = new Bundle();
        // 传入课程Item详情参数
        CourseDetailItemParams params3 = (CourseDetailItemParams) params1.clone();
        params3.setDataType(CourseDetailItemParams.COURSE_DETAIL_ITEM_COURSE_COMMENT);
        params3.setComment(true);
        bundle3.putSerializable(CourseDetailsItemFragment.FRAGMENT_BUNDLE_OBJECT, params3);
        courseCommentFragment.setArguments(bundle3);


        Bundle bundle4 = new Bundle();
        bundle4.putInt("type", 1);
        bundle4.putString("id", id);
        homeworkListFragment.setArguments(bundle4);
        Bundle bundle5 = new Bundle();
        bundle5.putInt("type", 0);
        bundle5.putString("id", id);
        bundle5.putString("memberId", memberId);
        // 是否是在线课堂老师
        bundle5.putBoolean(ExamListFragment.KEY_EXTRA_ONLINE_TEACHER, isOnlineTeacher);
        bundle5.putSerializable(CourseDetailParams.class.getSimpleName(), mCourseDetailParams);
        bundle5.putSerializable(CourseVo.class.getSimpleName(), courseVo);
        examListFragment.setArguments(bundle5);


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, studyPlanFragment);
        fragmentTransaction.add(R.id.fragment_container, courseCommentFragment);
        fragmentTransaction.add(R.id.fragment_container, homeworkListFragment);
        if (mCourseDetailParams != null && mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_TEACHING_PLAN) {
            //关联教辅
            fragmentTransaction.add(R.id.fragment_container, mAssistanceFragment);
        } else {
            // 空中课堂
            fragmentTransaction.add(R.id.fragment_container, mOnlineClassFragment);
        }
        // 添加帮辅群
        fragmentTransaction.add(R.id.fragment_container, mTutorialGroupFragment);

        fragmentTransaction.show(studyPlanFragment);
        fragmentTransaction.hide(courseCommentFragment);
        fragmentTransaction.hide(homeworkListFragment);
        if (mCourseDetailParams != null && mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_TEACHING_PLAN) {
            fragmentTransaction.hide(mAssistanceFragment);
        } else {
            fragmentTransaction.hide(mOnlineClassFragment);
        }
        fragmentTransaction.hide(mTutorialGroupFragment);
        fragmentTransaction.commit();

        rg_tab.setOnCheckedChangeListener(tabChangeListener);
        rg_tab_f.setOnCheckedChangeListener(tabChangeListener);
    }

    RadioGroup.OnCheckedChangeListener tabChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (rg_tab.getVisibility() == View.VISIBLE && group.getId() == R.id.rg_tab) {
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(studyPlanFragment);
                fragmentTransaction.hide(courseCommentFragment);
                fragmentTransaction.hide(homeworkListFragment);
                fragmentTransaction.hide(examListFragment);
                if (mCourseDetailParams != null && mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_TEACHING_PLAN) {
                    fragmentTransaction.hide(mAssistanceFragment);
                }else {
                    fragmentTransaction.hide(mOnlineClassFragment);
                }
                fragmentTransaction.hide(mTutorialGroupFragment);
                pullToRefreshView.setLoadMoreEnable(false);
                pullToRefreshView.onFooterRefreshComplete();

                if (checkedId == R.id.rb_task) {
                    fragmentTransaction.show(studyPlanFragment);
                    rg_tab_f.check(R.id.rb_task_f);
                    pullToRefreshView.setLoadMoreEnable(canLoadMore[1]);
                } else if (checkedId == R.id.rb_scoring_criteria) {
                    fragmentTransaction.show(courseCommentFragment);
                    rg_tab_f.check(R.id.rb_scoring_criteria_f);
                    pullToRefreshView.setLoadMoreEnable(canLoadMore[2]);
                    pullToRefreshView.setLoadMoreEnable(mCanLoadMore);
                } else if (checkedId == R.id.rb_homework) {
                    fragmentTransaction.show(homeworkListFragment);
                    rg_tab_f.check(R.id.rb_homework_f);
                    pullToRefreshView.setLoadMoreEnable(canLoadMore[3]);
                } else if (checkedId == R.id.rb_exam) {
                    // fragmentTransaction.show(examListFragment);
                    rg_tab_f.check(R.id.rb_exam_f);
                    pullToRefreshView.setLoadMoreEnable(canLoadMore[4]);
                } else if (checkedId == R.id.rb_live) {
                    if (mCourseDetailParams != null && mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_TEACHING_PLAN) {
                        fragmentTransaction.show(mAssistanceFragment);
                    }else {
                        fragmentTransaction.show(mOnlineClassFragment);
                    }
                    rg_tab_f.check(R.id.rb_live_f);
                    pullToRefreshView.setLoadMoreEnable(canLoadMore[5]);
                } else if (checkedId == R.id.rb_tutorial_group) {
                    fragmentTransaction.show(mTutorialGroupFragment);
                    rg_tab_f.check(R.id.rb_tutorial_group_f);
                    pullToRefreshView.setLoadMoreEnable(canLoadMore[6]);
                    pullToRefreshView.setLoadMoreEnable(mCanLoadMore);
                }

                fragmentTransaction.commitAllowingStateLoss();
            } else if (rg_tab_f.getVisibility() == View.VISIBLE && group.getId() == R.id.rg_tab_f) {
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(studyPlanFragment);
                fragmentTransaction.hide(courseCommentFragment);
                fragmentTransaction.hide(homeworkListFragment);
                fragmentTransaction.hide(examListFragment);
                if (mCourseDetailParams != null && mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_TEACHING_PLAN) {
                    fragmentTransaction.hide(mAssistanceFragment);
                }else {
                    fragmentTransaction.hide(mOnlineClassFragment);
                }
                fragmentTransaction.hide(mTutorialGroupFragment);
                pullToRefreshView.setLoadMoreEnable(false);
                pullToRefreshView.onFooterRefreshComplete();
                if (checkedId == R.id.rb_task_f) {
                    fragmentTransaction.show(studyPlanFragment);
                    rg_tab.check(R.id.rb_task);
                    pullToRefreshView.setLoadMoreEnable(canLoadMore[1]);
                } else if (checkedId == R.id.rb_scoring_criteria_f) {
                    fragmentTransaction.show(courseCommentFragment);
                    rg_tab.check(R.id.rb_scoring_criteria);
                    pullToRefreshView.setLoadMoreEnable(canLoadMore[2]);
                    pullToRefreshView.setLoadMoreEnable(mCanLoadMore);
                } else if (checkedId == R.id.rb_homework_f) {
                    fragmentTransaction.show(homeworkListFragment);
                    rg_tab.check(R.id.rb_homework);
                    pullToRefreshView.setLoadMoreEnable(canLoadMore[3]);
                } else if (checkedId == R.id.rb_exam_f) {
                    // fragmentTransaction.show(examListFragment);
                    rg_tab.check(R.id.rb_exam);
                    pullToRefreshView.setLoadMoreEnable(canLoadMore[4]);
                } else if (checkedId == R.id.rb_live_f) {
                    // @date   :2018/6/8 0008 上午 1:04
                    // @func   :V5.7将直播换成了在线课堂
                    // fragmentTransaction.show(mClassroomFragment);
                    if (mCourseDetailParams != null && mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_TEACHING_PLAN) {
                        fragmentTransaction.show(mAssistanceFragment);
                    }else {
                        fragmentTransaction.show(mOnlineClassFragment);
                    }
                    rg_tab.check(R.id.rb_live);
                    pullToRefreshView.setLoadMoreEnable(canLoadMore[5]);
                } else if (checkedId == R.id.rb_tutorial_group_f) {
                    fragmentTransaction.show(mTutorialGroupFragment);
                    rg_tab.check(R.id.rb_tutorial_group);
                    pullToRefreshView.setLoadMoreEnable(canLoadMore[6]);
                }
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    };

    private int getRadioBtnIndex(int id) {
        /*if (id == R.id.rb_course_introduction || id == R.id.rb_course_introduction_f) {
            return 0;
        } else */
        if (id == R.id.rb_task || id == R.id.rb_task_f) {
            return 1;
        } else if (id == R.id.rb_scoring_criteria || id == R.id.rb_scoring_criteria_f) {
            return 2;
        } else if (id == R.id.rb_homework || id == R.id.rb_homework_f) {
            return 3;
        } else if (id == R.id.rb_exam || id == R.id.rb_exam_f) {
            return 4;
        } else if (id == R.id.rb_live || id == R.id.rb_live_f) {
            return 5;
        } else if (id == R.id.rb_tutorial_group || id == R.id.rb_tutorial_group_f) {
            return 6;
        } else {
            return 0;
        }
    }

    //scrollview 滚动条位置变动时调整顶部工具条的状态及tab
    @Override
    public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y > 1000) {
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
            if (!EmptyUtil.isEmpty(leftFunctionImage1)) {
                leftFunctionImage1.setBackground(activity.getResources().getDrawable(
                        R.drawable.com_circle_black_trans_bg_selecter));
            }
        }
        if (courseVo != null) {
            if (oldy == 0 && y > 0) {
                topBar.setTitle(courseVo.getName());
            }
        }
        if (y == 0) {
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
    }

    private void updateData() {
        /*if (introductionFragment.isVisible()) {
            introductionFragment.updateData();
        } else */
        if (studyPlanFragment.isVisible()) {
            studyPlanFragment.updateData();
        } else if (courseCommentFragment.isVisible()) {
            refreshData();
            courseCommentFragment.updateData();
        } else if (homeworkListFragment.isVisible()) {
            homeworkListFragment.updateData();
        } else if (examListFragment.isVisible()) {
            examListFragment.updateData();
        }
//        else if (mOnlineClassFragment.isVisible()) {
//            // 下拉刷新
//            mOnlineClassFragment.onHeaderRefresh();
//        }
        else if (mTutorialGroupFragment.isVisible()) {
            // 帮辅群显示
            mTutorialGroupFragment.onHeaderRefresh();
        }else if (mCourseDetailParams != null && mCourseDetailParams.getLibraryType() == OrganLibraryType.TYPE_TEACHING_PLAN) {
            if (mAssistanceFragment.isVisible()) {
                mAssistanceFragment.requestSxRelationCourse(false);
            }
        }else {
            if (mOnlineClassFragment.isVisible()) {
                mOnlineClassFragment.onHeaderRefresh();
            }
        }

        // @date   :2018/6/8 0008 上午 1:04
        // @func   :V5.7将直播换成了在线课堂
        /*else if (mClassroomFragment.isVisible()) {
            mClassroomFragment.updateData();
        }*/
    }

    private void initData() {
        String token = UserHelper.getUserId();
        if (!mCanEdit) {
            token = mCurMemberId;
        }

        int dataType = CourseDetailItemParams.COURSE_DETAIL_ITEM_INTRODUCTION;
        String schoolIds = null;
        if (isLqExcellent) {
            //来自LQ精品学程
            schoolIds = getIntent().getStringExtra("schoolId");
        } else if (UserHelper.isLogin() && mCanEdit) {
            schoolIds = UserHelper.getUserInfo().getSchoolIds();
        }

        CourseHelper.getCourseDetailsById(token, id, dataType, schoolIds, new DataSource.Callback<CourseDetailsVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(CourseDetailsVo courseDetailsVo) {
                boolean tutorialMode = MainApplication.isTutorialMode();
                tutorialMode = tutorialMode && mCourseDetailParams.getCourseEnterType(false) == CourseDetailType.COURSE_DETAIL_MOOC_ENTER;

                if (isFromScan) {
                    tutorialMode = false;
                }

                MyCourseDetailsActivity.this.courseDetailsVo = courseDetailsVo;
                collected = courseDetailsVo.isIsCollect();
                courseScore = courseDetailsVo.getCourseScore();
                List<CourseVo> voList = courseDetailsVo.getCourse();
                if (voList != null && voList.size() > 0) {
                    courseVo = voList.get(0);
                    // 加载到课程信息
                    String teachersId = courseVo.getTeachersId();
                    if (mCourseDetailParams.isClassTeacher() || tutorialMode) {
                        // 当前人为班级老师
                        // 如果是帮辅模式，那么进入到这页面，肯定是帮辅老师
                        // 如果不为空
                        if (EmptyUtil.isEmpty(teachersId)) {
                            teachersId = UserHelper.getUserId();
                        } else {
                            teachersId = teachersId + "," + UserHelper.getUserId();
                        }

                        courseVo.setTeachersId(teachersId);
                    }

                    if (isOnlineTeacher ||
                            mCourseDetailParams.isClassParent()
                            || mCourseDetailParams.isOrganCounselor()) {
                        String counselorId = courseVo.getCounselorId();
                        // 从关联学程进来的 在线课堂的老师
                        // 或者是机构已经授权的老师
                        if (EmptyUtil.isEmpty(counselorId)) {
                            counselorId = UserHelper.getUserId();
                        } else {
                            counselorId = counselorId + "," + UserHelper.getUserId();
                        }

                        courseVo.setCounselorId(counselorId);
                    }

                    if (mCourseDetailParams.isMyCourse()) {
                        mCourseDetailParams.setClassId(courseVo.getBindClassId());
                    }

                    // 加载到courseVo
                    initMenu();

                    if(!TextUtils.isEmpty(courseVo.getBindClassId())) {
                        mCourseDetailParams.setBindClassId(courseVo.getBindClassId());
                    }

                    updateView();
                    initTabAndFragment();
                    // TODO 没办法,这里只能启动延时任务了。因为历史原因,无法将课程传到CourseDetailsFragment,后续会改进
                    UIUtil.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            courseObservable.triggerObservers(courseVo);
                        }
                    }, 0);


                    // 获取用户的机构关注状态
                    String userId = UserHelper.getUserId();
                    String schoolId = courseVo.getOrganId();
                    SchoolHelper.requestSchoolInfo(userId, schoolId, new DataSource.Callback<SchoolInfoEntity>() {
                        @Override
                        public void onDataNotAvailable(int strRes) {

                        }

                        @Override
                        public void onDataLoaded(SchoolInfoEntity entity) {
                            MyCourseDetailsActivity.this.mSchoolEntity = entity;
                            // mSchoolEnter.setTextColor(entity.getState() != 0 ? UIUtil.getColor(R.color.colorGary) : UIUtil.getColor(R.color.colorAccent));
                            // mSchoolEnter.setBackgroundResource(entity.getState() != 0 ? R.drawable.bg_rectangle_gary_radius_10 : R.drawable.bg_rectangle_accent_radius_10);
                            mSchoolEnter.setTextColor(UIUtil.getColor(R.color.colorAccent));
                            mSchoolEnter.setBackgroundResource(R.drawable.bg_rectangle_accent_radius_10);
                            mSchoolEnter.setText(getString(R.string.label_enter_school));
                            /*if(entity.getState() != 0){
                                // 关注状态
                                mSchoolEnter.setText(getString(R.string.label_yet_attention));
                            }else{
                                mSchoolEnter.setText(getString(R.string.label_add_attention));
                            }*/
                        }
                    });
                }
            }
        });
        getCourseLearningProcess();
        getCourseNoticeData();
    }

    /**
     * 设置学习进度隐藏或者显示
     *
     * @param vo 课程详细信息
     */
    private void setProgressVisiable(@NonNull CourseVo vo) {
        // 如果身份是该课程的老师,隐藏学习进度
        // TODO 是否未加入课程详情需要传是否是在线课堂的老师,来判断是否显示进度
        if (canEdit && UserHelper.checkCourseAuthor(vo, isOnlineTeacher)) {
            // 老师身份
            mProgressLayout.setVisibility(View.GONE);
        } else {
            // 老师身份
            mProgressLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 下拉刷新,刷新课程信息
     */
    private void refreshData() {
        String token = UserHelper.getUserId();
        if (!mCanEdit) {
            token = mCurMemberId;
        }

        int dataType = CourseDetailItemParams.COURSE_DETAIL_ITEM_INTRODUCTION;
        String schoolIds = null;
        if (isLqExcellent) {
            //来自LQ精品学程
            schoolIds = getIntent().getStringExtra("schoolId");
        } else if (UserHelper.isLogin() && mCanEdit) {
            schoolIds = UserHelper.getUserInfo().getSchoolIds();
        }

        CourseHelper.getCourseDetailsById(token, id, dataType, schoolIds, new DataSource.Callback<CourseDetailsVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(CourseDetailsVo courseDetailsVo) {
                pullToRefreshView.onHeaderRefreshComplete();
                pullToRefreshView.onFooterRefreshComplete();
                MyCourseDetailsActivity.this.courseDetailsVo = courseDetailsVo;
                collected = courseDetailsVo.isIsCollect();
                courseScore = courseDetailsVo.getCourseScore();
                List<CourseVo> voList = courseDetailsVo.getCourse();
                if (voList != null && voList.size() > 0) {
                    courseVo = voList.get(0);
                    fillCourseDetailInfo(courseVo);
                }
            }
        });
    }

    /**
     * 公告列表
     */
    private List<NoticeVo> mNoticeVos;

    /**
     * 获取公告信息
     */
    /**
     * 获取公告信息
     */
    private void getCourseNoticeData() {
        final String courseId = id;
        boolean canEdit = getIntent().getBooleanExtra("canEdit", false);
        String token = null;
        if (!canEdit) {
            token = getIntent().getStringExtra("memberId");
        }
        CourseHelper.getCourseNoticeData(token, courseId, new DataSource.Callback<List<NoticeVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(List<NoticeVo> noticeVos) {
                if (!EmptyUtil.isEmpty(noticeVos)) {
                    mNoticeVos = noticeVos;
                    NoticeVo noticeVo = noticeVos.get(0);
                    mNoticeContent.setText(noticeVo.getTitle());
                }
            }
        });
    }

    /**
     * 获取课程学习进度信息
     */
    private void getCourseLearningProcess() {
        // @date   :2018/4/9 0009 上午 10:17
        // @func   :v5.5取消进度列表显示进度条
        boolean canEdit = getIntent().getBooleanExtra("canEdit", false);
        String token = null;
        if (!canEdit) {
            token = getIntent().getStringExtra("memberId");
        }
        final String courseId = id;
        CourseHelper.getCourseLearningProcess(token, courseId, new DataSource.Callback<CourseRateEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(CourseRateEntity entity) {
                int learnRate = entity.getLearnRate();
                if (UIUtil.isRunInMainThread()) {
                    mLearnRate.setProgress(learnRate);
                    mLearnPercent.setText(String.format(getText(R.string.label_course_progress_percent).toString(), learnRate));
                }
            }
        });
    }

    private void updateView() {
        if (courseVo != null) {
            /**
             * Edit by ChenXin at 2017/10/17
             * For 资源浏览时带入机构id
             */
            getIntent().putExtra("schoolId", courseVo.getOrganId());

            if (!UserHelper.checkCourseAuthor(courseVo, isOnlineTeacher) && courseDetailsVo.isIsExpire()) {//课程权限已到期
                // 不是老师，并且过期
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
                        activity.finish();
                    }
                });

                builder.create().show();
                return;
            }

            // @date   :2018/4/10 0010 下午 4:44
            // @func   :TODO 因为将我的课程详情课程标准换成课程评价,需要注释掉的内容
            // courseCommentFragment.updateContent(courseVo.getScoreCriteria());
            if (initTabIndex == 1) {
                rg_tab.check(R.id.rb_task);
                initTabIndex = 0;
            } else if (initTabIndex == 3) {
                rg_tab.check(R.id.rb_homework);
                initTabIndex = 0;
            } else if (initTabIndex == 4) {
                rg_tab.check(R.id.rb_exam);
                initTabIndex = 0;
            }

            fillCourseDetailInfo(courseVo);
        }
    }

    /**
     * 填充课程详情信息
     */
    private void fillCourseDetailInfo(@NonNull CourseVo vo) {
        // 设置学习进度信息
        setProgressVisiable(vo);

        float score = courseVo.getCommentNum() == 0 ? 0 :
                1.0f * courseVo.getTotalScore() / courseVo.getCommentNum();
        ratingBarGrade.setRating(score);
        XImageLoader.loadImage(imageViewCover,
                courseVo.getThumbnailUrl().trim(),
                imageOptions);
        textViewCourseName.setText(courseVo.getName());
        textViewOrganName.setText(courseVo.getOrganName());
        textViewTeacherName.setText(courseVo.getTeachersName());
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
        switch (courseVo.getProgressStatus()) {
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
        if (courseVo.getProgressStatus() == 1) {
            processStr.append(" ");
            processStr.append(String.format(getResources().getString(R.string.update_to_the),
                    "" + courseVo.getProgress()));
        }
        processStr.append(")");
        greenSpan = new ForegroundColorSpan(
                activity.getResources().getColor(R.color.com_text_green));
        processStr.setSpan(greenSpan, 0, processStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewCourseProcess.append(processStr);

        textViewGrade.setText("(" + courseVo.getCommentNum() + ")");
        textViewStrudyNumber.setText(String.format(getText(R.string.some_study).toString(), courseVo.getStudentNum()));
        if (courseVo.getPrice() > 0) {
            textViewePriceTitle.setVisibility(View.VISIBLE);
            textViewCoursePrice.setText("¥" + courseVo.getPrice());
        } else {
            textViewePriceTitle.setVisibility(View.GONE);
            textViewCoursePrice.setText(activity.getResources().getString(R.string.free));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            initData();
        } else if (view.getId() == R.id.tv_school_enter || view.getId() == R.id.organ_name_tv) {
            // 点击关注
            // 进入机构主页
            if (EmptyUtil.isNotEmpty(courseVo)) {
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

                if (EmptyUtil.isEmpty(mSchoolEntity)) {
                    // 已经进入机构
                    return;
                }

                if (mSchoolEntity.hasJoinedSchool() || mSchoolEntity.hasSubscribed()) {
                    // 已关注
                    sendSchoolSpaceRefreshBroadcast();
                } else {
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
        } else if (view.getId() == R.id.more_tv) {
            if (isComeFromDetail) {
                finish();
            } else if (courseVo != null) {
                CourseDetailsActivity.start(activity, courseVo.getId(), true, 0, true,
                        getIntent().getStringExtra("memberId"));
            }
        } else if (view.getId() == R.id.add_to_cart_tv) {
            if (!UserHelper.isLogin()) {
                LoginActivity.loginForResult(this);
            }
        } else if (view.getId() == R.id.live_timetable_tv) {
            //跳转到课程表界面
            LiveTimetableActivity.start(activity,
                    LiveTimetableActivity.LiveSourceType.Type_course,
                    id, "", "", "");
        } else if (view.getId() == R.id.notice_container) {
            CourseNoticeListActivity.start(this, id, memberId, canEdit);
        } else if (view.getId() == R.id.btn_introduction) {
            // 简介
            if (EmptyUtil.isEmpty(courseVo)) return;
            CourseDetailItemParams params = new CourseDetailItemParams(true, mCurMemberId, !mCanEdit, courseVo.getId());
            params.setDataType(CourseDetailItemParams.COURSE_DETAIL_ITEM_INTRODUCTION);
            CourseIntroductionActivity.show(this, params);
        } else if (view.getId() == R.id.iv_share) {
            // 分享
            if (EmptyUtil.isEmpty(courseVo)) return;
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
            if (mCourseDetailParams.getLibraryType() != OrganLibraryType.TYPE_TEACHING_PLAN) {
                if (courseVo.getPrice() == 0) {
                    descriptionBuilder.append(UIUtil.getString(R.string.label_class_gratis) + "\n");
                } else {
                    descriptionBuilder.append(Common.Constance.MOOC_MONEY_MARK + " " + courseVo.getPrice() + "\n");
                }
            }
            float score = courseVo.getCommentNum() == 0 ? 0 :
                    1.0f * courseVo.getTotalScore() / courseVo.getCommentNum();
            for (int index = 0; index < Math.ceil(score); index++) {
                descriptionBuilder.append("\u2B50");
            }
            final String thumbnailUrl = courseVo.getThumbnailUrl();
            final String url = AppConfig.ServerUrl.CourseDetailShareUrl.replace("{id}", courseVo.getId());
            share(titleBuilder.toString(), descriptionBuilder.toString(), thumbnailUrl, url);
        } else if (view.getId() == R.id.btn_play_list) {
            //播放列表
            if (EmptyUtil.isNotEmpty(TaskSliderHelper.onPlayListListener)) {
                TaskSliderHelper.onPlayListListener.showPlayListDialog(MyCourseDetailsActivity.this);
            }
        }
    }

    /**
     * 课程分享
     *
     * @param title        标题
     * @param description  描述
     * @param thumbnailUrl 缩略图
     * @param url          分享地址
     */
    public void share(String title, String description, String thumbnailUrl, String url) {
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
        utils.share(activity.getWindow().getDecorView(), shareInfo);

    }

    /**
     * 发送一个去空中学校并且刷新的广播
     */
    private void sendSchoolSpaceRefreshBroadcast() {
        //关注/取消关注成功后，向校园空间发广播
        Intent broadIntent = new Intent();
        broadIntent.setAction("action_change_lqCourse_tab");
        broadIntent.putExtra("schoolId", courseVo.getOrganId());
        activity.sendBroadcast(broadIntent);
    }

    private void collect(final boolean isCollect) {
        if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
            LoginActivity.loginForResult(this);
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("id", id);
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
                    courseDetailsVo.setIsCollect(isCollect);

                    /*topBar.setRightFunctionImage1(isCollect ? R.drawable.ic_collection_on :
                            R.drawable.ic_collect_off, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            collect(!myCourseDetailVo.isIsCollect());
                        }
                    });*/
                    setResult(RESULT_OK);
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
        if (requestCode == CourseDetailsActivity.Rs_collect && resultCode == Activity.RESULT_OK) {
            initData();
        } else if (requestCode == 105) {//提交任务单成功后返回
            if (examListFragment != null) {
                examListFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == LiveDetails.MOOC_LIVE) {//直播
            // @date   :2018/6/8 0008 上午 1:04
            // @func   :V5.7将直播换成了在线课堂
            /*if (mClassroomFragment != null) {
                mClassroomFragment.onActivityResult(requestCode, resultCode, data);
            }*/
        } else if (requestCode == RESOURCE_PLAY_COMPLETED_REQUEST_CODE) {
            TaskSliderHelper.onPlayListListener.onActivityResult(requestCode, resultCode, data);
        }
    }

    public String getChapterNumString(String chapterName) {
        if (languageIsEnglish()) {
            if (chapterName.equals("单元")) {
                return "Unit";
            } else if (chapterName.equals("章")) {
                return "Chapter";
            } else if (chapterName.equals("周")) {
                return "Week";
            }
        }
        return chapterName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcastReceiver();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (TaskSliderHelper.onPlayListListener != null) {
            TaskSliderHelper.onPlayListListener.releasePlayResource();
        }
    }

    @Override
    public void commitComment() {
        // 提交评论,刷新课程进度等信息
        requestCourseDetailsData();
    }

    @Override
    public void courseCommentVisible() {
        // 评论区域显示
        mCommentLayout.setVisibility(View.VISIBLE);
        // 隐藏课程表
        textViewLiveTimetable.setVisibility(View.GONE);
        mLLPlayList.setVisibility(View.GONE);

    }

    @Override
    public void otherFragmentVisible() {
        // 评论区域隐藏
        mCommentLayout.setVisibility(View.GONE);
        // 显示课程表
        // @date   :2018/6/7 0007 下午 11:23
        // @func   :Mooc 所有地方都隐藏课程表
        textViewLiveTimetable.setVisibility(View.GONE);
        //播放列表按钮隐藏
        mLLPlayList.setVisibility(View.GONE);
    }

    @Override
    public void coursePlayListVisible() {
        //播放列表按钮显示
        mLLPlayList.setVisibility(isPlayListNotEmpty() ? View.VISIBLE : View.GONE);
        // 评论区域显示
        mCommentLayout.setVisibility(View.GONE);
        // 隐藏课程表
        textViewLiveTimetable.setVisibility(View.GONE);

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
     *
     * @return
     */
    @Override
    public Observable getCourseObservable() {
        return courseObservable;
    }

    /**
     * BroadcastReceiver
     ************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConfig.ServerUrl.userExamSave)//提交试卷成功
                    || action.equals(AppConfig.ServerUrl.userTaskSave)//提交任务单成功
                    || action.equals(AppConfig.ServerUrl.joinInCourse)
                    || action.equals(CourseDetailsItemFragment.LQWAWA_ACTION_CAN_COURSEWARE)
                    || action.equals(CourseDetailsItemFragment.LQWAWA_ACTION_LISTEN_AND_WRITE)
                    || action.equals(CourseDetailsItemFragment.LQWAWA_ACTION_READ_WRITE_SINGLE)) {//课程参加成功
                requestCourseDetailsData();
            }
        }
    };

    /**
     * 获取课程详细信息
     */
    private void requestCourseDetailsData() {
        boolean canEdit = getIntent().getBooleanExtra("canEdit", false);
        String memberId = getIntent().getStringExtra("memberId");
        String schoolIds = getIntent().getStringExtra("SchoolId");
        String token = null;
        if (!canEdit) {
            token = memberId;
        }

        final String courseId = id;

        if (UserHelper.isLogin() && TextUtils.equals(memberId, UserHelper.getUserId())) {
            schoolIds = UserHelper.getUserInfo().getSchoolIds();
        }

        CourseHelper.getCourseDetailsData(token, 1, courseId, schoolIds, new DataSource.Callback<CourseVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(CourseVo courseVo) {
                MyCourseDetailsActivity.this.courseVo = courseVo;
                updateView();
                getCourseLearningProcess();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event) {
        if (EventWrapper.isMatch(event, EventConstant.APPOINT_COURSE_IN_CLASS_EVENT)) {
            // 刷新UI
            courseVo.setInClass(true);
        } else if (EventWrapper.isMatch(event, EventConstant.GENERATE_PLAY_LIST_EVENT)) {
            playListVo = (List<CourseResourceEntity>) event.getData();
            updatePlayCourseList(playListVo);
            setPlayListVisible();
        }
    }


    public void updatePlayCourseList(List<CourseResourceEntity> playListVo) {
        if (EmptyUtil.isNotEmpty(playListVo) && TaskSliderHelper.onPlayListListener != null) {
            TaskSliderHelper.onPlayListListener.setPlayListInfo(playListVo);
            TaskSliderHelper.onPlayListListener.setActivity(MyCourseDetailsActivity.this);
            if (TaskSliderHelper.onPlayListListener.getPlayResourceSize() > 0) {
                TaskSliderHelper.onPlayListListener.startPlay();
            }
        }
    }

    //创建完成后显示播放列表按钮
    private void setPlayListVisible() {
        boolean isShow =
                (rg_tab.getVisibility() == View.VISIBLE && rg_tab.getCheckedRadioButtonId() == R.id.rb_task)
                || (rg_tab_f.getVisibility() == View.VISIBLE && rg_tab_f.getCheckedRadioButtonId() == R.id.rb_task_f);
        if (isShow && isPlayListNotEmpty()) {
            mLLPlayList.setVisibility(View.VISIBLE);
        } else {
            mLLPlayList.setVisibility(View.GONE);
        }
    }

    private boolean isPlayListNotEmpty() {
        return playListVo != null && !playListVo.isEmpty();
    }



    /**
     * 注册广播事件
     */
    protected void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        //要接收的类型
        myIntentFilter.addAction(AppConfig.ServerUrl.userExamSave);//提交了考试
        myIntentFilter.addAction(AppConfig.ServerUrl.userTaskSave);//提交任务单成功
        myIntentFilter.addAction(AppConfig.ServerUrl.joinInCourse);//课程参加成功
        myIntentFilter.addAction(CourseDetailsItemFragment.LQWAWA_ACTION_CAN_COURSEWARE);// 看课件
        myIntentFilter.addAction(CourseDetailsItemFragment.LQWAWA_ACTION_LISTEN_AND_WRITE);// 听说课
        myIntentFilter.addAction(CourseDetailsItemFragment.LQWAWA_ACTION_READ_WRITE_SINGLE);//读写单
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 取消注册广播事件
     */
    private void unRegisterBroadcastReceiver() {
        unregisterReceiver(mBroadcastReceiver);
    }

}