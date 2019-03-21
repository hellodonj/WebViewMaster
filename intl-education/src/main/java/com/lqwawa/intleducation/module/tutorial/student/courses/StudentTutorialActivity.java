package com.lqwawa.intleducation.module.tutorial.student.courses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
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
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorEntity;
import com.lqwawa.intleducation.module.tutorial.course.filtrate.TutorialFiltrateGroupActivity;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialRoleType;
import com.lqwawa.intleducation.module.tutorial.target.TutorialTargetTaskActivity;
import com.lqwawa.intleducation.module.tutorial.target.TutorialTargetTaskParams;

import java.util.Date;
import java.util.List;

/**
 * @authr mrmedici
 * @desc 学生帮辅列表的View
 */
public class StudentTutorialActivity extends PresenterActivity<StudentTutorialContract.Presenter>
    implements StudentTutorialContract.View,View.OnClickListener{

    private TopBar mTopBar;
    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private CourseEmptyView mEmptyView;
    private TextView mTvAddTutorial;
    // Adapter
    private StudentTutorialAdapter mTutorialAdapter;


    // 分页数
    private int currentPage;

    private StudentTutorialParams mStudentTutorialParams;
    private String mMemberId;
    private String mConfigValue;
    private boolean isParent;

    private String mSearchKey;

    @Override
    protected StudentTutorialContract.Presenter initPresenter() {
        return new StudentTutorialPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_student_tutorial;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        if(bundle.containsKey(ACTIVITY_BUNDLE_OBJECT)){
            mStudentTutorialParams = (StudentTutorialParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
            if(EmptyUtil.isNotEmpty(mStudentTutorialParams)){
                mMemberId = mStudentTutorialParams.getMemberId();
                mConfigValue = mStudentTutorialParams.getConfigValue();
                isParent = mStudentTutorialParams.isParent();
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
                    KeyboardUtil.hideSoftInput(StudentTutorialActivity.this);
                    requestTutorData(false);
                }
                return true;
            }
        });

        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mEmptyView = (CourseEmptyView) findViewById(R.id.empty_layout);
        mTvAddTutorial = (TextView) findViewById(R.id.tv_add_tutorial);
        mTvAddTutorial.setOnClickListener(this);

        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mTutorialAdapter = new StudentTutorialAdapter();
        mRecycler.setAdapter(mTutorialAdapter);
        mRecycler.addItemDecoration(new RecyclerItemDecoration(this,RecyclerItemDecoration.VERTICAL_LIST));

        mTutorialAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<TutorEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, TutorEntity tutorEntity) {
                super.onItemClick(holder, tutorEntity);
                TutorialTargetTaskParams params = new TutorialTargetTaskParams(mMemberId,tutorEntity.getTutorMemberId(),getString(R.string.label_committed_works,tutorEntity.getTutorName()));
                params.setParent(isParent);
                params.setRole(isParent?TutorialRoleType.TUTORIAL_TYPE_PARENT:TutorialRoleType.TUTORIAL_TYPE_STUDENT);
                TutorialTargetTaskActivity.show(StudentTutorialActivity.this,params);
            }
        });

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                requestTutorData(false);
            }
        });

        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                requestTutorData(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestTutorData(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        requestTutorData(false);
    }

    /**
     * 请求课程
     * @param moreData 是否更多数据
     */
    private void requestTutorData(boolean moreData){
        mSearchKey = mSearchContent.getText().toString().trim();
        if(moreData){
            currentPage ++;
            mPresenter.requestStudentTutorialData(mMemberId,mSearchKey,currentPage);
        }else{
            currentPage = 0;
            mPresenter.requestStudentTutorialData(mMemberId,mSearchKey,currentPage);
        }
    }

    @Override
    public void updateStudentTutorialView(List<TutorEntity> entities) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(entities.size() >= AppConfig.PAGE_SIZE);
        mTutorialAdapter.replace(entities);

        if(EmptyUtil.isEmpty(entities)){
            // 数据为空
            mRecycler.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRecycler.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateMoreStudentTutorialView(List<TutorEntity> entities) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(entities.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        mTutorialAdapter.add(entities);
        mTutorialAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tv_filter){
            // 搜索 兼容其它平板问题，收起软件盘
            KeyboardUtil.hideSoftInput(this);
            requestTutorData(false);
        }else if(viewId == R.id.iv_search_clear){
            // 删除关键字
            mSearchContent.getText().clear();
            requestTutorData(false);
        }else if(viewId == R.id.et_search){
            // 点击搜索框
        }else if(viewId == R.id.tv_add_tutorial){
            // 添加帮辅
            TutorialFiltrateGroupActivity.show(this,mMemberId);
        }
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
    }

    /**
     * 学生帮辅列表的入口
     * @param context
     */
    public static void show(@NonNull final Context context, @NonNull StudentTutorialParams params){
        Intent intent = new Intent(context,StudentTutorialActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
