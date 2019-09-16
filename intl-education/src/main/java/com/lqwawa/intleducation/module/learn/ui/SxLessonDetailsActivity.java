package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.widgets.ExpandableTextView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.PagerSelectedAdapter;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.TreeView;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.RefreshUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.sxdetail.SxLessonSourceFragment;
import com.lqwawa.intleducation.module.discovery.ui.lesson.sxdetail.SxLessonSourceNavigator;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.chapter.CourseChapterParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.AddSubjectActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.ExerciseTypeVo;
import com.lqwawa.intleducation.module.learn.vo.SectionDetailsVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskListVo;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.tools.DialogHelper;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.util.DensityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SxLessonDetailsActivity extends AppCompatActivity implements View.OnClickListener, SxLessonSourceFragment.OnItemCheckBoxSelectedChanged {

    private static final int SUBJECT_SETTING_REQUEST_CODE = 1 << 1;
    // 是否是空中课堂老师
    public static final String KEY_EXTRA_ONLINE_TEACHER = "KEY_EXTRA_ONLINE_TEACHER";
    // 是否是游离的身份
    public static final String KEY_ROLE_FREE_USER = "KEY_ROLE_FREE_USER";
    public static final String ACTIVITY_BUNDLE_OBJECT = "ACTIVITY_BUNDLE_OBJECT";
    private static final String KEY_EXTRA_MULTIPLE_CHOICE_COUNT = "KEY_EXTRA_MULTIPLE_CHOICE_COUNT";
    private boolean mFreeUser;
    public static String COURSE_ID = "course_id";
    public static String SECTION_ID = "section_id";
    public static String SECTION_NAME = "section_name";
    public static String SECTION_TITLE = "section_title";
    public static String NEED_FLAG = "need_flag";
    public static String CAN_READ = "can_read";
    public static String CAN_EDIT = "can_edit";
    public static String STATUS = "status";
    public static String ISCONTAINASSISTANTWORK = "isContainAssistantWork";
    private static Activity activitys;
    private SxLessonSourceFragment currentSelectedFragment;
    private List<Fragment> fragments = new ArrayList<>();
    ;

    /**
     * @param activity               启动此界面的activity
     * @param courseId               课程id
     * @param sectionId              章节id
     * @param sectionName            章节名
     * @param sectionTitle           章节标题
     * @param needFlag               是否需要标记已读
     * @param canRead                是否可以查阅资源
     * @param canEdit                是否可以编辑资源/做任务单/附属课件
     * @param status                 章节的更新状态
     * @param memberId               用户id
     * @param isContainAssistantWork 是否显示助教工作平台
     * @param schoolId               机构id
     * @param isFreeUser             自由用户，浏览者
     * @param params                 课程大纲的核心参数信息
     */
    public static void start(Activity activity, String courseId, String sectionId,
                             String sectionName, String sectionTitle, boolean needFlag,
                             boolean canRead, boolean canEdit, int status, String memberId,
                             boolean isContainAssistantWork, String schoolId,
                             boolean isFromMyCourse, CourseVo courseVo, boolean isOnlineTeacher,
                             boolean isFreeUser, @NonNull CourseChapterParams params,
                             @Nullable Bundle extras) {
        if (activity instanceof Activity) activitys = activity;
        activity.startActivity(new Intent(activity, SxLessonDetailsActivity.class)
                .putExtra(COURSE_ID, courseId)
                .putExtra(SECTION_ID, sectionId)
                .putExtra(SECTION_NAME, sectionName)
                .putExtra(SECTION_TITLE, sectionTitle)
                .putExtra(NEED_FLAG, needFlag)
                .putExtra(CAN_READ, canRead)
                .putExtra(CAN_EDIT, canEdit)
                .putExtra(STATUS, status)
                .putExtra(ISCONTAINASSISTANTWORK, isContainAssistantWork)
                .putExtra(KEY_EXTRA_ONLINE_TEACHER, isOnlineTeacher)
                .putExtra("memberId", memberId)
                .putExtra("schoolId", schoolId)
                .putExtra(MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, isFromMyCourse)
                .putExtra(KEY_ROLE_FREE_USER, isFreeUser)
                .putExtra(CourseVo.class.getSimpleName(), courseVo)
                .putExtra(ACTIVITY_BUNDLE_OBJECT, params)
                .putExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK, extras));
    }

    public static void start(Activity activity, int taskType, int multipleChoiceCount, String courseId, String sectionId,
                             String sectionName, String sectionTitle, boolean needFlag,
                             boolean canRead, boolean canEdit, int status, String memberId,
                             boolean isContainAssistantWork, String schoolId,
                             boolean isFromMyCourse, CourseVo courseVo, boolean isOnlineTeacher,
                             boolean isFreeUser, @NonNull CourseChapterParams params,
                             LessonSourceParams lessonSourceParams,
                             @Nullable Bundle extras) {
        if (activity instanceof Activity) activitys = activity;
        activity.startActivity(new Intent(activity, SxLessonDetailsActivity.class)
                .putExtra("taskType", taskType)
                .putExtra(KEY_EXTRA_MULTIPLE_CHOICE_COUNT, multipleChoiceCount)
                .putExtra(COURSE_ID, courseId)
                .putExtra(SECTION_ID, sectionId)
                .putExtra(SECTION_NAME, sectionName)
                .putExtra(SECTION_TITLE, sectionTitle)
                .putExtra(NEED_FLAG, needFlag)
                .putExtra(CAN_READ, canRead)
                .putExtra(CAN_EDIT, canEdit)
                .putExtra(STATUS, status)
                .putExtra(ISCONTAINASSISTANTWORK, isContainAssistantWork)
                .putExtra(KEY_EXTRA_ONLINE_TEACHER, isOnlineTeacher)
                .putExtra("memberId", memberId)
                .putExtra("schoolId", schoolId)
                .putExtra(MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, isFromMyCourse)
                .putExtra(KEY_ROLE_FREE_USER, isFreeUser)
                .putExtra(CourseVo.class.getSimpleName(), courseVo)
                .putExtra(ACTIVITY_BUNDLE_OBJECT, params)
                .putExtra(LessonSourceParams.class.getSimpleName(), lessonSourceParams)
                .putExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK, extras));
    }


    private TopBar topBar;
    private ExpandableTextView textViewLessonIntroduction;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private String sectionTitle;
    private String courseId;
    private String sectionName;
    private String sectionId;
    private String memberId;
    private int status, taskType;
    private CourseVo courseVo;
    // 课程大纲参数
    private CourseChapterParams mChapterParams;
    private LessonSourceParams lessonSourceParams;
    private SectionDetailsVo sectionDetailsVo;
    // 是否是空中课堂老师过来的
    private boolean isOnlineTeacher;

    private FrameLayout mNewCartContainer;
    private TextView mTvWorkCart;
    private TextView mTvCartPoint;

    private LinearLayout mBottomLayout1;
    private LinearLayout mBottomLayout;
    private FrameLayout mSelectContainer;
    private FrameLayout mCartContainer;
    private FrameLayout mAddCartContainer;
    private Button mBtnAddHomework;
    private Button mSelectAll;
    private Button mBtnCart;
    private TextView mTvPoint;
    private Button mBtnAction;
    private LinearLayout mTopLayout;
    private LinearLayout mTaskLayout;
    private CourseEmptyView mEmptyPlanView;

    private boolean needFlag;
    private boolean canRead, isContainAssistantWork;
    private boolean canEdit = false;
    // 可以选择的最大条目
    private int mMultipleChoiceCount;
    private int maxSelect = 1;

    private List<String> mTabLists = new ArrayList<>();
    private Map<Integer, List<SectionResListVo>> addToCartInDifferentTypes = new HashMap<>();
    private List<SxLessonSourceNavigator> mTabSourceNavigator = new ArrayList<>();
    private static String[] mTypes = UIUtil.getStringArray(R.array.label_lesson_source_type);
    private ArrayList<SectionResListVo> selectedTask = new ArrayList<>();
    private DialogHelper.LoadingDialog loadingDialog;
    private List<ExerciseTypeVo> mExerciseTypeVoList = new ArrayList<ExerciseTypeVo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sx_lesson_details);

        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setBack(true);
        textViewLessonIntroduction = (ExpandableTextView) findViewById(R.id.lesson_introduction_tv);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mNewCartContainer = (FrameLayout) findViewById(R.id.new_cart_container);
        mTvWorkCart = (TextView) findViewById(R.id.tv_work_cart);
        mTvCartPoint = (TextView) findViewById(R.id.tv_cart_point);
        mBottomLayout1 = (LinearLayout) findViewById(R.id.bottom_layout_1);
        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mSelectContainer = (FrameLayout) findViewById(R.id.select_container);
        mCartContainer = (FrameLayout) findViewById(R.id.cart_container);
        mAddCartContainer = (FrameLayout) findViewById(R.id.action_container);
        mBtnAddHomework = (Button) findViewById(R.id.btn_add_homework);
        mSelectAll = (Button) findViewById(R.id.btn_all_select);
        mBtnCart = (Button) findViewById(R.id.btn_work_cart);
        mTvPoint = (TextView) findViewById(R.id.tv_point);
        mBtnAction = (Button) findViewById(R.id.btn_action);
        mTopLayout = (LinearLayout) findViewById(R.id.lesson_top_layout);
        mTaskLayout = (LinearLayout) findViewById(R.id.layout_task);
        mEmptyPlanView = findViewById(R.id.empty_plan_layout);

        int color = UIUtil.getColor(R.color.colorPink);
        int radius = DisplayUtil.dip2px(UIUtil.getContext(), 16);
        mTvPoint.setBackground(DrawableUtil.createDrawable(color, color, radius));

        courseId = getIntent().getStringExtra(COURSE_ID);
        sectionId = getIntent().getStringExtra(SECTION_ID);
        sectionName = getIntent().getStringExtra(SECTION_NAME);
        sectionTitle = getIntent().getStringExtra(SECTION_TITLE);
        memberId =  getIntent().getStringExtra("memberId");

        topBar.setTitle(sectionTitle);
        topBar.setTitleWide(DensityUtil.dip2px(120));

        isOnlineTeacher = getIntent().getBooleanExtra(KEY_EXTRA_ONLINE_TEACHER, false);
        needFlag = getIntent().getBooleanExtra(NEED_FLAG, false);
        canRead = getIntent().getBooleanExtra(CAN_READ, false);
        canEdit = getIntent().getBooleanExtra(CAN_EDIT, false);
        mFreeUser = getIntent().getBooleanExtra(KEY_ROLE_FREE_USER, false);
        if (getIntent().getExtras().containsKey(ACTIVITY_BUNDLE_OBJECT)) {
            mChapterParams = (CourseChapterParams) getIntent().getSerializableExtra(ACTIVITY_BUNDLE_OBJECT);
        }
        lessonSourceParams =
                (LessonSourceParams) getIntent().getSerializableExtra(LessonSourceParams.class.getSimpleName());
        status = getIntent().getIntExtra(STATUS, -1);
        taskType = getIntent().getIntExtra("taskType", -1);
        courseVo = (CourseVo) getIntent().getSerializableExtra(CourseVo.class.getSimpleName());
        mMultipleChoiceCount = getIntent().getIntExtra(KEY_EXTRA_MULTIPLE_CHOICE_COUNT, 10);
        // 判断是否显示BottomLayout
        CourseDetailParams courseParams = mChapterParams.getCourseParams();
        if (!mChapterParams.isTeacherVisitor() &&
                courseParams.isClassCourseEnter() &&
                courseParams.isClassTeacher() ||
                mChapterParams.isChoiceMode()) {

            mBottomLayout1.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.GONE);
            mNewCartContainer.setVisibility(View.VISIBLE);
            mNewCartContainer.setOnClickListener(this);

            mBtnAddHomework.setOnClickListener(this);
            mSelectAll.setOnClickListener(this);
            mSelectContainer.setOnClickListener(this);
            mCartContainer.setOnClickListener(this);
            mAddCartContainer.setOnClickListener(this);
        } else {
            mBtnAddHomework.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.GONE);
            mBottomLayout1.setVisibility(View.GONE);
        }

        boolean isVideoCourse =
                courseParams != null && (courseParams.getLibraryType() == OrganLibraryType.TYPE_VIDEO_LIBRARY
                        || (courseParams.getLibraryType() == OrganLibraryType.TYPE_BRAIN_LIBRARY && courseParams.isVideoCourse()));
        mTopLayout.setVisibility(isVideoCourse ? View.GONE : View.VISIBLE);

        //被动进入选择,并且是选择模式
        if (mChapterParams != null && mChapterParams.isChoiceMode() &&
                !mChapterParams.isInitiativeTrigger()) {
            mTopLayout.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.GONE);
            mBottomLayout1.setVisibility(View.GONE);
            mBtnAddHomework.setVisibility(View.GONE);
            mNewCartContainer.setVisibility(View.GONE);
            topBar.setRightFunctionText1(getString(R.string.ok), v -> {
                maxSelect = mMultipleChoiceCount;
                selectedTask.clear();
                int currentPosition = mViewPager.getCurrentItem();
                SxLessonSourceNavigator navigator = mTabSourceNavigator.get(currentPosition);
                List<TreeNode> selectedNodes = navigator.getChoiceResource();
                for (TreeNode selectedNode : selectedNodes) {
                    Object value = selectedNode.getValue();
                    if (value instanceof SectionResListVo) {
                        SectionResListVo vo = (SectionResListVo) value;
                        selectedTask.add(vo);
                    }
                }
                if (selectedTask.size() <= 0) {
                    ToastUtil.showToast(this, getString(R.string.str_select_tips));
                    return;
                } else {
                    if (selectedTask.size() > mMultipleChoiceCount) {
                        ToastUtil.showToast(this, getString(R.string.str_select_count_tips, maxSelect));
                        return;
                    }

                }
                // 学程馆选取资源使用的
                EventBus.getDefault().post(new EventWrapper(selectedTask, EventConstant.COURSE_SELECT_RESOURCE_EVENT));
                //数据回传
                setResult(Activity.RESULT_OK, getIntent().putExtra(CourseSelectItemFragment.RESULT_LIST, selectedTask));
                RefreshUtil.getInstance().clear();
                if (activitys != null) activitys.finish();
                finish();
            });
        }

        // 刷新数目
        refreshCartPoint();
        getData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 更新作业库条目数量
        refreshCartPoint();
    }

    //初始化数据
    private void getData() {
        String token = mChapterParams.getMemberId();
        LessonHelper.requestExerciseTypeListBySectionId(token, sectionId, new DataSource.Callback<List<ExerciseTypeVo>>() {
            @Override
            public void onDataLoaded(List<ExerciseTypeVo> exerciseTypeList) {
                SxLessonDetailsActivity.this.mExerciseTypeVoList = exerciseTypeList;
                //班级老师才有课中实施方案
                CourseDetailParams courseParams = mChapterParams.getCourseParams();
                if (EmptyUtil.isEmpty(exerciseTypeList)) {
                    return;
                } else if (exerciseTypeList.size() == 1 && (exerciseTypeList.get(0).getExerciseType() == 2)
                        && (mChapterParams.getRole() != UserHelper.MoocRoleType.TEACHER)) {
                    mTaskLayout.setVisibility(View.GONE);
                    mEmptyPlanView.setVisibility(View.VISIBLE);
                    return;
                }
                requestChapterTask();
                for (int i = 0; i < mExerciseTypeVoList.size(); i++) {
                    ExerciseTypeVo exerciseTypeVo = exerciseTypeList.get(i);
                    if (courseParams.isClassTeacher() && exerciseTypeVo.getExerciseType() == 2){
                        topBar.setRightFunctionText1(getString(R.string.class_implementation_plan), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CourseHelper.queryIfExistPlan(memberId, sectionId, courseParams.getClassId(),
                                        new DataSource.Callback<Boolean>() {
                                            @Override
                                            public void onDataNotAvailable(int strRes) {

                                            }

                                            @Override
                                            public void onDataLoaded(Boolean result) {
                                                TaskSliderHelper.onImplementationPlanListener.
                                                        enterImplementationPlanActivity(SxLessonDetailsActivity.this,
                                                                sectionId,memberId,courseId,courseParams.getClassId(),
                                                                !result);
                                            }
                                        });
                            }
                        });
                    }
                    if (exerciseTypeVo.getExerciseType() == 1) {
                        mTabLists.add(getResources().getString(R.string.label_sx_preview));
                    } else if (exerciseTypeVo.getExerciseType() == 2) {
                        if (mChapterParams.getRole() == UserHelper.MoocRoleType.TEACHER){
                            mTabLists.add(getResources().getString(R.string.label_sx_practice));
                        }
                    } else if (exerciseTypeVo.getExerciseType() == 3) {
                        mTabLists.add(getResources().getString(R.string.label_sx_review));
                    }
                }
            }

            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }
        });

    }

    private void requestChapterTask() {
        String token = mChapterParams.getMemberId();
        int role = 2;
        if (mChapterParams.getRole() == UserHelper.MoocRoleType.TEACHER) {
            role = 1;
        }
        String classId = "";
        if (role == 1 && mChapterParams.getCourseParams().isClassCourseEnter()) {
            classId = mChapterParams.getCourseParams().getClassId();
        }
        loadingDialog = DialogHelper.getIt(SxLessonDetailsActivity.this).GetLoadingDialog(0);
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LessonHelper.requestChapterStudyTask(languageRes, token, classId, courseId, sectionId, role, -1, new DataSource.Callback<SectionDetailsVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(SectionDetailsVo sectionDetailsVo) {
                SxLessonDetailsActivity.this.sectionDetailsVo = sectionDetailsVo;
                if (EmptyUtil.isEmpty(sectionDetailsVo)) return;
                updateView();
                loadingDialog.dismiss();
            }
        });
    }

    private void updateView() {
        if (sectionDetailsVo != null) {
            getIntent().putExtra(SECTION_NAME, sectionDetailsVo.getSectionName());
            topBar.setTitle(sectionDetailsVo.getSectionName());
            getIntent().putExtra(SECTION_TITLE, sectionDetailsVo.getSectionTitle());
            getIntent().putExtra(STATUS, sectionDetailsVo.getStatus());
            getIntent().putExtra("isPublic", sectionDetailsVo.isIsOpen());
            List<SectionTaskListVo> taskList = sectionDetailsVo.getTaskList();

            fragments.clear();
            if (EmptyUtil.isNotEmpty(taskList)) {

                LessonSourceParams params = LessonSourceParams.buildParams(mChapterParams);
                if (lessonSourceParams != null) {
                    params.setFilterArray(lessonSourceParams.getFilterArray());
                }
                boolean isDirect =
                        mChapterParams.isChoiceMode() && mChapterParams.isInitiativeTrigger();
                if (!isDirect) {
                    params.setAddMode(mBottomLayout.isActivated());
                    mCartContainer.setVisibility(View.VISIBLE);
                    mBottomLayout.setVisibility(View.GONE);
                    mBottomLayout1.setVisibility(View.VISIBLE);
                } else {
                    params.setAddMode(true);
                    mBottomLayout.setActivated(true);
                    mCartContainer.setVisibility(View.GONE);
                    mBottomLayout.setVisibility(View.VISIBLE);
                    mBottomLayout1.setVisibility(View.GONE);
                }
                //1 预习 2练习 3复习
                if (mTabLists.size()==1){
                    mTabLayout.setBackground(new ColorDrawable(UIUtil.getColor(R.color.colorAccent)));
                }
                for (int i = 0; i < mTabLists.size(); i++) {
                    int exerciseType = 0;
                    if (getResources().getString(R.string.label_sx_preview).equals(mTabLists.get(i))){
                        exerciseType =1;
                    }else  if (getResources().getString(R.string.label_sx_practice).equals(mTabLists.get(i))){
                        exerciseType =2;
                    }else  if (getResources().getString(R.string.label_sx_review).equals(mTabLists.get(i))){
                        exerciseType =3;
                    }
                    SxLessonSourceFragment fragment = SxLessonSourceFragment.newInstance(needFlag, canEdit, canRead, isOnlineTeacher, courseId, sectionId, status,
                            exerciseType, courseVo.getLibraryType(), taskType, mMultipleChoiceCount, params);
                    fragment.setOnItemCheckBoxSelectedChanged(this);
                    mTabSourceNavigator.add(fragment);
                    fragments.add(fragment);
                }
                if (!fragments.isEmpty())
                    currentSelectedFragment = (SxLessonSourceFragment) fragments.get(0);
            }

            mViewPager.setAdapter(new SxLessonSourcePagerAdapter(getSupportFragmentManager(), fragments));
            mViewPager.setOffscreenPageLimit(fragments.size());
            mTabLayout.setupWithViewPager(mViewPager);

            mViewPager.addOnPageChangeListener(mSelectedAdapter);

            textViewLessonIntroduction.setText(sectionDetailsVo.getIntroduction());
            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    initBottomLayout();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.cart_container) {
            // 取消
            boolean originalActivated = mBottomLayout.isActivated();
            if (originalActivated) {
                mBottomLayout.setActivated(!originalActivated);
                initBottomLayout();
                cancelResource();
            } else {
                // triggerWatchCart();
                handleSubjectSettingData(this, UserHelper.getUserId(), false);
            }
        } else if (viewId == R.id.action_container) {
            // 点击添加到作业库
            if (mChapterParams.isChoiceMode() && mChapterParams.isInitiativeTrigger()) {
                // 直接添加到作业库

                int currentPosition = mViewPager.getCurrentItem();
                SxLessonSourceNavigator navigator = mTabSourceNavigator.get(currentPosition);
                List<TreeNode> selectedNodes = navigator.getChoiceResource();
                if (EmptyUtil.isEmpty(selectedNodes)) {
                    UIUtil.showToastSafe(R.string.str_select_tips);
                    return;
                }
                if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
                    int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
                    if (count >= 0 && count < 6) {
                        int count1 = chooseResourceSum();
                        if (count + count1 > 6) {
                            int needCount = 6 - count;
                            UIUtil.showToastSafe(String.format(UIUtil.getString(R.string.label_work_cart_add_count_tip), needCount));
                            return;
                        } else {
                            //子任务个数
                            List<String> taskNameLists = choosedChildResource();
                            if (EmptyUtil.isNotEmpty(taskNameLists) && taskNameLists != null) {
                                String nameStr = StringUtils.join(taskNameLists, "、");
                                UIUtil.showToastSafe(String.format(UIUtil.getString(R.string.label_work_cart_choose_count_tip), nameStr));
                                return;
                            }
                        }
                    } else if (count >= 6) {
                        UIUtil.showToastSafe(R.string.label_work_cart_max_count_tip);
                        return;
                    }
                }
                // 直接添加到作业库
                mBottomLayout.setActivated(false);
                confirmResourceCart(false);
            } else {
                boolean originalActivated = mBottomLayout.isActivated();
                if (originalActivated) {
                    // 点击确定
                    if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
                        //作业库已经选择的
                        int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
                        if (count >= 0 && count < 6) {
                            //本次选择
                            int count1 = chooseResourceSum();
                            if (count + count1 > 6) {
                                int needCount = 6 - count;
                                UIUtil.showToastSafe(String.format(UIUtil.getString(R.string.label_work_cart_add_count_tip), needCount));
                                return;
                            } else {
                                //子任务个数
                                List<String> taskNameLists = choosedChildResource();
                                if (EmptyUtil.isNotEmpty(taskNameLists) && taskNameLists != null) {
                                    String nameStr = StringUtils.join(taskNameLists, "、");
                                    UIUtil.showToastSafe(String.format(UIUtil.getString(R.string.label_work_cart_choose_count_tip), nameStr));
                                    return;
                                } else {
                                    int count2 = confirmResourceCart();
                                    if (count2 > 0) {
                                        mBottomLayout.setActivated(!originalActivated);
                                    }
                                }
                            }
                        } else if (count >= 6) {
                            UIUtil.showToastSafe(R.string.label_work_cart_max_count_tip);
                            return;
                        }
                    }
                } else {
                    // triggerToCartAction();
                    // mBottomLayout.setActivated(!originalActivated);
                    handleSubjectSettingData(this, UserHelper.getUserId(), true);
                }
                initBottomLayout();
            }
            refreshCartPoint();
        } else if (viewId == R.id.btn_add_homework) {
            boolean originalActivated = mBottomLayout.isActivated();
            if (!originalActivated) {
                if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
                    int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
                    if (count >= 6) {
                        UIUtil.showToastSafe(R.string.label_work_cart_max_count_tip);
                        return;
                    }
                }
            }
            handleSubjectSettingData(this, UserHelper.getUserId(), true);
            initBottomLayout();
            refreshCartPoint();
        } else if (viewId == R.id.btn_all_select) {
            //全选
            String selectAllText = getString(R.string.select_all);
            String deselectAll = getString(R.string.deselect_all);
            String text = mSelectAll.getText().toString();
            boolean isSelectState = selectAllText.equals(text);
            // 获取指定Tab所有的选中的作业库资源
            int currentPosition = mViewPager.getCurrentItem();
            SxLessonSourceNavigator navigator = mTabSourceNavigator.get(currentPosition);
            TreeView treeView = navigator.getTreeView();
            if (isSelectState) {
                mSelectAll.setText(deselectAll);
                treeView.selectAll();
            } else {
                mSelectAll.setText(selectAllText);
                treeView.deselectAll();
            }
        } else if (viewId == R.id.new_cart_container) {
            //作业库
            handleSubjectSettingData(this, UserHelper.getUserId(), false);
        }
    }


    public void handleSubjectSettingData(Context context,
                                         String memberId,
                                         final boolean rightAction) {
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQConfigHelper.requestSetupConfigData(memberId, SetupConfigType.TYPE_TEACHER, languageRes, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                //没有数据
                popChooseSubjectDialog(context);
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                if (entities == null || entities.size() == 0) {
                    popChooseSubjectDialog(context);
                } else {
                    if (rightAction) {
                        triggerToCartAction();
                        mBottomLayout.setActivated(true);
                        initBottomLayout();
                        refreshCartPoint();
                    } else {
                        //有数据
                        triggerWatchCart();
                    }
                }
            }
        });
    }

    private static void popChooseSubjectDialog(Context context) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                context,
                null,
                context.getString(R.string.label_unset_choose_subject),
                context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                context.getString(R.string.label_choose_subject),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AddSubjectActivity.show((Activity) context, false, SUBJECT_SETTING_REQUEST_CODE);
                    }
                });
        messageDialog.show();
    }

    /**
     * 触发添加到作业库的动作
     */
    private void triggerToCartAction() {
        // UIUtil.showToastSafe("触发添加到作业库的动作");
        switchAdapterMode(true);
    }

    /**
     * 触发查看作业库的动作
     */
    private void triggerWatchCart() {
        // UIUtil.showToastSafe("触发查看作业库的动作");
        if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
            CourseDetailParams courseParams = mChapterParams.getCourseParams();
            if (EmptyUtil.isNotEmpty(courseParams)) {
                Bundle extras = getIntent().getBundleExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK);
                TaskSliderHelper.onWorkCartListener.enterIntroTaskDetailActivity(this, courseParams.getSchoolId(), courseParams.getClassId(), extras);
            }
        }
    }

    /**
     * 取消资源
     */
    private void cancelResource() {
        // UIUtil.showToastSafe("取消资源");
        // 清楚所有的作业库资源选中状态
        for (SxLessonSourceNavigator navigator : mTabSourceNavigator) {
            navigator.clearAllResourceState();
        }
        switchAdapterMode(false);
        refreshCartPoint();
    }

    private int confirmResourceCart() {
        return confirmResourceCart(true);
    }

    /**
     * @return 选中了几条资源
     */
    private int chooseResourceSum() {
        // 获取指定Tab所有的选中的作业库资源
        int currentPosition = mViewPager.getCurrentItem();
        SxLessonSourceNavigator navigator = mTabSourceNavigator.get(currentPosition);
        List<TreeNode> selectedNodes = navigator.getChoiceResource();
        addToCartInDifferentTypes.clear();
        for (int i = 0; i < selectedNodes.size(); i++) {
            Object value = selectedNodes.get(i).getValue();
            if (value instanceof SectionResListVo) {
                SectionResListVo vo = (SectionResListVo) value;
                int taskType = vo.getTaskType();
                List<SectionResListVo> vos = addToCartInDifferentTypes.get(taskType);
                if (vos == null) vos = new ArrayList<>();
                vos.add(vo);
                addToCartInDifferentTypes.put(taskType, vos);
            }
        }
        Set<Map.Entry<Integer, List<SectionResListVo>>> entries = addToCartInDifferentTypes.entrySet();
        return entries.size();
    }

    /**
     * @return 选中的子任务的超过10的typeName
     */
    private List<String> choosedChildResource() {
        // 获取指定Tab所有的选中的作业库资源
        int currentPosition = mViewPager.getCurrentItem();
        SxLessonSourceNavigator navigator = mTabSourceNavigator.get(currentPosition);
        List<TreeNode> selectedNodes = navigator.getChoiceResource();
        addToCartInDifferentTypes.clear();
        for (int i = 0; i < selectedNodes.size(); i++) {
            Object value = selectedNodes.get(i).getValue();
            if (value instanceof SectionResListVo) {
                SectionResListVo vo = (SectionResListVo) value;
                int taskType = vo.getTaskType();
                List<SectionResListVo> vos = addToCartInDifferentTypes.get(taskType);
                if (vos == null) vos = new ArrayList<>();
                vos.add(vo);
                addToCartInDifferentTypes.put(taskType, vos);
            }
        }
        List<String> taskNameLists = new ArrayList<>();
        for (Map.Entry<Integer, List<SectionResListVo>> entry : addToCartInDifferentTypes.entrySet()) {
            int taskType = entry.getKey();
            List<SectionResListVo> taskList = entry.getValue();
            if (taskList.size() > 10) {
                taskNameLists.add(mTypes[taskType - 1]);
            }
        }
        return taskNameLists;
    }

    /**
     * 确定所有作业库中的资源
     *
     * @return 添加了几条资源
     */
    private int confirmResourceCart(boolean clearStatus) {
        // 获取指定Tab所有的选中的作业库资源
        int currentPosition = mViewPager.getCurrentItem();
        SxLessonSourceNavigator navigator = mTabSourceNavigator.get(currentPosition);
        List<TreeNode> selectedNodes = navigator.getChoiceResource();
        addToCartInDifferentTypes.clear();
        for (int i = 0; i < selectedNodes.size(); i++) {
            Object value = selectedNodes.get(i).getValue();
            if (value instanceof SectionResListVo) {
                SectionResListVo vo = (SectionResListVo) value;
                int taskType = vo.getTaskType();
                List<SectionResListVo> vos = addToCartInDifferentTypes.get(taskType);
                if (vos == null) vos = new ArrayList<>();
                vos.add(vo);
                addToCartInDifferentTypes.put(taskType, vos);
            }
        }
        List<SectionResListVo> choiceArray = new ArrayList<SectionResListVo>();
        Set<Map.Entry<Integer, List<SectionResListVo>>> entries = addToCartInDifferentTypes.entrySet();
        if (EmptyUtil.isEmpty(entries)) {
            UIUtil.showToastSafe(R.string.str_select_tips);
            return 0;
        }
        for (Map.Entry<Integer, List<SectionResListVo>> entry : entries) {
            choiceArray = entry.getValue();
            // 添加到作业库中
            if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
                // 默认看课件
                int lqwawaTaskType = 9;
                int moocTaskType = choiceArray.get(0).getTaskType();
                if (moocTaskType == 1) {
                    lqwawaTaskType = 9;
                } else if (moocTaskType == 2) {
                    lqwawaTaskType = 5;
                } else if (moocTaskType == 3) {
                    lqwawaTaskType = 8;
                } else if (moocTaskType == 4) {
                    // 多出来的看课本类型
                    lqwawaTaskType = 9;
                } else if (moocTaskType == 5) {
                    // 讲解课类型
                    lqwawaTaskType = 5;
                } else if (moocTaskType == 6) {
                    // Q配音
                    lqwawaTaskType = 14;
                }
                TaskSliderHelper.onWorkCartListener.putResourceToCart((ArrayList<SectionResListVo>) choiceArray, lqwawaTaskType);
                // 刷新数目
                refreshCartPoint();
            }
        }


        // 清楚所有的作业库资源选中状态
        clearAllResource();

        if (clearStatus) {
            switchAdapterMode(false);
        }

        return entries.size();
    }

    /**
     * 清除所有选定的资源
     */
    private void clearAllResource() {
        for (SxLessonSourceNavigator navigator : mTabSourceNavigator) {
            navigator.clearAllResourceState();
        }
    }

    // 更改资源查看模式
    private void switchAdapterMode(boolean choice) {
        for (SxLessonSourceNavigator navigator : mTabSourceNavigator) {
            navigator.triggerChoice(choice);
        }
    }

    /**
     * 刷新红点
     */
    private void refreshCartPoint() {
        if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
            int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
            mTvPoint.setText(Integer.toString(count));
            mTvCartPoint.setText(Integer.toString(count));
            if (count == 0 || mBottomLayout.isActivated()) {
                mTvPoint.setVisibility(View.GONE);
                if (count == 0) {
                    mTvCartPoint.setVisibility(View.GONE);
                }
            } else {
                mTvPoint.setVisibility(View.VISIBLE);
                mTvCartPoint.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 重新初始化底部布局
     */
    private void initBottomLayout() {
        if (mBottomLayout.isActivated()) {
            // 已经是激活状态,显示取消,确定
            mBtnCart.setText(getString(R.string.label_cancel));
            mSelectAll.setText(getString(R.string.select_all));
            mBtnAction.setText(getString(R.string.label_confirm_authorization));
            mTvPoint.setVisibility(View.GONE);

            mBottomLayout1.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.VISIBLE);
        } else {
            // 当前是未激活状态,显示作业库和添加到作业库
            mTvPoint.setVisibility(View.VISIBLE);
            mBottomLayout1.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.GONE);
        }
    }

    private String TAG = getClass().getSimpleName();
    /**
     * 当前显示在哪一页面
     */
    private PagerSelectedAdapter mSelectedAdapter = new PagerSelectedAdapter() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            // 清除所有的作业库资源选中状态
            // 当前是否显示BottomLayout,以及BottomLayout是否是激活状态
            if ((mBottomLayout.getVisibility() == View.VISIBLE &&
                    mBottomLayout.isActivated()) ||
                    mChapterParams.isChoiceMode()) {
                clearAllResource();
            }
            currentSelectedFragment = (SxLessonSourceFragment) fragments.get(position);
        }
    };

    @Override
    public void onItemCheckBoxSelectedChanged(Context context, TreeNode treeNode, boolean checked) {
        if (!checked) mSelectAll.setText(getString(R.string.select_all));
        else {
            if (currentSelectedFragment != null && currentSelectedFragment.getTreeView() != null) {
                boolean isAllSelected = true;
                List<TreeNode> allNodes = currentSelectedFragment.getTreeView().getAllNodes();
                for (TreeNode allNode : allNodes) {
                    if (!allNode.isSelected()) {
                        isAllSelected = false;
                        break;
                    }
                }
                if (isAllSelected) mSelectAll.setText(getString(R.string.deselect_all));
            }
        }
    }

    class SxLessonSourcePagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public SxLessonSourcePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabLists.get(position);
        }
    }


}
