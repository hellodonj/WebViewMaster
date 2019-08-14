package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.letvcloud.cmf.utils.LocalBroadcastManager;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.LqResponseDataVo;
import com.lqwawa.intleducation.base.vo.PagerArgs;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.lessontask.missionrequire.MissionRequireFragment;
import com.lqwawa.intleducation.module.discovery.tool.CourseDetails;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.discovery.ui.task.detail.SectionTaskParams;
import com.lqwawa.intleducation.module.discovery.ui.task.list.TaskCommitParams;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.TaskUploadBackVo;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.tools.DialogHelper;
import com.oosic.apps.iemaker.base.BaseUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SectionTaskDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ACTIVITY_BUNDLE_OBJECT = "ACTIVITY_BUNDLE_OBJECT";
    // 是否是游离的身份
    public static final String KEY_ROLE_FREE_USER = "KEY_ROLE_FREE_USER";

    public static final String KEY_IS_FROM_MY = "isFromMy";
    public static int Rs_task_commit = 2048;
    protected static String TAG = SectionTaskDetailsActivity.class.getSimpleName();

    protected Activity activity;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    // Tab 标题
    private List<String> mTabTexts;
    // private List<String> mTabList;
    // Fragment
    private List<Fragment> mFragments = new ArrayList<>();
    protected TaskCommitListFragment mFragment0, mFragment1;

    protected int img_width;
    protected int img_height;
    protected ImageOptions imageOptions;

    protected TopBar topBar;
    protected ImageView mResIcon;
    protected TextView mTvTotalGrade;
    protected TextView mResName;
    private FrameLayout mFromLayout;
    private TextView mTvFromCourse;
    // protected SlidePagerAdapter tabPagerAdapter;

    protected SectionResListVo sectionResListVo;
    // protected SectionTaskDetailsVo sectionTaskDetailsVo;
    // protected TaskCommitListFragment fragment0;
    // protected TaskCommentListFragment fragment1;
    // @date   :2018/4/14 0014 下午 1:36
    // @func   :V5.5对批阅页面的更改
    // protected RadioButton radioButton0;
    // protected RadioButton radioButton1;
    // 获取到提交列表，以及复述课件，读写单的按钮
    protected LqTaskCommitListVo mLqTaskCommitListVo;

    protected String zipFilePath;
    protected boolean isLive = false;
    protected boolean isHost = false;
    protected String taskId;
    protected String examId;
    protected int mOriginalRole;
    protected int mHandleRole;
    protected boolean isAudition;
    protected SectionTaskParams mTaskParams;
    protected CourseDetailParams mCourseParams;
    protected PagerArgs pagerArgs = null;
    private int orderByType = 0;
    private int libraryType;
    private String courseId,memberId;

    public static void startForResult(Activity activity, SectionResListVo vo) {
        activity.startActivityForResult(new Intent(activity, SectionTaskDetailsActivity.class)
                .putExtra("SectionResListVo", vo), Rs_task_commit);
    }

    public static void startForResultEx(Activity activity, SectionResListVo vo,
                                        String memberId, String schoolId, boolean isFromMyCourse,
                                        String taskId, int originRoleType, int roleType, String examId,int libraryType,
                                        @NonNull SectionTaskParams params) {
        Intent intent = new Intent();
        intent.putExtra("SectionResListVo", vo);
        intent.putExtra("memberId", memberId);
        intent.putExtra("schoolId", schoolId);
        intent.putExtra(KEY_IS_FROM_MY, isFromMyCourse);
        intent.putExtra("taskId", taskId);
        intent.putExtra("originRoleType", originRoleType);
        intent.putExtra("roleType", roleType);
        intent.putExtra("examId", examId);
        intent.putExtra("libraryType",libraryType);
        intent.putExtra(ACTIVITY_BUNDLE_OBJECT, params);
        intent.setClassName(MainApplication.getInstance().getPackageName(),
                "com.lqwawa.mooc.modle.MyCourse.ui.SectionTaskDetailsActivityEx");
        activity.startActivityForResult(intent, Rs_task_commit);
    }

    /**
     * Mooc 入口 游客进入
     *
     * @param activity
     * @param vo
     * @param memberId
     * @param schoolId
     * @param isFromMyCourse
     * @param taskId
     * @param originRoleType
     * @param roleType
     * @param examId
     * @param freeUser
     */
    public static void startForResultEx(Activity activity, SectionResListVo vo,
                                        String memberId, String schoolId, boolean isFromMyCourse,
                                        String taskId, int originRoleType, int roleType,
                                        String examId, boolean freeUser,int libraryType,
                                        @NonNull SectionTaskParams params) {
        Intent intent = new Intent();
        intent.putExtra("SectionResListVo", vo);
        intent.putExtra("memberId", memberId);
        intent.putExtra("schoolId", schoolId);
        intent.putExtra(KEY_IS_FROM_MY, isFromMyCourse);
        intent.putExtra("taskId", taskId);
        intent.putExtra("originRoleType", originRoleType);
        intent.putExtra("roleType", roleType);
        intent.putExtra("examId", examId);
        intent.putExtra(KEY_ROLE_FREE_USER, freeUser);
        intent.putExtra("libraryType",libraryType);
        intent.putExtra(ACTIVITY_BUNDLE_OBJECT, params);
        intent.setClassName(MainApplication.getInstance().getPackageName(),
                "com.lqwawa.mooc.modle.MyCourse.ui.SectionTaskDetailsActivityEx");
        activity.startActivityForResult(intent, Rs_task_commit);
    }

    public static void startLiveForResultEx(Activity activity, SectionResListVo vo,
                                            String memberId, String schoolId, boolean isHost,
                                            boolean isFromMyLive, int roleType) {
        Intent intent = new Intent();
        intent.putExtra("SectionResListVo", vo);
        intent.putExtra("memberId", memberId);
        intent.putExtra("schoolId", schoolId);
        intent.putExtra("isLive", true);
        intent.putExtra("isHost", isHost);
        intent.putExtra(KEY_IS_FROM_MY, isFromMyLive);
        intent.putExtra("roleType", roleType);
        intent.setClassName(MainApplication.getInstance().getPackageName(),
                "com.lqwawa.mooc.modle.MyCourse.ui.SectionTaskDetailsActivityEx");
        activity.startActivityForResult(intent, Rs_task_commit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_task_details_head);
        activity = this;
        sectionResListVo = (SectionResListVo) getIntent().getSerializableExtra("SectionResListVo");
        memberId = getIntent().getStringExtra("memberId");
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        topBar = (TopBar) findViewById(R.id.top_bar);
        // @date   :2018/4/14 0014 下午 1:37
        // @func   :V5.5对批阅页面的更改
        // radioButton0 = (RadioButton) findViewById(R.id.rb0);
        // radioButton1 = (RadioButton) findViewById(R.id.rb1);
        // fragment0 = new TaskCommitListFragment();
        // fragment1 = new TaskCommentListFragment();
        isLive = getIntent().getBooleanExtra("isLive", false);
        isHost = getIntent().getBooleanExtra("isHost", false);
        taskId = getIntent().getStringExtra("taskId");
        mOriginalRole = getIntent().getIntExtra("originRoleType", 0);
        mHandleRole = getIntent().getIntExtra("roleType", 0);
        examId = getIntent().getStringExtra("examId");
        isAudition = getIntent().getBooleanExtra(KEY_ROLE_FREE_USER, false);
        libraryType = getIntent().getIntExtra("libraryType",5);
        if (getIntent().getExtras().containsKey(ACTIVITY_BUNDLE_OBJECT)) {
            mTaskParams = (SectionTaskParams) getIntent().getSerializableExtra(ACTIVITY_BUNDLE_OBJECT);
            mCourseParams = mTaskParams.getCourseParams();
            mOriginalRole = mTaskParams.getOriginalRole();
            mHandleRole = mTaskParams.getHandleRole();
            isAudition = mTaskParams.isAudition();
        }

        if (mTaskParams.isTeacherVisitor()) {
            isAudition = false;
        }

        mTabLayout.setupWithViewPager(mViewPager);

        //来自直播两个角色一致
        if (isLive) {
            mOriginalRole = mHandleRole;
        }
        /*Bundle bundle = new Bundle();
        bundle.putSerializable("SectionResListVo", sectionResListVo);
        bundle.putBoolean("isLive", isLive);
        bundle.putBoolean("isHost", isHost);
        fragment0.setArguments(bundle);
        fragment0.setDoWorkListener(doWorkListener);
        fragment1.setArguments(bundle);
        fragment1.setOnDataUpdateListener(onDataUpdateListener);*/
        // @date   :2018/4/14 0014 下午 1:37
        // @func   :V5.5对批阅页面的更改
        // radioButton0.setChecked(true);
        // tabPagerAdapter = new SlidePagerAdapter(getSupportFragmentManager());
        // viewPager.setAdapter(tabPagerAdapter);
        // @date   :2018/4/14 0014 下午 1:39
        // @func   :V5.5对批阅页面的更改
        /*viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                hideKeyboard();
                if(i == 0){
                    radioButton0.setChecked(true);
                }else{
                    radioButton1.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });*/
        // @date   :2018/4/14 0014 下午 1:38
        // @func   :V5.5对批阅页面的更改
        /*radioButton0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager.getCurrentItem() != 0){
                    viewPager.setCurrentItem(0);
                    hideKeyboard();
                }
            }
        });
        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager.getCurrentItem() != 1){
                    viewPager.setCurrentItem(1);
                    hideKeyboard();
                }
            }
        });*/
        initImageConfig();
        initViews();
    }

    /**
     * LQwawa发送广播的api
     */
    public static void triggerSendBroadcast() {
        LocalBroadcastManager mManager = LocalBroadcastManager.getInstance(UIUtil.getContext());
        Intent intent = new Intent();
        intent.setAction("com.galaxyschool.app.wawaschool.Action_Mark_score");
        mManager.sendBroadcast(intent);
    }

    TaskCommitListFragment.DoWorkListener doWorkListener = new TaskCommitListFragment.DoWorkListener() {
        @Override
        public void onDoWork() {
            if(mCourseParams != null && mCourseParams.getLibraryType() == OrganLibraryType.TYPE_TEACHING_PLAN &&
                    (mCourseParams.isClassCourseEnter()  || mCourseParams.isMyCourse())) {
                ToastUtil.showToast(SectionTaskDetailsActivity.this, R.string.teaching_plan_do_homework_tip);
                return;
            }
            doTask();
        }

        @Override
        public void onSpeechEvaluation() {
            if(mCourseParams != null && mCourseParams.getLibraryType() == OrganLibraryType.TYPE_TEACHING_PLAN &&
                    (mCourseParams.isClassCourseEnter()  || mCourseParams.isMyCourse())) {
                ToastUtil.showToast(SectionTaskDetailsActivity.this,
                        R.string.teaching_plan_do_homework_tip);
                return;
            }
            doSpeechEvaluation();
        }

        @Override
        public void onStatisticalScores(@NonNull List<LqTaskCommitVo> data) {
            requestDataWithStatisticalScores();
        }

        @Override
        public void onItemClick(@NonNull LqTaskCommitVo vo, boolean isCheckMark, int sourceType, boolean taskCourseWare) {
            if (EmptyUtil.isEmpty(sectionResListVo) || EmptyUtil.isEmpty(vo)) {
                return;
            }
            
            if (vo.isSpeechEvaluation() || vo.isVideoType()) {
                // 点击的是语音评测的cell
                checkSpeechTaskDetail(vo, isCheckMark);
            } else {
                checkMarkTaskDetail(SectionTaskDetailsActivity.this,
                        mHandleRole, sectionResListVo, vo,
                        isCheckMark, sourceType, taskCourseWare);
            }
        }

        @Override
        public void onClickCommitListItem(SectionTaskCommitListVo vo) {
            clickCommitListItem(vo);
        }

        @Override
        public void openCourseWareDetails(String resId, int resType,
                                          @NonNull String resTitle, int screenType,
                                          @NonNull String resourceUrl, @Nullable String
                                                  resourceThumbnailUrl, int commitTaskId) {
            updateCommitTaskResId(resId, resType, resTitle, screenType, resourceUrl, resourceThumbnailUrl, commitTaskId);
        }

        @Override
        public void openCourseWareDetails(@NonNull LqTaskCommitVo vo) {
            openSpeechEvaluationCourseWareDetails(vo);
        }

        @Override
        public void onShareCourseWare(@NonNull LqTaskCommitVo vo) {
            shareCourseWare(vo);
        }
    };

    /**
     * 请求最新数据做成绩统计
     */
    private void requestDataWithStatisticalScores() {
        if (EmptyUtil.isNotEmpty(sectionResListVo)) {
            String studentId = getStudentId();
            // Mooc大厅的入口，拉取列表，不需要传 机构Id和ClassId,因为要拉所有的已经提交的学习任务
            // 但是提交是需要的
            String schoolId = mCourseParams.getSchoolId();
            String classId = mCourseParams.getClassId();

            if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_MOOC_ENTER) {
                schoolId = null;
                classId = null;
            } else if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER) {
                // 学程馆学习任务入口
                // 课程发生了绑定
                // 如果绑定的机构Id等于学程馆的Id 提交和列表都是用学程馆的机构Id， 只有提交才传ClassId
                // 如果绑定的机构Id不等于学程馆的Id 列表用学程馆的Id
                if (mCourseParams.isBindClass()) {
                    if (TextUtils.equals(mCourseParams.getSchoolId(), mCourseParams.getBindSchoolId())) {
                        // 学程馆Id和绑定的Id,相等
                        schoolId = mCourseParams.getBindSchoolId();
                        classId = null;
                    }
                } else {
                    schoolId = mCourseParams.getSchoolId();
                    classId = null;
                }
            }

            // 只有班级学程有成绩统计，pager为null， 拉取全部，commitType传0拉取所有提交列表
            LessonHelper.getNewCommittedTaskByTaskId(sectionResListVo.getTaskId(),
                    studentId,
                    classId,
                    schoolId, null, TaskCommitParams.TYPE_ALL,
                    pagerArgs, orderByType, new DataSource.Callback<LqTaskCommitListVo>() {
                        @Override
                        public void onDataNotAvailable(int strRes) {
                        }

                        @Override
                        public void onDataLoaded(LqTaskCommitListVo lqTaskCommitListVo) {
                            SectionTaskDetailsActivity.this.mLqTaskCommitListVo = lqTaskCommitListVo;
                            List<LqTaskCommitVo> data = mLqTaskCommitListVo.getListCommitTaskOnline();
                            // doWorkListener.onStatisticalScores(data);
                            doStatisticalScores(data);
                        }
                    });
        }
    }

    /**
     * 做听读课or读写单
     */
    protected void doTask() {

    }

    /**
     * 打开语音评测
     */
    protected void doSpeechEvaluation() {

    }

    /**
     * 点击cell事件回调
     *
     * @param vo               model
     * @param isCheckImmediate true 点评事件
     */
    protected void checkSpeechTaskDetail(@NonNull LqTaskCommitVo vo, boolean isCheckImmediate) {

    }

    /**
     * 打开成绩统计
     */
    protected void doStatisticalScores(@NonNull List<LqTaskCommitVo> data) {

    }

    protected void clickCommitListItem(SectionTaskCommitListVo vo) {

    }

    protected void commitStudentStudyTask(TaskUploadBackVo taskUploadBackVo, int roleType) {

    }

    protected void updateCommitTaskResId(String resId, int resType,
                                         String resTitle, int screenType,
                                         String resourceUrl, String resourceThumbnailUrl,
                                         int commitTaskId) {
    }

    protected void openSpeechEvaluationCourseWareDetails(@NonNull LqTaskCommitVo vo) {

    }

    protected void shareCourseWare(@NonNull LqTaskCommitVo vo) {

    }

    /**
     * 打开课件详情
     *
     * @param resId                资源Id
     * @param resType              资源类型
     * @param resTitle             资源标题
     * @param screenType           屏幕类型 默认传 0
     * @param resourceUrl          资源Url地址
     * @param resourceThumbnailUrl 资源缩略图地址
     */
    protected void openCourseWareDetails(String resId, int resType,
                                         @NonNull String resTitle, int screenType,
                                         @NonNull String resourceUrl, @Nullable String resourceThumbnailUrl) {

    }

    TaskCommentListFragment.OnDataUpdateListener onDataUpdateListener =
            new TaskCommentListFragment.OnDataUpdateListener() {
                @Override
                public void onDataUpdate(int count) {
                    // @date   :2018/4/14 0014 下午 1:38
                    // @func   :V5.5对批阅页面的更改
            /*radioButton1.setText(getResources().getString(R.string.discussion_count));
            radioButton1.append("(" + count + ")");*/
                }
            };

    protected void initImageConfig() {
        int p_width = Math.min(activity.getWindowManager().getDefaultDisplay().getWidth()
                , activity.getWindowManager().getDefaultDisplay().getHeight());
        img_width = (p_width - DisplayUtil.dip2px(activity, 20)) / 2;
        img_height = img_width * 9 / 16;

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setCrop(false)
                .setRadius(DisplayUtil.dip2px(activity, 8))
                .setLoadingDrawableId(R.drawable.img_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.img_def)//加载失败后默认显示图片
                .build();
    }

    protected void initViews() {
        //初始化顶部工具条
        topBar.setBack(true);
        if (sectionResListVo != null) {
            /*if (sectionResListVo.getResType() == 23) {
                topBar.setTitle(getResources().getString(R.string.do_task));
            } else {
                topBar.setTitle(getResources().getString(R.string.retell_course));
            }*/
            if (sectionResListVo.getTaskType() == 3) {
                topBar.setTitle(getResources().getString(R.string.do_task));
            } else if (sectionResListVo.getTaskType() == 2) {
                topBar.setTitle(getResources().getString(R.string.retell_course));
            } else if (sectionResListVo.getTaskType() == 5) {
                topBar.setTitle(getResources().getString(R.string.label_lecture_course));
            } else if (sectionResListVo.getTaskType() == 6) {
                topBar.setTitle(getResources().getString(R.string.dubbing));
            }
            // 加载Tab资源
            if (sectionResListVo.getTaskType() == 2) {
                // 复述课件
                String[] titles = UIUtil.getStringArray(R.array.label_new_lesson_task_tab_array);
                mTabTexts = new ArrayList<>(Arrays.asList(titles));
            } else if (sectionResListVo.getTaskType() == 3) {
                // 做读写单
                String[] titles = UIUtil.getStringArray(R.array.label_lesson_task_tab_array);
                mTabTexts = new ArrayList<>(Arrays.asList(titles));
            } else if (sectionResListVo.getTaskType() == 5) {
                // 试讲
                String[] titles = UIUtil.getStringArray(R.array.label_lecture_lesson_task_tab_array);
                mTabTexts = new ArrayList<>(Arrays.asList(titles));
            } else if (sectionResListVo.getTaskType() == 6) {
                // Q配音
                String[] titles = UIUtil.getStringArray(R.array.dubbing_task_tab_array);
                mTabTexts = new ArrayList<>(Arrays.asList(titles));
            }
        }

        mResIcon = (ImageView) findViewById(R.id.iv_res_icon);
        mTvTotalGrade = (TextView) findViewById(R.id.tv_total_grade);
        mResName = (TextView) findViewById(R.id.tv_res_name);
        mFromLayout = (FrameLayout) findViewById(R.id.from_layout);
        mTvFromCourse = (TextView) findViewById(R.id.tv_from_course);
        mFromLayout.setOnClickListener(this);
        updateData();
    }

    protected void updateData() {
        getData();
    }

    protected void updateViews() {
        // @date   :2018/4/16 0016 下午 1:55
        // @func   :V5.5需求更改
        /*if (sectionTaskDetailsVo != null) {
            if (sectionTaskDetailsVo.getOrigin() != null) {
                SectionTaskOriginVo originVo = sectionTaskDetailsVo.getOrigin();
                if (StringUtils.isValidString(originVo.getThumbnail())) {
                    // x.image().bind(coverIv, originVo.getThumbnail(), imageOptions);
                }





            }
        }*/
        // LQLessonIconHandler.fillImage(mResIcon, sectionResListVo);
        // @date   :2018/4/26 0026 下午 1:59
        // @func   :V5.5版本,显示有问题
        // mResName.setText("" + originVo.getName());
        if (sectionResListVo.isAutoMark()) {
            mTvTotalGrade.setVisibility(View.VISIBLE);
            StringUtil.fillSafeTextView(mTvTotalGrade, String.format(UIUtil.getString(R.string.label_total_gradle), sectionResListVo.getPoint()));
        } else {
            mTvTotalGrade.setVisibility(View.GONE);
        }
        mResName.setText(sectionResListVo.getName());

        if (libraryType== OrganLibraryType.TYPE_TEACHING_PLAN){
            mFromLayout.setVisibility(View.VISIBLE);
            mTvFromCourse.setVisibility(View.VISIBLE);
            mTvFromCourse.setText(String.format(UIUtil.getString(R.string.label_from_course), sectionResListVo.getLinkCourseName()));
        }else {
            mTvFromCourse.setVisibility(View.GONE);
            mFromLayout.setVisibility(View.GONE);
        }
        if (/*sectionTaskDetailsVo.getCommitList() != null &&*/ sectionResListVo != null) {
            String studentId = getStudentId();
            // Mooc大厅的入口，拉取列表，不需要传 机构Id和ClassId,因为要拉所有的已经提交的学习任务
            // 但是提交是需要的
            String schoolId = mCourseParams.getSchoolId();
            String classId = mCourseParams.getClassId();

            if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_MOOC_ENTER) {
                schoolId = null;
                classId = null;
            } else if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER) {
                // 学程馆学习任务入口
                // 课程发生了绑定
                // 如果绑定的机构Id等于学程馆的Id 提交和列表都是用学程馆的机构Id， 只有提交才传ClassId
                // 如果绑定的机构Id不等于学程馆的Id 列表用学程馆的Id
                if (mCourseParams.isBindClass()) {
                    if (TextUtils.equals(mCourseParams.getSchoolId(), mCourseParams.getBindSchoolId())) {
                        // 学程馆Id和绑定的Id,相等
                        schoolId = mCourseParams.getBindSchoolId();
                        classId = null;
                    }
                } else {
                    schoolId = mCourseParams.getSchoolId();
                    classId = null;
                }
            }

            if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_CLASS_ENTER) {
                pagerArgs = new PagerArgs(0, 10000);
            } else {
                pagerArgs = new PagerArgs(0, AppConfig.PAGE_SIZE);
            }

            // 使用新接口,拉数据
            LessonHelper.getNewCommittedTaskByTaskId(sectionResListVo.getTaskId(),
                    studentId,
                    classId,
                    schoolId, null, TaskCommitParams.TYPE_ALL, pagerArgs, 0,
                    new DataSource.Callback<LqTaskCommitListVo>() {
                        @Override
                        public void onDataNotAvailable(int strRes) {
                        }

                        @Override
                        public void onDataLoaded(LqTaskCommitListVo lqTaskCommitListVo) {
                            SectionTaskDetailsActivity.this.mLqTaskCommitListVo = lqTaskCommitListVo;
                            if (EmptyUtil.isEmpty(mLqTaskCommitListVo)) return;
                            ImageUtil.fillDefaultView(mResIcon, lqTaskCommitListVo.getTaskInfo().getResThumbnailUrl());

                            mFragments.clear();

                            // TODO 新需求更改
                            if (!EmptyUtil.isEmpty(lqTaskCommitListVo)
                                    && !EmptyUtil.isEmpty(lqTaskCommitListVo.getTaskInfo())
                                    && EmptyUtil.isEmpty(lqTaskCommitListVo.getTaskInfo().getDiscussContent())) {
                                // 没有任务要求
                                mTabTexts.remove(0);
                            } else {
                                // 有任务要求
                                mFragments.add(MissionRequireFragment.getInstance(null));
                            }

                            orderByType = 0;
                            if ((mOriginalRole == UserHelper.MoocRoleType.EDITOR || mOriginalRole == UserHelper.MoocRoleType.TEACHER)
                                    && !isEvalutedCourse()) {
                                orderByType = 1;
                            }

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("SectionResListVo", sectionResListVo);
                            bundle.putBoolean("isLive", isLive);
                            bundle.putBoolean("isHost", isHost);
                            bundle.putInt(TaskCommitListFragment.KEY_EXTRA_ROLE_TYPE, mOriginalRole);
                            bundle.putString("examId", examId);
                            bundle.putBoolean(TaskCommitListFragment.KEY_ROLE_FREE_USER, isAudition);
                            TaskCommitParams params = TaskCommitParams.build(mTaskParams);
                            if (sectionResListVo.getTaskType() == 2 || sectionResListVo.getTaskType() == 5) {
                                params.setCommitType(TaskCommitParams.TYPE_RETELL_COMMIT);
                            } else if (sectionResListVo.getTaskType() == 3 || sectionResListVo.getTaskType() == 6) {
                                params.setCommitType(TaskCommitParams.TYPE_ALL);
                            }
                            params.setOrderByType(orderByType);
                            params.setPagerArgs(pagerArgs);

                            bundle.putSerializable(TaskCommitListFragment.FRAGMENT_BUNDLE_OBJECT, params);

                            TaskCommitListFragment retellFragment = new TaskCommitListFragment();
                            retellFragment.setArguments(bundle);
                            retellFragment.setDoWorkListener(doWorkListener);
                            mFragment0 = retellFragment;
                            // 复述列表
                            mFragments.add(retellFragment);


                            // 评测列表
                            if (sectionResListVo.getTaskType() == 2 &&
                                    SectionResListVo.EXTRAS_AUTO_READ_OVER.equals(sectionResListVo.getResProperties())) {
                                // 听读课,并且是支持语音评测才添加评测列表
                                TaskCommitListFragment speechFragment = new TaskCommitListFragment();
                                Bundle _bundle = (Bundle) bundle.clone();
                                TaskCommitParams _params = (TaskCommitParams) params.clone();
                                _params.setCommitType(TaskCommitParams.TYPE_SPEECH_EVALUATION);
                                _bundle.putSerializable(TaskCommitListFragment.FRAGMENT_BUNDLE_OBJECT, _params);

                                speechFragment.setArguments(_bundle);
                                speechFragment.setDoWorkListener(doWorkListener);

                                mFragment1 = speechFragment;
                                mFragments.add(speechFragment);
                            } else if (sectionResListVo.getTaskType() == 2) {
                                // 没有评测列表
                                if (!EmptyUtil.isEmpty(lqTaskCommitListVo)
                                        && !EmptyUtil.isEmpty(lqTaskCommitListVo.getTaskInfo())
                                        && EmptyUtil.isEmpty(lqTaskCommitListVo.getTaskInfo().getDiscussContent())) {
                                    // 没有任务要求,删除Tab Text 1
                                    mTabTexts.remove(1);
                                    // 第一个Tab改标题文本  复述列表改为提交列表
                                    mTabTexts.remove(0);
                                    mTabTexts.add(0, UIUtil.getString(R.string.label_committed_list));
                                } else {
                                    mTabTexts.remove(2);
                                    // 第二个Tab改标题文本  复述列表改为提交列表
                                    mTabTexts.remove(1);
                                    mTabTexts.add(1, UIUtil.getString(R.string.label_committed_list));
                                }
                            }

                            // 讨论列表
                            TaskCommentListFragment commentFragment = new TaskCommentListFragment();
                            commentFragment.setArguments(bundle);
                            mFragments.add(commentFragment);
                            mTabLayout.setVisibility(View.VISIBLE);
                            mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), false));
                            mTabLayout.setupWithViewPager(mViewPager);
                            mViewPager.setOffscreenPageLimit(mTabTexts.size());




                            /*if (!EmptyUtil.isEmpty(lqTaskCommitListVo)
                                    && !EmptyUtil.isEmpty(lqTaskCommitListVo.getTaskInfo())
                                    && EmptyUtil.isEmpty(lqTaskCommitListVo.getTaskInfo().getDiscussContent())) {
                                // 没有任务要求
                                mTabLayout.setVisibility(View.VISIBLE);
                                mTabList.remove(0);
                                // mViewPager = (ViewPager) findViewById(R.id.viewpager);
                                mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), false));
                                mTabLayout.setupWithViewPager(mViewPager);
                                mViewPager.setOffscreenPageLimit(mTabList.size());
                            } else {
                                // 有任务要求
                                mTabLayout.setVisibility(View.VISIBLE);
                                mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), true));
                                mTabLayout.setupWithViewPager(mViewPager);
                                mViewPager.setOffscreenPageLimit(mTabList.size());
                            }*/

                            /*if (!EmptyUtil.isEmpty(fragment0)) {
                                fragment0.updateViews(lqTaskCommitListVo);
                                // 既然都网络请求TaskInfo了,说明 sectionResListVo 肯定不为空
                                // 重新Fill一下资源
                                fragment0.fillBtnResource(sectionResListVo);
                            }

                            if (!EmptyUtil.isEmpty(fragment1)) {
                                fragment1.updateViews(lqTaskCommitListVo);
                            }

                            if(!EmptyUtil.isEmpty(fragment2)){
                                fragment2.updateData(sectionResListVo);
                            }*/

                            if (EmptyUtil.isNotEmpty(mFragments)) {
                                for (Fragment fragment : mFragments) {
                                    if (fragment instanceof MissionRequireFragment) {
                                        MissionRequireFragment _fragment = (MissionRequireFragment) fragment;
                                        _fragment.updateViews(lqTaskCommitListVo);
                                    }

                                    if (fragment instanceof TaskCommitListFragment) {
                                        TaskCommitListFragment _fragment = (TaskCommitListFragment) fragment;
                                        _fragment.updateViews(lqTaskCommitListVo);
                                        _fragment.fillBtnResource(sectionResListVo);
                                    }

                                    if (fragment instanceof TaskCommentListFragment) {
                                        TaskCommentListFragment _fragment = (TaskCommentListFragment) fragment;
                                        _fragment.updateData(sectionResListVo);
                                    }
                                }
                            }
                        }
                    });

        }
    }

    /**
     * 学生穿StudentId,老师,主编,小编不传,家长穿孩子的memberId;
     *
     * @return
     */
    protected String getStudentId() {
        String memberId = null;
        if (mOriginalRole == UserHelper.MoocRoleType.TEACHER ||
                mOriginalRole == UserHelper.MoocRoleType.EDITOR ||
                mTaskParams.isAudition()) {
            if (mTaskParams.isTeacherVisitor()) {
                memberId = activity.getIntent().getStringExtra("memberId");
            } else {
                // 如果是主编和小编,或者是试听,就不传
                memberId = "";
            }
        } else if (mOriginalRole == UserHelper.MoocRoleType.STUDENT) {
            // 如果是学生,就传自己的Id
            memberId = UserHelper.getUserId();
        } else {
            // 剩下的就是家长,传孩子的Id
            memberId = activity.getIntent().getStringExtra("memberId");
        }
        return memberId;
    }

    private boolean isEvalutedCourse() {
        if (sectionResListVo != null) {
            return SectionResListVo.EXTRAS_AUTO_READ_OVER.equals(sectionResListVo.getResProperties());
        }
        return false;
    }

    protected void getData() {
        if (sectionResListVo == null) {
            return;
        }

        updateViews();

        /*if (TextUtils.isEmpty(sectionResListVo.getId())) {
         *//*sectionTaskDetailsVo = new SectionTaskDetailsVo();
            sectionTaskDetailsVo.setOrigin(sectionResListVo.getSectionTaskOriginVo());*//*

        } else {
            RequestVo requestVo = new RequestVo();
            requestVo.addParams("token", activity.getIntent().getStringExtra("memberId"));
            requestVo.addParams("cwareId", sectionResListVo.getId());
            requestVo.addParams("type", 0);
            LogUtil.d(TAG, requestVo.getParams());
            RequestParams params =
                    new RequestParams((isLive ? AppConfig.ServerUrl.GetLiveResCommitList
                            : AppConfig.ServerUrl.cwareCommitList) + requestVo.getParams());
            params.setConnectTimeout(10000);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    SectionTaskDetailsVo result = JSON.parseObject(s,
                            new TypeReference<SectionTaskDetailsVo>() {
                            });
                    *//*if (result != null && result.getCode() == 0) {
                        sectionTaskDetailsVo = result;
                        if (sectionTaskDetailsVo.getData() != null) {
                            sectionTaskDetailsVo = sectionTaskDetailsVo.getData();
                        }
                        sectionResListVo.setResId(sectionTaskDetailsVo.getOrigin().getId());
                        updateViews();
                    } else {
                        // 资源不存在
                        UIUtil.showToastSafe(R.string.tip_not_find_ware_course_source);
                        finish();
                    }*//*
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
        }*/
    }

    public void updateCommentCount() {
        if (sectionResListVo == null) {
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("TaskId", sectionResListVo.getResId());
        requestVo.addParams("SortStudentId", UserHelper.getUserId());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GET_COMMITTED_TASK_BY_TASK_ID_URL);
        params.setConnectTimeout(10000);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LqResponseDataVo<LqTaskCommitListVo> result = JSON.parseObject(s,
                        new TypeReference<LqResponseDataVo<LqTaskCommitListVo>>() {
                        });
                if (result.isHasError() == false) {
                    int commentCount = result.getModel().getData().getTaskInfo().getCommentCount();
                    String commentCountText = commentCount > 99 ? "99+"
                            : String.valueOf(commentCount);
                    /*radioButton1.setText(String.format(
                            getResources().getString(R.string.discussion_count)
                                    + "（%s）", commentCountText));*/
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // @date   :2018/4/14 0014 下午 1:39
            // @func   :V5.5对批阅页面的更改
            /*if (requestCode == SlideActivityForRetell.Rc_retll
                    || requestCode == TaskEditActivity.Rc_task_commit) {
                setResult(RESULT_OK);
                getData();
                updateCommentCount();
            }*/
        }
    }

    /*private class SlidePagerAdapter extends OrderPagerAdapter {
        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
			*//*
     * IMPORTANT: This is the point. We create a RootFragment acting as
     * a container for other fragments
     *//*
//            return fragment0;
            if (position == 0) {
                return fragment0;
            }
            else {
                return fragment1;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    }*/


    // -------------------------------------隐藏输入法-----------------------------------------------------
    // 获取点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        boolean result = super.dispatchTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
            }
        }
        return result;
    }

    // 判定是否需要隐藏
    protected boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    // 隐藏软键盘
    protected void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected void uploadCourseFile(String filePath, String fileName, long duration, String description) {
        RequestVo requestVo = new RequestVo();
        File file = new File(filePath);
//        String fileName = BaseUtils.getFileNameFromPath(filePath);
        requestVo.addParams("fileName", fileName);
        if (fileName.contains(".")) {
            requestVo.addParams("nickName", fileName.substring(0, fileName.indexOf(".")));
        } else {
            requestVo.addParams("nickName", fileName);
        }
        requestVo.addParams("createName", UserHelper.getUserName());
        requestVo.addParams("memberId", UserHelper.getUserId());
        requestVo.addParams("account", UserHelper.getLastAccount());
        requestVo.addParams("resType",
                (sectionResListVo.getResType() == 18 || sectionResListVo.getResType() == 23)
                        ? 18 : 19);
        requestVo.addParams("type", 5);
        requestVo.addParams("colType", 1);
        requestVo.addParams("screenType", activity.getIntent().getIntExtra("orientation", 0));
        requestVo.addParams("description", description);
        requestVo.addParams("size", file.length());
        requestVo.addParams("totalTime", duration);
        requestVo.addParams("message", "");
        requestVo.addParams("point", "");
        String paramString = requestVo.getParamsWithoutToken();
        try {
            paramString = URLEncoder.encode(paramString, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.uploadAndCreate + paramString);
        params.setConnectTimeout(60 * 10000);
        params.addBodyParameter("file", new File(filePath));
        params.setMultipart(true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                try {
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.upload_failed) + ":"
                                    + throwable.getMessage());
                } catch (Exception ex) {

                }
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String s) {
                ResponseVo<List<TaskUploadBackVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<TaskUploadBackVo>>>() {
                        });
                if (result.getCode() == 0) {
                    if (result.getData() != null && result.getData().size() > 0) {
                        BaseUtils.deleteFile(zipFilePath);
                        commitStudentStudyTask(result.getData().get(0), mHandleRole);
//                        flagRead(result.getData().get(0), sectionResListVo.getId()
//                                , "" + result.getData().get(0).getId());
                    }

                    return;
                } else {
                    closeProgressDialog();
                }

                ToastUtil.showToast(activity,
                        result.getMessage());

            }
        });
    }

    protected void flagRead(final TaskUploadBackVo taskUploadBackVo, String id, String resId) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("cwareId", id);
        requestVo.addParams("resId", resId);
        int resType = sectionResListVo.getResType();
        if (resType == 23) {
            resType = 18;
        }
        requestVo.addParams("resType", resType);
        RequestParams params =
                new RequestParams((isLive ? AppConfig.ServerUrl.CommitLiveRes
                        : AppConfig.ServerUrl.setReaded) + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    if (EmptyUtil.isNotEmpty(taskUploadBackVo)) {
                        if (TaskSliderHelper.onTaskSliderListener != null) {
                            TaskSliderHelper.onTaskSliderListener.studyInfoRecord(
                                    activity, sectionResListVo.getResId(),
                                    taskUploadBackVo,
                                    sectionResListVo.getResType(),
                                    getSourceType());
                        }
                    }

                    if (EmptyUtil.isNotEmpty(mFragments)) {
                        for (Fragment fragment : mFragments) {
                            if (fragment instanceof TaskCommitListFragment) {
                                TaskCommitListFragment _fragment = (TaskCommitListFragment) fragment;
                                _fragment.updateData(false);
                            }
                        }
                    }

                    /*if (!EmptyUtil.isEmpty(fragment0)) {
                        fragment0.updateData();
                    }*/

                    // 不管听说课还是读写单,都发送广播
                    CourseDetails.courseDetailsTriggerStudyTask(getApplicationContext(), CourseDetailsItemFragment.LQWAWA_ACTION_READ_WRITE_SINGLE);
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

    /**
     * @param activity       上下文
     * @param roleType       角色信息
     *                       roleType == ROLE_TYPE_TEACHER [小编 或者 任务的创建着]
     *                       roleType == ROLE_TYPE_EDITOR [主编]
     *                       roleType == ROLE_TYPE_STUDENT [学生身份]
     *                       roleType == ROLE_TYPE_VISITOR [无权限对该任务进行操作 俗称 游客]
     * @param task           任务对象
     * @param studentCommit  学生提交的任务
     * @param isCheckMark    是不是查看批阅 （查看批阅[true] 查看item进入提问和批阅的详情[false]）
     * @param sourceType     资源type
     * @param taskCourseWare 是否是读写单课件
     */
    protected void checkMarkTaskDetail(Activity activity,
                                       int roleType,
                                       SectionResListVo task,
                                       LqTaskCommitVo studentCommit,
                                       boolean isCheckMark,
                                       int sourceType,
                                       boolean taskCourseWare) {

    }

    protected int getSourceType() {
        return activity.getIntent().getBooleanExtra("isLive", false) ?
                (activity.getIntent().getBooleanExtra(SectionTaskDetailsActivity
                        .KEY_IS_FROM_MY, false)
                        ? SourceFromType.MY_ONLINE_LIVE : SourceFromType.ONLINE_LIVE)
                : (activity.getIntent().getBooleanExtra(SectionTaskDetailsActivity
                .KEY_IS_FROM_MY, false)
                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.from_layout) {
            boolean canEdit = TextUtils.equals(UserHelper.getUserId(), memberId);
            if (EmptyUtil.isNotEmpty(sectionResListVo)){
                courseId = String.valueOf(sectionResListVo.getLinkCourseId());
            }
            CourseDetailsActivity.start(SectionTaskDetailsActivity.this,courseId,canEdit,memberId,mCourseParams.isAuthorized(),mCourseParams,true);
        }
    }

    // protected TaskCommitListFragment fragment0;
    // private MissionRequireFragment fragment1;
    // private TaskCommentListFragment fragment2;

    /**
     * ViewPager显示的Adapter
     */
    public class PagerAdapter extends FragmentPagerAdapter {
        // 有无任务要求
        private boolean hasTaskRequire;

        public PagerAdapter(FragmentManager fm, boolean hasTaskRequire) {
            super(fm);
            this.hasTaskRequire = hasTaskRequire;
        }

        @Override
        public Fragment getItem(int position) {
            /*Fragment fragment = null;
            Bundle bundle = new Bundle();
            bundle.putSerializable("SectionResListVo", sectionResListVo);
            bundle.putBoolean("isLive", isLive);
            bundle.putBoolean("isHost", isHost);
            bundle.putInt(TaskCommitListFragment.KEY_EXTRA_ROLE_TYPE, mOriginalRole);
            bundle.putString("examId", examId);
            bundle.putBoolean(TaskCommitListFragment.KEY_ROLE_FREE_USER,isAudition);
            TaskCommitParams params = TaskCommitParams.build(mTaskParams);
            bundle.putSerializable(TaskCommitListFragment.FRAGMENT_BUNDLE_OBJECT,params);
            if (hasTaskRequire) {
                if (position == 0) {
                    fragment1 = (MissionRequireFragment) MissionRequireFragment.getInstance(null);
                    // fragment1.setArguments(bundle);
                    // fragment1.setOnDataUpdateListener(onDataUpdateListener);
                    fragment = fragment1;
                } else if (position == 1) {

                    fragment0 = new TaskCommitListFragment();
                    fragment0.setArguments(bundle);
                    fragment0.setDoWorkListener(doWorkListener);
                    fragment = fragment0;
                } else if(position == 2){
                    fragment2 = new TaskCommentListFragment();
                    fragment2.setArguments(bundle);
                    fragment = fragment2;
                }
            } else {
                if(position == 0){
                    fragment0 = new TaskCommitListFragment();
                    fragment0.setArguments(bundle);
                    fragment0.setDoWorkListener(doWorkListener);
                    fragment = fragment0;
                }else{
                    fragment2 = new TaskCommentListFragment();
                    fragment2.setArguments(bundle);
                    fragment = fragment2;
                }

            }*/


            /*return fragment;*/
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            /*return hasTaskRequire ? mTabTexts.length : 2;*/
            return mTabTexts.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTexts.get(position);
        }
    }

    private DialogHelper.LoadingDialog progressDialog;

    /**
     * 显示提示框
     */
    protected void showProgressDialog(String msg) {

        if ((!isFinishing()) && (progressDialog == null)) {
//            progressDialog = new ProgressDialog(activity, ProgressDialog.THEME_HOLO_LIGHT);
            progressDialog = DialogHelper.getIt(this).GetLoadingDialog(0);
        }

        this.progressDialog.setContent(msg);
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
    }

    /**
     * 关闭提示框
     */
    protected void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
