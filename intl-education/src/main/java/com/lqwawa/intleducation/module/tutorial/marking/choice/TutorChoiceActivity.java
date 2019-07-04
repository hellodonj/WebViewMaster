package com.lqwawa.intleducation.module.tutorial.marking.choice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorChoiceEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.ui.PayActivity;
import com.lqwawa.intleducation.module.tutorial.marking.result.QuestionResultActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class TutorChoiceActivity extends PresenterActivity<TutorChoiceContract.Presenter>
        implements TutorChoiceContract.View, View.OnClickListener {

    private TopBar mTopBar;
    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private CourseEmptyView mEmptyView;
    private Button mBtnConfirm;

    private TutorChoiceAdapter mAdapter;

    private TutorChoiceParams mChoiceParams;
    private String mCurMemberId;
    private String mCourseId;
    private String mChapterId;
    private QuestionResourceModel mResourceModel;
    private int pageIndex;
    private boolean mIsMyAssistantMark;

    @Override
    protected TutorChoiceContract.Presenter initPresenter() {
        return new TutorChoicePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return R.layout.activity_tutor_choice;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mChoiceParams = (TutorChoiceParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if (EmptyUtil.isNotEmpty(mChoiceParams)) {
            mCurMemberId = mChoiceParams.getMemberId();
            mCourseId = mChoiceParams.getCourseId();
            mChapterId = mChoiceParams.getChapterId();
            mResourceModel = mChoiceParams.getModel();
            mIsMyAssistantMark = mChoiceParams.isMyAssistantMark();

            if (!TextUtils.isEmpty(mCourseId) && !mCourseId.equals("0")
                    || !TextUtils.isEmpty(mChapterId) && !mChapterId.equals("0")) {
                mIsMyAssistantMark = false;
            } else {
                mIsMyAssistantMark = true;
            }

            if (EmptyUtil.isEmpty(mCurMemberId) ||
                    (EmptyUtil.isEmpty(mCourseId) && EmptyUtil.isEmpty(mChapterId))) {
                return false;
            }

            if (EmptyUtil.isNotEmpty(mCourseId)
                    && Integer.parseInt(mCourseId) <= 0) {
                mCourseId = "";
            }

            if (EmptyUtil.isNotEmpty(mChapterId) &&
                    Integer.parseInt(mChapterId) <= 0) {
                mChapterId = "";
            }
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_choice_tutor);
        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mEmptyView = (CourseEmptyView) findViewById(R.id.empty_layout);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);

        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new TutorChoiceAdapter();
        mRecycler.setAdapter(mAdapter);
        mRecycler.addItemDecoration(new RecyclerItemDecoration(this, RecyclerItemDecoration.VERTICAL_LIST));

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<TutorChoiceEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, TutorChoiceEntity tutorChoiceEntity) {
                super.onItemClick(holder, tutorChoiceEntity);
                List<TutorChoiceEntity> items = mAdapter.getItems();
                for (TutorChoiceEntity item : items) {
                    if (tutorChoiceEntity.equals(item)) {
                        tutorChoiceEntity.setChecked(!tutorChoiceEntity.isChecked());
                    } else {
                        item.setChecked(false);
                    }
                }
                mAdapter.notifyDataSetChanged();
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

    /**
     * 获取帮辅老师列表
     *
     * @param moreData 是否更多数据
     */
    private void requestTutorData(boolean moreData) {
        if (!moreData) {
            pageIndex = 0;
        } else {
            pageIndex++;
        }

        if (!mIsMyAssistantMark) {
            mPresenter.requestChoiceTutorData(mCurMemberId, mCourseId, mChapterId, pageIndex);
        } else {
            mPresenter.requestChoiceTutorData(mCurMemberId, pageIndex);
        }
    }

    @Override
    public void updateChoiceTutorView(List<TutorChoiceEntity> entities) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);
        filterEntities(entities);
        mAdapter.replace(entities);

        if (EmptyUtil.isEmpty(entities)) {
            // 数据为空
            mRecycler.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            // 数据不为空
            mRecycler.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateMoreChoiceTutorView(List<TutorChoiceEntity> entities) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);
        filterEntities(entities);
        // 设置数据
        mAdapter.add(entities);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 过滤数据
     *
     * @param entities 帮辅老师数据
     */
    private void filterEntities(@NonNull List<TutorChoiceEntity> entities) {
        if (EmptyUtil.isNotEmpty(entities)) {
            ListIterator<TutorChoiceEntity> iterator = entities.listIterator();
            while (iterator.hasNext()) {
                TutorChoiceEntity next = iterator.next();
                if (TextUtils.equals(next.getMemberId(), UserHelper.getUserId())) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_confirm) {
            List<TutorChoiceEntity> items = mAdapter.getItems();
            boolean trigger = false;
            for (TutorChoiceEntity item : items) {
                if (item.isChecked()) {
                    trigger = true;
                    PayActivity.newInstance(this, true,mChoiceParams, item);
                    finish();
                }
            }

            if (!trigger) {
                UIUtil.showToastSafe(R.string.label_choice_tutor_tip);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event) {
        if (EventWrapper.isMatch(event, EventConstant.CREATE_TUTOR_ORDER)) {
            List<TutorChoiceEntity> items = mAdapter.getItems();
            for (TutorChoiceEntity item : items) {
                if (item.isChecked()) {
                    // 发送作业
                    final QuestionResourceModel model = mResourceModel;
                    model.setStuMemberId(mCurMemberId);
                    model.setAssMemberId(item.getMemberId());
                    model.setOrderId((Integer) event.getData());
                    String object = JSON.toJSONString(model);
                    showLoading();
                    mPresenter.requestAddAssistTask(item, object);

                }
            }
        }
    }

    @Override
    public void updateAddAssistTaskView(@NonNull TutorChoiceEntity entity) {
        hideLoading();
        // 进入下一个页面
        QuestionResultActivity.show(this, mCurMemberId, entity.getTutorName());
        finish();
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 申请批阅选择入口
     *
     * @param context 上下文对象
     * @param params  参数
     */
    public static void show(@NonNull Context context, @NonNull TutorChoiceParams params) {
        Intent intent = new Intent(context, TutorChoiceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT, params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
