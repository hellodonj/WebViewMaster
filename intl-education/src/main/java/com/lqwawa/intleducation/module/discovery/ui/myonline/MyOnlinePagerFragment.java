package com.lqwawa.intleducation.module.discovery.ui.myonline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.factory.role.LQwawaRoleType;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.tab.TabCourseEmptyView;
import com.lqwawa.intleducation.module.discovery.ui.person.mygive.pager.NewOnlineClassAdapter;
import com.lqwawa.intleducation.module.discovery.ui.person.timetable.TimeTableActivity;
import com.lqwawa.intleducation.module.discovery.ui.person.timetable.TimeTableParams;
import com.lqwawa.intleducation.module.discovery.ui.study.classifyclass.OnlineClassClassifyActivity;
import com.lqwawa.intleducation.module.learn.ui.MyLiveListActivity;
import com.lqwawa.intleducation.module.onclass.OnlineClassAdapter;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassInfoParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.oosic.apps.iemaker.base.slide_audio.AudioRecorder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;

/**
 * @author medici
 * @desc 我的课程，我的在线学习
 */
public class MyOnlinePagerFragment extends PresenterFragment<MyOnlinePagerContract.Presenter>
    implements MyOnlinePagerContract.View,View.OnClickListener{

    // 去在线学习
    public static final String ACTION_GO_ONLINE_STUDY = "ACTION_GO_ONLINE_STUDY";

    // 当前MemberId的Key
    private static final String KEY_EXTRA_CURRENT_MEMBER_ID = "KEY_EXTRA_CURRENT_MEMBER_ID";
    private static final String KEY_EXTRA_HIDE_TOP_SEARCH = "KEY_EXTRA_HIDE_TOP_SEARCH";

    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private PullToRefreshView mRefreshLayout;
    private TabCourseEmptyView mEmptyLayout;
    private RecyclerView mRecycler;
    private NewOnlineClassAdapter mClassAdapter;

    private TextView mTvTimeTable;

    private String mCurMemberId;
    // 是否隐藏搜索
    private boolean mHideTopSearch;
    // 页码
    private int pageIndex;

    // 当前点击的在线课堂班级
    private OnlineClassEntity mCurrentClickEntity;

    public static MyOnlinePagerFragment newInstance(@NonNull String curMemberId,boolean hideTopSearch){
        MyOnlinePagerFragment fragment = new MyOnlinePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_CURRENT_MEMBER_ID,curMemberId);
        bundle.putBoolean(KEY_EXTRA_HIDE_TOP_SEARCH,hideTopSearch);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected MyOnlinePagerContract.Presenter initPresenter() {
        return new MyOnlinePagerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_my_online_pager;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mCurMemberId = bundle.getString(KEY_EXTRA_CURRENT_MEMBER_ID);
        mHideTopSearch = bundle.getBoolean(KEY_EXTRA_HIDE_TOP_SEARCH);
        if(EmptyUtil.isEmpty(mCurMemberId)){
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
        mTvTimeTable = (TextView) mRootView.findViewById(R.id.tv_timetable);

        mSearchFilter.setVisibility(View.VISIBLE);
        mSearchClear.setOnClickListener(this);
        mSearchFilter.setOnClickListener(this);
        mTvTimeTable.setOnClickListener(this);

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
                    requestClassData(false);
                }
                return true;
            }
        });


        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mEmptyLayout = (TabCourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mRecycler.setLayoutManager(mLayoutManager);
        // mRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(2,8,false));
        mClassAdapter = new NewOnlineClassAdapter();
        mRecycler.setAdapter(mClassAdapter);

        mClassAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<OnlineClassEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, OnlineClassEntity onlineClassEntity) {
                super.onItemClick(holder, onlineClassEntity);
                onClickClass(onlineClassEntity);
            }
        });

        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                requestClassData(false);
            }
        });

        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                requestClassData(true);
            }
        });

        mEmptyLayout.setSubmitListener(v->{
            // 去在线学习
            Intent broadIntent = new Intent();
            broadIntent.setAction(ACTION_GO_ONLINE_STUDY);
            getContext().sendBroadcast(broadIntent);
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        requestClassData(false);
    }


    /**
     * 加载班级数据
     * @param isMoreData 是否是加载更多
     */
    private void requestClassData(boolean isMoreData){
        String searchKey = mSearchContent.getText().toString().trim();

        if(isMoreData){
            ++ pageIndex;
        }else{
            pageIndex = 0;
            mRefreshLayout.showRefresh();
        }

        mPresenter.requestOnlineCourseData(mCurMemberId,searchKey,pageIndex);
    }

    @Override
    public void updateOnlineCourseView(@NonNull List<OnlineClassEntity> entities) {
        mClassAdapter.replace(entities);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);

        if(EmptyUtil.isEmpty(entities)){
            // 数据为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateMoreOnlineCourseView(@NonNull List<OnlineClassEntity> entities) {
        mClassAdapter.add(entities);
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);

        if(EmptyUtil.isEmpty(mClassAdapter.getItems())){
            // 数据为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 点击在线课堂班级
     * @param onlineClassEntity 班级实体
     */
    private void onClickClass(OnlineClassEntity onlineClassEntity) {
        mCurrentClickEntity = onlineClassEntity;
        ClassInfoParams params = new ClassInfoParams(onlineClassEntity);
        if(!TextUtils.equals(mCurMemberId,UserHelper.getUserId())){
            // 家长查看孩子
            params.setParent(true,mCurMemberId);
        }
        ClassDetailActivity.show(getActivity(),params);
        // String classId = onlineClassEntity.getClassId();
        // mPresenter.requestLoadClassInfo(classId);
    }

    @Override
    public void onClassCheckSucceed(JoinClassEntity entity) {
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
            requestClassData(false);
        }else if(viewId == R.id.iv_search_clear){
            // 删除关键字
            mSearchContent.getText().clear();
            requestClassData(false);
        }else if(viewId == R.id.et_search){
            // 点击搜索框
        }else if(viewId == R.id.tv_timetable){
            // 从我的课程入口进来 角色都传1
            TimeTableParams params = new TimeTableParams(mCurMemberId,LQwawaRoleType.ROLE_TYPE_STUDENT);
            TimeTableActivity.show(getActivity(),params);
            // MyLiveListActivity.start(getActivity());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!getUserVisibleHint()){
            // 隐藏
            if(EmptyUtil.isNotEmpty(mSearchContent))
            mSearchContent.getText().clear();
        }
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event){
        if(EventWrapper.isMatch(event, EventConstant.ONLINE_CLASS_COMPLETE_GIVE_EVENT)){
            // 刷新UI
            requestClassData(false);
        }else if(EventWrapper.isMatch(event,EventConstant.JOIN_IN_CLASS_EVENT)){
            // 成功参加在线课堂
            // 刷新UI
            requestClassData(false);
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
