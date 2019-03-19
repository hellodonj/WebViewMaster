package com.galaxyschool.app.wawaschool.medias.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.BookCatalogListActivity;
import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.BookListActivity;
import com.galaxyschool.app.wawaschool.CatalogLessonListActivity;
import com.galaxyschool.app.wawaschool.CatalogSelectActivity;
import com.galaxyschool.app.wawaschool.CustomerServiceActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CheckAuthorizationPmnHelper;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.SchoolMaterialData;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.lqbaselib.net.library.DataModel;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.AuthorizationInfo;
import com.galaxyschool.app.wawaschool.pojo.AuthorizationInfoResult;
import com.galaxyschool.app.wawaschool.pojo.Calalog;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.SubscribeSchoolListResult;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.PagerSlidingTabStrip;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @创建者 Blizzard-liu
 * @创建时间 2017/3/27 16:25
 * @描述 ${校本资源库展示页,包括LQ课件,任务单,图片,音频,视频,PDF,PPT,
 * 统一处理搜索,上一节,下一节,查看目录,isPick选择课件,ishidetab控制显示页签tab}
 * @更新者 ${Author}
 * @更新时间 2017/3/27
 * @更新描述 ${TODO}
 */

public class SchoolResourceContainerFragment extends ContactsListFragment {
    public static final String TAG = "SchoolResourceContainer";
    private ImageView mContactsHeaderLeftBtn;//返回按钮
    public TextView mContactsHeaderTitle;//标题
    private PagerSlidingTabStrip mSchoolResourceTabs;//页签
    private MyViewPager mSchoolResourcePager;
    private MyPagerAdapter mAdapter;
    private String bookCatalogName;
    private ClearEditText mEditText;//搜索框
    private TextView mSearchBtn;//搜索按钮
    private LinearLayout mContactsSearchBarLayout;
    private TextView mPrivousTextview;//上一页
    private LinearLayout mAllCatalogLayout;
    private TextView mNextTextview;//下一页
    private String mKeyword;
    private int CURRENTSTATE = -1, CURRENTINDEX = 0;
    private static final int
            PREVIOUS_SECTION = 0x001,//上一节
            NEXT_SECTION = 0x002,//下一节
            CATALOG = 0x003,//查看目录
            SEARCH = 0x004;//搜索

    public TextView mContactsHeaderRightBtn;
    private boolean isPick, isHideTab;//控制显示tab

    private AuthorizationInfo authorizationInfo; //保存授权信息
    private SchoolInfo schoolInfo; //我所在学校信息
    private String schoolId, bookCatalogId;
    private String bookId;//书本的id
    private String originSchoolId;
    //保存每个界面的搜索关键字
    private String keywordLq, keywordTask, keywordPic, keywordAudio, keywordVideo, keywordPdf, keywordPpt;
    private int position = 0;
    private List<Calalog> calalogs;
    private List<Calalog> calalogsNochildren;

    //我的书架中的资源收藏来源
    protected String collectSchoolId;
    protected boolean isFromChoiceLib;
    //校本资源库老师身份显示下载 我的书架过来隐藏下载 角色置为false
    protected boolean isTeacherInSchool;
    private String currentOrzSchoolId;
    public boolean isVipSchool = false;
    private int taskType;
    private String myShelfBookId;

    public interface Constants {
        String EXTRA_BOOK_SOURCE = BookListActivity.EXTRA_BOOK_SOURCE;
        String EXTRA_SCHOOL_ID = BookListActivity.EXTRA_SCHOOL_ID;
        String EXTRA_SCHOOL_NAME = BookListActivity.EXTRA_SCHOOL_NAME;
        String EXTRA_BOOK_PRIMARY_KEY = BookCatalogListActivity.EXTRA_BOOK_PRIMARY_KEY;
        String EXTRA_BOOK_ID = BookCatalogListActivity.EXTRA_BOOK_ID;
        String EXTRA_BOOK_NAME = BookCatalogListActivity.EXTRA_BOOK_NAME;
        String EXTRA_BOOK_CATALOG_ID = "bookCatalogId";
        String EXTRA_BOOK_CATALOG_NAME = "bookCatalogName";
        String BOOK_CATALOG_LIST = "bookCatalogList";
        String BOOK_CATALOG_ITEM = "bookCatalog";
        String EXTRA_ORIGIN_SCHOOL_ID = "originSchoolId";
        String FROM_SCHOOLRESOURCE = "from_schoolresource";//从校本资源库进来
        String KEY_ISHIDETAB = "ishidetab";//控制显示tab
        String FROM_AIRCLASS_ONLINE = "from_online";
        String KEY_ISSHOWCOURSEANDREADING = "isShowCourseAndReading";//是否显示“我要做课件”，“我要加点读”

        int PLATFORM_BOOK = BookListActivity.PLATFORM_BOOK;
        int SCHOOL_BOOK = BookListActivity.SCHOOL_BOOK;
        int PERSONAL_BOOK = BookListActivity.PERSONAL_BOOK;
        String CURRENTINDEX = "currentindex";


        int LQ_COURSE = 1;
        int TASK_ORDER = 2;
        int TEACHING_MATERIAL = 3;
        int LESSON = 4;
        int VIDEO = 5;
        int PDF = 6;
        int PPT = 7;
        int PICTURE = 8;
        int AUDIO = 9;
        int DOC = 10;
        int LESSON_BOOK = 0;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isPick = getArguments().getBoolean(ActivityUtils.EXTRA_IS_PICK);
            isHideTab = getArguments().getBoolean(Constants.KEY_ISHIDETAB);
            bookCatalogName = getArguments().getString(Constants.EXTRA_BOOK_CATALOG_NAME);
            CURRENTINDEX = getArguments().getInt(Constants.CURRENTINDEX, 0);
            schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
            originSchoolId = getArguments().getString(Constants.EXTRA_ORIGIN_SCHOOL_ID);
            bookCatalogId = getArguments().getString(Constants.EXTRA_BOOK_CATALOG_ID);
            bookId = getArguments().getString(Constants.EXTRA_BOOK_PRIMARY_KEY);
            myShelfBookId = getArguments().getString(BookDetailActivity.BOOK_OUTLINE_ID);
            calalogs = (List<Calalog>) getArguments().getSerializable(CatalogLessonListActivity.BOOK_CATALOG_LIST);
            //我的书架中来的资源
            collectSchoolId = getArguments().getString(BookDetailActivity.COLLECT_ORIGIN_SCHOOLID);
            isFromChoiceLib = getArguments().getBoolean(ActivityUtils.IS_FROM_CHOICE_LIB);
            //获取当前所在的机构schoolId
            currentOrzSchoolId = getArguments().getString(BookDetailActivity.CURRENT_ORZ_SCHOOLID);
            //判断是不是vipSchool(例如两栖蛙蛙体验学校)
            isVipSchool = getArguments().getBoolean(ActivityUtils.IS_LQWWA_VIP_SCHOOL, false);
            isTeacherInSchool = getArguments().getBoolean(ActivityUtils.EXTRA_IS_TEACHER, false);
            taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
            getArguments().putBoolean(Constants.FROM_SCHOOLRESOURCE, true);
        }
        initCatalogsNochildren();

        if (getActivity() != null) {
            if (VipConfig.isVip(getActivity()) || isVipSchool) {
                //vip不用检查权限
                return;
            }
        }
        if (!TextUtils.isEmpty(originSchoolId) && !TextUtils.isEmpty(schoolId)) {
            if (originSchoolId.equalsIgnoreCase(schoolId)) {
                loadSchool(originSchoolId, false);
            } else {
                loadSchool(originSchoolId, true);
            }
        } else if (!TextUtils.isEmpty(originSchoolId)) {
            loadSchool(originSchoolId, false);
        } else {
            //精品资源库originSchoolId不为空，其他为空。
            //检测是否加入了学校
//            loadSchool(currentOrzSchoolId,false);
            //检测是否加入了学校 如果schoolId为空时,即是从我的书架过来的
            if (!TextUtils.isEmpty(schoolId)) {
                if (isSaveSchoolDataSuccess()) {
                    checkBookOriginPermission(currentOrzSchoolId);
                } else {
                    loadAllSchoolInfo(false);
                }
            } else {
                //从我的书架过来的数据
                isTeacherInSchool = false;
            }
        }
    }

    private void initCatalogsNochildren() {
        calalogsNochildren = new ArrayList<Calalog>();
        for (Calalog calalog : calalogs) {
            if (calalog.getChildren() != null && calalog.getChildren().size() > 0) {
                calalogsNochildren.addAll(calalog.getChildren());
            } else {
                calalogsNochildren.add(calalog);
            }
        }
        if (bookCatalogId != null && calalogsNochildren.size() > 0) {
            for (int i = 0; i < calalogsNochildren.size(); i++) {
                if (bookCatalogId.equals(calalogsNochildren.get(i).getId())) {
                    position = i;
                    break;
                }
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_school_resource_container, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initListener();
        //tab数据为空时的标识
        loadBookResourceCount();
    }

    private void initView() {

        mContactsHeaderLeftBtn = (ImageView) findViewById(R.id.contacts_header_left_btn);
        mContactsHeaderRightBtn = (TextView) findViewById(R.id.contacts_header_right_btn);
        mContactsHeaderTitle = (TextView) findViewById(R.id.contacts_header_title);
        mSchoolResourceTabs = (PagerSlidingTabStrip) findViewById(R.id.school_resource_tabs);
        mSchoolResourcePager = (MyViewPager) findViewById(R.id.school_resource_pager);
        mAdapter = new MyPagerAdapter(getChildFragmentManager());
        mSchoolResourcePager.setAdapter(mAdapter);
        mSchoolResourceTabs.setViewPager(mSchoolResourcePager);
        mSchoolResourcePager.setCurrentItem(CURRENTINDEX);
        mSchoolResourcePager.setOffscreenPageLimit(mAdapter.getCount() - 1);

        mEditText = (ClearEditText) findViewById(R.id.search_keyword);
        mSearchBtn = (TextView) findViewById(R.id.search_btn);
        mContactsSearchBarLayout = (LinearLayout) findViewById(R.id.contacts_search_bar_layout);
        mPrivousTextview = (TextView) findViewById(R.id.privous_textview);
        mAllCatalogLayout = (LinearLayout) findViewById(R.id.all_catalog_layout);
        mNextTextview = (TextView) findViewById(R.id.next_textview);

        mContactsHeaderRightBtn.setVisibility(isPick ? View.VISIBLE : View.GONE);
        mContactsHeaderRightBtn.setText(getString(R.string.confirm));
        mContactsHeaderRightBtn.setOnClickListener(this);
        mContactsHeaderLeftBtn.setOnClickListener(this);
        mSearchBtn.setVisibility(View.VISIBLE);
        initTitle(CURRENTINDEX);

        if (mEditText != null) {
            mEditText.setHint(getString(R.string.search_title_or_author));
            mEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadResourceDataList();
                        return true;
                    }
                    return false;
                }
            });
            mEditText.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
                @Override
                public void onClearClick() {
                    mKeyword = "";
                    CURRENTSTATE = SEARCH;
                    switchFrg("");
                }
            });
            mEditText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }

        mSchoolResourceTabs.setVisibility(isHideTab ? View.GONE : View.VISIBLE);
        mSchoolResourcePager.setCanScroll(!isHideTab);

    }

    private void initTitle(int currentindex) {
        switch (currentindex) {
            case 0:
                mContactsHeaderTitle.setText(isPick ? getString(R.string.lesson_book) : bookCatalogName);
                break;
            case 1:
                String courseTitle = bookCatalogName;
                if (isPick) {
                    if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
                        courseTitle = getString(R.string.appoint_course_no_point);
                    } else {
                        courseTitle = getString(R.string.microcourse);
                    }
                }
                mContactsHeaderTitle.setText(courseTitle);
                break;
            case 2:
                String taskOrderTitle = bookCatalogName;
                if (isPick) {
                    if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
                        taskOrderTitle = getString(R.string.pls_add_work_task);
                    } else {
                        taskOrderTitle = getString(R.string.task_order);
                    }
                }
                mContactsHeaderTitle.setText(taskOrderTitle);
                break;
            case 3:
                mContactsHeaderTitle.setText(isPick ? getString(R.string.str_teaching_material) : bookCatalogName);
                break;

            case 4:
                mContactsHeaderTitle.setText(isPick ? getString(R.string.lesson_plan) : bookCatalogName);
                break;

            case 5:
                mContactsHeaderTitle.setText(isPick ? getString(R.string.videos) : bookCatalogName);
                break;

            case 6:
                mContactsHeaderTitle.setText(isPick ? getString(R.string.txt_pdf) : bookCatalogName);
                break;
            case 7:
                mContactsHeaderTitle.setText(isPick ? getString(R.string.txt_ppt) : bookCatalogName);
                break;
            case 8:
                mContactsHeaderTitle.setText(isPick ? getString(R.string.pictures) : bookCatalogName);
                break;
            case 9:
                mContactsHeaderTitle.setText(isPick ? getString(R.string.audios) : bookCatalogName);
                break;
            case 10:
                mContactsHeaderTitle.setText(isPick ? getString(R.string.DOC) : bookCatalogName);
                break;
            default:
                break;
        }
    }

    private void loadResourceDataList() {
        loadResourceDataList(mEditText.getText().toString());
    }

    protected void loadResourceDataList(String keyword) {
        CURRENTSTATE = SEARCH;
        keyword = keyword.trim();
        switchFrg(keyword);
        this.mKeyword = keyword;
    }

    private void switchFrg(String keyword) {
        int currentItem = mSchoolResourcePager.getCurrentItem();
        ISchoolResource fragment = (ISchoolResource) mAdapter.getItem(currentItem);

        switch (CURRENTSTATE) {
            //上一节 下一节
            case PREVIOUS_SECTION:
            case NEXT_SECTION:
                mEditText.setText("");
                fragment.privousNextClickEvent(position);
                if (calalogsNochildren != null && calalogsNochildren.size() > 0) {
                    bookCatalogId = calalogsNochildren.get(position).getId();
                    loadBookResourceCount();
                }
                break;
            //查看目录
            case CATALOG:
                fragment.selectCatalogEvent();
                break;
            //搜索
            case SEARCH:
                keywordLq = keyword;
                if (!keyword.equals(this.mKeyword)) {
                    fragment.clearSerachData();
                }
                fragment.loadResourceList(keyword);
                break;
            default:
                break;
        }
        this.bookCatalogName = fragment.getBookCatalogName();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn://搜索
                hideSoftKeyboard(getActivity());
                loadResourceDataList();
                break;
            case R.id.privous_textview://上一节
                CURRENTSTATE = PREVIOUS_SECTION;
                if (position == 0) {
                    TipsHelper.showToast(getActivity(), R.string.no_privous_note);
                    return;
                } else {
                    position = position - 1;
                }
                switchFrg("");
                break;
            case R.id.next_textview://下一节
                CURRENTSTATE = NEXT_SECTION;
                if (position == calalogsNochildren.size() - 1) {
                    TipsHelper.showToast(getActivity(), R.string.no_next_note);
                    return;
                } else {
                    position = position + 1;
                }
                switchFrg("");
                break;
            case R.id.all_catalog_layout://查看目录
                CURRENTSTATE = CATALOG;
                switchFrg("");
                break;
            case R.id.contacts_header_right_btn://isPick选择课件
                selectData();
                break;
            case R.id.contacts_header_left_btn:
                hideSoftKeyboard(getActivity());
                if (isPick) {
                    popStack();
                    return;
                }
                getActivity().finish();
                break;
            default:
                break;
        }

    }


    /**
     * isPick选择课件
     */
    private void selectData() {
        int currentItem = mSchoolResourcePager.getCurrentItem();
        ISchoolResource fragment = (ISchoolResource) mAdapter.getItem(currentItem);
        fragment.getSelectData();
    }

    private void initListener() {
        mAllCatalogLayout.setOnClickListener(this);
        mNextTextview.setOnClickListener(this);
        mSearchBtn.setOnClickListener(this);
        mPrivousTextview.setOnClickListener(this);
        mContactsSearchBarLayout.setOnClickListener(this);
        mEditText.setOnClickListener(this);
        mSchoolResourcePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setTitle(int index) {
        mEditText.clearFocus();
        mEditText.setText("");
        int currentItem = mSchoolResourcePager.getCurrentItem();
        ISchoolResource fragment = (ISchoolResource) mAdapter.getItem(currentItem);
        fragment.privousNextClickEvent(position);
        fragment.setTitle();
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {
                getString(R.string.lesson_book),
                getString(R.string.str_lecture_course),
                getString(R.string.microcourse),
                getString(R.string.task_order),
                getString(R.string.str_teaching_material),
                getString(R.string.lesson_plan),
                getString(R.string.videos),
                getString(R.string.txt_pdf),
                getString(R.string.txt_ppt),
                getString(R.string.pictures),
                getString(R.string.audios),
                getString(R.string.DOC)
        };

        private List<Fragment> mFragmentList = new ArrayList<>(TITLES.length);

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
            initFrg(mFragmentList);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
    }

    private void initFrg(List<Fragment> mFragmentList) {
        mFragmentList.add(new MyRemoteBookListFragment());
        mFragmentList.add(new MyRemoteLQCourseListFragment());
        mFragmentList.add(new MyRemoteLQCourseListFragment());
        mFragmentList.add(new MyTaskOrderFragment());
        mFragmentList.add(new MyTaskOrderFragment());
        mFragmentList.add(new MyRemoteLessonListFragment());
        mFragmentList.add(new MyRemoteVideoListFragment());
        mFragmentList.add(new MyRemotePDFListFragment());
        mFragmentList.add(new MyRemotePPTListFragment());
        mFragmentList.add(new MyRemotePictureListFragment());
        mFragmentList.add(new MyRemoteAudioListFragment());
        mFragmentList.add(new MyRemoteDOCListFragment());

        boolean isTeachingMaterial = false;
        boolean isLectureCourse = true;
        for (Fragment fragment : mFragmentList) {
            if (fragment instanceof MyTaskOrderFragment) {
                if (isTeachingMaterial) {
                    ((MyTaskOrderFragment) fragment).setIsTeachingMarterialType(true);
                }
                isTeachingMaterial = true;
            }
            if (fragment instanceof MyRemoteLQCourseListFragment) {
                if (isLectureCourse) {
                    ((MyRemoteLQCourseListFragment)fragment).setLectureCourse(true);
                }
                isLectureCourse = false;
            }
            fragment.setArguments(getArguments());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == CatalogSelectActivity.REQUEST_CODE_SELECT_CATALOG) {
                mEditText.setText("");
                Calalog calalog = (Calalog) data.getSerializableExtra(CatalogLessonListActivity.BOOK_CATALOG_ITEM);
                bookCatalogId = calalog.getId();
                loadBookResourceCount();
                if (bookCatalogId != null && calalogsNochildren.size() > 0) {
                    for (int i = 0; i < calalogsNochildren.size(); i++) {
                        if (bookCatalogId.equals(calalogsNochildren.get(i).getId())) {
                            position = i;
                            break;
                        }
                    }
                }
            }
        }
        mAdapter.getItem(mSchoolResourcePager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取学校详情
     *
     * @param
     * @param isCheckAuthorization 该值为true执行授权检查
     */
    private void loadSchool(final String originSchoolId, final boolean isCheckAuthorization) {
        if (getUserInfo() == null) {
            return;
        }
        if (TextUtils.isEmpty(originSchoolId)) {
            return;
        }
        Map<String, Object> params = new ArrayMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", originSchoolId);
        params.put("VersionCode", 1);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.SUBSCRIBE_SCHOOL_INFO_URL,
                params,
                new DefaultListener<SchoolInfoResult>(SchoolInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        schoolInfo = getResult().getModel();
                        if (!isCheckAuthorization) {
                            isSchoolJoin();
                        } else {
                            checkAuthorizationCondition(originSchoolId, schoolId);
                        }
                    }
                });
    }

    private void isSchoolJoin() {
        if (schoolInfo != null) {
            if (schoolInfo.hasJoinedSchool()) {
                if (isFromChoiceLib) {
                    checkChoicePer(currentOrzSchoolId);
                }
            } else {
                oneButtonDialog(false);
            }
        }
    }

    /**
     * 校验精品资源的权限
     */
    private void checkChoicePer(String schoolId) {
        if (TextUtils.isEmpty(schoolId) || TextUtils.isEmpty(getMemeberId())) {
            return;
        }
        final boolean isNeedInputMode = isPick || !TextUtils.isEmpty(collectSchoolId);
        CheckAuthorizationPmnHelper helper = new CheckAuthorizationPmnHelper(getActivity());
        helper.setSchoolId(schoolId).setMemberId(getMemeberId()).setIsNeedInputCode(!isNeedInputMode)
                .setListener(new CheckAuthorizationPmnHelper.checkResultListener() {
                    @Override
                    public void onResult(boolean isSuccess) {
                        if (!isSuccess) {
                            if (isNeedInputMode) {
                                oneButtonDialog(true);
                            } else {
                                getActivity().finish();
                            }
                        }
                    }
                }).check();

    }


    private boolean checkAuthorizationCondition(final String schoolId, final String feeSchoolId) {
        if (getUserInfo() == null) {
            return false;
        }
        Map<String, Object> params = new ArrayMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("CourseId", feeSchoolId);
        RequestHelper.RequestDataResultListener<AuthorizationInfoResult> listener =
                new RequestHelper.RequestDataResultListener<AuthorizationInfoResult>
                        (getActivity(), AuthorizationInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        AuthorizationInfoResult result = (AuthorizationInfoResult) getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            twoButtonDialog();
                            return;
                        }
                        authorizationInfo = result.getModel().getData();
                        if (authorizationInfo != null) {
                            if (authorizationInfo.isIsMemberAuthorized()) {

                            } else {
                                if (authorizationInfo.isIsCanAuthorize()) {
                                    authorizeToMembers(schoolId, feeSchoolId);
                                } else {

                                    twoButtonDialog();
                                }
                            }
                        }
                    }
                };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.CHECK_AUTHONRIZE_CONDITION_URL,
                params, listener);
        return false;
    }

    private void authorizeToMembers(String schoolId, String feeSchoolId) {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new ArrayMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("CourseId", feeSchoolId);
        if (schoolInfo != null) {
            params.put("RoleTypes", schoolInfo.getRoles());
        }
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.AUTHONRIZE_TO_MEMBER_URL,
                params, new RequestHelper.RequestModelResultListener<ModelResult>
                        (getActivity(), ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        ModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            return;
                        }
                        if (result.isSuccess()) {
                            if (authorizationInfo != null) {
                                authorizationInfo.setIsMemberAuthorized(true);
                            } else {

                                twoButtonDialog();
                            }
                        }
                    }
                });
    }

    private void twoButtonDialog() {

//        DialogHelper dialogHelper = DialogHelper.getIt(getActivity());
//        final DialogHelper.WarningDialog warningDialog = dialogHelper.getWarningDialog();
//        warningDialog.setCancelable(false);
//        warningDialog.setCanceledOnTouchOutside(false);
//        warningDialog.setButtonNames(R.string.str_ok,R.string.str_consultation);
//        warningDialog.setContent(R.string.course_is_not_authorize);
//        warningDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (view.getId() == R.id.confirm) {
//                    ActivityUtils.openCustomerService(getActivity()); //精品资源库开通咨询
//                }
//                warningDialog.dismiss();
//                getActivity().finish();
//
//            }
//        });

        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), "", getString(R.string.course_is_not_authorize),
                getString(R.string.str_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//
                getActivity().finish();
            }
        }, getString(R.string.str_consultation), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CustomerServiceActivity.start(getActivity(), CustomerServiceActivity.SOURCE_TYPE_OPEN_CONSULTION);
//
                getActivity().finish();
            }
        });
        dialog.setIsAutoDismiss(true);
        dialog.setCancelable(false);
        dialog.show();

    }

    private void oneButtonDialog(boolean isFromChoiceLibPop) {
//        DialogHelper dialogHelper = DialogHelper.getIt(getActivity());
//        final DialogHelper.OneButtonDialog warningDialog = dialogHelper.getOneButtonDialog();
//        warningDialog.setCancelable(false);
//        warningDialog.setCanceledOnTouchOutside(false);
//        warningDialog.setButtonNames(R.string.str_ok);
//        warningDialog.setContent(R.string.course_is_not_watch);
//        warningDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                warningDialog.dismiss();
//                getActivity().finish();
//
//            }
//        });

        String content = getString(R.string.resource_is_not_watch);
        if (isFromChoiceLibPop) {
            content = getString(R.string.choice_library_resource_over_date);
        }
        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), "", content,
                "", null, getString(R.string.str_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isPick) {
                    popStack();
                } else {
                    getActivity().finish();
                }
            }
        });
        dialog.setIsAutoDismiss(true);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //校验我的书架收藏的资源
        if (TextUtils.isEmpty(schoolId) && !VipConfig.isVip(getActivity()) && !isVipSchool) {
            loadAllSchoolInfo(true);
        }
    }

    /**
     * 检验我的书架资源的权限
     */
    private void checkMyBookShelfPermission() {
        //schoolId为空时 表示从我书架过来的
        if (TextUtils.isEmpty(schoolId)) {
            if (TextUtils.isEmpty(collectSchoolId)) {
                boolean isHasJoinSchool = Utils.isHasJoinSchool(getMemeberId());
                if (!isHasJoinSchool) {
                    oneButtonDialog(false);
                }
            } else {
                checkBookOriginPermission(collectSchoolId);
            }
        }
    }

    /**
     * 校验的资源的权限
     */
    private void checkBookOriginPermission(String schoolId) {
        if (!TextUtils.isEmpty(schoolId)) {
            boolean isJoinSchool = Utils.isJoinSchool(schoolId, getMemeberId());
            if (isJoinSchool) {
                if (isFromChoiceLib) {
                    checkChoicePer(schoolId);
                }
            } else {
                oneButtonDialog(false);
            }
        } else {
            oneButtonDialog(false);
        }
    }

    /**
     * 判断有没有保存school信息到本地
     */
    private boolean isSaveSchoolDataSuccess() {
        List<SchoolInfo> allSchoolInfoList = DemoApplication.getInstance().getPrefsManager()
                .getAllSchoolInfoList(getMemeberId());
        if (allSchoolInfoList == null || allSchoolInfoList.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 加载下载信息
     */
    private void loadAllSchoolInfo(final boolean isFromMyShelf) {
        if (!isLogin()) {
            return;
        }
        showLoadingDialog();
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getMemeberId());
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_SCHOOL_LIST_URL, params,
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
                        List<SchoolInfo> list = result.getModel().getSubscribeNoList();
                        //保存当前所有学校的集合
                        try {
                            DemoApplication.getInstance().getPrefsManager().setDataList
                                    (getUserInfo().getMemberId() + PrefsManager.PrefsItems
                                            .ALL_SCHOOLINFO_LIST, list);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                        if (isFromMyShelf) {
                            checkMyBookShelfPermission();
                        } else {
                            checkBookOriginPermission(currentOrzSchoolId);
                        }
                    }
                });
    }

    /**
     * 加载章节详情页数据tab含有类型资源的数量
     * 备注:如果count == 0 时显示灰色
     */
    private void loadBookResourceCount() {
        if (isPick) {
            return;
        }
        if (TextUtils.isEmpty(bookId) || TextUtils.isEmpty(bookCatalogId)) {
            return;
        }
        Map<String, Object> param = new HashMap<>();
        if (TextUtils.isEmpty(myShelfBookId)) {
            param.put("BookId", bookId);
        } else {
            param.put("BookId", myShelfBookId);
        }
        param.put("SectionId", bookCatalogId);
        DefaultDataListener listener = new DefaultDataListener<DataModelResult>(DataModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                DataModelResult result = getResult();
                if (result != null && result.isSuccess()) {
                    DataModel dataModel = result.getModel();
                    if (dataModel != null) {
                        List<SchoolMaterialData> data = JSONObject.parseArray(dataModel
                                .getData().toString(), SchoolMaterialData.class);
                        if (data != null && data.size() > 0) {
                            updateResultData(data);
                        }
                    }
                }
            }
        };
        listener.setShowErrorTips(false);
        postRequest(ServerUrl.GET_LOAD_BOOK_RESOURCE_COUNT_BASE_URL, param, listener);
    }

    private void updateResultData(List<SchoolMaterialData> data) {
        List<Integer> zeroData = new ArrayList<>();
        for (SchoolMaterialData info : data) {
            if (info.getSchoolMaterialTypeCount() != 0) {
                zeroData.add(transferTabIndex(info.getSchoolMaterialType()));
            }
        }
        mSchoolResourceTabs.setChangeTabTextColor(true);
        mSchoolResourceTabs.setChangeTabTextColorIndex(zeroData);
        mSchoolResourceTabs.notifyDataSetChanged();
    }

    private int transferTabIndex(int position) {
        if (position == MediaType.SCHOOL_PPT) {
            return 8;
        } else if (position == MediaType.SCHOOL_PDF) {
            return 7;
        } else if (position == MediaType.SCHOOL_PICTURE) {
            return 9;
        } else if (position == MediaType.SCHOOL_VIDEO) {
            return 6;
        } else if (position == MediaType.SCHOOL_AUDIO) {
            return 10;
        } else if (position == MediaType.SCHOOL_COURSEWARE) {
            return 2;
        } else if (position == MediaType.SCHOOL_TASKORDER) {
            return 3;
        } else if (position == MediaType.SCHOOL_TEACHINGMATERIAL) {
            return 4;
        } else if (position == MediaType.SCHOOL_LESSON) {
            return 5;
        } else if (position == MediaType.SCHOOL_DOC) {
            return 11;
        } else if (position == MediaType.SCHOOL_LESSON_BOOK) {
            return 0;
        } else if (position == MediaType.SCHOOL_LECTURE_COURSE) {
            return 1;
        } else {
            return -1;
        }
    }
}
