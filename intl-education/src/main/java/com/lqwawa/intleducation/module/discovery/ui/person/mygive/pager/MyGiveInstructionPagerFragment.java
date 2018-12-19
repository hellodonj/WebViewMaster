package com.lqwawa.intleducation.module.discovery.ui.person.mygive.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.role.LQwawaRoleType;
import com.lqwawa.intleducation.module.discovery.ui.myonline.MyOnlinePagerFragment;
import com.lqwawa.intleducation.module.discovery.ui.person.myhistory.HistoryClassActivity;
import com.lqwawa.intleducation.module.discovery.ui.person.timetable.TimeTableActivity;
import com.lqwawa.intleducation.module.discovery.ui.person.timetable.TimeTableParams;
import com.lqwawa.intleducation.module.onclass.OnlineClassAdapter;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassInfoParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;

/**
 * @author medici
 * @desc 我的授课片段
 */
public class MyGiveInstructionPagerFragment extends PresenterFragment<MyGiveInstructionPagerContract.Presenter>
    implements MyGiveInstructionPagerContract.View,View.OnClickListener{

    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private PullToRefreshView mRefreshLayout;
    private CourseEmptyView mEmptyLayout;
    private RecyclerView mRecycler;
    // 查看历史班
    private TextView mTvWatchHistory;
    private Button mBtnWatchHistory;
    private Button mBtnTimeTable;
    private NewOnlineClassAdapter mClassAdapter;

    // 页码
    private int pageIndex;

    // 当前点击的在线课堂班级
    private OnlineClassEntity mCurrentClickEntity;

    public static MyGiveInstructionPagerFragment newInstance(){
        MyGiveInstructionPagerFragment fragment = new MyGiveInstructionPagerFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected MyGiveInstructionPagerContract.Presenter initPresenter() {
        return new MyGiveInstructionPagerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_my_give_instruction_pager;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mSearchContent = (EditText) mRootView.findViewById(R.id.et_search);
        mSearchClear = (ImageView) mRootView.findViewById(R.id.iv_search_clear);
        mSearchFilter = (TextView) mRootView.findViewById(R.id.tv_filter);

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
                    requestClassData(false);
                }
                return true;
            }
        });


        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mEmptyLayout = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mTvWatchHistory = (TextView) mRootView.findViewById(R.id.tv_watch_history);
        mBtnWatchHistory = (Button) mRootView.findViewById(R.id.btn_watch_history);
        mBtnTimeTable = (Button) mRootView.findViewById(R.id.btn_timetable);
        mTvWatchHistory.setOnClickListener(this);
        mBtnWatchHistory.setOnClickListener(this);
        mBtnTimeTable.setOnClickListener(this);
        mRecycler.setNestedScrollingEnabled(false);
        /*GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };*/

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());

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

        mPresenter.requestMyGiveOnlineCourse(UserHelper.getUserId(),searchKey,pageIndex);
    }


    @Override
    public void updateMyGiveOnlineCourseView(@NonNull List<OnlineClassEntity> entities) {
        mClassAdapter.replace(entities);
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
    public void updateMyGiveOnlineMoreCourseView(@NonNull List<OnlineClassEntity> entities) {
        mClassAdapter.add(entities);
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);

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
     * 点击在线课堂班级
     * @param onlineClassEntity 班级实体
     */
    private void onClickClass(OnlineClassEntity onlineClassEntity) {
        mCurrentClickEntity = onlineClassEntity;
        /*String classId = onlineClassEntity.getClassId();
        mPresenter.requestLoadClassInfo(classId);*/
        ClassInfoParams params = new ClassInfoParams(onlineClassEntity);
        ClassDetailActivity.show(getActivity(),params);
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
        }else if(viewId == R.id.et_search){
            // 点击搜索框
        }else if(viewId == R.id.tv_watch_history || viewId == R.id.btn_watch_history){
            // 查看历史班
            HistoryClassActivity.show(getContext(),null);
        }else if(viewId == R.id.btn_timetable){
            // 课程表
            String curMemberId = UserHelper.getUserId();
            TimeTableParams params = new TimeTableParams(curMemberId,LQwawaRoleType.ROLE_TYPE_TEACHER);
            TimeTableActivity.show(getActivity(),params);
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
