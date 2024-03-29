package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.ExpandableTextView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.PagerSelectedAdapter;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceFragment;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceNavigator;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.chapter.CourseChapterParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.AddSubjectActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.SectionDetailsVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskListVo;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @param needFlagRead 是否需要标志已读，未加入课程全部是false,已加入页面全部是true
 * @param canRead      是否可以进提交列表Item,目前是全部都可以的
 * @param canEdit      家长身份 false
 * @desc 节详情页面
 */
public class LessonDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int SUBJECT_SETTING_REQUEST_CODE = 1 << 1;

    // 是否是空中课堂老师
    public static final String KEY_EXTRA_ONLINE_TEACHER = "KEY_EXTRA_ONLINE_TEACHER";
    // 是否是游离的身份
    public static final String KEY_ROLE_FREE_USER = "KEY_ROLE_FREE_USER";

    public static final String ACTIVITY_BUNDLE_OBJECT = "ACTIVITY_BUNDLE_OBJECT";

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
        activity.startActivity(new Intent(activity, LessonDetailsActivity.class)
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

    private TopBar topBar;
    private ExpandableTextView textViewLessonIntroduction;

    private FrameLayout mNewCartContainer;
    private TextView mTvWorkCart;
    private TextView mTvCartPoint;

    private LinearLayout mBottomLayout;
    private FrameLayout mCartContainer;
    private FrameLayout mAddCartContainer;
    private Button mBtnCart;
    private TextView mTvPoint;
    private Button mBtnAction;
    private LinearLayout mTopLayout;

    private boolean needFlag;
    private boolean canRead, isContainAssistantWork;
    private boolean canEdit = false;

    // 课程大纲参数
    private CourseChapterParams mChapterParams;

    private String courseId;
    private String sectionId;
    private String sectionName;
    private String sectionTitle;
    private CourseVo courseVo;

    // 是否是空中课堂老师过来的
    private boolean isOnlineTeacher;
    private SectionDetailsVo sectionDetailsVo;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<String> mTabLists = new ArrayList<>();
    private List<LessonSourceNavigator> mTabSourceNavigator = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_details);
        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setBack(true);
        textViewLessonIntroduction = (ExpandableTextView) findViewById(R.id.lesson_introduction_tv);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mNewCartContainer = (FrameLayout) findViewById(R.id.new_cart_container);
        mTvWorkCart = (TextView) findViewById(R.id.tv_work_cart);
        mTvCartPoint = (TextView) findViewById(R.id.tv_cart_point);

        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mCartContainer = (FrameLayout) findViewById(R.id.cart_container);
        mAddCartContainer = (FrameLayout) findViewById(R.id.action_container);
        mBtnCart = (Button) findViewById(R.id.btn_work_cart);
        mTvPoint = (TextView) findViewById(R.id.tv_point);
        mBtnAction = (Button) findViewById(R.id.btn_action);
        mTopLayout = (LinearLayout) findViewById(R.id.lesson_top_layout);

        int color = UIUtil.getColor(R.color.colorPink);
        int radius = DisplayUtil.dip2px(UIUtil.getContext(), 16);
        mTvPoint.setBackground(DrawableUtil.createDrawable(color, color, radius));

        courseId = getIntent().getStringExtra(COURSE_ID);
        sectionId = getIntent().getStringExtra(SECTION_ID);
        sectionName = getIntent().getStringExtra(SECTION_NAME);
        sectionTitle = getIntent().getStringExtra(SECTION_TITLE);

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
        courseVo = (CourseVo) getIntent().getSerializableExtra(CourseVo.class.getSimpleName());

        // 判断是否显示BottomLayout
        CourseDetailParams courseParams = mChapterParams.getCourseParams();
        if (!mChapterParams.isTeacherVisitor() &&
                courseParams.isClassCourseEnter() &&
                courseParams.isClassTeacher() ||
                mChapterParams.isChoiceMode()) {
            mBottomLayout.setVisibility(View.VISIBLE);
            mNewCartContainer.setVisibility(View.VISIBLE);
            mNewCartContainer.setOnClickListener(this);

            mCartContainer.setOnClickListener(this);
            mAddCartContainer.setOnClickListener(this);
        } else {
            mBottomLayout.setVisibility(View.GONE);
        }

        boolean isVideoCourse =
                courseParams != null && (courseParams.getLibraryType() == OrganLibraryType.TYPE_VIDEO_LIBRARY
                        || (courseParams.getLibraryType() == OrganLibraryType.TYPE_BRAIN_LIBRARY && courseParams.isVideoCourse()));
        mTopLayout.setVisibility(isVideoCourse ? View.GONE : View.VISIBLE);

        isContainAssistantWork = getIntent().getBooleanExtra(ISCONTAINASSISTANTWORK, false);
        if (isContainAssistantWork) {
            topBar.setRightFunctionText1(getString(R.string.assistant_desk), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //助教工作台
                    String url = AppConfig.ServerUrl.ASSISTANT_DESK + "courseId=" + courseId + "&chapterId=" + sectionId + "&hidefooter=true";
                    Intent intent = new Intent();
                    intent.setClassName(MainApplication.getInstance().getPackageName(), "com.galaxyschool.app.wawaschool.CampusOnlineWebActivity");
                    intent.putExtra("url", url);
                    intent.putExtra("isMooc", true);
                    intent.putExtra("title", getString(R.string.assistant_desk));
                    startActivity(intent);
//                    WebActivity.start(LessonDetailsActivity.this,url,getString(R.string.assistant_desk));
                }
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

    private void getData() {

        String token = mChapterParams.getMemberId();

        int role = 2;
        if (mChapterParams.getRole() == UserHelper.MoocRoleType.TEACHER) {
            role = 1;
        }

        String classId = "";
        if (role == 1 && mChapterParams.getCourseParams().isClassCourseEnter()) {
            classId = mChapterParams.getCourseParams().getClassId();
        }

        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        //exerciseType 不传或者-1 全部
        LessonHelper.requestChapterStudyTask(languageRes, token, classId, courseId, sectionId, role,-1, new DataSource.Callback<SectionDetailsVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(SectionDetailsVo sectionDetailsVo) {
                LessonDetailsActivity.this.sectionDetailsVo = sectionDetailsVo;
                if (EmptyUtil.isEmpty(sectionDetailsVo)) return;
                updateView();
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

            List<Fragment> fragments = new ArrayList<>();
            if (EmptyUtil.isNotEmpty(taskList)) {

                LessonSourceParams params = LessonSourceParams.buildParams(mChapterParams);
                boolean isDirect =
                        mChapterParams.isChoiceMode() && mChapterParams.isInitiativeTrigger();
                if (!isDirect) {
                    params.setAddMode(mBottomLayout.isActivated());
                } else {
                    params.setAddMode(true);
                }
                for (int index = 0; index < taskList.size(); index++) {
                    SectionTaskListVo listVo = taskList.get(index);
                    if (EmptyUtil.isNotEmpty(listVo.getData())) {
                        int taskType = listVo.getTaskType();
                        String taskName = listVo.getTaskName();
                        mTabLists.add(taskName);
                        LessonSourceFragment fragment = LessonSourceFragment.newInstance(needFlag, canEdit, canRead, isOnlineTeacher, courseId, sectionId, taskType,courseVo.getLibraryType(), params);
                        mTabSourceNavigator.add(fragment);
                        fragments.add(fragment);
                    }
                }
            }

            mViewPager.setAdapter(new LessonSourcePagerAdapter(getSupportFragmentManager(), fragments));
            mViewPager.setOffscreenPageLimit(fragments.size());
            mTabLayout.setupWithViewPager(mViewPager);

            mViewPager.addOnPageChangeListener(mSelectedAdapter);

            if (mTabLayout.getTabCount() > 4) {
                mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            } else {
                mTabLayout.setTabMode(TabLayout.MODE_FIXED);
            }

            textViewLessonIntroduction.setText(sectionDetailsVo.getIntroduction());

        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.cart_container) {
            // 点击作业库,或者取消
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
            if (mChapterParams.isChoiceMode() && mChapterParams.isInitiativeTrigger()) {
                // 直接添加到作业库

                // 获取指定Tab所有的选中的作业库资源
                int currentPosition = mViewPager.getCurrentItem();
                LessonSourceNavigator navigator = mTabSourceNavigator.get(currentPosition);
                List<SectionResListVo> choiceArray = navigator.takeChoiceResource();
                if (EmptyUtil.isEmpty(choiceArray)) {
                    UIUtil.showToastSafe(R.string.str_select_tips);
                    return;
                }

                if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
                    int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
                    if (count >= 6) {
                        UIUtil.showToastSafe(R.string.label_work_cart_max_count_tip);
                        return;
                    }
                }

                int count = confirmResourceCart(false);
            } else {
                boolean originalActivated = mBottomLayout.isActivated();
                if (!originalActivated) {
                    // 点击添加到作业库,或者确定
                    if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
                        int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
                        if (count >= 6) {
                            UIUtil.showToastSafe(R.string.label_work_cart_max_count_tip);
                            return;
                        }
                    }
                }

                if (originalActivated) {
                    int count = confirmResourceCart();
                    if (count > 0) {
                        mBottomLayout.setActivated(!originalActivated);
                    }
                } else {
                    // triggerToCartAction();
                    // mBottomLayout.setActivated(!originalActivated);
                    handleSubjectSettingData(this, UserHelper.getUserId(), true);
                }

                initBottomLayout();
                refreshCartPoint();
            }
        } else if (viewId == R.id.new_cart_container) {
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
        for (LessonSourceNavigator navigator : mTabSourceNavigator) {
            navigator.clearAllResourceState();
        }
        switchAdapterMode(false);
        refreshCartPoint();
    }

    private int confirmResourceCart() {
        return confirmResourceCart(true);
    }

    /**
     * 确定所有作业库中的资源
     *
     * @return 添加了几条资源
     */
    private int confirmResourceCart(boolean clearStatus) {
        // UIUtil.showToastSafe("确定所有作业库中的资源");
        // 获取指定Tab所有的选中的作业库资源
        int currentPosition = mViewPager.getCurrentItem();
        LessonSourceNavigator navigator = mTabSourceNavigator.get(currentPosition);
        List<SectionResListVo> choiceArray = navigator.takeChoiceResource();
        if (EmptyUtil.isEmpty(choiceArray)) {
            UIUtil.showToastSafe(R.string.str_select_tips);
            return 0;
        }
//        for (int i = 0; i < choiceArray.size(); i++) {
//            SectionResListVo resListVo = choiceArray.get(i);
//            resListVo.setCourseId(courseId);
//            resListVo.setSourceType(TYPE_HOMEWORK);
//        }
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

        // 清楚所有的作业库资源选中状态
        clearAllResource();

        if (clearStatus) {
            switchAdapterMode(false);
        }

        return choiceArray.size();
    }

    /**
     * 清除所有选定的资源
     */
    private void clearAllResource() {
        for (LessonSourceNavigator navigator : mTabSourceNavigator) {
            navigator.clearAllResourceState();
        }
    }

    // 更改资源查看模式
    private void switchAdapterMode(boolean choice) {
        for (LessonSourceNavigator navigator : mTabSourceNavigator) {
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
            mBtnAction.setText(getString(R.string.label_confirm_authorization));
            mTvPoint.setVisibility(View.GONE);

            // 如果是激活状态
            mCartContainer.setVisibility(View.VISIBLE);
        } else {
            // 当前是未激活状态,显示作业库和添加到作业库
            mBtnCart.setText(getString(R.string.label_work_cart));
            mBtnAction.setText(getString(R.string.label_action_to_cart));
            mTvPoint.setVisibility(View.VISIBLE);

            mCartContainer.setVisibility(View.GONE);
        }
    }

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
        }
    };

    class LessonSourcePagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public LessonSourcePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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
