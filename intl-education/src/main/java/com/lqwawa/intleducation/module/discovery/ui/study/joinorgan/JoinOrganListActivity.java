package com.lqwawa.intleducation.module.discovery.ui.study.joinorgan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.study.OrganAdapter;
import com.lqwawa.intleducation.module.onclass.OnlineClassListActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 入驻机构列表
 */
public class JoinOrganListActivity extends PresenterActivity<JoinOrganListContract.Presenter>
    implements JoinOrganListContract.View,View.OnClickListener{

    private static final String KEY_EXTRA_OBJECT = "KEY_EXTRA_OBJECT";

    private TopBar mTopBar;
    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private PullToRefreshView mRefreshLayout;
    private CourseEmptyView mEmptyLayout;
    private RecyclerView mRecycler;
    private OrganAdapter mOrganAdapter;
    private List<OnlineStudyOrganEntity> mTestEntities;

    // 页码
    private int pageIndex;


    @Override
    protected JoinOrganListContract.Presenter initPresenter() {
        return new JoinOrganListPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_join_organ_list;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        // mTestEntities = (List<OnlineStudyOrganEntity>) bundle.getSerializable(KEY_EXTRA_OBJECT);
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_join_school);
        mSearchContent = (EditText) findViewById(R.id.et_search);
        mSearchClear = (ImageView) findViewById(R.id.iv_search_clear);
        mSearchFilter = (TextView) findViewById(R.id.tv_filter);

        mSearchFilter.setVisibility(View.VISIBLE);
        mSearchClear.setOnClickListener(this);
        mSearchFilter.setOnClickListener(this);

        // 更改搜索框的提示语  输入您要查找的学校名
        mSearchContent.setHint(R.string.label_search_school_hint);

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
                    KeyboardUtil.hideSoftInput(JoinOrganListActivity.this);
                    requestOrganData(false);
                }
                return true;
            }
        });


        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mEmptyLayout = (CourseEmptyView) findViewById(R.id.empty_layout);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        /*GridLayoutManager mLayoutManager = new GridLayoutManager(this,3){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };*/
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
                    LoginHelper.enterLogin(JoinOrganListActivity.this);
                    return;
                }
                mPresenter.requestSchoolInfo(entity.getId(),entity);
            }
        });

        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                requestOrganData(false);
            }
        });

        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                requestOrganData(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        // mOrganAdapter.replace(mTestEntities);
        requestOrganData(false);
    }

    /**
     * 加载班级数据
     * @param isMoreData 是否是加载更多
     */
    private void requestOrganData(boolean isMoreData){
        String searchKey = mSearchContent.getText().toString().trim();

        if(isMoreData){
            ++ pageIndex;
        }else{
            pageIndex = 0;
            mRefreshLayout.showRefresh();
        }

        mPresenter.requestSchoolData(pageIndex,searchKey);
    }

    @Override
    public void updateSchoolDataView(@NonNull List<OnlineStudyOrganEntity> entities) {
        mOrganAdapter.replace(entities);
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
    public void updateSchoolMoreDataView(@NonNull List<OnlineStudyOrganEntity> entities) {
        mOrganAdapter.add(entities);
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);

        if(EmptyUtil.isEmpty(mOrganAdapter.getItems())){
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
    public void updateSchoolInfoView(@NonNull SchoolInfoEntity infoEntity, @NonNull OnlineStudyOrganEntity entity) {
        // 关注机构
        subscribeSchool(infoEntity,entity);
    }

    /**
     * 检查关注信息
     */
    private void subscribeSchool(@NonNull SchoolInfoEntity mSchoolEntity,@NonNull OnlineStudyOrganEntity entity){
        // 点击关注
        if(EmptyUtil.isEmpty(mSchoolEntity)){
            // 已经进入机构
            return;
        }

        if(mSchoolEntity.hasJoinedSchool() || mSchoolEntity.hasSubscribed()){
            // 已关注
            // 点击机构，进入机构开课班
            OnlineClassListActivity.show(this,entity.getId(),entity.getName());
        }else{
            // 如果没有关注 +关注
            SchoolHelper.requestSubscribeSchool(mSchoolEntity.getSchoolId(), new DataSource.Callback<Object>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                    UIUtil.showToastSafe(strRes);
                }

                @Override
                public void onDataLoaded(Object object) {
                    // 关注成功,发送广播,刷新UI
                    // 点击机构，进入机构开课班
                    OnlineClassListActivity.show(JoinOrganListActivity.this,entity.getId(),entity.getName());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tv_filter){
            // 搜索 兼容其它平板问题，收起软件盘
            KeyboardUtil.hideSoftInput(this);
            requestOrganData(false);
        }else if(viewId == R.id.iv_search_clear){
            // 删除关键字
            mSearchContent.getText().clear();
            requestOrganData(false);
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
     * 更多入驻机构的入口
     * @param context 上下文对象
     * @param entities 测试数据入口
     */
    public static void show(@NonNull Context context,List<OnlineStudyOrganEntity> entities){
        Intent intent = new Intent(context,JoinOrganListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_OBJECT, (Serializable) entities);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
