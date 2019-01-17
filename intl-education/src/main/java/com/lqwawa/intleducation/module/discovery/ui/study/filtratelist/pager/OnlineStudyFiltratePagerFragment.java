package com.lqwawa.intleducation.module.discovery.ui.study.filtratelist.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.courselist.LQCourseListActivity;
import com.lqwawa.intleducation.module.onclass.OnlineClassAdapter;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassInfoParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * @author medici
 * @desc 在线学习二级列表Pager页
 */
public class OnlineStudyFiltratePagerFragment extends PresenterFragment<OnlineStudyFiltratePagerContract.Presenter>
    implements OnlineStudyFiltratePagerContract.View, View.OnClickListener{
    // 分类信息Entity
    private static final String KEY_EXTRA_CONFIG_ENTITY = "KEY_EXTRA_CONFIG_ENTITY";
    // 滑动到的Label Position
    private static final String KEY_EXTRA_LABEL_POSITION = "KEY_EXTRA_LABEL_POSITION";
    // 是否是全部Tab
    private static final String KEY_EXTRA_ALL_TAB = "KEY_EXTRA_ALL_TAB";
    // 搜索的关键字
    private static final String KEY_EXTRA_SEARCH_KEY = "KEY_EXTRA_SEARCH_KEY";

    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private OnlineClassAdapter mClassAdapter;
    private CourseEmptyView mEmptyLayout;

    private OnlineConfigEntity mConfigEntity;
    private int mLabelPosition;
    private boolean isAll;
    private int pageIndex;
    private String mSearchKey;
    // 当前点击的班级
    private OnlineClassEntity mCurrentClickEntity;

    public static OnlineStudyFiltratePagerFragment newInstance(
            @NonNull OnlineConfigEntity entity,
            int labelPosition,boolean isAll,
            @NonNull String searchKey){
        OnlineStudyFiltratePagerFragment fragment = new OnlineStudyFiltratePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_CONFIG_ENTITY,entity);
        bundle.putInt(KEY_EXTRA_LABEL_POSITION,labelPosition);
        bundle.putBoolean(KEY_EXTRA_ALL_TAB,isAll);
        bundle.putString(KEY_EXTRA_SEARCH_KEY,searchKey);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected OnlineStudyFiltratePagerContract.Presenter initPresenter() {
        return new OnlineStudyFiltratePagerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_online_study_filtrate_pager;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mConfigEntity = (OnlineConfigEntity) bundle.getSerializable(KEY_EXTRA_CONFIG_ENTITY);
        mLabelPosition = bundle.getInt(KEY_EXTRA_LABEL_POSITION);
        isAll = bundle.getBoolean(KEY_EXTRA_ALL_TAB);
        mSearchKey = bundle.getString(KEY_EXTRA_SEARCH_KEY);
        if(EmptyUtil.isEmpty(mConfigEntity)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mSearchContent = (EditText) mRootView.findViewById(R.id.et_search);
        mSearchClear = (ImageView) mRootView.findViewById(R.id.iv_search_clear);
        mSearchFilter = (TextView) mRootView.findViewById(R.id.tv_filter);

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
                    KeyboardUtil.hideSoftInput(getActivity());
                    loadData(false);
                }
                return true;
            }
        });

        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mEmptyLayout = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(2,8,false));
        mClassAdapter = new OnlineClassAdapter();
        mRecycler.setAdapter(mClassAdapter);

        mClassAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<OnlineClassEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, OnlineClassEntity onlineClassEntity) {
                super.onItemClick(holder, onlineClassEntity);
                onClickClass(onlineClassEntity);
            }
        });

        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                loadData(false);
            }
        });

        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                loadData(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        loadData(false);
    }

    private void loadData(boolean moreData){
        if(moreData){
            pageIndex++;
        }else{
            pageIndex = 0;
        }

        int firstId = mConfigEntity.getFirstId();
        int secondId = mConfigEntity.getSecondId();
        int thirdId = mConfigEntity.getThirdId();
        int fourthId = mConfigEntity.getFourthId();

        mSearchKey = mSearchContent.getText().toString().trim();

        if(!isAll){
            int num = mConfigEntity.getNum();
            List<OnlineConfigEntity.OnlineLabelEntity> childList = mConfigEntity.getChildList();
            switch (num){
                case 1:
                    firstId = childList.get(mLabelPosition).getId();
                    break;
                case 2:
                    secondId = childList.get(mLabelPosition).getId();
                    break;
                case 3:
                    thirdId = childList.get(mLabelPosition).getId();
                    break;
                case 4:
                    fourthId = childList.get(mLabelPosition).getId();
                    break;
            }
        }

        mPresenter.requestOnlineStudyDataFromLabel(pageIndex,mSearchKey,-1,firstId,secondId,thirdId,fourthId);
    }

    @Override
    public void updateOnlineStudyClassDataView(@NonNull List<OnlineClassEntity> entities) {
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mClassAdapter.replace(entities);
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
    public void updateOnlineStudyMoreClassDataView(@NonNull List<OnlineClassEntity> entities) {
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);
        mClassAdapter.getItems().addAll(entities);
        mClassAdapter.notifyDataSetChanged();

        if(EmptyUtil.isEmpty(mClassAdapter.getItems())){
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 点击在线班级
     * @param entity 点击在线课堂班级的实体
     */
    private void onClickClass(@NonNull OnlineClassEntity entity){
        mCurrentClickEntity = entity;
        ClassInfoParams params = new ClassInfoParams(entity);
        ClassDetailActivity.show(getActivity(),params);
        // mPresenter.requestLoadClassInfo(entity.getClassId());
    }

    @Override
    public void onClassCheckSucceed(@NonNull JoinClassEntity entity) {
        // 非空判断
        if(EmptyUtil.isEmpty(entity) || EmptyUtil.isEmpty(mCurrentClickEntity)){
            return;
        }

        boolean needToJoin = entity.isIsInClass();
        String roles = entity.getRoles();
        String role = UserHelper.getOnlineRoleWithUserRoles(roles);
        if(needToJoin || OnlineClassRole.ROLE_TEACHER.equals(role)){
            // 已经加入班级 或者是老师身份
            JoinClassDetailActivity.show(getActivity(),entity.getClassId(),entity.getSchoolId(),mCurrentClickEntity.getId(),role,false);
        }else{
            // 未加入班级
            ClassDetailActivity.show(getActivity(),entity.getClassId(),entity.getSchoolId(),mCurrentClickEntity.getId(),role,false);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tv_filter){
            // 搜索 兼容其它平板问题，收起软件盘
            KeyboardUtil.hideSoftInput(getActivity());
            loadData(false);
        }else if(viewId == R.id.iv_search_clear){
            // 删除关键字
            mSearchContent.getText().clear();
            loadData(false);
        }else if(viewId == R.id.et_search){
            // 点击搜索框
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event){
        if(EventWrapper.isMatch(event, EventConstant.ONLINE_CLASS_COMPLETE_GIVE_EVENT)){
            // 刷新UI
            loadData(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
