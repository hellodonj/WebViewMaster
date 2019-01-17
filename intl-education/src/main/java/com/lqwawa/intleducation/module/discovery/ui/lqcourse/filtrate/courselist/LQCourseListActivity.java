package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.courselist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.Date;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 显示课程列表的Activity
 * @date 2018/05/03 13:37
 * @history v1.0
 * **********************************
 */
public class LQCourseListActivity extends PresenterActivity<LQCourseListContract.Presenter>
    implements LQCourseListContract.View, View.OnClickListener{

    private static final String KEY_EXTRA_TITLE_TEXT = "KEY_EXTRA_TITLE_TEXT";

    private static final String KEY_EXTRA_SORT_TYPE = "KEY_EXTRA_SORT_TYPE";

    private static final String KEY_EXTRA_SEARCH_KEY = "KEY_EXTRA_SEARCH_KEY";

    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";

    private static final String KEY_EXTRA_IS_SCHOOL_ENTER = "KEY_EXTRA_IS_SCHOOL_ENTER";
    // 是不是从在线课堂班级进入的
    private static final String KEY_EXTRA_IS_ONLINE_CLASS_ENTER = "KEY_EXTRA_IS_ONLINE_CLASS_ENTER";

    // 搜索的关键词Key
    private String mSearchKey;
    // 是否是搜索过滤
    private boolean isSearchFilter;

    // 头部布局
    private TopBar mTopBar;
    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;
    // 下拉刷新布局
    private PullToRefreshView mRefreshLayout;
    // 空布局
    private CourseEmptyView mEmptyView;
    // 列表布局
    private ListView mListView;
    private CourseListAdapter courseListAdapter;
    // 当前页
    private int currentPage;

    private String mTitle;
    private String mSortType;
    private String mSchoolId;
    private boolean isSchoolEnter;
    private boolean isOnlineClassEnter;

    @Override
    protected LQCourseListContract.Presenter initPresenter() {
        return new LQCourseListPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_lq_course_list;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mTitle = (String) bundle.get(KEY_EXTRA_TITLE_TEXT);
        mSortType = (String) bundle.get(KEY_EXTRA_SORT_TYPE);
        mSearchKey = (String) bundle.get(KEY_EXTRA_SEARCH_KEY);
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        isSchoolEnter = bundle.getBoolean(KEY_EXTRA_IS_SCHOOL_ENTER);
        isOnlineClassEnter = bundle.getBoolean(KEY_EXTRA_IS_ONLINE_CLASS_ENTER);
        if(!EmptyUtil.isEmpty(mSearchKey)){
            // 搜索页面过来的
            isSearchFilter = true;
        }
        if(EmptyUtil.isEmpty(mTitle) || EmptyUtil.isEmpty(mSortType)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(mTitle);

        // 添加搜索 之前在线课堂不加搜索
        // if(!isSearchFilter && !HideSortType.TYPE_SORT_ONLINE_COURSE.equals(mSortType)){
        if(!isSearchFilter && false){
            mTopBar.setRightFunctionImage1(R.drawable.search, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(HideSortType.TYPE_SORT_ONLINE_COURSE.equals(mSortType)){
                        // 在线课堂类型
                        SearchActivity.show(LQCourseListActivity.this,mSortType,mTitle,mSchoolId,isSchoolEnter);
                    }else{
                        SearchActivity.show(LQCourseListActivity.this,mSortType,mTitle,isSchoolEnter);
                    }
                }
            });
        }

        mSearchContent = (EditText) findViewById(R.id.et_search);
        mSearchClear = (ImageView) findViewById(R.id.iv_search_clear);
        mSearchFilter = (TextView) findViewById(R.id.tv_filter);

        mSearchContent.setHint(R.string.search_hit);

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
                    KeyboardUtil.hideSoftInput(LQCourseListActivity.this);
                    requestCourseData(false);
                }
                return true;
            }
        });

        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mListView = (ListView) findViewById(R.id.listView);
        mEmptyView = (CourseEmptyView) findViewById(R.id.empty_layout);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) courseListAdapter.getItem(position);
                CourseDetailsActivity.start(LQCourseListActivity.this,isSchoolEnter,isOnlineClassEnter, vo.getId(), true, UserHelper.getUserId());
            }
        });

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                requestCourseData(false);
            }
        });

        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                requestCourseData(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        // 启动下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        requestCourseData(false);
    }

    /**
     * 查询课程 isMoreLoaded=true 加载更多数据
     */
    public void requestCourseData(boolean isMoreLoaded){
        int payType = Integer.MAX_VALUE;
        if(!AppConfig.BaseConfig.needShowPay()){
            //只显示免费课程
            payType = 0;
        }

        mSearchKey = mSearchContent.getText().toString().trim();

        if(HideSortType.TYPE_SORT_ONLINE_COURSE.equals(mSortType)){
            // 加载在线课堂关联课程

            /*if(isMoreLoaded){
                currentPage++;
                mPresenter.requestOnlineSchoolInfoData(currentPage,mSchoolId);
            }else{
                currentPage = 0;
                mPresenter.requestOnlineSchoolInfoData(currentPage,mSchoolId);
            }*/

            if(isMoreLoaded){
                currentPage++;
                mPresenter.requestMoreCourseData(mSchoolId,currentPage,0,mSortType,payType,mSearchKey);
            }else{
                currentPage = 0;
                mPresenter.requestCourseData(mSchoolId,currentPage,0,mSortType,payType,mSearchKey);
            }
        }else{
            if(isMoreLoaded){
                currentPage++;
                mPresenter.requestMoreCourseData(null,currentPage,0,mSortType,payType,mSearchKey);
            }else{
                currentPage = 0;
                mPresenter.requestCourseData(null,currentPage,0,mSortType,payType,mSearchKey);
            }
        }
    }

    @Override
    public void onCourseLoaded(List<CourseVo> courseVos) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        courseListAdapter = new CourseListAdapter(this);
        courseListAdapter.setData(courseVos);
        mListView.setAdapter(courseListAdapter);

        if(EmptyUtil.isEmpty(courseVos)){
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMoreCourseLoaded(List<CourseVo> courseVos) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        courseListAdapter.addData(courseVos);
        courseListAdapter.notifyDataSetChanged();
    }


    @Override
    public void updateOnlineSchoolCourseView(@NonNull List<CourseVo> courseVos) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        courseListAdapter = new CourseListAdapter(this);
        courseListAdapter.setData(courseVos);
        mListView.setAdapter(courseListAdapter);

        if(EmptyUtil.isEmpty(courseVos)){
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tv_filter){
            // 搜索 兼容其它平板问题，收起软件盘
            KeyboardUtil.hideSoftInput(this);
            requestCourseData(false);
        }else if(viewId == R.id.iv_search_clear){
            // 删除关键字
            mSearchContent.getText().clear();
            requestCourseData(false);
        }else if(viewId == R.id.et_search){
            // 点击搜索框
        }
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
    }

    /**
     * 课程列表显示入口
     * @param context 上下文对象
     * @param sort 热门列表或者其它
     * @param title 标题文本
     */
    public static void show(@NonNull Context context, @NonNull @HideSortType.SortRes String sort, @NonNull String title){
        Intent intent = new Intent(context, LQCourseListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT_TYPE, sort);
        bundle.putString(KEY_EXTRA_TITLE_TEXT, title);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 课程列表显示入口 在线课堂机构信息进来的
     * @param context 上下文对象
     * @param sort 热门列表或者其它
     * @param title 标题文本
     * @param schoolId 机构Id
     * @param isSchoolEnter 是否从在线机构主页进来的
     * @param isOnlineClassEnter 是否是在线课堂班级过来的，如果是，需要隐藏在线课堂Tab
     */
    public static void show(@NonNull Context context, @NonNull @HideSortType.SortRes String sort, @NonNull String title,@NonNull String schoolId,
                            boolean isSchoolEnter,boolean isOnlineClassEnter){
        Intent intent = new Intent(context, LQCourseListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT_TYPE, sort);
        bundle.putString(KEY_EXTRA_TITLE_TEXT, title);
        bundle.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        bundle.putBoolean(KEY_EXTRA_IS_SCHOOL_ENTER,isSchoolEnter);
        bundle.putBoolean(KEY_EXTRA_IS_ONLINE_CLASS_ENTER,isOnlineClassEnter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 课程列表显示入口
     * @param context 上下文对象
     * @param sort 热门列表或者其它
     * @param title 标题文本
     * @param key 搜索关键词
     */
    public static void showFromSearch(@NonNull Context context, @NonNull @HideSortType.SortRes String sort,
                                      @NonNull String title,@NonNull String key,boolean isOnlineSchoolEnter){
        Intent intent = new Intent(context, LQCourseListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT_TYPE, sort);
        bundle.putString(KEY_EXTRA_TITLE_TEXT, title);
        bundle.putString(KEY_EXTRA_SEARCH_KEY,key);
        bundle.putBoolean(KEY_EXTRA_IS_SCHOOL_ENTER,isOnlineSchoolEnter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 在线课堂课程列表显示入口
     * @param context 上下文对象
     * @param sort 热门列表或者其它
     * @param title 标题文本
     * @param key 搜索关键词
     * @param schoolId 机构Id
     */
    public static void showFromSearch(@NonNull Context context, @NonNull @HideSortType.SortRes String sort,
                                      @NonNull String title,@NonNull String key,@NonNull String schoolId,boolean isOnlineSchoolEnter){
        Intent intent = new Intent(context, LQCourseListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT_TYPE, sort);
        bundle.putString(KEY_EXTRA_TITLE_TEXT, title);
        bundle.putString(KEY_EXTRA_SEARCH_KEY,key);
        bundle.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        bundle.putBoolean(KEY_EXTRA_IS_SCHOOL_ENTER,isOnlineSchoolEnter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
