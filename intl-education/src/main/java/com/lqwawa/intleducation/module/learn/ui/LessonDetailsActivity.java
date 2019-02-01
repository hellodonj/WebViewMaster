package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.libs.gallery.ImageBrowserActivity;
import com.libs.gallery.ImageInfo;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.helper.SharedPreferencesHelper;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.NetWorkUtils;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.ExpandableTextView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.SuperListView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.PagerSelectedAdapter;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LetvVodHelperNew;
import com.lqwawa.intleducation.common.utils.TabLayoutUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseResListAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.LessonDetailLiveAdapter;
import com.lqwawa.intleducation.module.discovery.tool.CourseDetails;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceFragment;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceNavigator;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.chapter.CourseChapterParams;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.learn.vo.SectionDetailsVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqresviewlib.LqResViewHelper;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 节详情页面
 * @param needFlagRead 是否需要标志已读，未加入课程全部是false,已加入页面全部是true
 * @param canRead 是否可以进提交列表Item,目前是全部都可以的
 * @param canEdit 家长身份 false
 */
public class LessonDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    // 是否是在线课堂老师
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
     * @param isFreeUser 自由用户，浏览者
     * @param params 课程大纲的核心参数信息
     */
    public static void start(Activity activity, String courseId, String sectionId,
                             String sectionName, String sectionTitle, boolean needFlag,
                             boolean canRead, boolean canEdit, int status, String memberId,
                             boolean isContainAssistantWork, String schoolId,
                             boolean isFromMyCourse, CourseVo courseVo, boolean isOnlineTeacher,
                             boolean isFreeUser, @NonNull CourseChapterParams params) {
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
                .putExtra(KEY_EXTRA_ONLINE_TEACHER,isOnlineTeacher)
                .putExtra("memberId", memberId)
                .putExtra("schoolId", schoolId)
                .putExtra(MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, isFromMyCourse)
                .putExtra(KEY_ROLE_FREE_USER,isFreeUser)
                .putExtra(CourseVo.class.getSimpleName(), courseVo)
                .putExtra(ACTIVITY_BUNDLE_OBJECT,params));
    }

    /**
     * @param activity  启动此界面的activity
     * @param courseId  课程id
     * @param sectionId 章节id
     * @param memberId  用户id
     * @param schoolId  机构id
     */
    /*public static void start(Activity activity, String courseId, String sectionId,
                             String memberId, String schoolId) {
        activity.startActivity(new Intent(activity, LessonDetailsActivity.class)
                .putExtra(COURSE_ID, courseId)
                .putExtra(SECTION_ID, sectionId)
                .putExtra(NEED_FLAG, false)
                .putExtra(CAN_READ, true)
                .putExtra(CAN_EDIT, false)
                .putExtra(ISCONTAINASSISTANTWORK, false)
                .putExtra("memberId", memberId)
                .putExtra("schoolId", schoolId));
    }*/

    private TopBar topBar;
    private ExpandableTextView textViewLessonIntroduction;
    private SuperListView listView;
    // 直播View容器
    private LinearLayout mLiveLayout;
    // 直播列表
    private SuperListView mLiveView;

    private PullToRefreshView pullToRefreshView;
    private RelativeLayout loadFailedLayout;
    private Button reloadBt;
    private TextView resTitleTv;
    private TextView introductionTitleTv;

    private LinearLayout mBottomLayout;
    private FrameLayout mCartContainer;
    private FrameLayout mAddCartContainer;
    private Button mBtnCart;
    private TextView mTvPoint;
    private Button mBtnAction;

    private boolean needFlag;
    private boolean canRead, isContainAssistantWork;
    private boolean canEdit = false;

    // 课程大纲参数
    private CourseChapterParams mChapterParams;

    private String courseId;
    private String sectionId;
    private String sectionName;
    private String sectionTitle;

    // 是否是在线课堂老师过来的
    private boolean isOnlineTeacher;

    private SectionDetailsVo sectionDetailsVo;
    private CourseResListAdapter courseResListAdapter;
    // 直播View Adapter
    private LessonDetailLiveAdapter mLiveAdapter;

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
        listView = (SuperListView) findViewById(R.id.listView);

        mLiveView = (SuperListView) findViewById(R.id.lv_live);
        mLiveLayout = (LinearLayout) findViewById(R.id.live_layout);


        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mCartContainer = (FrameLayout) findViewById(R.id.cart_container);
        mAddCartContainer = (FrameLayout) findViewById(R.id.action_container);
        mBtnCart = (Button) findViewById(R.id.btn_work_cart);
        mTvPoint = (TextView) findViewById(R.id.tv_point);
        mBtnAction = (Button) findViewById(R.id.btn_action);

        int color = UIUtil.getColor(R.color.colorPink);
        int radius = DisplayUtil.dip2px(UIUtil.getContext(),16);
        mTvPoint.setBackground(DrawableUtil.createDrawable(color,color,radius));

        introductionTitleTv = (TextView) findViewById(R.id.introduction_title_tv);
        resTitleTv = (TextView) findViewById(R.id.res_title_tv);

        courseId = getIntent().getStringExtra(COURSE_ID);
        sectionId = getIntent().getStringExtra(SECTION_ID);
        sectionName = getIntent().getStringExtra(SECTION_NAME);
        sectionTitle = getIntent().getStringExtra(SECTION_TITLE);

        topBar.setTitle(sectionTitle);
        topBar.setTitleWide(DensityUtil.dip2px(120));

        isOnlineTeacher = getIntent().getBooleanExtra(KEY_EXTRA_ONLINE_TEACHER,false);
        needFlag = getIntent().getBooleanExtra(NEED_FLAG, false);
        canRead = getIntent().getBooleanExtra(CAN_READ, false);
        canEdit = getIntent().getBooleanExtra(CAN_EDIT, false);
        mFreeUser = getIntent().getBooleanExtra(KEY_ROLE_FREE_USER,false);
        if(getIntent().getExtras().containsKey(ACTIVITY_BUNDLE_OBJECT)){
            mChapterParams = (CourseChapterParams) getIntent().getSerializableExtra(ACTIVITY_BUNDLE_OBJECT);
        }

        // 判断是否显示BottomLayout
        CourseDetailParams courseParams = mChapterParams.getCourseParams();
        if(!mChapterParams.isTeacherVisitor() &&
                courseParams.isClassCourseEnter() &&
                courseParams.isClassTeacher()){
            mBottomLayout.setVisibility(View.VISIBLE);
            mCartContainer.setOnClickListener(this);
            mAddCartContainer.setOnClickListener(this);
        }else{
            mBottomLayout.setVisibility(View.GONE);
        }

        /*List<Fragment> fragments = new ArrayList<>();
        fragments.add(LessonSourceFragment.newInstance(needFlag,canEdit,canRead,isOnlineTeacher,courseId,sectionId,1));
        fragments.add(LessonSourceFragment.newInstance(needFlag,canEdit,canRead,isOnlineTeacher,courseId,sectionId,2));
        fragments.add(LessonSourceFragment.newInstance(needFlag,canEdit,canRead,isOnlineTeacher,courseId,sectionId,3));
        mViewPager.setAdapter(new LessonSourcePagerAdapter(getSupportFragmentManager(),fragments));
        mTabLayout.setupWithViewPager(mViewPager);
        // 设置Indicator长度
        TabLayoutUtil.setIndicatorMargin(UIUtil.getContext(),mTabLayout,20,20);*/
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

        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        reloadBt = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.hideFootView();
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });
        pullToRefreshView.showRefresh();

        courseResListAdapter = new CourseResListAdapter(this, needFlag);
        mLiveAdapter = new LessonDetailLiveAdapter();
        // mLiveView.setAdapter(mLiveAdapter);
        // TODO 课程节详情添加直播显示,未加任何条件判断
        mLiveView.setOnItemClickListener(new SuperListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearLayout parent, View view, int position) {
                LiveVo vo = (LiveVo) mLiveAdapter.getItem(position);
                if (vo != null) {
                    String memberId = getIntent().getStringExtra("memberId");
                    LiveDetails.jumpToLiveDetailsFromCourse(LessonDetailsActivity.this, vo,memberId,false);
                    if (vo.getState() != 0) {
                        vo.setBrowseCount(vo.getBrowseCount() + 1);
                    }
                }
            }
        });

        if (canRead) {
            // 是否可以查阅资源
            courseResListAdapter.setOnItemClickListener(new CourseResListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, View convertView) {
                    if (ButtonUtils.isFastDoubleClick()) {
                        return;
                    }

                    // 找到资源显示控件,获取到资源Id,传到下一层页面,直接显示
                    // TODO 行不通,Drawable 没有实现序列化接口
                    int mDrawableResId;
                    if (convertView instanceof ViewGroup) {
                        ViewGroup viewGroup = (ViewGroup) convertView;
                        ImageView itemIcon = (ImageView) viewGroup.findViewById(R.id.res_icon_iv);
                        if (!EmptyUtil.isEmpty(itemIcon)) {
                            BitmapDrawable bitmap = (BitmapDrawable) itemIcon.getDrawable();
                        }
                    }

                    final SectionResListVo resVo = (SectionResListVo) courseResListAdapter.getItem(position);
                    if (resVo.isIsShield()) {
                        UIUtil.showToastSafe(R.string.res_has_shield);
                        return;
                    }
                    if (resVo.getTaskType() == 1) {//看课件
                        readWeike(resVo, position);
                        if (needFlag && canEdit) {
                            if(getRoleWithCourse() != UserHelper.MoocRoleType.EDITOR
                                    && getRoleWithCourse() != UserHelper.MoocRoleType.TEACHER){
                                // Teacher 是小编 Editor 是主编
                                // 不是主编和小编才FlagRead
                                flagRead(resVo, position);
                            }
                        }
                    } else if (resVo.getTaskType() == 2) {//复述微课
                        if(canEdit && needFlag
                                || !TextUtils.equals(getIntent().getStringExtra("memberId"),
                                UserHelper.getUserId())){
                            enterSectionTaskDetail(resVo);
                        }else{
                            readWeike(resVo, position);
                        }
                    } else if (resVo.getTaskType() == 3) {
                        if(canEdit && needFlag
                                || !TextUtils.equals(getIntent().getStringExtra("memberId"),
                                UserHelper.getUserId())) {
                            enterSectionTaskDetail(resVo);
                        }else{
                            if(TaskSliderHelper.onTaskSliderListener != null) {
                                TaskSliderHelper.onTaskSliderListener.viewCourse(LessonDetailsActivity.this,
                                        resVo.getResId(), resVo.getResType(),
                                        getIntent().getStringExtra("schoolId"),
                                        getIntent().getBooleanExtra("isPublic", false),
                                        getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                .KEY_IS_FROM_MY_COURSE, false)
                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                            }
                        }
                    }
                }

                @Override
                public void onItemChoice(int position, View convertView) {

                }
            });
        } else if (!needFlag) {
            resTitleTv.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
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

    /**
     * 点击听说课,读写单 分发任务
     *
     * @param TaskId    任务Id
     * @param StudentId 学生Id,如果不是学生就传""
     */
    private void dispatchTask(@NonNull String TaskId, String StudentId, DataSource.Callback<Void> callback) {
        LessonHelper.DispatchTask(TaskId, StudentId, callback);
    }

    private void readWeike(final SectionResListVo resVo, int position) {
        int resType = resVo.getResType();
        if (resType > 10000) {
            resType -= 10000;
        }
        switch (resType) {
            case 1:
                showPic(resVo);
//
//                ImageDetailActivity.showStatic(activity,
//                        resVo.getResourceUrl().trim(), resVo.getName());
                break;
            case 2:
                playMedia(resVo, VodVideoSettingUtil.AUDIO_TYPE);
                break;
            case 6:
            case 20:
                if (TaskSliderHelper.onTaskSliderListener != null) {
                    TaskSliderHelper.onTaskSliderListener
                            .viewPdfOrPPT(LessonDetailsActivity.this, "" + resVo.getResId(), resVo.getResType(),
                                    resVo.getOriginName(), resVo.getCreateId(),
                                    getIntent().getBooleanExtra(MyCourseDetailsActivity
                                            .KEY_IS_FROM_MY_COURSE, false)
                                            ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                }
                break;
            case 24:
            case 25:
                LqResViewHelper.playBaseRes(resVo.getResType(), LessonDetailsActivity.this,
                        resVo.getResourceUrl().trim(), resVo.getName());
                break;
            case 5:
            case 16:
            case 17:
            case 18:
            case 19:
            case 3:
            case 23:
                if (!SharedPreferencesHelper.getBoolean(LessonDetailsActivity.this,
                        AppConfig.BaseConfig.KEY_ALLOW_4G, false)) {
                    if (NetWorkUtils.isWifiActive(getApplication().getApplicationContext())) {
                        if (resVo.getResType() == 3) {
//                            LqResViewHelper.playBaseRes(resVo.getResType(), activity, resVo.getVuid().trim(), resVo.getName());
                            playMedia(resVo, VodVideoSettingUtil.VIDEO_TYPE);
                        } else {
                            /*LqResViewHelper.playWeike(activity,
                                    UserHelper.getUserId(),
                                    UserHelper.getUserName(),
                                    resVo.getResourceUrl().trim(),
                                    resVo.getOriginName(),
                                    1,
                                    Utils.getCacheDir(),
                                    resVo.getScreenType(),
                                    resVo.getResType());*/
                            if (TaskSliderHelper.onTaskSliderListener != null) {
                                TaskSliderHelper.onTaskSliderListener.viewCourse(LessonDetailsActivity.this,
                                        resVo.getResId(), resVo.getResType(),
                                        getIntent().getStringExtra("schoolId"),
                                        getIntent().getBooleanExtra("isPublic", false),
                                        getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                .KEY_IS_FROM_MY_COURSE, false)
                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                            }
                        }
                    } else {
                        UIUtil.showToastSafe(R.string.can_not_use_4g);
                    }
                } else {
                    if (NetWorkUtils.isWifiActive(getApplication().getApplicationContext())) {
                        if (resVo.getResType() == 3) {
//                            LqResViewHelper.playBaseRes(resVo.getResType(), activity, resVo.getVuid().trim(), resVo.getName());
                            playMedia(resVo, VodVideoSettingUtil.VIDEO_TYPE);
                        } else {
                            /*LqResViewHelper.playWeike(activity,
                                    UserHelper.getUserId(),
                                    UserHelper.getUserName(),
                                    resVo.getResourceUrl().trim(),
                                    resVo.getOriginName(),
                                    1,
                                    Utils.getCacheDir(),
                                    resVo.getScreenType(),
                                    resVo.getResType());*/
                            if (TaskSliderHelper.onTaskSliderListener != null) {
                                TaskSliderHelper.onTaskSliderListener.viewCourse(LessonDetailsActivity.this,
                                        resVo.getResId(), resVo.getResType(),
                                        getIntent().getStringExtra("schoolId"),
                                        getIntent().getBooleanExtra("isPublic", false),
                                        getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                .KEY_IS_FROM_MY_COURSE, false)
                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                            }
                        }
                    } else {
                        CustomDialog.Builder builder = new CustomDialog.Builder(this);
                        builder.setMessage(UIUtil.getResources().getString(R.string.play_use_4g) + "?");
                        builder.setTitle(UIUtil.getResources().getString(R.string.tip));
                        builder.setPositiveButton(UIUtil.getResources().getString(R.string.continue_play),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if (resVo.getResType() == 3) {
//                                            LqResViewHelper.playBaseRes(resVo.getResType(), activity, resVo.getVuid().trim(), resVo.getName());
                                            playMedia(resVo, VodVideoSettingUtil.VIDEO_TYPE);
                                        } else {
                                            /*LqResViewHelper.playWeike(activity,
                                                    UserHelper.getUserId(),
                                                    UserHelper.getUserName(),
                                                    resVo.getResourceUrl().trim(),
                                                    resVo.getOriginName(),
                                                    1,
                                                    Utils.getCacheDir(),
                                                    resVo.getScreenType(),
                                                    resVo.getResType());*/
                                            if (TaskSliderHelper.onTaskSliderListener != null) {
                                                TaskSliderHelper.onTaskSliderListener.viewCourse(LessonDetailsActivity.this,
                                                        resVo.getResId(), resVo.getResType(),
                                                        getIntent().getStringExtra("schoolId"),
                                                        getIntent().getBooleanExtra("isPublic", false),
                                                        getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                                .KEY_IS_FROM_MY_COURSE, false)
                                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                                            }
                                        }
                                    }
                                });
                        builder.setNegativeButton(UIUtil.getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builder.create().show();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 图片浏览
     *
     * @param resVo
     */
    private void showPic(SectionResListVo resVo) {
        List<ImageInfo> resourceInfoList = new ArrayList<>();
        ImageInfo newResourceInfo = new ImageInfo();
        newResourceInfo.setTitle(resVo.getName());
        newResourceInfo.setResourceUrl(resVo.getResourceUrl().trim());
        newResourceInfo.setResourceId(resVo.getResId() + "-" + resVo.getResType());
        newResourceInfo.setAuthorId(resVo.getCreateId());
        newResourceInfo.setResourceType(resVo.getResType());
        resourceInfoList.add(newResourceInfo);


        Intent intent = new Intent();
        intent.setClassName(MainApplication.getInstance().getPackageName(), "com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity");
        intent.putParcelableArrayListExtra(ImageBrowserActivity.EXTRA_IMAGE_INFOS, (ArrayList<? extends Parcelable>) resourceInfoList);
        intent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_INDEX, 0);
        intent.putExtra(ImageBrowserActivity.ISPDF, false);

        intent.putExtra(ImageBrowserActivity.KEY_ISHIDEMOREBTN, false);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOURSEANDREADING, true);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOLLECT, false);//隐藏收藏功能
        startActivity(intent);
    }

    /**
     * 音视频播放
     *
     * @param resVo
     * @param type
     */
    private void playMedia(SectionResListVo resVo, int type) {
        new LetvVodHelperNew.VodVideoBuilder(this)
                .setNewUI(true)//使用自定义UI
                .setTitle(resVo.getName())//视频标题
                .setAuthorId(resVo.getCreateId())
                .setResId(resVo.getResId() + "-" + resVo.getResType())
                .setResourceType(resVo.getResType())
                .setVuid(resVo.getVuid())
                .setUrl(resVo.getResourceUrl())
                .setMediaType(type)//设置媒体类型
                .setPackageName(MainApplication.getInstance().getPackageName())
                .setClassName("com.galaxyschool.app.wawaschool.medias.activity.VodPlayActivity")
                .setHideBtnMore(true)
                .setLeStatus(resVo.getLeStatus())
                .setIsPublic(getIntent().getBooleanExtra("isPublic", false))
                .create();
    }

    private void getData() {
        loadFailedLayout.setVisibility(View.GONE);
        /*final RequestVo requestVo = new RequestVo();
        String token = null;
        if (StringUtils.isValidString(getIntent().getStringExtra("memberId"))) {
            token = getIntent().getStringExtra("memberId");
            requestVo.addParams("token", getIntent().getStringExtra("memberId"));
        }
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("sectionId", sectionId);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.courseSectionDetail + requestVo.getParams());
        params.setConnectTimeout(10000);*/

        // 获取课程节直播列表数据
        // @date   :2018/6/7 0007 下午 6:30
        // @func   :V5.7去取消直播显示
        /**
        LiveHelper.getCourseChapterLives(token, courseId, sectionId, new DataSource.Callback<List<LiveVo>>() {
            @Override
            public void onDataLoaded(List<LiveVo> data) {
                if (EmptyUtil.isEmpty(data)) {
                    // 没有直播数据
                    mLiveLayout.setVisibility(View.GONE);
                } else {
                    mLiveLayout.setVisibility(View.VISIBLE);
                    mLiveAdapter.setData(data);
                    mLiveAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onDataNotAvailable(int strRes) {
                // 不显示错误信息
            }
        });*/

        /*x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<SectionDetailsVo> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<SectionDetailsVo>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    sectionDetailsVo = result.getData();
                    updateView();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(getClass().getSimpleName(), "获取我的订单列表失败:" + throwable.getMessage());
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });*/


        String token = mChapterParams.getMemberId();

        int role = 2;
        if(mChapterParams.getRole() == UserHelper.MoocRoleType.TEACHER){
            role = 1;
        }

        String classId = "";
        if(role == 1 && mChapterParams.getCourseParams().isClassCourseEnter()){
            classId = mChapterParams.getCourseParams().getClassId();
        }

        LessonHelper.requestChapterStudyTask(token, classId, courseId, sectionId, role,new DataSource.Callback<SectionDetailsVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(SectionDetailsVo sectionDetailsVo) {
                loadFailedLayout.setVisibility(View.GONE);
                LessonDetailsActivity.this.sectionDetailsVo = sectionDetailsVo;
                if(EmptyUtil.isEmpty(sectionDetailsVo)) return;
                updateView();
            }
        });
    }

    protected void enterSectionTaskDetail(SectionResListVo vo) {
        String memberId = getIntent().getStringExtra("memberId");
        CourseVo courseVo = (CourseVo) getIntent().getSerializableExtra(CourseVo
                .class.getSimpleName());
        int originRole = UserHelper.getCourseAuthorRole(memberId, courseVo);
        final String taskId = vo.getTaskId();
        if (originRole == UserHelper.MoocRoleType.STUDENT && !TextUtils.isEmpty(taskId)) {
            LessonHelper.DispatchTask(taskId, memberId, null);
        }

        // 辅导老师的身份等同家长处理 已经角色处理过的 role
        int role = originRole;
        if(UserHelper.isCourseCounselor(courseVo,isOnlineTeacher)){
            // 如果是在线课堂的老师,当做家长处理
            originRole = UserHelper.MoocRoleType.PARENT;
        }

        if (originRole == UserHelper.MoocRoleType.TEACHER || isOnlineTeacher) {
            if (UserHelper.isCourseCounselor(courseVo,isOnlineTeacher)) {
                role = UserHelper.MoocRoleType.PARENT;
            }
            if (UserHelper.isCourseTeacher(courseVo)) {
                role = UserHelper.MoocRoleType.EDITOR;
            }
        }

        /*SectionTaskDetailsActivity.startForResultEx(this, vo, memberId, this.getIntent
                ().getStringExtra("schoolId"), this.getIntent().getBooleanExtra
                (MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, false), null, originRole,role, null,mFreeUser);*/
    }

    /**
     * 根据上层页面传来的信息判断角色
     */
    private int getRoleWithCourse(){
        String memberId = getIntent().getStringExtra("memberId");
        CourseVo courseVo = (CourseVo) getIntent().getSerializableExtra(CourseVo
                .class.getSimpleName());
        int role = UserHelper.getCourseAuthorRole(memberId, courseVo);
        return role;
    }

    private void updateView() {
        courseResListAdapter.setData(null);
        if (sectionDetailsVo != null) {
            getIntent().putExtra(SECTION_NAME, sectionDetailsVo.getSectionName());
            topBar.setTitle(sectionDetailsVo.getSectionName());
            getIntent().putExtra(SECTION_TITLE, sectionDetailsVo.getSectionTitle());
            getIntent().putExtra(STATUS, sectionDetailsVo.getStatus());
            getIntent().putExtra("isPublic", sectionDetailsVo.isIsOpen());
            List<SectionTaskListVo> taskList = sectionDetailsVo.getTaskList();
            boolean showReadWare = false;
            boolean showListenRead = false;
            boolean showReadWrite = false;
            boolean showTextBook = false;
            if(EmptyUtil.isNotEmpty(taskList)){
                for (SectionTaskListVo taskListVo:taskList) {
                    if(taskListVo.getTaskType() == 1 && EmptyUtil.isNotEmpty(taskListVo.getData())){
                        mTabs[0] = taskListVo.getTaskName();
                        showReadWare = true;
                    }

                    if(taskListVo.getTaskType() == 2 && EmptyUtil.isNotEmpty(taskListVo.getData())){
                        mTabs[1] = taskListVo.getTaskName();
                        showListenRead = true;
                    }

                    if(taskListVo.getTaskType() == 3 && EmptyUtil.isNotEmpty(taskListVo.getData())){
                        mTabs[2] = taskListVo.getTaskName();
                        showReadWrite = true;
                    }

                    if(taskListVo.getTaskType() == 4 && EmptyUtil.isNotEmpty(taskListVo.getData())){
                        mTabs[3] = taskListVo.getTaskName();
                        showTextBook = true;
                    }
                }
            }

            List<Fragment> fragments = new ArrayList<>();

            LessonSourceParams params = LessonSourceParams.buildParams(mChapterParams);

            if(showReadWare){
                mTabLists.add(mTabs[0]);
                LessonSourceFragment fragment = LessonSourceFragment.newInstance(needFlag,canEdit,canRead,isOnlineTeacher,courseId,sectionId,1,params);
                mTabSourceNavigator.add(fragment);
                fragments.add(fragment);
            }

            if(showListenRead){
                mTabLists.add(mTabs[1]);
                LessonSourceFragment fragment = LessonSourceFragment.newInstance(needFlag,canEdit,canRead,isOnlineTeacher,courseId,sectionId,2,params);
                mTabSourceNavigator.add(fragment);
                fragments.add(fragment);
            }

            if(showReadWrite){
                mTabLists.add(mTabs[2]);
                LessonSourceFragment fragment = LessonSourceFragment.newInstance(needFlag,canEdit,canRead,isOnlineTeacher,courseId,sectionId,3,params);
                mTabSourceNavigator.add(fragment);
                fragments.add(fragment);
            }

            if(showTextBook){
                mTabLists.add(mTabs[3]);
                LessonSourceFragment fragment = LessonSourceFragment.newInstance(needFlag,canEdit,canRead,isOnlineTeacher,courseId,sectionId,4,params);
                mTabSourceNavigator.add(fragment);
                fragments.add(fragment);
            }

            mViewPager.setAdapter(new LessonSourcePagerAdapter(getSupportFragmentManager(),fragments));
            mViewPager.setOffscreenPageLimit(fragments.size());
            mTabLayout.setupWithViewPager(mViewPager);

            mViewPager.addOnPageChangeListener(mSelectedAdapter);

            // 设置Indicator长度
            if(fragments.size() > 1)
            TabLayoutUtil.setIndicatorMargin(UIUtil.getContext(),mTabLayout,20,20);

            textViewLessonIntroduction.setText(sectionDetailsVo.getIntroduction());

            /*if (sectionDetailsVo.getTaskList() != null) {
                if (sectionDetailsVo.getTaskList().size() > 0) {
                    if (sectionDetailsVo.getTaskList().get(0).getData() != null) {
                        this.textViewLessonIntroduction.setText("" + sectionDetailsVo.getIntroduction());
                        List<SectionResListVo> voList = sectionDetailsVo.getTaskList().get(0).getData();
                        if (voList.size() > 0) {
                            voList.get(0).setIsTitle(true);
                        }
                        for (SectionResListVo vo : voList) {
                            vo.setTaskName(getTaskName(0));
                            vo.setTaskType(sectionDetailsVo.getTaskList().get(0).getTaskType());
                        }
                        courseResListAdapter.setData(voList);
                        listView.setAdapter(courseResListAdapter);
                    }
                }
                if (sectionDetailsVo.getTaskList().size() > 1) {
                    if (sectionDetailsVo.getTaskList().get(1).getData() != null) {
                        this.textViewLessonIntroduction.setText("" + sectionDetailsVo.getIntroduction());
                        List<SectionResListVo> voList = sectionDetailsVo.getTaskList().get(1).getData();
                        if (voList.size() > 0) {
                            voList.get(0).setIsTitle(true);
                        }
                        for (SectionResListVo vo : voList) {
                            vo.setTaskName(getTaskName(1));
                            vo.setTaskType(sectionDetailsVo.getTaskList().get(1).getTaskType());
                        }
                        courseResListAdapter.addData(voList);
                    }
                }
                if (sectionDetailsVo.getTaskList().size() > 2) {
                    if (sectionDetailsVo.getTaskList().get(2).getData() != null) {
                        this.textViewLessonIntroduction.setText("" + sectionDetailsVo.getIntroduction());
                        List<SectionResListVo> voList = sectionDetailsVo.getTaskList().get(2).getData();
                        if (voList.size() > 0) {
                            voList.get(0).setIsTitle(true);
                        }
                        for (SectionResListVo vo : voList) {
                            vo.setTaskName(getTaskName(2));
                            vo.setTaskType(sectionDetailsVo.getTaskList().get(2).getTaskType());
                        }
                        courseResListAdapter.addData(voList);
                    }
                }
                courseResListAdapter.notifyDataSetChanged();
            }*/
        }
    }

    @NonNull
    private String getTaskName(int i) {
        String taskName = "";
        int taskType = sectionDetailsVo.getTaskList().get(i).getTaskType();
        if (taskType == 1) {//看课件
            taskName = getString(R.string.lq_watch_course);
        } else if (taskType == 2) {//复述课件
            taskName = getResources().getString(R.string.retell_course);
        } else if (taskType == 3) {//任务单
            taskName = getResources().getString(R.string.coursetask);
        }
        return taskName;
    }

    private void flagRead(final SectionResListVo vo, final int position) {

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("cwareId", vo.getId());
        if(vo.getTaskType() != 1){
            // 1是看课件 除了看课件，其它都需要传resId
            requestVo.addParams("resId", vo.getResId());
        }
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.setReaded + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            // TODO 还是刚刚那个接口的问题，课程是国际课程测试，接口返回flagRead是正确的。但是在加载的时候，isRead还是false
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ((SectionResListVo) courseResListAdapter.getItem(position)).setStatus(1);
                    courseResListAdapter.notifyDataSetChanged();
                    // 发送广播
                    CourseDetails.courseDetailsTriggerStudyTask(getApplicationContext(), CourseDetailsItemFragment.LQWAWA_ACTION_CAN_COURSEWARE);
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                UIUtil.showToastSafe(R.string.net_error_tip);
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SectionTaskDetailsActivity.Rs_task_commit) {
            // getData();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.cart_container){
            // 点击作业库,或者取消
            boolean originalActivated = mBottomLayout.isActivated();
            if(originalActivated){
                mBottomLayout.setActivated(!originalActivated);
                initBottomLayout();
                cancelResource();
            }else{
                triggerWatchCart();
            }
        }else if(viewId == R.id.action_container){
            boolean originalActivated = mBottomLayout.isActivated();
            if(!originalActivated){
                // 点击添加到作业库,或者确定
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)){
                    int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
                    if(count >= 6){
                        UIUtil.showToastSafe(R.string.label_work_cart_max_count_tip);
                        return;
                    }
                }
            }

            if(originalActivated){
                int count = confirmResourceCart();
                if(count > 0){
                    mBottomLayout.setActivated(!originalActivated);
                }
            }else{
                triggerToCartAction();
                mBottomLayout.setActivated(!originalActivated);
            }

            initBottomLayout();
            refreshCartPoint();
        }
    }

    /**
     * 触发添加到作业库的动作
     */
    private void triggerToCartAction(){
        // UIUtil.showToastSafe("触发添加到作业库的动作");
        switchAdapterMode(true);
    }

    /**
     * 触发查看作业库的动作
     */
    private void triggerWatchCart(){
        // UIUtil.showToastSafe("触发查看作业库的动作");
        if(EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)){
            CourseDetailParams courseParams = mChapterParams.getCourseParams();
            if(EmptyUtil.isNotEmpty(courseParams)){
                TaskSliderHelper.onWorkCartListener.enterIntroTaskDetailActivity(this,courseParams.getSchoolId(),courseParams.getClassId());
            }
        }
    }

    /**
     * 取消资源
     */
    private void cancelResource(){
        // UIUtil.showToastSafe("取消资源");
        // 清楚所有的作业库资源选中状态
        for (LessonSourceNavigator navigator : mTabSourceNavigator) {
            navigator.clearAllResourceState();
        }
        switchAdapterMode(false);
        refreshCartPoint();
    }

    /**
     * 确定所有作业库中的资源
     * @return 添加了几条资源
     */
    private int confirmResourceCart(){
        // UIUtil.showToastSafe("确定所有作业库中的资源");
        // 获取指定Tab所有的选中的作业库资源
        int currentPosition = mViewPager.getCurrentItem();
        LessonSourceNavigator navigator = mTabSourceNavigator.get(currentPosition);
        List<SectionResListVo> choiceArray = navigator.takeChoiceResource();
        if(EmptyUtil.isEmpty(choiceArray)){
            UIUtil.showToastSafe(R.string.str_select_tips);
            return 0;
        }
        // 添加到作业库中
        if(EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)){
            // 默认看课件
            int lqwawaTaskType = 9;
            int moocTaskType = choiceArray.get(0).getTaskType();
            if(moocTaskType == 1){
                lqwawaTaskType = 9;
            }else if(moocTaskType == 2){
                lqwawaTaskType = 5;
            }else if(moocTaskType == 3){
                lqwawaTaskType = 8;
            }
            TaskSliderHelper.onWorkCartListener.putResourceToCart((ArrayList<SectionResListVo>) choiceArray,lqwawaTaskType);
            // 刷新数目
            refreshCartPoint();
        }

        // 清楚所有的作业库资源选中状态
        clearAllResource();
        switchAdapterMode(false);

        return choiceArray.size();
    }

    /**
     * 清除所有选定的资源
     */
    private void clearAllResource(){
        for (LessonSourceNavigator navigator : mTabSourceNavigator) {
            navigator.clearAllResourceState();
        }
    }

    // 更改资源查看模式
    private void switchAdapterMode(boolean choice){
        for (LessonSourceNavigator navigator : mTabSourceNavigator) {
            navigator.triggerChoice(choice);
        }
    }

    /**
     * 刷新红点
     */
    private void refreshCartPoint(){
        if(EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)){
            int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
            mTvPoint.setText(Integer.toString(count));
            if(count == 0 || mBottomLayout.isActivated()){
                mTvPoint.setVisibility(View.GONE);
            }else{
                mTvPoint.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 重新初始化底部布局
     */
    private void initBottomLayout(){
        if(mBottomLayout.isActivated()){
            // 已经是激活状态,显示取消,确定
            mBtnCart.setText(getString(R.string.label_cancel));
            mBtnAction.setText(getString(R.string.label_confirm_authorization));
            mTvPoint.setVisibility(View.GONE);
        }else{
            // 当前是未激活状态,显示作业库和添加到作业库
            mBtnCart.setText(getString(R.string.label_work_cart));
            mBtnAction.setText(getString(R.string.label_action_to_cart));
            mTvPoint.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 当前显示在哪一页面
     */
    private PagerSelectedAdapter mSelectedAdapter = new PagerSelectedAdapter(){
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            // 清除所有的作业库资源选中状态
            // 当前是否显示BottomLayout,以及BottomLayout是否是激活状态
            if(mBottomLayout.getVisibility() == View.VISIBLE &&
                    mBottomLayout.isActivated()){
                clearAllResource();
            }
        }
    };

    class LessonSourcePagerAdapter extends FragmentPagerAdapter{

        private List<Fragment> mFragments;

        public LessonSourcePagerAdapter(FragmentManager fm,List<Fragment> fragments) {
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

    private static String[] mTabs = UIUtil.getStringArray(R.array.label_lesson_source_tabs);
}
