package com.lqwawa.intleducation.module.discovery.ui.classcourse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.ActivityUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.course.ClassCourseEntity;
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

    private static final String KEY_EXTRA_RESOURCE_FLAG = "KEY_EXTRA_RESOURCE_FLAG";
    private static final String KEY_EXTRA_RESOURCE_DATA = "KEY_EXTRA_RESOURCE_DATA";

    private TopBar mTopBar;

    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private ClassCourseAdapter mCourseAdapter;
    private CourseEmptyView mEmptyLayout;

    private ClassCourseParams mClassCourseParams;
    private boolean mResourceFlag;
    private ClassResourceData mResourceData;
    private String mSchoolId;
    private String mClassId;
    private String mRoles;

    private int pageIndex;
    // 是否是Hold状态
    private boolean holdState;

    private boolean isAuthorized;
    // 授权码是否过期
    private boolean isExist;

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





        boolean isTeacher = UserHelper.isTeacher(mRoles);
        if(!mResourceFlag && isTeacher){
            // 只有老师才显示添加学程
            mTopBar.setRightFunctionText1(R.string.label_add_course,view->{
                switchHoldState(false);
                addCourseToClass();
            });
        }

        if(UserHelper.isStudent(mRoles)){
            // 学程显示获取授权
            mTopBar.setRightFunctionText1(R.string.label_request_authorization, new View.OnClickListener() {
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
            });
        }

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
        requestClassCourse(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        requestClassCourse(false);
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
        String token = UserHelper.getUserId();
        String name = mSearchContent.getText().toString().trim();
        int role = 1; // 默认学生
        // 如果是老师身份 role传0
        if(UserHelper.isTeacher(mRoles)) role = 0;
        mPresenter.requestClassCourseData(token,mClassId,role,name,pageIndex, AppConfig.PAGE_SIZE);
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
        requestClassCourse(false);
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
        // 重新拉取课程
        requestClassCourse(false);
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
