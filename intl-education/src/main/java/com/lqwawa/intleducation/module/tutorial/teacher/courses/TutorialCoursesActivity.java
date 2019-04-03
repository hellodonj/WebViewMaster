package com.lqwawa.intleducation.module.tutorial.teacher.courses;

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
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.record.AuditRecordActivity;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.record.AuditType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 我帮辅的课程列表页面
 */
public class TutorialCoursesActivity extends PresenterActivity<TutorialCoursesContract.Presenter>
    implements TutorialCoursesContract.View,View.OnClickListener{

    private TopBar mTopBar;
    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private PullToRefreshView mRefreshLayout;
    private ListView mListView;
    private CourseEmptyView mEmptyView;
    // Adapter
    private CourseListAdapter mAdapter;


    // 分页数
    private int currentPage;

    private TutorialCoursesParams mTutorialParams;
    private String mMemberId;
    private String mConfigValue;

    private String mSearchKey;

    @Override
    protected TutorialCoursesContract.Presenter initPresenter() {
        return new TutorialCoursesPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tutorial_courses;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        if(bundle.containsKey(ACTIVITY_BUNDLE_OBJECT)){
            mTutorialParams = (TutorialCoursesParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
            if(EmptyUtil.isNotEmpty(mTutorialParams)){
                mMemberId = mTutorialParams.getMemberId();
                mConfigValue = mTutorialParams.getConfigValue();
            }
        }

        if(EmptyUtil.isEmpty(mMemberId) ||
                EmptyUtil.isEmpty(mConfigValue)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(mConfigValue);
        mTopBar.setRightFunctionText1TextColor(UIUtil.getColor(R.color.textAccent));
        mTopBar.setRightFunctionText1(R.string.label_audit,view-> AuditRecordActivity.show(this,mMemberId));
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
                    KeyboardUtil.hideSoftInput(TutorialCoursesActivity.this);
                    requestCourseData(false);
                }
                return true;
            }
        });

        mEmptyView = (CourseEmptyView) findViewById(R.id.empty_layout);
        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mListView = (ListView) findViewById(R.id.list_view);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) mAdapter.getItem(position);
                CourseDetailsActivity.start(TutorialCoursesActivity.this, vo.getCourseId(), true, UserHelper.getUserId());
            }
        });

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
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
        requestCourseData(false);
    }

    /**
     * 请求课程
     * @param moreData 是否更多数据
     */
    private void requestCourseData(boolean moreData){
        mSearchKey = mSearchContent.getText().toString().trim();
        if(moreData){
            currentPage ++;
            mPresenter.requestTutorialCoursesData(mMemberId,mSearchKey,currentPage);
        }else{
            currentPage = 0;
            mPresenter.requestTutorialCoursesData(mMemberId,mSearchKey,currentPage);
        }
    }

    @Override
    public void updateTutorialCoursesView(List<CourseVo> courseVos) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        mAdapter = new CourseListAdapter(true,AuditType.AUDITED_PASS,this);
        mAdapter.setData(courseVos);
        mListView.setAdapter(mAdapter);

        if(EmptyUtil.isEmpty(courseVos)){
            // 数据为空
            mListView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mListView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateMoreTutorialCoursesView(List<CourseVo> courseVos) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        mAdapter.addData(courseVos);
        mAdapter.notifyDataSetChanged();
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
     * 申请成为帮辅，注册信息的入口
     * @param context
     */
    public static void show(@NonNull final Context context, @NonNull TutorialCoursesParams params){
        Intent intent = new Intent(context,TutorialCoursesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
