package com.lqwawa.intleducation.module.tutorial.target.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.tutorial.marking.list.MarkingStateType;
import com.lqwawa.intleducation.module.tutorial.marking.list.OrderByType;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialRoleType;
import com.lqwawa.intleducation.module.tutorial.marking.list.pager.TutorialTaskAdapter;
import com.lqwawa.intleducation.module.tutorial.marking.require.TaskRequirementActivity;
import com.lqwawa.intleducation.module.tutorial.target.TutorialTargetTaskContract;
import com.lqwawa.intleducation.module.tutorial.target.TutorialTargetTaskParams;
import com.lqwawa.intleducation.module.tutorial.target.TutorialTargetTaskPresenter;

import java.util.Date;
import java.util.List;

/**
 * @authr mrmedici
 * @desc 我的帮辅学生，我的帮辅老师页面
 */
public class TutorialTargetPagerTaskFragment extends PresenterFragment<TutorialTargetPagerTaskContract.Presenter>
    implements TutorialTargetPagerTaskContract.View,View.OnClickListener{

    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private CourseEmptyView mEmptyView;
    // Adapter
    private TutorialTaskAdapter mTutorialAdapter;


    // 分页数
    private int currentPage;

    private TutorialTargetTaskParams mTargetTaskParams;
    private String mMemberId;
    private String mTutorMemberId;
    private String mConfigValue;
    private String mRole;
    private boolean isTutorial;
    private boolean isParent;
    private int mState;

    private String mSearchKey;

    @Override
    protected TutorialTargetPagerTaskContract.Presenter initPresenter() {
        return new TutorialTargetPagerTaskPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_tutorial_target_pager_task;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        if(bundle.containsKey(FRAGMENT_BUNDLE_OBJECT)){
            mTargetTaskParams = (TutorialTargetTaskParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
            if(EmptyUtil.isNotEmpty(mTargetTaskParams)){
                mMemberId = mTargetTaskParams.getMemberId();
                mTutorMemberId = mTargetTaskParams.getTutorMemberId();
                mConfigValue = mTargetTaskParams.getConfigValue();
                mRole = mTargetTaskParams.getRole();
                isTutorial = TextUtils.equals(mRole,TutorialRoleType.TUTORIAL_TYPE_TUTOR);
                isParent = mTargetTaskParams.isParent();
                mState = mTargetTaskParams.getState();
            }
        }

        if(EmptyUtil.isEmpty(mMemberId) ||
                // EmptyUtil.isEmpty(mTutorMemberId) ||
                EmptyUtil.isEmpty(mConfigValue)){
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
                    loadStudyTask();
                }
                return true;
            }
        });

        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mEmptyView = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);

        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollVertically() {
                return super.canScrollVertically();
            }
        };
        mRecycler.setLayoutManager(mLayoutManager);
        mTutorialAdapter = new TutorialTaskAdapter(isTutorial);
        mRecycler.setAdapter(mTutorialAdapter);
        mRecycler.addItemDecoration(new RecyclerItemDecoration(getActivity(),RecyclerItemDecoration.VERTICAL_LIST));

        mTutorialAdapter.setCallback(new TutorialTaskAdapter.EntityCallback() {
            @Override
            public void onRequireClick(View it, int position, @NonNull TaskEntity entity) {
                if(isTutorial || true) {
                    TaskRequirementActivity.show(getActivity(),entity);
                }else{
                    if (TaskSliderHelper.onTaskSliderListener != null) {
                        String id = entity.getResId();
                        if(EmptyUtil.isNotEmpty(id) && id.contains("-")){
                            String[] strings = id.split("-");
                            String resId = strings[0];
                            String resType = strings[1];
                            String title = entity.getTitle();
                            String resUrl = entity.getResUrl();
                            String resThumbnailUrl = entity.getResThumbnailUrl();
                            TaskSliderHelper.onTutorialMarkingListener.openCourseWareDetails(
                                    getActivity(),false,
                                    resId,Integer.parseInt(resType),
                                    title,1,
                                    resUrl,resThumbnailUrl);
                        }
                    }
                }
            }

            @Override
            public void onEntityClick(View it, int position, @NonNull TaskEntity entity, int state) {
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onTaskSliderListener)){
                    TaskSliderHelper.onTutorialMarkingListener.openAssistanceMark(getActivity(),entity,mRole);
                }
            }

            @Override
            public void onCheckMark(View it, int position, @NonNull TaskEntity entity, int state) {
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onTaskSliderListener)){
                    TaskSliderHelper.onTutorialMarkingListener.openAssistanceMark(getActivity(),entity,mRole);
                }
            }
        });

        mTutorialAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<TaskEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, TaskEntity entity) {
                super.onItemClick(holder, entity);
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onTaskSliderListener)){
                    TaskSliderHelper.onTutorialMarkingListener.openAssistanceMark(getActivity(),entity,mRole);
                }
            }
        });

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                loadStudyTask();
            }
        });

        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                loadStudyTask(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        loadStudyTask();
    }

    /**
     * 加载作业数据
     */
    private void loadStudyTask(){
        loadStudyTask(false);
    }

    /**
     * 加载作业数据
     * @param moreData 是否加载更多
     */
    private void loadStudyTask(boolean moreData){
        if(moreData){
            currentPage ++;
        }else{
            currentPage = 0;
        }

        String title = mSearchContent.getText().toString().trim();
        mPresenter.requestWorkDataWithIdentityId(mMemberId, mTutorMemberId,
                "", title,
                "", "",
                "", "",
                mState, OrderByType.MARKING_DESC_TIME_DESC,
                currentPage);
    }

    @Override
    public void updateWorkDataWithIdentityIdView(List<TaskEntity> entities) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
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
    public void updateMoreWorkDataWithIdentityIdView(List<TaskEntity> entities) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(entities.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        mTutorialAdapter.add(entities);
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tv_filter){
            // 搜索 兼容其它平板问题，收起软件盘
            KeyboardUtil.hideSoftInput(getActivity());
            loadStudyTask();
        }else if(viewId == R.id.iv_search_clear){
            // 删除关键字
            mSearchContent.getText().clear();
            loadStudyTask();
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
     * 学生帮辅列表的入口
     */
    public static Fragment newInstance(@NonNull TutorialTargetTaskParams params){
        Fragment fragment = new TutorialTargetPagerTaskFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        fragment.setArguments(arguments);
        return fragment;
    }
}
