package com.lqwawa.intleducation.module.tutorial.teacher.schools;

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
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.MemberSchoolEntity;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.study.OrganAdapter;
import com.lqwawa.intleducation.module.onclass.OnlineClassListActivity;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.TutorialCoursesContract;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.TutorialCoursesPresenter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 我的帮辅机构列表
 */
public class TutorialSchoolsActivity extends PresenterActivity<TutorialSchoolsContract.Presenter>
    implements TutorialSchoolsContract.View,View.OnClickListener{

    private TopBar mTopBar;
    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private CourseEmptyView mEmptyView;
    // Adapter
    private OrganAdapter mOrganAdapter;


    // 分页数
    private int currentPage;

    private TutorialSchoolsParams mTutorialParams;
    private String mMemberId;
    private String mConfigValue;

    private String mSearchKey;

    @Override
    protected TutorialSchoolsContract.Presenter initPresenter() {
        return new TutorialSchoolsPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tutorial_schools;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        if(bundle.containsKey(ACTIVITY_BUNDLE_OBJECT)){
            mTutorialParams = (TutorialSchoolsParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
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
                    KeyboardUtil.hideSoftInput(TutorialSchoolsActivity.this);
                    requestSchoolData(false);
                }
                return true;
            }
        });

        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mEmptyView = (CourseEmptyView) findViewById(R.id.empty_layout);

        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mOrganAdapter = new OrganAdapter(true);
        mRecycler.setAdapter(mOrganAdapter);
        mRecycler.addItemDecoration(new RecyclerItemDecoration(this,RecyclerItemDecoration.VERTICAL_LIST));

        mOrganAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<OnlineStudyOrganEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, OnlineStudyOrganEntity entity) {
                super.onItemClick(holder, entity);
                // 拉接口,获取机构信息
                if(!UserHelper.isLogin()){
                    LoginHelper.enterLogin(TutorialSchoolsActivity.this);
                    return;
                }
                // mPresenter.requestSchoolInfo(entity.getId(),entity);
                // 点击机构，进入机构开课班
                OnlineClassListActivity.show(TutorialSchoolsActivity.this,entity.getId(),entity.getName());
            }
        });

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                requestSchoolData(false);
            }
        });

        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                requestSchoolData(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestSchoolData(false);
    }

    /**
     * 请求课程
     * @param moreData 是否更多数据
     */
    private void requestSchoolData(boolean moreData){
        mSearchKey = mSearchContent.getText().toString().trim();
        if(moreData){
            currentPage ++;
            mPresenter.requestTutorialSchoolsData(mMemberId,mSearchKey,currentPage);
        }else{
            currentPage = 0;
            mPresenter.requestTutorialSchoolsData(mMemberId,mSearchKey,currentPage);
        }
    }

    @Override
    public void updateTutorialSchoolsView(List<MemberSchoolEntity> entities) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        // mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        mOrganAdapter.replace(transformEntities(entities));

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
    public void updateMoreTutorialSchoolsView(List<MemberSchoolEntity> entities) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        // mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        mOrganAdapter.add(transformEntities(entities));
        mOrganAdapter.notifyDataSetChanged();
    }

    private List<OnlineStudyOrganEntity> transformEntities(List<MemberSchoolEntity> entities){
        List<OnlineStudyOrganEntity> targetEntities = new ArrayList<>();
        if(EmptyUtil.isNotEmpty(entities)) {
            for (MemberSchoolEntity entity : entities) {
                OnlineStudyOrganEntity targetEntity = entity.buildOnlineStudyOrganEntitiy();
                targetEntities.add(targetEntity);
            }
        }
        return targetEntities;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tv_filter){
            // 搜索 兼容其它平板问题，收起软件盘
            KeyboardUtil.hideSoftInput(this);
            requestSchoolData(false);
        }else if(viewId == R.id.iv_search_clear){
            // 删除关键字
            mSearchContent.getText().clear();
            requestSchoolData(false);
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
    public static void show(@NonNull final Context context, @NonNull TutorialSchoolsParams params){
        Intent intent = new Intent(context,TutorialSchoolsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
