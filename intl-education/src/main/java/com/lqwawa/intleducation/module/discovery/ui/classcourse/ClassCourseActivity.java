package com.lqwawa.intleducation.module.discovery.ui.classcourse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.ActivityUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.course.ClassCourseEntity;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.response.CheckPermissionResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.CheckSchoolPermissionEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.ImputAuthorizationCodeDialog;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.MinorityLanguageHolder;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.organcourse.filtrate.OrganCourseFiltrateActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.watchcourse.WatchCourseResourceActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import static com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment.RESULT_LIST;

/**
 * @author mrmedici
 * @desc 班级学程页面
 */
public class ClassCourseActivity extends PresenterActivity<ClassCourseContract.Presenter>
    implements ClassCourseContract.View,View.OnClickListener{

    private static final int SEARCH_REQUEST_CODE = 1 << 0;

    private static final String KEY_EXTRA_RESOURCE_FLAG = "KEY_EXTRA_RESOURCE_FLAG";
    private static final String KEY_EXTRA_RESOURCE_DATA = "KEY_EXTRA_RESOURCE_DATA";
    // 小语种课程
    private static final int MINORITY_LANGUAGE_COURSE_ID = 2004;
    // 英语国际课程
    private static final int ENGLISH_INTERNATIONAL_COURSE_ID = 2001;
    // 特色课程
    private static final int CHARACTERISTIC_COURSE_ID = 2005;
    // 基础课程
    private static final int COUNTRY_COURSE_ID = 2003;

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
    private LinearLayout mTabVector1,mTabVector2,mTabVector3;
    private TextView mTabLabel1,mTabLabel2,mTabLabel3;
    private TabLayout mTabLayout1,mTabLayout2,mTabLayout3;

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private ClassCourseAdapter mCourseAdapter;
    private CourseEmptyView mEmptyLayout;
    private TextView mTvAction;

    private ClassCourseParams mClassCourseParams;
    private boolean mResourceFlag;
    private ClassResourceData mResourceData;
    private String mSchoolId;
    private String mClassId;
    private String mRoles;

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

    private boolean isAuthorized;
    // 授权码是否过期
    private boolean isExist;

    // 搜索关键词
    private String mKeyWord;

    private ImputAuthorizationCodeDialog imputAuthorizationCodeDialog;

    private static HashMap<String, String> authorizationErrorMapZh =
            new HashMap<>();
    private static HashMap<String, String> authorizationErrorMapEn =
            new HashMap<>();

    static{
        authorizationErrorMapZh.put("1001", "授权码错误，请重新输入");
        authorizationErrorMapZh.put("1002", "授权码已过期，请重新输入");
        authorizationErrorMapZh.put("1003", "授权码尚未生效，请重新输入");
        authorizationErrorMapZh.put("1004", "授权码已被使用，请重新输入");
        authorizationErrorMapEn.put("1001", "Incorrect authorization code, please re-enter");
        authorizationErrorMapEn.put("1002", "Authorization code expired，please re-enter");
        authorizationErrorMapEn.put("1003", "Invalid authorization code, please re-enter");
        authorizationErrorMapEn.put("1004", "Authorization code has been used, please re-enter");
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_class_course;
    }

    @Override
    protected ClassCourseContract.Presenter initPresenter() {
        return new ClassCoursePresenter(this);
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mClassCourseParams = (ClassCourseParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        mResourceFlag = bundle.getBoolean(KEY_EXTRA_RESOURCE_FLAG);
        if(mResourceFlag && bundle.containsKey(KEY_EXTRA_RESOURCE_DATA)){
            mResourceData = (ClassResourceData) bundle.getSerializable(KEY_EXTRA_RESOURCE_DATA);
        }
        if(EmptyUtil.isEmpty(mClassCourseParams)) return false;
        if(mResourceFlag && EmptyUtil.isEmpty(mResourceData)) return false;
        mSchoolId = mClassCourseParams.getSchoolId();
        mClassId = mClassCourseParams.getClassId();
        mRoles = mClassCourseParams.getRoles();
        if(EmptyUtil.isEmpty(mSchoolId) ||
                EmptyUtil.isEmpty(mClassId) ||
                EmptyUtil.isEmpty(mRoles)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTvAction = (TextView) findViewById(R.id.tv_action);

        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_class_course);
        mTopBar.findViewById(R.id.left_function1_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回
                if(!mResourceFlag){
                    if(mClassCourseParams.isHeadMaster() && holdState == true){
                        switchHoldState(false);
                    }else{
                        finish();
                    }
                }else{
                    finish();
                }
            }
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
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    // 搜索，收起软件盘
                    KeyboardUtil.hideSoftInput(ClassCourseActivity.this);
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

        if(mResourceFlag){
            mHeaderLayout.setVisibility(View.GONE);
            mTopBar.findViewById(R.id.right_function1_image).setVisibility(View.GONE);
        }


        boolean isTeacher = UserHelper.isTeacher(mRoles);
        if(!mResourceFlag && isTeacher){
            // 只有老师才显示添加学程
            mTvAction.setText(R.string.label_add_course_lines);
            mTvAction.setOnClickListener(this);
            mTvAction.setVisibility(View.VISIBLE);
            /*mTopBar.setRightFunctionText1(R.string.label_add_course,view->{
                switchHoldState(false);
                addCourseToClass();
            });*/
        }

        if(UserHelper.isStudent(mRoles)){
            // 学程显示获取授权
            mTvAction.setText(R.string.label_request_authorization_lines);
            mTvAction.setOnClickListener(this);
            mTvAction.setVisibility(View.VISIBLE);
            /*mTopBar.setRightFunctionText1(R.string.label_request_authorization, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击获取授权
                    if(isAuthorized){
                        // 已经获取到授权
                        UIUtil.showToastSafe(R.string.label_request_authorization_succeed);
                        return;
                    }
                    requestAuthorizedPermission(isExist);
                }
            });*/
        }

        mTopBar.setRightFunctionImage1(R.drawable.search,view->{
            // 搜索
            SearchActivity.show(
                    ClassCourseActivity.this,
                    HideSortType.TYPE_SORT_CLASS_COURSE,
                    UIUtil.getString(R.string.title_class_course),
                    SEARCH_REQUEST_CODE);
        });

        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mEmptyLayout = (CourseEmptyView) findViewById(R.id.empty_layout);

        mCourseAdapter = new ClassCourseAdapter(mClassCourseParams.isHeadMaster(),mRoles);
        mRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this,3){
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

                if(mResourceFlag) {
                    if (!isAuthorized) {
                        UIUtil.showToastSafe(R.string.label_please_request_authorization);
                        return;
                    }

                    String courseId = entity.getCourseId();
                    // 进入选择资源的Activity
                    WatchCourseResourceActivity.show(
                            ClassCourseActivity.this,
                            courseId,
                            mResourceData.getTaskType(),
                            mResourceData.getMultipleChoiceCount(),
                            mResourceData.getFilterArray(),
                            0);
                }else{
                    // 班级学程的详情入口
                    String courseId = entity.getCourseId();
                    // 班级学程进入参数
                    boolean isTeacher = UserHelper.isTeacher(mRoles);
                    boolean isResult = isTeacher || mClassCourseParams.isHeadMaster();
                    boolean isParent = UserHelper.isParent(mRoles);

                    CourseDetailParams params = new CourseDetailParams(mSchoolId,mClassId,isAuthorized);
                    params.setClassTeacher(isResult);
                    // 优先老师处理
                    params.setClassParent(!isResult && isParent);

                    // CourseDetailsActivity.start(ClassCourseActivity.this , courseId, true, UserHelper.getUserId(),params);

                    CourseDetailsActivity.start(isAuthorized,params,false,ClassCourseActivity.this,courseId, true, UserHelper.getUserId());
                    // 如果是班主任,清除Hold状态
                    if(mClassCourseParams.isHeadMaster()){
                        switchHoldState(false);
                    }
                }
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder holder, ClassCourseEntity classCourseEntity) {
                super.onItemLongClick(holder, classCourseEntity);
                if(!mResourceFlag && mClassCourseParams.isHeadMaster()){
                    switchHoldState(true,classCourseEntity);
                }
            }
        });

        // 添加cell的删除事件
        mCourseAdapter.setNavigator(position -> {
            ClassCourseEntity entity = mCourseAdapter.getItems().get(position);
            deleteCourseFromClass(entity);
        });

        // 下拉刷新与加载更多
        mRefreshLayout.setOnHeaderRefreshListener(view->requestClassCourse(false));
        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnFooterRefreshListener(view->requestClassCourse(true));
    }

    @Override
    protected void initData() {
        super.initData();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        // 检查授权
        if(mResourceFlag){
            // 如果是学习任务的选择,默认检查授权
            mPresenter.requestCheckSchoolPermission(mSchoolId,0,true);
        }else{
            mPresenter.requestCheckSchoolPermission(mSchoolId,0,false);
        }

        // 获取标签
        mPresenter.requestClassConfigData(mClassId);
        // requestClassCourse(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // requestClassCourse(false);
        // 刷新标签和课程
        // mPresenter.requestClassConfigData(mClassId);
    }

    /**
     * 更换Hold状态
     * @param state 期望的状态
     * @return 返回更改好的状态
     */
    private boolean switchHoldState(boolean state){
        return switchHoldState(state,null);
    }

    /**
     * 更换Hold状态
     * @param state 期望的状态
     * @param hold state 为true ,hold不能为空
     * @return 返回更改好的状态
     */
    private boolean switchHoldState(boolean state,ClassCourseEntity hold){
        holdState = state;
        // 班主任Hold
        // 更改所有Hold的状态
        List<ClassCourseEntity> items = mCourseAdapter.getItems();
        if(EmptyUtil.isNotEmpty(items)){
            for (ClassCourseEntity entity:items) {
                if(state && entity.getId() == hold.getId()){
                    // 同一个课程
                    entity.setHold(state);
                    break;
                }

                if(!state){
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
    private void triggerUpdateData(){
        if (EmptyUtil.isNotEmpty(mConfigEntities)){
            clearConfigArrayStatus(mConfigEntities);
        }
        requestClassCourse(false);
    }

    /**
     * 获取班级学程数据
     * @param isMoreData 是否加载更多
     */
    private void requestClassCourse(boolean isMoreData){
        if(!isMoreData){
            pageIndex = 0;
        }else{
            pageIndex++;
        }

        mRefreshLayout.showRefresh();
        // String name = mSearchContent.getText().toString().trim();
        String name = mKeyWord;
        if(EmptyUtil.isEmpty(name)) name = "";
        int role = 1; // 默认学生
        // 如果是老师身份 role传0
        if(UserHelper.isTeacher(mRoles)) role = 0;

        // 准备Level,先获取到第一级别的Level
        String level = "";
        int paramOneId = 0;
        int paramTwoId = 0;

        if(mFiltrateArray1 != null &&
                mFiltrateArray2 != null &&
                mFiltrateArray3 != null){

            int rootId = 0;
            for (Tab tab:mFiltrateArray1) {
                if(tab.isChecked()){
                    level = tab.getLevel();

                    // 找到选中的第一分类
                    rootId = tab.getId();
                    break;
                }
            }

            // 查看TabLayout是全部选中,还是正确Tab选中
            int rootTypeId = 0;
            for (Tab tab:mFiltrateArray2) {
                if(!tab.isAll() && tab.isChecked()){
                    // 选择不是全部的Level
                    level = tab.getLevel();
                    rootTypeId = tab.getId();
                    break;
                }
            }

            if(rootId != MINORITY_LANGUAGE_COURSE_ID){
                // 不是小语种课程
                for (Tab tab:mFiltrateArray3) {
                    if(!tab.isAll() && tab.isChecked()){
                        if(rootId == CHARACTERISTIC_COURSE_ID || rootId == COUNTRY_COURSE_ID){
                            // 特色课程或者国家课程
                            paramTwoId = tab.getLabelId();
                        }else if(rootId == ENGLISH_INTERNATIONAL_COURSE_ID && rootTypeId == ENGLISH_INTERNATIONAL_ENGLISH_PRIMARY_ID){
                            // 英语国际课程 LQ English PRIMARY
                            paramTwoId = tab.getLabelId();
                        }else{
                            paramOneId = tab.getLabelId();
                        }
                    }
                }
            }
        }


        if(mResourceFlag){
            mPresenter.requestStudyTaskClassCourseData(mClassId,name,pageIndex);
        }else{
            mPresenter.requestClassCourseData(mClassId,role,name,level,paramOneId,paramTwoId,pageIndex);
        }
    }

    @Override
    public void updateClassCourseView(List<ClassCourseEntity> entities) {
        mCourseAdapter.replace(entities);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);

        if(EmptyUtil.isEmpty(entities)){
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateMoreClassCourseView(List<ClassCourseEntity> entities) {
        mCourseAdapter.add(entities);
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);

        if(EmptyUtil.isEmpty(mCourseAdapter.getItems())){
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateStudyTaskClassCourseView(List<ClassCourseEntity> entities) {
        updateClassCourseView(entities);
    }

    @Override
    public void updateMoreStudyTaskClassCourseView(List<ClassCourseEntity> entities) {
        updateMoreClassCourseView(entities);
    }

    @Override
    public void updateClassConfigView(@NonNull List<LQCourseConfigEntity> entities) {
        this.mConfigEntities = entities;
        // 组装Label数据
        // 默认第一个选中
        if(EmptyUtil.isNotEmpty(entities)){
            mFiltrateArray1 = new ArrayList<>();
            mFiltrateArray2 = new ArrayList<>();
            mFiltrateArray3 = new ArrayList<>();

            if(EmptyUtil.isEmpty(mConfigEntities)) return;
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
        }else{
            updateClassCourseView(null);
        }
    }

    /**
     * 组装Label数据
     */
    private void configLabel(@NonNull int rootId){
        // 是否配置全部
        mTabVector2.setVisibility(View.VISIBLE);

        if(rootId == MINORITY_LANGUAGE_COURSE_ID){
            // 小语种课程 二级页面
            mTabVector3.setVisibility(View.GONE);

            // 类型语言
            mTabLabel1.setText(getString(R.string.label_colon_type));
            mTabLabel2.setText(getString(R.string.label_colon_language));
        }else if(rootId == ENGLISH_INTERNATIONAL_COURSE_ID){
            // 英语国际课程 三级页面
            // 三级页面
            mTabVector3.setVisibility(View.VISIBLE);

            // 类型 类型 科目
            mTabLabel1.setText(getString(R.string.label_colon_type));
            mTabLabel2.setText(getString(R.string.label_colon_type));
            mTabLabel3.setText(getString(R.string.label_colon_subject));
        }else{
            // 三级页面
            mTabVector3.setVisibility(View.VISIBLE);

            // 类型 学段 科目
            mTabLabel1.setText(getString(R.string.label_colon_type));
            mTabLabel2.setText(getString(R.string.label_colon_period));
            mTabLabel3.setText(getString(R.string.label_colon_subject));


            // 特色课程 or 国家课程
            if(mFiltrateArray2.size() <= 1){
                // 标签隐藏
                mTabVector2.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 递归调用
     */
    private void recursionConfig(List<LQCourseConfigEntity> entities){
        clearArray(CONFIG_TYPE_1);
        recursionConfigArray(entities);
    }

    /**
     * 清空集合
     */
    private void clearArray(int configType){
        // 清空所有数据
        if(configType <= CONFIG_TYPE_4 || configType <= CONFIG_TYPE_3){
            // 清除第三个
            mFiltrateArray3.clear();
        }

        if(configType <= CONFIG_TYPE_2){
            // 清除第二个
            mFiltrateArray2.clear();
        }

        if(configType <= CONFIG_TYPE_1){
            // 清除第一个
            mFiltrateArray1.clear();
        }
    }

    /**
     * 清空默认设置科目的选择状态
     * @param array 标签数据
     */
    private void clearConfigArrayStatus(@NonNull List<LQCourseConfigEntity> array){
        if(EmptyUtil.isEmpty(array)) return;

        for (LQCourseConfigEntity entity:array) {
            entity.setSelected(false);

            // 递归调用
            List<LQCourseConfigEntity> childList = entity.getChildList();
            clearConfigArrayStatus(childList);
        }
    }

    /**
     * 递归调用
     */
    private void recursionConfigArray(@NonNull List<LQCourseConfigEntity> array){
        if(EmptyUtil.isEmpty(array)) return;

        for (LQCourseConfigEntity entity:array) {
            if(entity.getConfigType() == CONFIG_TYPE_1){
                Tab tab = Tab.build(entity);
                if(!mFiltrateArray1.contains(tab)){
                    mFiltrateArray1.add(Tab.build(entity));
                }
                // 第一个筛选容器,加全部
                /*Tab allTab1 = Tab.buildAll(mAllText,array);
                if(!mFiltrateArray1.contains(allTab1)){
                    mFiltrateArray1.add(0,allTab1);
                }*/
            }

            if(entity.getConfigType() == CONFIG_TYPE_2){
                Tab tab = Tab.build(entity);
                if(!mFiltrateArray2.contains(tab)){
                    mFiltrateArray2.add(Tab.build(entity));
                }
                // 第二个筛选容器,加全部
                /*Tab allTab2 = Tab.buildAll(mAllText,null);
                if(!mFiltrateArray2.contains(allTab2)){
                    mFiltrateArray2.add(0,allTab2);
                }*/
            }

            if(entity.getConfigType() == CONFIG_TYPE_3 || entity.getConfigType() == CONFIG_TYPE_4){
                Tab tab = Tab.build(entity);
                if(!mFiltrateArray3.contains(tab)){
                    mFiltrateArray3.add(Tab.build(entity));
                }
                // 第三个筛选容器,加全部
                Tab allTab3 = Tab.buildAll(mAllText,null);
                if(!mFiltrateArray3.contains(allTab3)){
                    mFiltrateArray3.add(0,allTab3);
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
    private void initTabControl(){
        initTabControl1();
        // initTabControl2();
        // initTabControl3();
    }

    private void initTabControl1(){
        mTabLayout1.removeAllTabs();
        // 查看是否有Selected的
        boolean haveSelected = false;
        for (Tab tab:mFiltrateArray1) {
            if(tab.isSelected()){
                haveSelected = true;
                break;
            }
        }

        boolean setSelected = false;
        for (Tab tab:mFiltrateArray1) {
            View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
            TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
            tvContent.setText(tab.getConfigValue());
            // 将tab数据作为Tag设置到TabLayout的TabLayout.Tab上
            TabLayout.Tab newTab = mTabLayout1.newTab().setCustomView(tabView).setTag(tab);

            if(!setSelected){
                setSelected = (mTabLayout1.getTabCount() == 0 && !haveSelected) || tab.isSelected();
                mTabLayout1.addTab(newTab,setSelected);
            }else{
                // 已经添加过已经选择的Tab
                mTabLayout1.addTab(newTab);
            }
        }


        mTabLayout1.smoothScrollTo(0,0);
    }

    private void initTabControl2(){
        mTabLayout2.removeAllTabs();

        // 查看是否有Selected的
        boolean haveSelected = false;
        for (Tab tab:mFiltrateArray2) {
            if(tab.isSelected()){
                haveSelected = true;
                break;
            }
        }

        boolean setSelected = false;
        for (Tab tab:mFiltrateArray2) {
            View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
            TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
            tvContent.setText(tab.getConfigValue());
            TabLayout.Tab newTab = mTabLayout2.newTab().setCustomView(tabView).setTag(tab);

            if(!setSelected){
                setSelected = (mTabLayout2.getTabCount() == 0 && !haveSelected) || tab.isSelected();
                mTabLayout2.addTab(newTab,setSelected);
            }else{
                // 已经添加过已经选择的Tab
                mTabLayout2.addTab(newTab);
            }
        }

        mTabLayout2.smoothScrollTo(0,0);
    }

    private void initTabControl3(){
        mTabLayout3.removeAllTabs();

        // 查看是否有Selected的
        boolean haveSelected = false;
        for (Tab tab:mFiltrateArray3) {
            if(tab.isSelected()){
                haveSelected = true;
                break;
            }
        }

        boolean setSelected = false;
        if(EmptyUtil.isNotEmpty(mFiltrateArray3)){
            for (Tab tab:mFiltrateArray3) {
                View tabView = UIUtil.inflate(R.layout.item_tab_control_layout);
                TextView tvContent = (TextView) tabView.findViewById(R.id.tv_content);
                tvContent.setText(tab.getConfigValue());
                TabLayout.Tab newTab = mTabLayout3.newTab().setCustomView(tabView).setTag(tab);

                if(!setSelected){
                    setSelected = (mTabLayout3.getTabCount() == 0 && !haveSelected) || tab.isSelected();
                    mTabLayout3.addTab(newTab,setSelected);
                }else{
                    // 已经添加过已经选择的Tab
                    mTabLayout3.addTab(newTab);
                }
            }
        }

        mTabLayout3.smoothScrollTo(0,0);
    }

    /**
     * 设置相关联动的监听
     */
    private void initTabListener(){
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
    }

    /**
     * 设置该Tab选中
     * @param array 对应的Tab集合
     * @param tab 选择的Tab
     */
    private void setTabItemSelected(@NonNull List<Tab> array, @NonNull Tab tab){
        if(EmptyUtil.isEmpty(array) || EmptyUtil.isEmpty(tab)) return;
        for (Tab item:array) {
            item.setChecked(false);
            if(item.equals(tab)){
                item.setChecked(true);
            }
        }
    }

    @Override
    public void updateCheckPermissionView(@NonNull CheckSchoolPermissionEntity entity, boolean autoRequest) {
        if(EmptyUtil.isNotEmpty(entity)){
            if(entity.isAuthorized()){
                // 已经获取授权,并且没有失效
                isAuthorized = true;
                // 授权码过期
                isExist = entity.isExist();
                // UIUtil.showToastSafe(R.string.label_old_request_authorization);
            }else{
                if(autoRequest){
                    // 点击获取授权
                    /*if(entity.isExist()){
                        // 授权过期的状态
                        UIUtil.showToastSafe(R.string.authorization_out_time_tip);
                    }*/
                    requestAuthorizedPermission(entity.isExist());
                }
            }
        }
    }

    /**
     * 申请授权
     */
    private void requestAuthorizedPermission(boolean isExist){

        String tipInfo = UIUtil.getString(R.string.label_request_authorization_tip);
        if(isExist){
            tipInfo = UIUtil.getString(R.string.authorization_out_time_tip);
        }
        if(imputAuthorizationCodeDialog == null) {
            imputAuthorizationCodeDialog = new ImputAuthorizationCodeDialog(this, tipInfo,
                    new ImputAuthorizationCodeDialog.CommitCallBack() {
                        @Override
                        public void onCommit(String code) {
                            commitAuthorizationCode(code);
                        }

                        @Override
                        public void onCancel() {
                            if(EmptyUtil.isNotEmpty(imputAuthorizationCodeDialog)){
                                imputAuthorizationCodeDialog.dismiss();
                            }
                        }
                    });
        }
        imputAuthorizationCodeDialog.setTipInfo(tipInfo);
        if(!imputAuthorizationCodeDialog.isShowing()) {
            imputAuthorizationCodeDialog.show();
        }
    }

    /**
     * @desc 申请授权
     * @author medici
     * @param code 授权码
     */
    private void commitAuthorizationCode(@NonNull String code){
        mPresenter.requestSaveAuthorization(mSchoolId,0,code);
    }

    @Override
    public void updateRequestPermissionView(@NonNull CheckPermissionResponseVo<Void> responseVo) {
        if(EmptyUtil.isEmpty(responseVo)) return;
        if(responseVo.isSucceed()){
            isAuthorized = true;
            isExist = false;
            if(imputAuthorizationCodeDialog != null){
                imputAuthorizationCodeDialog.setCommited(true);
                imputAuthorizationCodeDialog.dismiss();
            }
        }else{
            String language = Locale.getDefault().getLanguage();
            //提示授权码错误原因然后退出
            UIUtil.showToastSafe(language.equals("zh") ? authorizationErrorMapZh.get("" + responseVo.getCode()): authorizationErrorMapEn.get("" + responseVo.getCode()));

            if(imputAuthorizationCodeDialog != null){
                imputAuthorizationCodeDialog.clearPassword();
            }
        }
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
        if(viewId == R.id.tv_filter){
            // 搜索 兼容其它平板问题，收起软件盘
            KeyboardUtil.hideSoftInput(this);
            requestClassCourse(false);
        }else if(viewId == R.id.iv_search_clear){
            // 删除关键字
            mSearchContent.getText().clear();
        }else if(viewId == R.id.et_search){
            // 点击搜索框
        }else if(viewId == R.id.tv_action){
            boolean isTeacher = UserHelper.isTeacher(mRoles);
            if(!mResourceFlag && isTeacher){
                // 只有老师才显示添加学程
                switchHoldState(false);
                addCourseToClass();
            }

            if(UserHelper.isStudent(mRoles)){
                // 点击获取授权
                if(isAuthorized){
                    // 已经获取到授权
                    UIUtil.showToastSafe(R.string.label_request_authorization_succeed);
                    return;
                }
                requestAuthorizedPermission(isExist);
            }
        }
    }

    /**
     * 添加学程
     */
    private void addCourseToClass(){
        // 进入选择课程页面
        CourseShopClassifyParams params = new CourseShopClassifyParams(mSchoolId);
        CourseShopClassifyActivity.show(this,params);
    }

    @Override
    public void updateAddCourseFromClassView(Boolean aBoolean) {
        // 关闭Dialog
        this.hideLoading();
        // 提示添加成功
        UIUtil.showToastSafe(R.string.label_add_succeed);
        // 刷新UI
        // requestClassCourse(false);

        // 刷新标签和课程
        mPresenter.requestClassConfigData(mClassId);
    }

    /**
     * 删除学程
     * @param entity 删除的学程对象
     */
    private void deleteCourseFromClass(@NonNull ClassCourseEntity entity){
        final String classId = mClassId;
        String ids = Integer.toString(entity.getId());
        String token = UserHelper.getUserId();
        this.showLoading();
        mPresenter.requestDeleteCourseFromClass(token,classId,ids);
    }

    @Override
    public void updateDeleteCourseFromClassView(Boolean aBoolean) {
        this.hideLoading();
        // 刷新UI
        // 刷新标签和课程
        mPresenter.requestClassConfigData(mClassId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event){
        if(EventWrapper.isMatch(event, EventConstant.CLASS_COURSE_ADD_COURSE_EVENT)){
            // 销毁其它页面
            ActivityUtil.finishToActivity(this,false);
            // 获取到选取的课程
            List<CourseVo> selectArray = (List<CourseVo>) event.getData();
            // showLoading
            this.showLoading();
            String courseIds = "";
            ListIterator<CourseVo> listIterator = selectArray.listIterator();
            while (listIterator.hasNext()){
                courseIds += listIterator.next().getId();
                if(listIterator.hasNext()){
                    courseIds += ",";
                }
            }

            mPresenter.requestAddCourseFromClass(mSchoolId,mClassId,courseIds);
        }else if(EventWrapper.isMatch(event, EventConstant.COURSE_SELECT_RESOURCE_EVENT)){
            ArrayList<SectionResListVo> vos = (ArrayList<SectionResListVo>) event.getData();
            setResult(Activity.RESULT_OK,new Intent().putExtra(RESULT_LIST, vos));
            // 杀掉所有可能的UI
            // ActivityUtil.finishActivity(OrganCourseFiltrateActivity.class);
            // ActivityUtil.finishActivity(SearchActivity.class);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SEARCH_REQUEST_CODE){
                // 更新字符串发生更新
                // 设置Top隐藏
                mTopBar.findViewById(R.id.right_function1_image).setVisibility(View.GONE);
                mKeyWord = data.getStringExtra(SearchActivity.KEY_EXTRA_SEARCH_KEYWORD);
                // 刷新数据
                triggerUpdateData();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // 返回
        if(mClassCourseParams.isHeadMaster() && holdState == true){
            switchHoldState(false);
        }else{
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 班级学程页面的入口
     * @param context 上下文对象
     * @param params 核心参数
     */
    public static void show(@NonNull Context context,
                            @NonNull ClassCourseParams params){
        Intent intent = new Intent(context,ClassCourseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 班级学程页面的入口, 选择学习任务的入口
     * @param activity 上下文对象
     * @param params 核心参数
     * @param data 选择学习任务的筛选
     * <p>onActivityResult回调选择数据,resultCode = {@link Activity.RESULT_OK}</p>
     * <p>data 为List<SectionResListVo> Key = {@link CourseSelectItemFragment.RESULT_LIST}</p>
     */
    public static void show(@NonNull Activity activity,
                            @NonNull ClassCourseParams params,
                            @NonNull ClassResourceData data){
        Intent intent = new Intent(activity,ClassCourseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        bundle.putBoolean(KEY_EXTRA_RESOURCE_FLAG,true);
        bundle.putSerializable(KEY_EXTRA_RESOURCE_DATA,data);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent,data.getRequestCode());
    }
}
