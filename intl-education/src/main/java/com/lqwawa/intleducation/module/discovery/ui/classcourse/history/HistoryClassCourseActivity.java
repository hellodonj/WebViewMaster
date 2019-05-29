package com.lqwawa.intleducation.module.discovery.ui.classcourse.history;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.RefreshUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.course.ClassCourseEntity;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseAdapter;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseParams;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.Tab;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.addHistory.AddHistoryCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.common.ActionDialogFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 班级历史学程的页面
 */
public class HistoryClassCourseActivity extends PresenterActivity<HistoryClassCourseContract.Presenter>
        implements HistoryClassCourseContract.View, View.OnClickListener {

    private static final int SEARCH_REQUEST_CODE = 1 << 0;
    private static final int ADD_HISTORY_REQUEST_CODE = 1 << 1;

    public static final String KEY_EXTRA_TRIGGER = "KEY_EXTRA_TRIGGER";
    private static final String KEY_EXTRA_AUTHORIZATION = "KEY_EXTRA_AUTHORIZATION";

    // 小语种课程
    private static final int MINORITY_LANGUAGE_COURSE_ID = 2004;
    // 英语国际课程
    private static final int ENGLISH_INTERNATIONAL_COURSE_ID = 2001;
    // 特色课程
    private static final int CHARACTERISTIC_COURSE_ID = 2005;
    // 基础课程
    private static final int COUNTRY_COURSE_ID = 2003;

    // 分类阅读
    public static final int CLASSIFIED_READING_ID = 1001;
    // 绘本
    public static final int PICTURE_BOOK_ID = 1002;
    // Q配音
    public static final int Q_DUBBING_ID = 1003;

    // LQ English Primary
    private static final int ENGLISH_INTERNATIONAL_ENGLISH_PRIMARY_ID = 2011;

    private static final int CONFIG_TYPE_1 = 1;
    private static final int CONFIG_TYPE_2 = 2;
    private static final int CONFIG_TYPE_3 = 3;
    private static final int CONFIG_TYPE_4 = 4;

    private TopBar mTopBar;

    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private LinearLayout mHeaderLayout;
    private LinearLayout mTabVector1, mTabVector2, mTabVector3;
    private TextView mTabLabel1, mTabLabel2, mTabLabel3;
    private TabLayout mTabLayout1, mTabLayout2, mTabLayout3;

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private ClassCourseAdapter mCourseAdapter;
    private CourseEmptyView mEmptyLayout;
    private LinearLayout mBottomLayout;
    private Button mBtnAdd;
    private Button mBtnRemove;

    private ClassCourseParams mClassCourseParams;
    private String mSchoolId;
    private String mClassId;
    private String mClassName;
    private String mRoles;
    private boolean isTeacher;
    // 或者是班主任或者老师
    private boolean isHeadMasterOrTeacher;

    // 全部文本
    private String mAllText = UIUtil.getString(R.string.label_course_filtrate_all);
    private List<LQCourseConfigEntity> mConfigEntities;
    // 筛选集合1
    private List<Tab> mFiltrateArray1;
    // 筛选集合2
    private List<Tab> mFiltrateArray2;
    // 筛选集合3
    private List<Tab> mFiltrateArray3;

    private int pageIndex;
    // 是否是Hold状态
    private boolean holdState;
    // 搜索关键词
    private String mKeyWord;

    private boolean isUpdate;

    private boolean isAuthorized;

    // 暂存上个标签参数，当前标签参数与上个不一致，清空所保存的选择Id
    private String lastTagParams;


    @Override
    protected HistoryClassCourseContract.Presenter initPresenter() {
        return new HistoryClassCoursePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_history_class_course;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mClassCourseParams = (ClassCourseParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        isAuthorized = bundle.getBoolean(KEY_EXTRA_AUTHORIZATION);
        if (EmptyUtil.isEmpty(mClassCourseParams)) return false;
        mSchoolId = mClassCourseParams.getSchoolId();
        mClassId = mClassCourseParams.getClassId();
        mClassName = mClassCourseParams.getClassName();
        mRoles = mClassCourseParams.getRoles();
        isHeadMasterOrTeacher = mClassCourseParams.isHeadMaster() | mClassCourseParams.isTeacher();
        if (EmptyUtil.isEmpty(mSchoolId) ||
                EmptyUtil.isEmpty(mClassId) ||
                EmptyUtil.isEmpty(mRoles)) {
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_history_course);
        mTopBar.findViewById(R.id.left_function1_image).setOnClickListener(view -> {
            if (isUpdate) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean(KEY_EXTRA_TRIGGER, isUpdate);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
            }
            RefreshUtil.getInstance().clear();
            finish();
        });

        mTopBar.setRightFunctionImage1(R.drawable.search, view -> {
            // 搜索
            SearchActivity.show(
                    HistoryClassCourseActivity.this,
                    HideSortType.TYPE_SORT_CLASS_HISTORY_COURSE,
                    UIUtil.getString(R.string.title_history_course),
                    SEARCH_REQUEST_CODE);
            RefreshUtil.getInstance().clear();
        });

        mSearchContent = (EditText) findViewById(R.id.et_search);
        mSearchClear = (ImageView) findViewById(R.id.iv_search_clear);
        mSearchFilter = (TextView) findViewById(R.id.tv_filter);

        mSearchContent.setHint(R.string.label_please_input_keyword_hint);

        mSearchFilter.setVisibility(View.VISIBLE);
        mSearchClear.setOnClickListener(this);
        mSearchFilter.setOnClickListener(this);

        // @date   :2018/6/13 0013 上午 11:31
        // @func   :V5.7改用键盘搜索的方式
        mSearchContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (s.length() > 0) {
                    mSearchClear.setVisibility(View.VISIBLE);
                } else {
                    mSearchClear.setVisibility(View.INVISIBLE);
                }
                mSearchContent.setImeOptions(s.length() > 0
                        ? EditorInfo.IME_ACTION_SEARCH
                        : EditorInfo.IME_ACTION_DONE);
                mSearchContent.setMaxLines(1);
                mSearchContent.setInputType(EditorInfo.TYPE_CLASS_TEXT
                        | EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            }
        });

        mSearchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 搜索，收起软件盘
                    KeyboardUtil.hideSoftInput(HistoryClassCourseActivity.this);
                    requestClassCourse(false);
                }
                return true;
            }
        });


        mHeaderLayout = (LinearLayout) findViewById(R.id.header_layout);
        mTabVector1 = (LinearLayout) findViewById(R.id.tab_vector_1);
        mTabVector2 = (LinearLayout) findViewById(R.id.tab_vector_2);
        mTabVector3 = (LinearLayout) findViewById(R.id.tab_vector_3);
        mTabLabel1 = (TextView) findViewById(R.id.tab_label_1);
        mTabLabel2 = (TextView) findViewById(R.id.tab_label_2);
        mTabLabel3 = (TextView) findViewById(R.id.tab_label_3);
        mTabLayout1 = (TabLayout) findViewById(R.id.tab_layout_1);
        mTabLayout2 = (TabLayout) findViewById(R.id.tab_layout_2);
        mTabLayout3 = (TabLayout) findViewById(R.id.tab_layout_3);

        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mEmptyLayout = (CourseEmptyView) findViewById(R.id.empty_layout);
        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mBtnAdd = (Button) findViewById(R.id.btn_add_course);
        mBtnRemove = (Button) findViewById(R.id.btn_remove_course);

        boolean isTeacher = UserHelper.isTeacher(mRoles);
        this.isTeacher = isTeacher;

        // 班级学程进入参数
        boolean isResult = isTeacher || mClassCourseParams.isHeadMaster();
        if (isHeadMasterOrTeacher) {
            mBottomLayout.setVisibility(View.VISIBLE);
            mBtnAdd.setOnClickListener(this);
            mBtnRemove.setOnClickListener(this);
        } else {
            mBottomLayout.setVisibility(View.GONE);
        }

        mCourseAdapter = new ClassCourseAdapter(mClassCourseParams.isHeadMaster(), mRoles);
        mRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mCourseAdapter);

        mCourseAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<ClassCourseEntity>() {

            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, ClassCourseEntity entity) {
                super.onItemClick(holder, entity);
                if (mCourseAdapter.isChoiceMode()) {
                    // 添加选择,或者取消选择
                    entity.setChecked(!entity.isChecked());
                    mCourseAdapter.notifyDataSetChanged();
                    if (entity.isChecked()) {
                        RefreshUtil.getInstance().addId(entity.getId());
                    } else {
                        RefreshUtil.getInstance().removeId(entity.getId());
                    }
                } else {
                    // 班级学程的详情入口
                    String courseId = entity.getCourseId();
                    // 班级学程进入参数
                    boolean isTeacher = UserHelper.isTeacher(mRoles);
                    boolean isResult = isTeacher || mClassCourseParams.isHeadMaster();
                    boolean isParent = UserHelper.isParent(mRoles);

                    CourseDetailParams params = new CourseDetailParams(mSchoolId, mClassId, mClassName, isAuthorized);
                    params.setClassTeacher(isResult);
                    // 优先老师处理
                    params.setClassParent(!isResult && isParent);

                    // CourseDetailsActivity.start(ClassCourseActivity.this , courseId, true, UserHelper.getUserId(),params);

                    CourseDetailsActivity.start(HistoryClassCourseActivity.this, courseId, true,
                            UserHelper.getUserId(), isAuthorized, params, false);
                    // 如果是班主任,清除Hold状态
                    if (mClassCourseParams.isHeadMaster()) {
                        switchHoldState(false);
                    }
                }
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder holder, ClassCourseEntity classCourseEntity) {
                super.onItemLongClick(holder, classCourseEntity);
                if (isHeadMasterOrTeacher && !mCourseAdapter.isChoiceMode()) {
//                    switchHoldState(true, classCourseEntity);
                    int resId = mClassCourseParams.isHeadMaster() ? R.string.label_delete : 0;
                    ClassCourseEntity entity = classCourseEntity;
                    ActionDialogFragment.show(getSupportFragmentManager(),
                            R.string.label_remove_out, resId,
                            (button, tag) -> {
                                if (tag == ActionDialogFragment.Tag.UP) {
                                    // 移除
                                    List<ClassCourseEntity> entities = new ArrayList<>();
                                    entities.add(entity);
                                    mPresenter.requestRemoveHistoryCourseFromClass(mSchoolId, mClassId, entities);
                                } else if (tag == ActionDialogFragment.Tag.DOWN) {
                                    // 删除
                                    deleteCourseFromClass(entity);
                                }
                            });
                }
            }
        });

        // 添加cell的删除事件
        mCourseAdapter.setNavigator(position -> {

        });

        // 下拉刷新与加载更多
        mRefreshLayout.setOnHeaderRefreshListener(view -> requestClassCourse(false));
        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnFooterRefreshListener(view -> requestClassCourse(true));
    }

    @Override
    protected void initData() {
        super.initData();
        // 获取标签
        mPresenter.requestHistoryClassConfigData(mClassId);
    }

    /**
     * 更换Hold状态
     *
     * @param state 期望的状态
     * @return 返回更改好的状态
     */
    private boolean switchHoldState(boolean state) {
        return switchHoldState(state, null);
    }

    /**
     * 更换Hold状态
     *
     * @param state 期望的状态
     * @param hold  state 为true ,hold不能为空
     * @return 返回更改好的状态
     */
    private boolean switchHoldState(boolean state, ClassCourseEntity hold) {
        holdState = state;
        // 班主任Hold
        // 更改所有Hold的状态
        List<ClassCourseEntity> items = mCourseAdapter.getItems();
        if (EmptyUtil.isNotEmpty(items)) {
            for (ClassCourseEntity entity : items) {
                if (state && entity.getId() == hold.getId()) {
                    // 同一个课程
                    entity.setHold(state);
                    break;
                }

                if (!state) {
                    entity.setHold(state);
                }
            }
        }

        mCourseAdapter.notifyDataSetChanged();
        return state;
    }

    /**
     * 触发更新
     */
    private void triggerUpdateData() {
        if (EmptyUtil.isNotEmpty(mConfigEntities)) {
            clearConfigArrayStatus(mConfigEntities);
        }

//        mCourseAdapter.setChoiceMode(false);
//        updateActionStatus(false);
        requestClassCourse(false);
    }

    /**
     * 获取班级学程数据
     *
     * @param isMoreData 是否加载更多
     */
    private void requestClassCourse(boolean isMoreData) {
        if (!isMoreData) {
            pageIndex = 0;
        } else {
            pageIndex++;
        }

        mRefreshLayout.showRefresh();
        // String name = mSearchContent.getText().toString().trim();
        String name = mKeyWord;
        if (EmptyUtil.isEmpty(name)) name = "";
        int role = 1; // 默认学生
        // 如果是老师身份 role传0
        if (UserHelper.isTeacher(mRoles)) role = 0;

        // 准备Level,先获取到第一级别的Level
        String level = "";
        int paramOneId = 0;
        int paramTwoId = 0;

        if (mFiltrateArray1 != null &&
                mFiltrateArray2 != null &&
                mFiltrateArray3 != null) {

            int rootId = 0;
            for (Tab tab : mFiltrateArray1) {
                if (tab.isChecked()) {
                    level = tab.getLevel();

                    // 找到选中的第一分类
                    rootId = tab.getId();
                    break;
                }
            }

            // 查看TabLayout是全部选中,还是正确Tab选中
            int rootTypeId = 0;
            for (Tab tab : mFiltrateArray2) {
                if (!tab.isAll() && tab.isChecked()) {
                    // 选择不是全部的Level
                    level = tab.getLevel();
                    rootTypeId = tab.getId();
                    break;
                }
            }

            if (rootId != MINORITY_LANGUAGE_COURSE_ID) {
                // 不是小语种课程
                for (Tab tab : mFiltrateArray3) {
                    if (!tab.isAll() && tab.isChecked()) {
                        if (rootId == CHARACTERISTIC_COURSE_ID || rootId == COUNTRY_COURSE_ID) {
                            // 特色课程或者国家课程
                            paramTwoId = tab.getLabelId();
                        } else if (rootId == ENGLISH_INTERNATIONAL_COURSE_ID && rootTypeId == ENGLISH_INTERNATIONAL_ENGLISH_PRIMARY_ID) {
                            // 英语国际课程 LQ English PRIMARY
                            paramTwoId = tab.getLabelId();
                        } else {
                            paramOneId = tab.getLabelId();
                        }
                    }
                }
            }
        }

        String tagParams = getTagParams(name, level, paramOneId, paramTwoId);
        if (!TextUtils.equals(tagParams, lastTagParams)) {
            lastTagParams = tagParams;
            RefreshUtil.getInstance().clear();
        }

        mPresenter.requestHistoryClassCourseData(mClassId, role, name, level, paramOneId, paramTwoId, pageIndex);
    }

    private String getTagParams(String name, String level, int paramOneId, int paramTwoId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name);
        stringBuilder.append(level);
        stringBuilder.append(paramOneId);
        stringBuilder.append(paramTwoId);
        return stringBuilder.toString();
    }

    @Override
    public void updateHistoryClassConfigView(@NonNull List<LQCourseConfigEntity> entities) {
        this.mConfigEntities = entities;
        // 组装Label数据
        // 默认第一个选中
        if (EmptyUtil.isNotEmpty(entities)) {

            mFiltrateArray1 = new ArrayList<>();
            mFiltrateArray2 = new ArrayList<>();
            mFiltrateArray3 = new ArrayList<>();

            if (EmptyUtil.isEmpty(mConfigEntities)) return;
            recursionConfig(entities);


            LQCourseConfigEntity rootEntity = entities.get(0);
            int rootId = rootEntity.getId();
            configLabel(rootId);
            // 设置TabLayout相关监听
            initTabListener();
            // 设置数据到TabLayout上
            initTabControl();
            // 设置第一个选中
            // mTabLayout1.getTabAt(0).select();
            // mTabLayout1.getTabAt(0).getCustomView().setSelected(true);
            mHeaderLayout.setVisibility(View.VISIBLE);
        } else {
            // 隐藏HeaderLayout
            mHeaderLayout.setVisibility(View.GONE);
            updateHistoryClassCourseView(null);
        }
    }

    /**
     * 组装Label数据
     */
    private void configLabel(@NonNull int rootId) {
        // 类型固定
        mTabLabel1.setText(getString(R.string.label_colon_type));
        mTabVector2.setVisibility(View.VISIBLE);
        if (rootId == MINORITY_LANGUAGE_COURSE_ID) {
            // 小语种课程 二级页面
            mTabVector3.setVisibility(View.GONE);

            // 语言
            mTabLabel2.setText(getString(R.string.label_colon_language));
        } else if (rootId == ENGLISH_INTERNATIONAL_COURSE_ID) {
            // 英语国际课程 三级页面
            // 类型 科目

            mTabVector3.setVisibility(View.VISIBLE);

            mTabLabel2.setText(getString(R.string.label_colon_type));
            mTabLabel3.setText(getString(R.string.label_colon_subject));
        } else if (rootId == CLASSIFIED_READING_ID) {
            //分类阅读
            mTabVector3.setVisibility(View.GONE);

            // 科目, 级别
            mTabLabel2.setText(getString(R.string.label_colon_subject));
            mTabLabel3.setText(getString(R.string.label_colon_level));
        } else if (rootId == PICTURE_BOOK_ID) {
            //绘本  三级页面
            mTabVector3.setVisibility(View.VISIBLE);

            // 年龄段 语言
            mTabLabel2.setText(getString(R.string.label_colon_age));
            mTabLabel3.setText(getString(R.string.label_colon_language));
        } else if (rootId == Q_DUBBING_ID) {
            mTabVector2.setVisibility(View.GONE);
            mTabVector3.setVisibility(View.GONE);
        } else {
            // 三级页面
            mTabVector3.setVisibility(View.VISIBLE);

            // 学段 科目
            mTabLabel2.setText(getString(R.string.label_colon_period));
            mTabLabel3.setText(getString(R.string.label_colon_subject));


            // 特色课程 or 国家课程
            if (mFiltrateArray2.size() <= 1) {
                // 标签隐藏
                mTabVector2.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 递归调用
     */
    private void recursionConfig(List<LQCourseConfigEntity> entities) {
        clearArray(CONFIG_TYPE_1);
        recursionConfigArray(entities);
    }

    /**
     * 清空集合
     */
    private void clearArray(int configType) {
        // 清空所有数据
        if (configType <= CONFIG_TYPE_4 || configType <= CONFIG_TYPE_3) {
            // 清除第三个
            mFiltrateArray3.clear();
        }

        if (configType <= CONFIG_TYPE_2) {
            // 清除第二个
            mFiltrateArray2.clear();
        }

        if (configType <= CONFIG_TYPE_1) {
            // 清除第一个
            mFiltrateArray1.clear();
        }
    }

    /**
     * 清空默认设置科目的选择状态
     *
     * @param array 标签数据
     */
    private void clearConfigArrayStatus(@NonNull List<LQCourseConfigEntity> array) {
        if (EmptyUtil.isEmpty(array)) return;

        for (LQCourseConfigEntity entity : array) {
            entity.setSelected(false);

            // 递归调用
            List<LQCourseConfigEntity> childList = entity.getChildList();
            clearConfigArrayStatus(childList);
        }
    }

    /**
     * 递归调用
     */
    private void recursionConfigArray(@NonNull List<LQCourseConfigEntity> array) {
        if (EmptyUtil.isEmpty(array)) return;

        for (LQCourseConfigEntity entity : array) {
            if (entity.getConfigType() == CONFIG_TYPE_1) {
                Tab tab = Tab.build(entity);
                if (!mFiltrateArray1.contains(tab)) {
                    mFiltrateArray1.add(Tab.build(entity));
                }
                // 第一个筛选容器,加全部
                /*Tab allTab1 = Tab.buildAll(mAllText,array);
                if(!mFiltrateArray1.contains(allTab1)){
                    mFiltrateArray1.add(0,allTab1);
                }*/
            }

            if (entity.getConfigType() == CONFIG_TYPE_2) {
                Tab tab = Tab.build(entity);
                if (!mFiltrateArray2.contains(tab)) {
                    mFiltrateArray2.add(Tab.build(entity));
                }
                // 第二个筛选容器,加全部
                /*Tab allTab2 = Tab.buildAll(mAllText,null);
                if(!mFiltrateArray2.contains(allTab2)){
                    mFiltrateArray2.add(0,allTab2);
                }*/
            }

            if (entity.getConfigType() == CONFIG_TYPE_3 || entity.getConfigType() == CONFIG_TYPE_4) {
                Tab tab = Tab.build(entity);
                if (!mFiltrateArray3.contains(tab)) {
                    mFiltrateArray3.add(Tab.build(entity));
                }
                // 第三个筛选容器,加全部
                Tab allTab3 = Tab.buildAll(mAllText, null);
                if (!mFiltrateArray3.contains(allTab3)) {
                    mFiltrateArray3.add(0, allTab3);
                }
            }
            // 递归调用
            List<LQCourseConfigEntity> childList = entity.getChildList();
            recursionConfigArray(childList);
        }
    }

    /**
     * 填充数据,设置监听
     */
    private void initTabControl() {
        initTabControl1();
        // initTabControl2();
        // initTabControl3();
    }

    private void initTabControl1() {
        mTabLayout1.removeAllTabs();
        // 查看是否有Selected的
        boolean haveSelected = false;
        for (Tab tab : mFiltrateArray1) {
            if (tab.isSelected() && isTeacher) {
                haveSelected = true;
                break;
            }
        }

        boolean setSelected = false;
        for (Tab tab : mFiltrateArray1) {
            View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
            TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
            tvContent.setText(tab.getConfigValue());
            // 将tab数据作为Tag设置到TabLayout的TabLayout.Tab上
            TabLayout.Tab newTab = mTabLayout1.newTab().setCustomView(tabView).setTag(tab);

            if (!setSelected) {
                setSelected = (mTabLayout1.getTabCount() == 0 && !haveSelected) || (tab.isSelected() && isTeacher);
                mTabLayout1.addTab(newTab, setSelected);
            } else {
                // 已经添加过已经选择的Tab
                mTabLayout1.addTab(newTab);
            }
        }


        mTabLayout1.smoothScrollTo(0, 0);
    }

    private void initTabControl2() {
        mTabLayout2.removeAllTabs();

        // 查看是否有Selected的
        boolean haveSelected = false;
        for (Tab tab : mFiltrateArray2) {
            if (tab.isSelected() && isTeacher) {
                haveSelected = true;
                break;
            }
        }

        boolean setSelected = false;
        for (Tab tab : mFiltrateArray2) {
            View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
            TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
            tvContent.setText(tab.getConfigValue());
            TabLayout.Tab newTab = mTabLayout2.newTab().setCustomView(tabView).setTag(tab);

            if (!setSelected) {
                setSelected = (mTabLayout2.getTabCount() == 0 && !haveSelected) || (tab.isSelected() && isTeacher);
                mTabLayout2.addTab(newTab, setSelected);
            } else {
                // 已经添加过已经选择的Tab
                mTabLayout2.addTab(newTab);
            }
        }

        mTabLayout2.smoothScrollTo(0, 0);
    }

    private void initTabControl3() {
        mTabLayout3.removeAllTabs();

        // 查看是否有Selected的
        boolean haveSelected = false;
        for (Tab tab : mFiltrateArray3) {
            if (tab.isSelected() && isTeacher) {
                haveSelected = true;
                break;
            }
        }

        boolean setSelected = false;
        if (EmptyUtil.isNotEmpty(mFiltrateArray3)) {
            for (Tab tab : mFiltrateArray3) {
                View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
                TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
                tvContent.setText(tab.getConfigValue());
                TabLayout.Tab newTab = mTabLayout3.newTab().setCustomView(tabView).setTag(tab);

                if (!setSelected) {
                    setSelected = (mTabLayout3.getTabCount() == 0 && !haveSelected) || (tab.isSelected() && isTeacher);
                    mTabLayout3.addTab(newTab, setSelected);
                } else {
                    // 已经添加过已经选择的Tab
                    mTabLayout3.addTab(newTab);
                }
            }
        }

        mTabLayout3.smoothScrollTo(0, 0);
    }

    /**
     * 设置相关联动的监听
     */
    private void initTabListener() {
        mTabLayout1.removeOnTabSelectedListener(tabLayout1Adapter);
        mTabLayout1.addOnTabSelectedListener(tabLayout1Adapter);

        mTabLayout2.removeOnTabSelectedListener(tabLayout2Adapter);
        mTabLayout2.addOnTabSelectedListener(tabLayout2Adapter);

        // 小语种TabLayout3被隐藏了
        mTabLayout3.removeOnTabSelectedListener(tabLayout3Adapter);
        mTabLayout3.addOnTabSelectedListener(tabLayout3Adapter);
    }

    private TabSelectedAdapter tabLayout1Adapter = new TabSelectedAdapter() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            super.onTabSelected(tab);
            // 全部发生数据联动
            Tab tabData = (Tab) tab.getTag();
            setTabItemSelected(mFiltrateArray1, tabData);
            // 重新配置2,3数据的联动效果
            clearArray(CONFIG_TYPE_2);
            recursionConfigArray(tabData.getChildList());
            configLabel(tabData.getId());
            initTabControl2();
            if (tabData.getId() == PICTURE_BOOK_ID) {
                clearArray(CONFIG_TYPE_3);
                recursionConfigArray(tabData.getChildList());
                initTabControl3();
            }
            // 3在点1的时候则不需要初始化，因为全部都是三级联动的效果
            // initTabControl3();

            // 数据请求
            // triggerUpdateData();
            if (tabData.getChildList() == null || tabData.getChildList().isEmpty()) {
                triggerUpdateData();
            }
        }
    };

    private TabSelectedAdapter tabLayout2Adapter = new TabSelectedAdapter() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            super.onTabSelected(tab);
            Tab tabData = (Tab) tab.getTag();
            setTabItemSelected(mFiltrateArray2, tabData);
            // 所有标签都会发生二级，甚至三级联动
            TabLayout.Tab tabAt = mTabLayout1.getTabAt(mTabLayout1.getSelectedTabPosition());
            if (EmptyUtil.isNotEmpty(tabAt)) {
                if (tabData.isAll()) {
                    List<LQCourseConfigEntity> entities = new ArrayList<>();
                    for (Tab item : mFiltrateArray2) {
                        if (!item.isAll() && EmptyUtil.isNotEmpty(item.getChildList())) {
                            entities.addAll(item.getChildList());
                        }
                    }
                    tabData.setChildList(entities);
                }

                // 重新配置3数据的联动效果
                clearArray(CONFIG_TYPE_3);
                recursionConfigArray(tabData.getChildList());
                initTabControl3();
            }

            // 数据请求
            for (Tab tab1 : mFiltrateArray1) {
                if (tab1.getId() == MINORITY_LANGUAGE_COURSE_ID
                        || tab1.getId() == CLASSIFIED_READING_ID) {
                    // 小语种和分类阅读
                    triggerUpdateData();
                }
            }

        }
    };

    private TabSelectedAdapter tabLayout3Adapter = new TabSelectedAdapter() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            super.onTabSelected(tab);
            Tab tabData = (Tab) tab.getTag();
            setTabItemSelected(mFiltrateArray3, tabData);
            // 数据请求
            triggerUpdateData();
        }
    };
    /*private void initTabListener(){
        mTabLayout1.addOnTabSelectedListener(new TabSelectedAdapter(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                // 全部发生数据联动
                Tab tabData = (Tab) tab.getTag();
                setTabItemSelected(mFiltrateArray1,tabData);
                // 重新配置2,3数据的联动效果
                clearArray(CONFIG_TYPE_2);
                recursionConfigArray(tabData.getChildList());
                configLabel(tabData.getId());
                initTabControl2();
                // 3在点1的时候则不需要初始化，因为全部都是三级联动的效果
                // initTabControl3();

                // 数据请求
                // triggerUpdateData();
            }
        });

        mTabLayout2.addOnTabSelectedListener(new TabSelectedAdapter(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                Tab tabData = (Tab) tab.getTag();
                setTabItemSelected(mFiltrateArray2,tabData);
                // 所有标签都会发生二级，甚至三级联动
                TabLayout.Tab tabAt = mTabLayout1.getTabAt(mTabLayout1.getSelectedTabPosition());
                if(EmptyUtil.isNotEmpty(tabAt)){
                    if(tabData.isAll()){
                        List<LQCourseConfigEntity> entities = new ArrayList<>();
                        for (Tab item:mFiltrateArray2) {
                            if(!item.isAll() && EmptyUtil.isNotEmpty(item.getChildList())){
                                entities.addAll(item.getChildList());
                            }
                        }
                        tabData.setChildList(entities);
                    }

                    // 重新配置3数据的联动效果
                    clearArray(CONFIG_TYPE_3);
                    recursionConfigArray(tabData.getChildList());
                    initTabControl3();
                }

                // 数据请求
                for (Tab tab1:mFiltrateArray1){
                    if(tab1.getId() == MINORITY_LANGUAGE_COURSE_ID){
                        // 选中的是小语种的Id
                        triggerUpdateData();
                    }
                }

            }
        });

        // 小语种TabLayout3被隐藏了
        mTabLayout3.addOnTabSelectedListener(new TabSelectedAdapter(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                Tab tabData = (Tab) tab.getTag();
                setTabItemSelected(mFiltrateArray3,tabData);
                // 数据请求
                triggerUpdateData();
            }
        });
    }*/

    /**
     * 设置该Tab选中
     *
     * @param array 对应的Tab集合
     * @param tab   选择的Tab
     */
    private void setTabItemSelected(@NonNull List<Tab> array, @NonNull Tab tab) {
        if (EmptyUtil.isEmpty(array) || EmptyUtil.isEmpty(tab)) return;
        for (Tab item : array) {
            item.setChecked(false);
            if (item.equals(tab)) {
                item.setChecked(true);
            }
        }
    }

    @Override
    public void updateHistoryClassCourseView(List<ClassCourseEntity> entities) {
        restoreCheckState(entities);

        mCourseAdapter.replace(entities);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);

        if (EmptyUtil.isEmpty(entities)) {
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateMoreHistoryClassCourseView(List<ClassCourseEntity> entities) {
        restoreCheckState(entities);
        
        mCourseAdapter.add(entities);
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);

        if (EmptyUtil.isEmpty(mCourseAdapter.getItems())) {
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }
    }

    private void restoreCheckState(List<ClassCourseEntity> entities) {
        if (entities != null && entities.size() > 0) {
            for (ClassCourseEntity entity : entities) {
                if (entity != null && entity.getId() > 0) {
                    if (RefreshUtil.getInstance().contains(entity.getId())) {
                        entity.setChecked(true);
                    }
                }
            }
        }
    }

    /**
     * 删除学程
     *
     * @param entity 删除的学程对象
     */
    private void deleteCourseFromClass(@NonNull ClassCourseEntity entity) {
        final String classId = mClassId;
        String ids = Integer.toString(entity.getId());
        String token = UserHelper.getUserId();
        this.showLoading();
        mPresenter.requestDeleteCourseFromHistoryClass(token, classId, ids);
    }

    @Override
    public void updateDeleteCourseFromClassView(Boolean aBoolean) {
        this.hideLoading();
        // 刷新UI
        // 刷新标签和课程
        RefreshUtil.getInstance().clear();
        mPresenter.requestHistoryClassConfigData(mClassId);
    }

    @Override
    public void updateHistoryCourseFromClassView(Boolean aBoolean) {
        this.hideLoading();
        // 刷新UI
        // 刷新标签和课程
        mPresenter.requestHistoryClassConfigData(mClassId);
    }

    @Override
    public void updateRemoveCourseFromClassView(Boolean aBoolean) {
        this.hideLoading();
        // 刷新UI
        // 刷新标签和课程
        RefreshUtil.getInstance().clear();
        mPresenter.requestHistoryClassConfigData(mClassId);
    }

    @Override
    public void triggerUpdate() {
        isUpdate = true;
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_filter) {
            // 搜索 兼容其它平板问题，收起软件盘
            KeyboardUtil.hideSoftInput(this);
            requestClassCourse(false);
        } else if (viewId == R.id.iv_search_clear) {
            // 删除关键字
            mSearchContent.getText().clear();
        } else if (viewId == R.id.et_search) {
            // 点击搜索框
        } else if (viewId == R.id.btn_add_course) {
            if (v.isActivated()) {
                // 确认
                List<ClassCourseEntity> items = mCourseAdapter.getItems();
                ArrayList<ClassCourseEntity> entities = new ArrayList<>();
                for (ClassCourseEntity item : items) {
                    if (item.isChecked()) {
                        item.setChecked(false);
                        entities.add(item);
                    }
                }

                if (EmptyUtil.isEmpty(entities)) {
                    // 提示选择要移除的历史学程
                    UIUtil.showToastSafe(R.string.label_please_choice_remove_history_course);
                    return;
                }

                showLoading();
                mPresenter.requestRemoveHistoryCourseFromClass(mSchoolId, mClassId, entities);
                mCourseAdapter.setChoiceMode(false);
                updateActionStatus(false);
            } else {
                // 如果是班主任,清除Hold状态
                if (mClassCourseParams.isHeadMaster()) {
                    switchHoldState(false);
                }
                // UIUtil.showToastSafe(R.string.label_add_in);
                AddHistoryCourseActivity.show(this, mClassCourseParams, ADD_HISTORY_REQUEST_CODE);
            }
        } else if (viewId == R.id.btn_remove_course) {
            if (v.isActivated()) {
                // 取消
                mCourseAdapter.setChoiceMode(false);
                // 并且将所有已选中的数据置空
                List<ClassCourseEntity> items = mCourseAdapter.getItems();
                for (ClassCourseEntity item : items) {
                    item.setChecked(false);
                }
                mCourseAdapter.notifyDataSetChanged();
                updateActionStatus(false);
            } else {
                // 触发选择
                mCourseAdapter.setChoiceMode(true);
                mCourseAdapter.notifyDataSetChanged();
                updateActionStatus(true);

                // 如果是班主任,清除Hold状态
                if (mClassCourseParams.isHeadMaster()) {
                    switchHoldState(false);
                }
            }
        }
    }

    private void updateActionStatus(boolean checking) {
        if (checking) {
            mBtnAdd.setActivated(true);
            mBtnRemove.setActivated(true);
            mBtnRemove.setText(R.string.label_cancel);
            mBtnAdd.setText(R.string.label_confirm_authorization);
        } else {
            mBtnAdd.setActivated(false);
            mBtnRemove.setActivated(false);
            mBtnRemove.setText(R.string.label_remove_out);
            mBtnAdd.setText(R.string.label_add_in);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SEARCH_REQUEST_CODE) {
                // 更新字符串发生更新
                // 设置Top隐藏
                // mTopBar.findViewById(R.id.right_function1_image).setVisibility(View.GONE);
                mKeyWord = data.getStringExtra(SearchActivity.KEY_EXTRA_SEARCH_KEYWORD);
                // 刷新数据
                triggerUpdateData();
            } else if (requestCode == ADD_HISTORY_REQUEST_CODE) {
                List<ClassCourseEntity> entities = (List<ClassCourseEntity>) data.getSerializableExtra(AddHistoryCourseActivity.KEY_EXTRA_CHOICE_ENTITIES);
                if (EmptyUtil.isNotEmpty(entities)) {
                    showLoading();
                    mPresenter.requestAddHistoryCourseFromClass(mSchoolId, mClassId, entities);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mClassCourseParams.isHeadMaster() && holdState == true) {
            switchHoldState(false);
        } else {
            if (isUpdate) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean(KEY_EXTRA_TRIGGER, isUpdate);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
            }
            RefreshUtil.getInstance().clear();
            finish();
        }
    }

    /**
     * 班级历史学程页面的入口
     *
     * @param activity     上下文对象
     * @param params       核心参数
     * @param isAuthorized 是否授权
     */
    public static void show(@NonNull Activity activity,
                            @NonNull ClassCourseParams params,
                            boolean isAuthorized,
                            int requestCode) {
        Intent intent = new Intent(activity, HistoryClassCourseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT, params);
        bundle.putBoolean(KEY_EXTRA_AUTHORIZATION, isAuthorized);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }
}
